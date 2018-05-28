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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Byte Handle Helper
 */
public class Helper {
    public static String getbyteStr(byte[] bs) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bs) {
            sb.append(" ").append(Byte.toUnsignedInt(b));
        }
        return sb.substring(1);
    }

    public static byte[] reverse(byte[] v) {
        byte[] result = new byte[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[v.length - i - 1];
        }
        return result;
    }

    public static byte[] hexToBytes(String value) {
        if (value == null || value.length() == 0) {
            return new byte[0];
        }
        if (value.length() % 2 == 1) {
            throw new IllegalArgumentException();
        }
        byte[] result = new byte[value.length() / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }

    public static String toHexString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for (byte b : value) {
            int v = Byte.toUnsignedInt(b);
            sb.append(Integer.toHexString(v >>> 4));
            sb.append(Integer.toHexString(v & 0x0f));
        }
        return sb.toString();
    }

    public static String reverse(String value) {
        return toHexString(reverse(hexToBytes(value)));
    }

    public static byte[] removePrevZero(byte[] bt) {
        if (bt.length == 33 && bt[0] == 0) {
            return Arrays.copyOfRange(bt, 1, 33);
        }
        return bt;
    }

    public static String getContractAddress(String codeHexStr, byte vmtype) {
        Address code = Address.toScriptHash(Helper.hexToBytes(codeHexStr));
        byte[] hash = code.toArray();
        hash[0] = vmtype;
        String codeHash = Helper.toHexString(hash);
        return codeHash;
    }

    public static byte[] addBytes(byte[] data1, byte[] data2) {
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return data3;
    }


    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }

    public static String toString(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry e : map.entrySet()) {
            sb.append("\n").append(e.getKey() + ": " + e.getValue());
        }
        return sb.toString();
    }

    public static void print(Map<String, Object> map) {
        System.out.println(toString(map));
    }
    
}
