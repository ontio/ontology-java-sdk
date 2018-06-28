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

package com.github.ontio.common;

import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.crypto.Base58;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import org.bouncycastle.math.ec.ECPoint;

import static com.github.ontio.crypto.KeyType.ECDSA;
import static com.github.ontio.crypto.KeyType.SM2;

/**
 * Custom type which inherits base class defines 20-bit data,
 * it mostly used to defined contract address
 */
public class Address extends UIntBase implements Comparable<Address> {
    public static final Address ZERO = new Address();
    public static final byte COIN_VERSION = 0x17;

    public Address() {
        this(null);
    }

    public Address(byte[] value) {
        super(20, value);
    }


    public static Address parse(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value.startsWith("0x")) {
            value = value.substring(2);
        }
        if (value.length() != 40) {
            throw new IllegalArgumentException();
        }
        byte[] v = Helper.hexToBytes(value);
        return new Address(v);
//        return new UInt160(Helper.reverse(v));
    }

    public static boolean tryParse(String s, Address result) {
        try {
            Address v = parse(s);
            result.data_bytes = v.data_bytes;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Address AddressFromVmCode(String codeHexStr) {
        Address code = Address.toScriptHash(Helper.hexToBytes(codeHexStr));
        return code;
    }

    public static Address addressFromPubKey(String publicKey) {
        return addressFromPubKey(Helper.hexToBytes(publicKey));
    }

    public static Address addressFromPubKey(byte[] publicKey) {
        ScriptBuilder sb = new ScriptBuilder();
        sb.push(publicKey);
        sb.add(ScriptOp.OP_CHECKSIG);
        return new Address(Digest.hash160(sb.toArray()));
    }

    public static Address addressFromMultiPubKeys(int m, byte[]... publicKeys) throws Exception {
        if (m <= 0 || m > publicKeys.length || publicKeys.length > Common.MULTI_SIG_MAX_PUBKEY_SIZE) {
            throw new SDKException(ErrorCode.ParamError);
        }
        try (ScriptBuilder sb = new ScriptBuilder()) {
            sb.push(BigInteger.valueOf(m));
            publicKeys = Arrays.stream(publicKeys).sorted((o1, o2) -> {
                if (KeyType.fromPubkey(o1).getLabel() != KeyType.fromPubkey(o2).getLabel()) {
                    return KeyType.fromPubkey(o1).getLabel() >= KeyType.fromPubkey(o1).getLabel() ? 1 : -1;
                }
                switch (KeyType.fromPubkey(o1)) {
                    case SM2:
                        byte[] p = new byte[33];
                        System.arraycopy(o1, 2, p, 0, p.length);
                        o1 = p;
                        byte[] p2 = new byte[33];
                        System.arraycopy(o2, 2, p2, 0, p2.length);
                        o2 = p2;
                        ECPoint smPk1 = ECC.sm2p256v1.getCurve().decodePoint(o1);
                        ECPoint smPk2 = ECC.sm2p256v1.getCurve().decodePoint(o2);
                        return ECC.compare(smPk1, smPk2);
                    case ECDSA:
                        ECPoint pk1 = ECC.secp256r1.getCurve().decodePoint(o1);
                        ECPoint pk2 = ECC.secp256r1.getCurve().decodePoint(o2);
                        return ECC.compare(pk1, pk2);
                    case EDDSA:
                        //TODO
                        return Helper.toHexString(o1).compareTo(Helper.toHexString(o1));
                    default:
                        return Helper.toHexString(o1).compareTo(Helper.toHexString(o1));
                }
            }).toArray(byte[][]::new);

            for (byte[] publicKey : publicKeys) {
                sb.push(publicKey);
                System.out.println(Helper.toHexString(publicKey));
            }
            sb.push(BigInteger.valueOf(publicKeys.length));
            sb.add(ScriptOp.OP_CHECKMULTISIG);
            return new Address(Digest.hash160(sb.toArray()));
        }
    }

    public static Address decodeBase58(String address) throws SDKException {
        byte[] data = Base58.decode(address);
        if (data.length != 25) {
            throw new SDKException(ErrorCode.ParamError);
        }
        if (data[0] != COIN_VERSION) {
            throw new SDKException(ErrorCode.ParamError);
        }
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        for (int i = 0; i < 4; i++) {
            if (data[data.length - 4 + i] != checksum[i]) {
                throw new SDKException(ErrorCode.ParamError);
            }
        }
        byte[] buffer = new byte[20];
        System.arraycopy(data, 1, buffer, 0, 20);
        return new Address(buffer);
    }

    public static Address toScriptHash(byte[] script) {
        return new Address(Digest.hash160(script));
    }

    @Override
    public int compareTo(Address other) {
        byte[] x = this.data_bytes;
        byte[] y = other.data_bytes;
        for (int i = x.length - 1; i >= 0; i--) {
            int r = Byte.toUnsignedInt(x[i]) - Byte.toUnsignedInt(y[i]);
            if (r != 0) {
                return r;
            }
        }
        return 0;
    }

    public String toBase58() {
        byte[] data = new byte[25];
        data[0] = COIN_VERSION;
        System.arraycopy(toArray(), 0, data, 1, 20);
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        System.arraycopy(checksum, 0, data, 21, 4);
        return Base58.encode(data);
    }

}