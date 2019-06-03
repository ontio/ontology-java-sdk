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

import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Base58;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.bip32.derivation.CkdFunction;
import com.github.ontio.crypto.bip32.derivation.Derivation;
import com.github.ontio.crypto.bip32.derivation.CkdFunctionDerive;
import com.github.ontio.crypto.bip32.derivation.Derive;
import com.github.ontio.sdk.exception.SDKException;
import org.spongycastle.math.ec.ECPoint;

import java.math.BigInteger;

import static com.github.ontio.crypto.bip32.ByteArrayWriter.head32;
import static com.github.ontio.crypto.bip32.ByteArrayWriter.tail32;
import static com.github.ontio.crypto.bip32.HdKey.parse256;
import static com.github.ontio.crypto.bip32.Secp256r1SC.gMultiplyAndAddPoint;
import static com.github.ontio.crypto.bip32.Secp256r1SC.n;
import static com.github.ontio.crypto.bip32.Secp256r1SC.pointSerP;
import static com.github.ontio.crypto.bip32.derivation.CharSequenceDerivation.isHardened;

public final class HdPublicKey implements
        Derive<HdPublicKey>,
        CKDpub {

    private static Deserializer<HdPublicKey> deserializer() {
        return HdPublicKeyDeserializer.DEFAULT;
    }

    public static Deserializer<HdPublicKey> deserializer(final Network network) {
        return new HdPublicKeyDeserializer(network);
    }

    private static final CkdFunction<HdPublicKey> CKD_FUNCTION = HdPublicKey::cKDpub;

    static HdPublicKey from(final HdKey hdKey) {
        return new HdPublicKey(new HdKey.Builder()
                .network(hdKey.getNetwork())
                .neutered(true)
                .key(hdKey.getPoint())
                .parentFingerprint(hdKey.getParentFingerprint())
                .depth(hdKey.depth())
                .childNumber(hdKey.getChildNumber())
                .chainCode(hdKey.getChainCode())
                .build());
    }

    private final HdKey hdKey;

    HdPublicKey(final HdKey hdKey) {
        this.hdKey = hdKey;
    }

    @Override
    public HdPublicKey cKDpub(final int index) {
        if (isHardened(index)) {
            return null;
        }

        final HdKey parent = this.hdKey;
        final byte[] kPar = parent.getKey();

        final byte[] data = new byte[37];
        final ByteArrayWriter writer = new ByteArrayWriter(data);
        writer.concat(kPar, 33);
        writer.concatSer32(index);

        final byte[] I = Digest.hmacSha512(parent.getChainCode(), data);
        final byte[] Il = head32(I);
        final byte[] Ir = tail32(I);

        final BigInteger parse256_Il = parse256(Il);
        final ECPoint ki = gMultiplyAndAddPoint(parse256_Il, kPar);

        if (parse256_Il.compareTo(n()) >= 0 || ki.isInfinity()) {
            return cKDpub(index + 1);
        }

        final byte[] key = pointSerP(ki);

        return new HdPublicKey(new HdKey.Builder()
                .network(parent.getNetwork())
                .neutered(true)
                .depth(parent.depth() + 1)
                .parentFingerprint(parent.calculateFingerPrint())
                .key(key)
                .chainCode(Ir)
                .childNumber(index)
                .build());
    }

    public static HdPublicKey base58Decode(String key) throws SDKException {
        return HdPublicKey.deserializer().deserialize(Base58.decode(key));
    }

    public Address getAddress() {
        return Address.addressFromPubKey(toByteArray());
    }

    private Derive<HdPublicKey> derive() {
        return new CkdFunctionDerive<>(CKD_FUNCTION, this);
    }

    @Override
    public HdPublicKey fromPath(final CharSequence derivationPath) {
        final int length = derivationPath.length();
        if (length == 0)
            throw new IllegalArgumentException("Path cannot be empty");
        if (length == 1)
            return this;
        if (derivationPath.charAt(0) == 'm' && depth() == 0) {
            if (derivationPath.charAt(1) != '/')
                throw new IllegalArgumentException("Root key must be a master key if the path start with m/");
            return derive().fromPath(derivationPath.subSequence(2, derivationPath.length()));
        }
        return derive().fromPath(derivationPath);
    }

    @Override
    public <Path> HdPublicKey fromPath(final Path derivationPath, final Derivation<Path> derivation) {
        return derive().fromPath(derivationPath, derivation);
    }

    public HdPublicKey fromPath() {
        return fromPath("m/44'/1024'/0'");
    }

    public byte[] toByteArray() {
        return hdKey.serialize();
    }

    public Network network() {
        return hdKey.getNetwork();
    }

    public int depth() {
        return hdKey.depth();
    }

    public int childNumber() {
        return hdKey.getChildNumber();
    }

    public String toHexString() {
        return Helper.toHexString(hdKey.getKey());
    }

    public String base58Encode() {
        return Base58.encode(toByteArray());
    }
}