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

import com.github.ontio.common.UInt256;

/**
 * @Description:
 * @date 2018/4/4
 */
public class MerkleVerifier {
    private static TreeHasher hasher = new TreeHasher();


    public static boolean VerifyLeafHashInclusion(UInt256 leaf_hash,
                                                  int leaf_index, UInt256[] proof, UInt256 root_hash, int tree_size) throws Exception {

        if (tree_size <= leaf_index) {
            throw new Exception("Wrong params: the tree size is smaller than the leaf index");
        }
        UInt256 calculated_root_hash = calculate_root_hash_from_audit_path(leaf_hash,
                leaf_index, proof, tree_size);
        if (calculated_root_hash.equals(new UInt256())) {
            return false;
        }
        System.out.println(calculated_root_hash.toHexString());
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
}
