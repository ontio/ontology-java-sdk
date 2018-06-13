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

package com.github.ontio.smartcontract.neovm.abi;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.sdk.exception.SDKException;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/5/23
 */
public class BuildParams {
    public enum Type {
        ByteArrayType(0x00),
        BooleanType(0x01),
        IntegerType(0x02),
        InterfaceType(0x40),
        ArrayType(0x80),
        StructType(0x81),
        MapType(0x82);
        private byte type;

        private Type(int t) {
            this.type = (byte)t;
        }
        public byte getValue(){
            return type;
        }
    }
    /**
     * @param builder
     * @param list
     * @return
     */
    private static byte[] createCodeParamsScript(ScriptBuilder builder, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    builder.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.push((Boolean) val);
                } else if(val instanceof Integer){
                    builder.push(Helper.BigInt2Bytes(BigInteger.valueOf((int)val)));
                } else if (val instanceof Long) {
                    builder.push(Helper.BigInt2Bytes(BigInteger.valueOf((long)val)));
                } else if(val instanceof Map){
                    byte[] bys = getMapBytes(val);
                    System.out.println(Helper.toHexString(bys));
                    builder.push(bys);
                } else if(val instanceof Struct){
                    byte[] bys = getStructBytes(val);
                    System.out.println(Helper.toHexString(bys));
                    builder.push(bys);
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(builder, tmp);
                    builder.push(new BigInteger(String.valueOf(tmp.size())));
                    builder.pushPack();

                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toArray();
    }
    public static byte[] getStructBytes(Object val){

        ScriptBuilder sb = null;
        try {
            sb = new ScriptBuilder();
            List list = ((Struct)val).list;
            sb.add(Type.ArrayType.getValue());
            sb.add(Helper.BigInt2Bytes(BigInteger.valueOf( list.size())));
            for (int i = 0; i < list.size(); i++) {
                if(list.get(i) instanceof byte[]){
                    sb.add(Type.ByteArrayType.getValue());
                    sb.push((byte[]) list.get(i));
                } else if(list.get(i) instanceof String){
                    sb.add(Type.ByteArrayType.getValue());
                    sb.push(((String) list.get(i)).getBytes());
                } else if(list.get(i) instanceof Integer){
                    sb.add(Type.ByteArrayType.getValue());
                    sb.push(Helper.BigInt2Bytes(BigInteger.valueOf((Integer)list.get(i))));
                } else if(list.get(i) instanceof Long){
                    sb.add(Type.ByteArrayType.getValue());
                    sb.push(Helper.BigInt2Bytes(BigInteger.valueOf((Long) list.get(i))));
                } else {
                    throw new SDKException(ErrorCode.ParamError);
                }
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }
    public static byte[] getMapBytes(Object val){
        ScriptBuilder sb = null;
        try {
            sb = new ScriptBuilder();
            Map<String,Object> map = (Map)val;
            sb.add(Type.MapType.getValue());
            sb.add(Helper.BigInt2Bytes(BigInteger.valueOf( map.size())));
            for(Map.Entry e:map.entrySet()){
                sb.add(Type.ByteArrayType.getValue());
                sb.push(((String) e.getKey()).getBytes());
                if(e.getValue() instanceof byte[]){
                    sb.add(Type.ByteArrayType.getValue());
                    sb.push((byte[]) e.getValue());
                } else if(e.getValue() instanceof String){
                    sb.add(Type.ByteArrayType.getValue());
                    sb.push(((String) e.getValue()).getBytes());
                } else if(e.getValue() instanceof Integer){
                    sb.add(Type.IntegerType.getValue());
                    sb.push(Helper.BigInt2Bytes(BigInteger.valueOf((Integer) e.getValue())));
                } else if(e.getValue() instanceof Long){
                    sb.add(Type.IntegerType.getValue());
                    sb.push(Helper.BigInt2Bytes(BigInteger.valueOf((Long) e.getValue())));
                } else {
                    throw new SDKException(ErrorCode.ParamError);
                }
            }
        } catch (SDKException e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }
    /**
     * @param list
     * @return
     */
    public static byte[] createCodeParamsScript(List<Object> list) {
        ScriptBuilder sb = new ScriptBuilder();
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    sb.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.push((Boolean) val);
                } else if(val instanceof Integer){
                    sb.push(Helper.BigInt2Bytes(BigInteger.valueOf((int)val)));
                } else if (val instanceof Long) {
                    sb.push(Helper.BigInt2Bytes(BigInteger.valueOf((Long) val)));
                } else if(val instanceof BigInteger){
                    sb.push((BigInteger)val);
                } else if(val instanceof Map){
                    byte[] bys = getMapBytes(val);
                    sb.push(bys);
                } else if(val instanceof Struct){
                    byte[] bys = getStructBytes(val);
                    sb.push(bys);
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    createCodeParamsScript(sb, tmp);
                    sb.push(new BigInteger(String.valueOf(tmp.size())));
                    sb.pushPack();
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toArray();
    }

    /**
     * @param abiFunction
     * @return
     * @throws Exception
     */
    public static byte[] serializeAbiFunction( AbiFunction abiFunction) throws Exception {
        List list = new ArrayList<Object>();
        list.add(abiFunction.getName().getBytes());
        List tmp = new ArrayList<Object>();
        for (Parameter obj : abiFunction.getParameters()) {
            if ("ByteArray".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), byte[].class));
            } else if ("String".equals(obj.getType())) {
                tmp.add(obj.getValue());
            } else if ("Boolean".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), boolean.class));
            } else if ("Integer".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Long.class));
            } else if ("Array".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Array.class));
            } else if ("InteropInterface".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Object.class));
            } else if ("Void".equals(obj.getType())) {

            } else if ("Map".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Map.class));
            } else if ("Struct".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Struct.class));
            } else {
                throw new SDKException(ErrorCode.TypeError);
            }
        }
        if(list.size()>0) {
            list.add(tmp);
        }
        byte[] params = createCodeParamsScript(list);
        return params;
    }
}