/*
 * Copyright (C) 2018-2019 The ontology Authors
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

import com.github.ontio.common.ErrorCode;
import com.github.ontio.sdk.exception.SDKException;

import java.math.BigInteger;
import java.util.Arrays;

import static com.github.ontio.crypto.Digest.hash160;
import static com.github.ontio.crypto.bip32.Secp256r1SC.pointSerP_gMultiply;
import static io.github.novacrypto.hashing.Sha256.sha256Twice;

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

    static BigInteger parse256(final byte[] bytes) {
        return new BigInteger(1, bytes);
    }

    static void ser256(final byte[] target, final BigInteger integer) {
        if (integer.bitLength() > target.length * 8)
            throw new RuntimeException("ser256 failed, cannot fit integer in buffer");
        final byte[] modArr = integer.toByteArray();
        Arrays.fill(target, (byte) 0);
        copyTail(modArr, target);
        Arrays.fill(modArr, (byte) 0);
    }

    private static void copyTail(final byte[] src, final byte[] dest) {
        if (src.length < dest.length) {
            System.arraycopy(src, 0, dest, dest.length - src.length, src.length);
        } else {
            System.arraycopy(src, src.length - dest.length, dest, 0, dest.length);
        }
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

    static void confirmHdKeyChecksum(final byte[] extendedKeyData) throws SDKException {
        final byte[] checkSum = checksum(extendedKeyData);
        for (int i = 0; i < 4; i++) {
            if (extendedKeyData[78 + i] != checkSum[i])
                throw new SDKException(ErrorCode.OtherError("Checksum error"));
        }
    }

    static byte[] checksum(final byte[] privateKey) {
        return sha256Twice(privateKey, 0, 78);
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