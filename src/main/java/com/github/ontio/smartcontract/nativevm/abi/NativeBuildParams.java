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

package com.github.ontio.smartcontract.nativevm.abi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.core.ontid.Attribute;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.core.scripts.ScriptOp;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class NativeBuildParams {
    public static byte[] buildParams(Object... params) throws SDKException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryWriter bw = new BinaryWriter(baos);
        try {
            for (Object param : params) {
                if (param instanceof Integer) {
                    bw.writeInt(((Integer) param).intValue());
                } else if (param instanceof byte[]) {
                    bw.writeVarBytes((byte[]) param);
                } else if (param instanceof String) {
                    bw.writeVarString((String) param);
                } else if (param instanceof Attribute[]) {
                    bw.writeSerializableArray((Attribute[]) param);
                } else if (param instanceof Attribute) {
                    bw.writeSerializable((Attribute) param);
                } else if (param instanceof Address) {
                    bw.writeSerializable((Address) param);
                } else {
                    throw new SDKException(ErrorCode.WriteVarBytesError);
                }
            }
        } catch (IOException e) {
            throw new SDKException(ErrorCode.WriteVarBytesError);
        }
        return baos.toByteArray();
    }

    private static byte[] createCodeParamsScript(ScriptBuilder builder, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    builder.emitPushByteArray((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.emitPushBool((Boolean) val);
                } else if (val instanceof Integer) {
                    builder.emitPushInteger(BigInteger.valueOf((int) val));
                } else if (val instanceof Long) {
                    builder.emitPushInteger(BigInteger.valueOf((Long) val));
                } else if (val instanceof BigInteger) {
                    builder.emitPushInteger((BigInteger) val);
                } else if (val instanceof Address) {
                    builder.emitPushByteArray(((Address) val).toArray());
                } else if (val instanceof UInt256) {
                    builder.emitPushByteArray(((UInt256) val).toArray());
                } else if (val instanceof String) {
                    builder.emitPushByteArray(((String) val).getBytes());
                } else if (val instanceof Struct) {
                    builder.emitPushInteger(BigInteger.valueOf(0));
                    builder.add(ScriptOp.OP_NEWSTRUCT);
                    builder.add(ScriptOp.OP_TOALTSTACK);
                    for (int k = 0; k < ((Struct) val).list.size(); k++) {
                        Object o = ((Struct) val).list.get(k);
                        List tmpList = new ArrayList();
                        tmpList.add(o);
                        createCodeParamsScript(builder, tmpList);
                        builder.add(ScriptOp.OP_DUPFROMALTSTACK);
                        builder.add(ScriptOp.OP_SWAP);
                        builder.add(ScriptOp.OP_APPEND);
                    }
                    builder.add(ScriptOp.OP_FROMALTSTACK);
                } else if (val instanceof List) {
                    List tmp = (List) val;
                    for (int k = tmp.size() - 1; k >= 0; k--) {
                        List tmpList = new ArrayList();
                        tmpList.add(tmp.get(k));
                        createCodeParamsScript(builder, tmpList);
                    }
                    builder.emitPushInteger(new BigInteger(String.valueOf(tmp.size())));
                    builder.pushPack();
                } else {
                    throw new SDKException(ErrorCode.OtherError("not this type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toArray();
    }

    /**
     * @param list
     * @return
     */
    public static byte[] createCodeParamsScript(List<Object> list) {
        ScriptBuilder sb = new ScriptBuilder();
        return createCodeParamsScript(sb, list);
    }

    public static byte[] serializeAbiFunction(AbiFunction abiFunction) throws Exception {
        List list = new ArrayList<Object>();
        list.add(abiFunction.getName().getBytes());
        List tmp = new ArrayList<Object>();
        for (Parameter obj : abiFunction.getParameters()) {
            if ("Byte".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), byte.class));
            } else if ("ByteArray".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), byte[].class));
            } else if ("String".equals(obj.getType())) {
                tmp.add(obj.getValue());
            } else if ("Bool".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), boolean.class));
            } else if ("Int".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Long.class));
            } else if ("Array".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Array.class));
            } else if ("Struct".equals(obj.getType())) {
                //tmp.add(JSON.parseObject(obj.getValue(), Object.class));
            } else if ("Uint256".equals(obj.getType())) {

            } else if ("Address".equals(obj.getType())) {

            } else if ("BigInteger".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), BigInteger.class));
            } else {
                throw new SDKException(ErrorCode.TypeError);
            }
        }
        if (list.size() > 0) {
            list.add(tmp);
        }
        byte[] params = createCodeParamsScript(list);
        return params;
    }
}