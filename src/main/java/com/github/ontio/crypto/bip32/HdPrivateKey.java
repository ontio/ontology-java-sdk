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
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Base58;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.bip32.derivation.CkdFunction;
import com.github.ontio.crypto.bip32.derivation.CkdFunctionDerive;
import com.github.ontio.crypto.bip32.derivation.Derive;
import com.github.ontio.crypto.bip32.derivation.Derivation;

import com.github.ontio.sdk.exception.SDKException;
import io.github.novacrypto.bip39.SeedCalculator;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.toruntime.CheckedExceptionToRuntime;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.github.ontio.crypto.bip32.ByteArrayWriter.head32;
import static com.github.ontio.crypto.bip32.ByteArrayWriter.tail32;
import static com.github.ontio.crypto.bip32.HdKey.parse256;
import static com.github.ontio.crypto.bip32.HdKey.ser256;
import static com.github.ontio.crypto.bip32.Secp256r1SC.n;
import static com.github.ontio.crypto.bip32.derivation.CharSequenceDerivation.isHardened;
import static com.github.ontio.crypto.bip32.derivation.CkdFunctionResultCacheDecorator.newCacheOf;

import static io.github.novacrypto.toruntime.CheckedExceptionToRuntime.toRuntime;

public class HdPrivateKey implements
        Derive<HdPrivateKey>,
        CKDpriv,
        CKDpub {

    private static final byte[] BITCOIN_SEED = "Bitcoin seed".getBytes(StandardCharsets.UTF_8);

    private static final byte[] SEED_NAME = "Nist256p1 seed".getBytes(StandardCharsets.UTF_8);

    private static Deserializer<HdPrivateKey> deserializer() {
        return HdPrivateKeyDeserializer.DEFAULT;
    }

    public static Deserializer<HdPrivateKey> deserializer(final Network network) {
        return new HdPrivateKeyDeserializer(network);
    }

    private static final CkdFunction<HdPrivateKey> CKD_FUNCTION = new CkdFunction<HdPrivateKey>() {
        @Override
        public HdPrivateKey deriveChildKey(final HdPrivateKey parent, final int childIndex) {
            return parent.cKDpriv(childIndex);
        }
    };

    public static HdPrivateKey masterKeyFromMnemonic(String code, String passphrase) {
        byte[] seed = new SeedCalculator()
                .withWordsFromWordList(English.INSTANCE)
                .calculateSeed(Arrays.asList(code.split(" ")), passphrase);
        return HdPrivateKey.fromSeed(seed, SEED_NAME, Bitcoin.MAIN_NET);
    }

    public static HdPrivateKey masterKeyFromMnemonic(String code) {
        return masterKeyFromMnemonic(code, "");
    }

    public String toHexString() {
        return Helper.toHexString(hdKey.getKey());
    }

    public byte[] getPrivateKey() {
        return hdKey.getKey();
    }

    public HdPublicKey getHdPublicKey() throws Exception {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec((String) new Object[]{Curve.P256.toString()}[0]);
        ECPoint Q = spec.getG().multiply(new BigInteger(1, getPrivateKey())).normalize();
        if (Q == null || Q.getAffineXCoord() == null || Q.getAffineYCoord() == null) {
            throw new SDKException(ErrorCode.OtherError("normalize error"));
        }
        return new HdPublicKey(new HdKey.Builder()
                .network(hdKey.getNetwork())
                .neutered(true)
                .key(Q.getEncoded(true))
                .parentFingerprint(hdKey.getParentFingerprint())
                .depth(hdKey.depth())
                .childNumber(hdKey.getChildNumber())
                .chainCode(hdKey.getChainCode())
                .build());
    }

    private final HdKey hdKey;

    private HdPrivateKey(final Network network, final byte[] key, final byte[] chainCode) {
        this(new HdKey.Builder()
                .network(network)
                .neutered(false)
                .key(key)
                .chainCode(chainCode)
                .depth(0)
                .childNumber(0)
                .parentFingerprint(0)
                .build());
    }

    public HdPrivateKey(final HdKey hdKey) {
        this.hdKey = hdKey;
    }

    public static HdPrivateKey fromSeed(final byte[] seed, final Network network) {
        final byte[] I = Digest.hmacSha512(BITCOIN_SEED, seed);

        final byte[] Il = head32(I);
        final byte[] Ir = tail32(I);

        return new HdPrivateKey(network, Il, Ir);
    }

    public static HdPrivateKey fromSeed(final byte[] seed, byte[] byteKey, final Network network) {
        final byte[] I = Digest.hmacSha512(byteKey, seed);

        final byte[] Il = head32(I);
        final byte[] Ir = tail32(I);

        return new HdPrivateKey(network, Il, Ir);
    }

    private static byte[] getBytes(final String seed) {
        return toRuntime(new CheckedExceptionToRuntime.Func<byte[]>() {
            @Override
            public byte[] run() throws Exception {
                return seed.getBytes(StandardCharsets.UTF_8);
            }
        });
    }

    public static HdPrivateKey base58Decode(String key) throws SDKException {
        return HdPrivateKey.deserializer().deserialize(Base58.decode(key));
    }

    @Override
    public HdPrivateKey cKDpriv(final int index) {
        final byte[] data = new byte[37];
        final ByteArrayWriter writer = new ByteArrayWriter(data);

        if (isHardened(index)) {
            writer.concat((byte) 0);
            writer.concat(hdKey.getKey(), 32);
        } else {
            writer.concat(hdKey.getPoint());
        }
        writer.concatSer32(index);

        final byte[] I = Digest.hmacSha512(hdKey.getChainCode(), data);
        Arrays.fill(data, (byte) 0);

        final byte[] Il = head32(I);
        final byte[] Ir = tail32(I);

        final byte[] key = hdKey.getKey();
        final BigInteger parse256_Il = parse256(Il);
        final BigInteger ki = parse256_Il.add(parse256(key)).mod(n());

        if (parse256_Il.compareTo(n()) >= 0 || ki.equals(BigInteger.ZERO)) {
            return cKDpriv(index + 1);
        }

        ser256(Il, ki);

        return new HdPrivateKey(new HdKey.Builder()
                .network(hdKey.getNetwork())
                .neutered(false)
                .key(Il)
                .chainCode(Ir)
                .depth(hdKey.depth() + 1)
                .childNumber(index)
                .parentFingerprint(hdKey.calculateFingerPrint())
                .build());
    }

    @Override
    public HdPublicKey cKDpub(final int index) {
        return cKDpriv(index).neuter();
    }

    public HdPublicKey neuter() {
        return HdPublicKey.from(hdKey);
    }

    private Derive<HdPrivateKey> derive() {
        return derive(CKD_FUNCTION);
    }

    public Derive<HdPrivateKey> deriveWithCache() {
        return derive(newCacheOf(CKD_FUNCTION));
    }

    @Override
    public HdPrivateKey fromPath(final CharSequence derivationPath) {
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

    public HdPrivateKey fromPath() {
        return fromPath("m/44'/1024'/0'");
    }

    @Override
    public <Path> HdPrivateKey fromPath(Path derivationPath, Derivation<Path> derivation) {
        return derive().fromPath(derivationPath, derivation);
    }

    private Derive<HdPrivateKey> derive(final CkdFunction<HdPrivateKey> ckdFunction) {
        return new CkdFunctionDerive<>(ckdFunction, this);
    }

    public byte[] extendedKeyByteArray() {
        return hdKey.serialize();
    }

    public HdPrivateKey toNetwork(final Network otherNetwork) {
        if (otherNetwork == network()) {
            return this;
        }
        return new HdPrivateKey(
                hdKey.toBuilder()
                        .network(otherNetwork)
                        .build());
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

    public String base58Encode() {
        return Base58.encode(extendedKeyByteArray());
    }

}