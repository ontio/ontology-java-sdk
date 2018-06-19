/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.merkle;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.crypto.Digest;
import com.github.ontio.sdk.exception.SDKException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeHasher {
    public UInt256 hash_empty(){
        return new UInt256();
    }
    public UInt256 hash_leaf(byte[] data) {
        byte[] tmp = Helper.addBytes(new byte[]{0},data);
        return new UInt256(Digest.sha256(tmp));
    }
    public UInt256 hash_children(UInt256 left, UInt256 right ){
        byte[] data = Helper.addBytes(new byte[]{1},left.toArray());
        data = Helper.addBytes(data,right.toArray());
        return new UInt256(Digest.sha256(data));
    }
    public long countBit(long num ) {
        long count = 0;
        while(num != 0) {
            num &= (num - 1);
            count += 1;
        }
        return count;
    }
    public UInt256 HashFullTreeWithLeafHash(UInt256[] leaves ) throws Exception {
        long length =leaves.length;
        Obj obj = _hash_full(leaves, 0, length);

        if(obj.hashes.length != countBit(length) ){
            throw new SDKException(ErrorCode.AsserFailedHashFullTree);
        }
        return obj.root_hash;
    }
    public UInt256 HashFullTree(byte[][] leaves) throws Exception {
        int length = leaves.length;
        UInt256[] leafhashes = new UInt256[length];
        for (int i=0; i< length;i++) {
            leafhashes[i] = hash_leaf(leaves[i]);
        }
        Obj obj = _hash_full(leafhashes, 0, length);

        if (obj.hashes.length != countBit(length)) {
            throw new Exception(ErrorCode.AsserFailedHashFullTree);
        }
        return obj.root_hash;
    }

    public Obj _hash_full(UInt256[] leaves,long l_idx,long r_idx ) throws Exception {
        long width = r_idx - l_idx;
        if (width == 0 ){
            return new Obj(hash_empty(),null);
        } else if (width == 1) {
            UInt256 leaf_hash = leaves[(int)l_idx];
            return new Obj(leaf_hash,new UInt256[]{leaf_hash});
        } else {
            int split_width = 1 << (countBit(width-1) - 1);
            Obj lObj = _hash_full(leaves, l_idx, l_idx+split_width);
            if (lObj.hashes.length != 1 ){
                throw new Exception(ErrorCode.LeftTreeFull);
            }
            Obj rObj = _hash_full(leaves, l_idx+split_width, r_idx);
            UInt256 root_hash = hash_children(lObj.root_hash, rObj.root_hash);
            UInt256[] hashes = null;
            if (split_width * 2 == width) {
                hashes = new UInt256[]{root_hash};
            } else {
                hashes = Arrays.copyOf(lObj.hashes, lObj.hashes.length + rObj.hashes.length);
                System.arraycopy(rObj.hashes, 0, hashes, lObj.hashes.length, rObj.hashes.length);
            }
            return new Obj(root_hash, hashes);
        }
    }
    public UInt256 _hash_fold(UInt256[] hashes){
        int l = hashes.length;
        UInt256 accum = hashes[l-1];
        for(int i=l-2;i>=0;i--){
            accum = hash_children(hashes[i],accum);
        }
        return accum;
    }
    class Obj{
        public UInt256 root_hash;
        public UInt256[] hashes;
        public Obj(UInt256 root_hash,UInt256[] hashes){
            this.root_hash = root_hash;
            this.hashes = hashes;
        }
    }
}

