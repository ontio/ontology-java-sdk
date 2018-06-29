package com.github.neo.core;


import com.alibaba.fastjson.JSON;
import com.github.neo.core.*;
import com.github.neo.core.transaction.InvocationTransaction;
import com.github.ontio.common.Address;
import com.github.ontio.common.Fixed8;
import com.github.ontio.common.Helper;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.neo.core.transaction.PublishTransaction;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.Parameter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class SmartContract {

    public SmartContract(){

    }
    public static InvocationTransaction makeInvocationTransaction(String contractAddress, byte[] addr, AbiFunction abiFunction) throws Exception {
        if (contractAddress == null) {
            throw new Exception("null contractHash");
        }
        contractAddress = contractAddress.replace("0x", "");
        byte[] params = serializeAbiFunction(abiFunction);
        params = Helper.addBytes(params, new byte[]{0x67});
        params = Helper.addBytes(params, Helper.hexToBytes(contractAddress));

        InvocationTransaction tx = makeInvocationTransaction(params,addr);
        return tx;
    }
    private static byte[] serializeAbiFunction(AbiFunction abiFunction) throws Exception {
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
                tmp.add(JSON.parseObject(obj.getValue(), List.class));
            } else if ("InteropInterface".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Object.class));
            } else if ("Void".equals(obj.getType())) {

            } else {
                throw new Exception("type error");
            }
        }
        if(list.size()>0) {
            list.add(tmp);
        }
        byte[] params = createCodeParamsScript(list);
        return params;
    }

    private static byte[] createCodeParamsScript(ScriptBuilder builder, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    builder.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.push((Boolean) val);
                } else if (val instanceof Long) {
                    builder.push(BigInteger.valueOf((long)val));
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
                } else if (val instanceof Long) {
                    sb.push(BigInteger.valueOf(((long)val)));
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

    public static PublishTransaction makePublishTransaction(String codeStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, ContractParameterType returnType) throws Exception {
        PublishTransaction tx = new PublishTransaction();
        tx.version = 1;
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.DescriptionUrl;
        tx.attributes[0].data =UUID.randomUUID().toString().getBytes();
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        tx.script = Helper.hexToBytes(codeStr);
        tx.parameterList =  new ContractParameterType[]{ContractParameterType.ByteArray, ContractParameterType.Array};
        tx.returnType = returnType;
        tx.codeVersion = codeVersion;
        tx.needStorage = needStorage;
        tx.name = name;
        tx.author = author;
        tx.email = email;
        tx.description = desp;
        return tx;
    }

    public static InvocationTransaction makeInvocationTransaction(byte[] paramsHexStr, byte[] addr) throws Exception {
        InvocationTransaction tx = new InvocationTransaction();
        tx.version = 1;
        tx.attributes = new TransactionAttribute[2];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Script;
        tx.attributes[0].data = addr;
        tx.attributes[1] = new TransactionAttribute();
        tx.attributes[1].usage = TransactionAttributeUsage.DescriptionUrl;
        tx.attributes[1].data = UUID.randomUUID().toString().getBytes();
        tx.inputs = new TransactionInput[0];
        tx.outputs = new TransactionOutput[0];
        tx.script = paramsHexStr;
        tx.gas = new Fixed8(0);
        return tx;
    }
}
