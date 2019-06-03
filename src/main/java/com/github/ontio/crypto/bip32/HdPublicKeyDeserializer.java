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

import com.github.ontio.crypto.bip32.networks.DefaultNetworks;

import java.util.Arrays;

import static com.github.ontio.crypto.bip32.Checksum.confirmExtendedKeyChecksum;
import static io.github.novacrypto.base58.Base58.base58Decode;

final class HdPublicKeyDeserializer implements Deserializer<HdPublicKey> {

    static final HdPublicKeyDeserializer DEFAULT = new HdPublicKeyDeserializer(DefaultNetworks.INSTANCE);

    private final Networks networks;

    HdPublicKeyDeserializer(final Networks networks) {
        this.networks = networks;
    }

    @Override
    public HdPublicKey deserialize(final CharSequence extendedBase58Key) {
        final byte[] extendedKeyData = base58Decode(extendedBase58Key);
        try {
            return deserialize(extendedKeyData);
        } finally {
            Arrays.fill(extendedKeyData, (byte) 0);
        }
    }

    @Override
    public HdPublicKey deserialize(final byte[] extendedKeyData) {
        confirmExtendedKeyChecksum(extendedKeyData);
        final ByteArrayReader reader = new ByteArrayReader(extendedKeyData);
        return new HdPublicKey(new HdKey
                .Builder()
                .network(networks.findByPublicVersion(reader.readSer32()))
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