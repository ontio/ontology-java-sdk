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

package com.github.ontio.crypto.bip32;

import static com.github.ontio.crypto.Digest.hash160;
import static com.github.ontio.crypto.bip32.BigIntegerUtils.parse256;
import static com.github.ontio.crypto.bip32.Secp256r1SC.pointSerP_gMultiply;

public class HdKey {

    private final boolean neutered;
    private final Network network;
    private final byte[] chainCode;
    private final byte[] key;
    private final Serializer serializer;
    private final int parentFingerprint;
    private final int childNumber;
    private final int depth;

    private HdKey(final Builder builder) {
        neutered = builder.neutered;
        network = builder.network;
        key = builder.key;
        parentFingerprint = builder.parentFingerprint;
        childNumber = builder.childNumber;
        chainCode = builder.chainCode;
        depth = builder.depth;
        serializer = new Serializer.Builder()
                .network(builder.network)
                .neutered(builder.neutered)
                .depth(builder.depth)
                .childNumber(builder.childNumber)
                .fingerprint(builder.parentFingerprint)
                .build();
    }

    byte[] serialize() {
        return serializer.serialize(key, chainCode);
    }

    byte[] getPoint() {
        return pointSerP_gMultiply(parse256(key));
    }

    byte[] getKey() {
        return key;
    }

    boolean getNeutered() {
        return neutered;
    }

    int getParentFingerprint() {
        return parentFingerprint;
    }

    int calculateFingerPrint() {
        final byte[] point = getPublicBuffer();
        final byte[] o = hash160(point);
        return ((o[0] & 0xFF) << 24) |
                ((o[1] & 0xFF) << 16) |
                ((o[2] & 0xFF) << 8) |
                (o[3] & 0xFF);
    }

    private byte[] getPublicBuffer() {
        return neutered ? key : getPoint();
    }

    public int depth() {
        return depth;
    }

    Network getNetwork() {
        return network;
    }

    byte[] getChainCode() {
        return chainCode;
    }

    int getChildNumber() {
        return childNumber;
    }

    Builder toBuilder() {
        return new Builder()
                .neutered(neutered)
                .chainCode(chainCode)
                .key(key)
                .depth(depth)
                .childNumber(childNumber)
                .parentFingerprint(parentFingerprint);
    }

    static class Builder {

        private Network network;
        private boolean neutered;
        private byte[] chainCode;
        private byte[] key;
        private int depth;
        private int childNumber;
        private int parentFingerprint;

        Builder network(final Network network) {
            this.network = network;
            return this;
        }

        Builder neutered(final boolean neutered) {
            this.neutered = neutered;
            return this;
        }

        Builder key(final byte[] key) {
            this.key = key;
            return this;
        }

        Builder chainCode(final byte[] chainCode) {
            this.chainCode = chainCode;
            return this;
        }

        Builder depth(final int depth) {
            this.depth = depth;
            return this;
        }

        Builder childNumber(final int childNumber) {
            this.childNumber = childNumber;
            return this;
        }

        Builder parentFingerprint(final int parentFingerprint) {
            this.parentFingerprint = parentFingerprint;
            return this;
        }

        HdKey build() {
            return new HdKey(this);
        }
    }
}