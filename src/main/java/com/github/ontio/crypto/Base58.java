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

package com.github.ontio.crypto;

import com.github.ontio.common.ErrorCode;

import java.math.BigInteger;
import java.util.Arrays;

public class Base58 {
    /**
     *  base58
     */
    public static final String ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(ALPHABET.length());

    /**
     * decode base58
     * @param input
     * @return
     */
    public static byte[] decode(String input) {
        BigInteger bi = BigInteger.ZERO;
        for (int i = input.length() - 1; i >= 0; i--) {
            int index = ALPHABET.indexOf(input.charAt(i));
            if (index == -1) {
                throw new IllegalArgumentException();
            }
            bi = bi.add(BASE.pow(input.length() - 1 - i).multiply(BigInteger.valueOf(index)));
        }
        byte[] bytes = bi.toByteArray();
        boolean stripSignByte = bytes.length > 1 && bytes[0] == 0 && bytes[1] < 0;
        int leadingZeros = 0;
        for (; leadingZeros < input.length() && input.charAt(leadingZeros) == ALPHABET.charAt(0); leadingZeros++){};
        byte[] tmp = new byte[bytes.length - (stripSignByte ? 1 : 0) + leadingZeros];
        System.arraycopy(bytes, stripSignByte ? 1 : 0, tmp, leadingZeros, tmp.length - leadingZeros);
        return tmp;
    }

    /**
     * encode
     * @param input
     * @return
     */
    public static String encode(byte[] input) {
        BigInteger value = new BigInteger(1, input);
        StringBuilder sb = new StringBuilder();
        while (value.compareTo(BASE) >= 0) {
        	BigInteger[] r = value.divideAndRemainder(BASE);
            sb.insert(0, ALPHABET.charAt(r[1].intValue()));
            value = r[0];
        }
        sb.insert(0, ALPHABET.charAt(value.intValue()));
        for (byte b : input) {
            if (b == 0) {
                sb.insert(0, ALPHABET.charAt(0));
            } else {
                break;
            }
        }
        return sb.toString();
    }
    public static String checkSumEncode(byte[] in) {
        byte[] hash = Digest.sha256(Digest.sha256(in));
        byte[] checksum = Arrays.copyOfRange(hash, 0, 4);
        byte[] input = new byte[in.length+4];
        System.arraycopy(in, 0, input, 0, in.length);
        System.arraycopy(checksum, 0, input, in.length, 4);
        return  encode(input);
    }

    public static byte[] decodeChecked(String input) throws Exception {

        byte[] decoded  = decode(input);
        if (decoded.length < 4) {
            throw new Exception(ErrorCode.InputTooShort);
        }
        byte[] data = Arrays.copyOfRange(decoded, 0, decoded.length - 4);
        byte[] checksum = Arrays.copyOfRange(decoded, decoded.length - 4, decoded.length);
        byte[] actualChecksum = Arrays.copyOfRange(Digest.sha256(Digest.sha256(data)), 0, 4);
        if (!Arrays.equals(checksum, actualChecksum)) {
            throw new Exception(ErrorCode.ChecksumNotValidate);
        }
        return decoded;
    }
}
