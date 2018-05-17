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

package com.github.ontio.sdk.manager;

import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Contract;
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.AttributeUsage;
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.core.payload.InvokeCode;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.Parameter;
import com.github.ontio.sdk.info.AccountInfo;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.rmi.dgc.VMID;
import java.util.*;

/**
 *
 */
public class SmartcodeTx {
    private OntSdk sdk;
    private String contractAddress = null;
    public String getCodeAddress() {
        return contractAddress;
    }
    public void setCodeAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public SmartcodeTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    /**
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
//    public String sendInvokeSmartCodeWithNoSign(AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
//        Transaction tx = invokeTransaction(null, null, abiFunction, vmtype,gas);
//        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
//        if (!b) {
//            throw new SDKException(ErrorCode.SendRawTxError);
//        }
//        return tx.hash().toString();
//    }

    public String sendInvokeSmartCodeWithNoSignPreExec(AbiFunction abiFunction, byte vmtype) throws Exception {
        Transaction tx = invokeTransaction(null, null, abiFunction, vmtype,0);
        String result = (String)sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        return result;
    }
    /**
     *
     * @param ontid
     * @param password
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    public String sendInvokeSmartCodeWithSign(String ontid, String password, AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
        Transaction tx = invokeTransaction( ontid, password, abiFunction, vmtype,gas);
        sdk.signTx(tx,ontid,password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toString();
    }

    public String sendInvokeSmartCodeWithSignPreExec(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        Transaction tx = invokeTransaction( ontid, password, abiFunction, vmtype,0);
        sdk.signTx(tx, new Account[][]{{sdk.getWalletMgr().getAccount(ontid, password)}});
        String result = (String)sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        return result;
    }
    /**
     * @param ontid
     * @param password
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    public Object sendInvokeTransactionPreExec(String ontid, String password, AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
        Transaction tx = invokeTransaction( ontid, password, abiFunction, vmtype,gas);
        return sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
    }
    public Transaction invokeTransactionNoSign(AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
        return invokeTransaction(null,null,abiFunction,vmtype,gas);
    }
    /**
     * @param ontid
     * @param password
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    public Transaction invokeTransaction(String ontid, String password, AbiFunction abiFunction, byte vmtype,long gas) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

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
                tmp.add(JSON.parseObject(obj.getValue(), int.class));
            } else if ("Array".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Array.class));
            } else if ("InteropInterface".equals(obj.getType())) {
                tmp.add(JSON.parseObject(obj.getValue(), Object.class));
            } else if ("Void".equals(obj.getType())) {

            } else {
                throw new SDKException(ErrorCode.TypeError);
            }
        }
        if(list.size()>0) {
            list.add(tmp);
        }
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);

        Transaction tx = null;
        if (ontid == null && password == null) {
            tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,params, vmtype, ontid,gas);
        } else {
            tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(contractAddress,null,params, vmtype, ontid,gas);
        }
        return tx;
    }


    public static byte[] Int2Bytes_LittleEndian(int iValue){
        byte[] rst = new byte[4];
        rst[0] = (byte)(iValue & 0xFF);
        rst[1] = (byte)((iValue & 0xFF00) >> 8 );
        rst[2] = (byte)((iValue & 0xFF0000) >> 16 );
        rst[3] = (byte)((iValue & 0xFF000000) >> 24 );
        return rst;
    }
    /**
     * @param builder
     * @param list
     * @return
     */
    private byte[] createCodeParamsScript(ScriptBuilder builder, List<Object> list) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    builder.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.push((Boolean) val);
                } else if (val instanceof Integer) {
                    builder.push(new BigInteger(Int2Bytes_LittleEndian((int)val)));
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
    public byte[] createCodeParamsScript(List<Object> list) {
        ScriptBuilder sb = new ScriptBuilder();
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                Object val = list.get(i);
                if (val instanceof byte[]) {
                    sb.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.push((Boolean) val);
                } else if (val instanceof Integer) {
                    sb.push(new BigInteger(Int2Bytes_LittleEndian((int)val)));
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

    public String buildWasmContractJsonParam(Object[] objs) {
        List params = new ArrayList();
        for (int i = 0; i < objs.length; i++) {
            Object val = objs[i];
            if (val instanceof String) {
                Map map = new HashMap();
                map.put("type","string");
                map.put("value",val);
                params.add(map);
            } else if (val instanceof Integer) {
                Map map = new HashMap();
                map.put("type","int");
                map.put("value",String.valueOf(val));
                params.add(map);
            } else if (val instanceof Long) {
                Map map = new HashMap();
                map.put("type","int64");
                map.put("value",String.valueOf(val));
                params.add(map);
            } else if (val instanceof int[]) {
                Map map = new HashMap();
                map.put("type","int_array");
                map.put("value",val);
                params.add(map);
            } else if (val instanceof long[]) {
                Map map = new HashMap();
                map.put("type","int_array");
                map.put("value",val);
                params.add(map);
            } else {
                continue;
            }
        }
        Map result = new HashMap();
        result.put("Params",params);
        return JSON.toJSONString(result);
    }
    public byte[] buildWasmContractRawParam(List<Object> list) {
        List params = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Object val = list.get(i);
            if (val instanceof String) {
                Map map = new HashMap();
                map.put("type","string");
                map.put("value",val);
                params.add(map);
            } else if (val instanceof Integer) {
                Map map = new HashMap();
                map.put("type","int");
                map.put("value",val);
                params.add(map);
            } else if (val instanceof Long) {
                Map map = new HashMap();
                map.put("type","int64");
                map.put("value",val);
                params.add(map);
            } else if (val instanceof int[]) {
                Map map = new HashMap();
                map.put("type","int_array");
                map.put("value",val);
                params.add(map);
            } else if (val instanceof long[]) {
                Map map = new HashMap();
                map.put("type","int_array");
                map.put("value",val);
                params.add(map);
            } else {
                continue;
            }
        }
        return JSON.toJSONString(params).getBytes();
    }
    /**
     * @param codeStr
     * @param needStorage
     * @param name
     * @param codeVersion
     * @param author
     * @param email
     * @param desp
     * @param vmtype
     * @return
     * @throws SDKException
     */
    public DeployCode makeDeployCodeTransaction(String payer,String codeStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, byte vmtype) throws SDKException {
        if (("").equals(payer)){
            throw new SDKException(ErrorCode.ParamError);
        }
        DeployCode tx = new DeployCode();
        tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        tx.attributes = new Attribute[1];
        tx.attributes[0] = new Attribute();
        tx.attributes[0].usage = AttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = Helper.hexToBytes(codeStr);
        tx.version = codeVersion;
        tx.vmType = vmtype;
        tx.needStorage = needStorage;
        tx.name = name;
        tx.author = author;
        tx.email = email;
        tx.gasPrice = 0;
        tx.gasLimit = 0;
        tx.description = desp;
        return tx;
    }

    public InvokeCode makeInvokeCodeTransaction(String codeAddr,String method,byte[] params, byte vmtype, String payer,long gas) throws SDKException {
        if(vmtype == VmType.NEOVM.value()) {
            Contract contract = new Contract((byte) 0, null, Address.parse(codeAddr), "", params);
            params = Helper.addBytes(new byte[]{0x67}, contract.toArray());
        }else if(vmtype == VmType.WASMVM.value()) {
            Contract contract = new Contract((byte) 1, null, Address.parse(codeAddr), method, params);
            params = contract.toArray();
        } else if(vmtype == VmType.Native.value()) {
            Contract contract = new Contract((byte) 0, null, Address.parse(codeAddr), method, params);
            params = contract.toArray();
        }
        InvokeCode tx = new InvokeCode();
        tx.attributes = new Attribute[1];
        tx.attributes[0] = new Attribute();
        tx.attributes[0].usage = AttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = params;
        tx.gasLimit = sdk.DEFAULT_GAS_LIMIT;
        if(sdk.DEFAULT_GAS_LIMIT == 0){
            tx.gasPrice = 0;
        }else {
            tx.gasPrice = gas / sdk.DEFAULT_GAS_LIMIT;
        }
        if(payer != null){
            tx.payer = Address.decodeBase58(payer.replace(Common.didont,""));
        }

        tx.vmType = vmtype;
        return tx;
    }
}
