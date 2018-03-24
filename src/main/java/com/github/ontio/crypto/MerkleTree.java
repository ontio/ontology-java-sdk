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

package com.github.ontio.crypto;

import java.util.Arrays;

import com.github.ontio.common.UInt256;

/**
 *  哈希树
 */
public class MerkleTree {
    /**
     *  计算根节点的值
     *  <param name="hashes">子节点列表</param>
     *  <returns>返回计算的结果</returns>
     */
    public static UInt256 computeRoot(UInt256[] hashes) {
        if (hashes.length == 0) {
            throw new IllegalArgumentException();
        }
        if (hashes.length == 1) {
            return hashes[0];
        }
        return new UInt256(computeRoot(Arrays.stream(hashes).map(p -> p.toArray()).toArray(byte[][]::new)));
    }

    private static byte[] computeRoot(byte[][] hashes) {
        if (hashes.length == 0) {
            throw new IllegalArgumentException(); 
        }
        if (hashes.length == 1) {
            return hashes[0];
        }
        if (hashes.length % 2 == 1) {
        	byte[][] temp = new byte[hashes.length + 1][];
        	System.arraycopy(hashes, 0, temp, 0, hashes.length);
        	temp[temp.length - 1] = hashes[hashes.length - 1];
        	hashes = temp;
        }
        byte[][] hashes_new = new byte[hashes.length / 2][];
        for (int i = 0; i < hashes_new.length; i++) {
        	byte[] buffer = new byte[hashes[i * 2].length + hashes[i * 2 + 1].length];
        	System.arraycopy(hashes[i * 2], 0, buffer, 0, hashes[i * 2].length);
        	System.arraycopy(hashes[i * 2 + 1], 0, buffer, hashes[i * 2].length, hashes[i * 2 + 1].length);
            hashes_new[i] = Digest.hash256(buffer);
        }
        return computeRoot(hashes_new);
    }
}
