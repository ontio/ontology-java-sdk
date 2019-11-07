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
import com.alibaba.fastjson.JSONArray;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static byte[] BigIntToNeoBytes(BigInteger data){
        if (data.equals(BigInteger.ZERO)) {
            return new byte[0];
        }
        byte[] bs = data.toByteArray();
        if(bs.length == 0) {
            return new byte[0];
        }
        if(data.signum() < 0) {
            byte b = data.negate().toByteArray()[0];
            byte[] res = reverse(bs);
            if(b >> 7 == 1){
                byte[] t = new byte[res.length + 1];
                System.arraycopy(res,0,t,0,res.length);
                t[res.length] = (byte)255;
                return t;
            }
            return res;
        }else{
            byte b = bs[0];
            byte[] res = reverse(bs);
            if(b >> 7 == 1){
                byte[] t = new byte[res.length + 1];
                System.arraycopy(res,0,t,0,res.length);
                t[res.length] = (byte)0;
                return t;
            }
            return res;
        }
    }

    public static BigInteger BigIntFromNeoBytes(byte[] ba){
        if(ba.length == 0){
            return BigInteger.ZERO;
        }
        byte[] bs = reverse(ba);
        if(bs[0] >> 7 == 1) {
            for(int i = 0;i < bs.length;i++) {
                bs[i] = (byte)~bs[i];
            }
            BigInteger temp = new BigInteger(bs);
            temp.add(BigInteger.ONE);
            return temp.negate();
        }
        return new BigInteger(bs);
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

    private static String getBalanceFromHex(String hex)
    {
        if(!hex.equals("")) {
            return Helper.BigIntFromNeoBytes(Helper.hexToBytes(hex)).toString();
        }
        return BigInteger.ZERO.toString();
    }

    public static String parseBalanceArray(JSONArray jsonArray) {
        List balancesArray = new ArrayList();
        int currentBalanceIndex = 0;

        for (Object object : jsonArray) {
            List balanceArray = new ArrayList();

            if (object instanceof JSONArray) {
                JSONArray balanceJsonArray = (JSONArray) object;
                String hexSymbol = (String) balanceJsonArray.get(0);
                String hexBalance = (String) balanceJsonArray.get(1);

                balanceArray.add(hexSymbol);
                balanceArray.add(getBalanceFromHex(hexBalance));
            } else if (object instanceof String){
                balanceArray.add(getBalanceFromHex((String) object));
            }

            balancesArray.add(balanceArray);
            currentBalanceIndex++;
        }

        return JSON.toJSONString(balancesArray);
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
