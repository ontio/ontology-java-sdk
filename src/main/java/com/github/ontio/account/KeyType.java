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

package com.github.ontio.account;

public enum KeyType {
    ECDSA(0x12),
    SM2(0x14); //TODO

    private int label;

    private KeyType(int b) {
        this.label = b;
    }

    public int getLabel() {
        return label;
    }

    // get the crypto.KeyType according to the input label
    public static KeyType fromLabel(byte label) throws Exception {
        for (KeyType k : KeyType.values()) {
            if (k.label == label) {
                return k;
            }
        }
        throw new Exception("unknown asymmetric key type");
    }
}
