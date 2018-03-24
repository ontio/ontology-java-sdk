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

import com.alibaba.fastjson.JSON;
import com.github.ontio.crypto.Base58;
import com.github.ontio.crypto.Digest;
import com.github.ontio.io.BinaryWriter;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Custom type which inherits base class defines 20-bit data, 
 * it mostly used to defined contract address
 * 
 * @author 12146
 * @since  JDK1.8
 *
 */
public class Address extends UIntBase implements Comparable<Address> {
    public static final Address ZERO = new Address();
    public static final byte COIN_VERSION = 0x41;

    public Address() {
        this(null);
    }

    public Address(byte[] value) {
        super(20, value);
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
    public static Address addressFromPubKey(ECPoint publicKey) {
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                writer.writeVarBytes(Helper.removePrevZero(publicKey.getXCoord().toBigInteger().toByteArray()));
                writer.writeVarBytes(Helper.removePrevZero(publicKey.getYCoord().toBigInteger().toByteArray()));
                writer.flush();
                byte[] bys = Digest.hash160(ms.toByteArray());
                bys[0] = 0x01;
                Address u160 = new Address(bys);
                return u160;
            }
        } catch (IOException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    public static Address addressFromMultiPubKeys(int m, ECPoint... publicKeys) {
        if(m<=0 || m > publicKeys.length || publicKeys.length > 24){
            throw new IllegalArgumentException();
        }
        try (ByteArrayOutputStream ms = new ByteArrayOutputStream()) {
            try (BinaryWriter writer = new BinaryWriter(ms)) {
                writer.writeByte((byte)publicKeys.length);
                writer.writeByte((byte)m);
                ECPoint[] ecPoint = Arrays.stream(publicKeys).sorted((o1, o2) -> {
                    if (o1.getXCoord().toString().compareTo(o2.getXCoord().toString()) <= 0) {
                        return -1;
                    }
                    return 1;
                }).toArray(ECPoint[]::new);
                for(ECPoint publicKey:ecPoint) {
                    writer.writeVarBytes(Helper.removePrevZero(publicKey.getXCoord().toBigInteger().toByteArray()));
                    writer.writeVarBytes(Helper.removePrevZero(publicKey.getYCoord().toBigInteger().toByteArray()));
                }
                writer.flush();
                byte[] bys = Digest.hash160(ms.toByteArray());
                bys[0] = 0x02;
                Address u160 = new Address(bys);
                return u160;
            }
        } catch (IOException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }
    public String toBase58() {
        byte[] data = new byte[25];
        data[0] = COIN_VERSION;
        System.arraycopy(toArray(), 0, data, 1, 20);
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        System.arraycopy(checksum, 0, data, 21, 4);
        return Base58.encode(data);
    }
    public static Address decodeBase58(String address) {
        byte[] data = Base58.decode(address);
        if (data.length != 25) {
            throw new IllegalArgumentException();
        }
        if (data[0] != COIN_VERSION) {
            throw new IllegalArgumentException();
        }
        byte[] checksum = Digest.sha256(Digest.sha256(data, 0, 21));
        for (int i = 0; i < 4; i++) {
            if (data[data.length - 4 + i] != checksum[i]) {
                throw new IllegalArgumentException();
            }
        }
        byte[] buffer = new byte[20];
        System.arraycopy(data, 1, buffer, 0, 20);
        return new Address(buffer);
    }
    public static Address toScriptHash(byte[] script) {
        return new Address(Digest.hash160(script));
    }
    public String toHexString(){
        return Helper.toHexString(this.toArray());
    }
}