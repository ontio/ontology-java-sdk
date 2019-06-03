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

import java.util.Arrays;

import static com.github.ontio.crypto.bip32.Checksum.confirmExtendedKeyChecksum;
import static io.github.novacrypto.base58.Base58.base58Decode;

final class HdPublicKeyDeserializer implements Deserializer<HdPublicKey> {

    static final HdPublicKeyDeserializer DEFAULT = new HdPublicKeyDeserializer(Bitcoin.MAIN_NET);

    private final Network network;

    HdPublicKeyDeserializer(final Network network) {
        this.network = network;
    }

    @Override
    public HdPublicKey deserialize(final CharSequence extendedBase58Key) throws SDKException {
        final byte[] extendedKeyData = base58Decode(extendedBase58Key);
        try {
            return deserialize(extendedKeyData);
        } finally {
            Arrays.fill(extendedKeyData, (byte) 0);
        }
    }

    @Override
    public HdPublicKey deserialize(final byte[] extendedKeyData) throws SDKException {
        confirmExtendedKeyChecksum(extendedKeyData);
        final ByteArrayReader reader = new ByteArrayReader(extendedKeyData);
        final int version = reader.readSer32();
        if (version != Bitcoin.MAIN_NET.getPublicVersion()) {
            throw new SDKException(ErrorCode.OtherError(String.format("Can't find network that matches private version 0x%x", version)));
        }
        return new HdPublicKey(new HdKey
                .Builder()
                .network(Bitcoin.MAIN_NET)
                .depth(reader.read())
                .parentFingerprint(reader.readSer32())
                .childNumber(reader.readSer32())
                .chainCode(reader.readRange(32))
                .key(reader.readRange(33))
                .neutered(true)
                .build()
        );
    }
}