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
import com.github.ontio.common.UInt256;
import com.github.ontio.sdk.exception.SDKException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MerkleVerifier {
    private static TreeHasher hasher = new TreeHasher();


    public static boolean VerifyLeafHashInclusion(UInt256 leaf_hash,
                                                  int leaf_index, UInt256[] proof, UInt256 root_hash, int tree_size) throws Exception {

        if (tree_size <= leaf_index) {
            throw new SDKException(ErrorCode.MerkleVerifierErr);
        }
        UInt256 calculated_root_hash = calculate_root_hash_from_audit_path(leaf_hash,
                leaf_index, proof, tree_size);
        if (calculated_root_hash.equals(new UInt256())) {
            return false;
        }
        if (!calculated_root_hash.equals(root_hash)) {
            throw new Exception("Constructed root hash differs from provided root hash. Constructed: %x, Expected: " +
                    calculated_root_hash + root_hash);
        }
        return true;
    }

    public static UInt256 calculate_root_hash_from_audit_path(UInt256 leaf_hash,
                                                              int node_index, UInt256[] audit_path, int tree_size) {
        UInt256 calculated_hash = leaf_hash;
        int last_node = tree_size - 1;
        int pos = 0;
        int path_len = audit_path.length;
        for (; last_node > 0; ) {
            if (pos >= path_len) {
                return new UInt256();
            }
            if (node_index % 2 == 1) {
                calculated_hash = hasher.hash_children(audit_path[pos], calculated_hash);
                pos += 1;
            } else if (node_index < last_node) {
                calculated_hash = hasher.hash_children(calculated_hash, audit_path[pos]);
                pos += 1;
            }
            node_index /= 2;
            last_node /= 2;
        }

        if (pos < path_len) {
            return new UInt256();
        }

        return calculated_hash;
    }

    public static List getProof(UInt256 leaf_hash, int node_index, UInt256[] audit_path, int tree_size) {
        List nodes = new ArrayList<>();
        int last_node = tree_size - 1;
        int pos = 0;
        for (; last_node > 0; ) {
            if (node_index % 2 == 1) {
                Map map = new HashMap();
                map.put("Direction","Left");
                map.put("TargetHash",audit_path[pos].toHexString());
                nodes.add(map);
                pos += 1;
            } else if (node_index < last_node) {
                Map map = new HashMap();
                map.put("Direction","Right");
                map.put("TargetHash",audit_path[pos].toHexString());
                nodes.add(map);
                pos += 1;
            }
            node_index /= 2;
            last_node /= 2;
        }
        return nodes;
    }
    public static boolean Verify(UInt256 leaf_hash,List targetHashes, UInt256 root_hash) throws SDKException {
        UInt256 calculated_hash = leaf_hash;
        for(int i=0;i<targetHashes.size();i++){
            String direction = (String)((Map)targetHashes.get(i)).get("Direction");
            String tmp = (String)((Map)targetHashes.get(i)).get("TargetHash");
            UInt256 targetHash = UInt256.parse(tmp);
            if(direction.equals("Left")){
                calculated_hash = hasher.hash_children(targetHash, calculated_hash);
            }else if(direction.equals("Right")){
                calculated_hash = hasher.hash_children(calculated_hash,targetHash);
            }else{
                throw new SDKException(ErrorCode.TargetHashesErr);
            }
        }
        if (calculated_hash.equals(new UInt256())) {
            return false;
        }
        if (!calculated_hash.equals(root_hash)) {
            throw new SDKException(ErrorCode.ConstructedRootHashErr("Constructed root hash differs from provided root hash. Constructed: %x, Expected: " +
                    calculated_hash + root_hash));
        }
        return true;
    }
}
