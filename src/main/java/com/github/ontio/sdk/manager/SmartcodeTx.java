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
import com.github.ontio.core.transaction.Attribute;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.core.transaction.AttributeUsage;
import com.github.ontio.common.Helper;
import com.github.ontio.core.asset.Fee;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 */
public class SmartcodeTx {
    private OntSdk sdk;
    private String codeAddress = null;
    private String wsSessionId = "";

    public void setCodeAddress(String codeHash) {
        this.codeAddress = codeHash.replace("0x", "");
    }

    public SmartcodeTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setWsSessionId(String sessionId) {
        if (!this.wsSessionId.equals(sessionId)) {
            this.wsSessionId = sessionId;
        }
    }

    /**
     * @return
     */
    public String getWsSessionId() {
        return wsSessionId;
    }

    /**
     * @param ontid
     * @param password
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    public String sendInvokeSmartCode(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        return (String) invokeTransaction(false, null, null, abiFunction, vmtype);
    }

    public String sendInvokeSmartCodeWithSign(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        return (String) invokeTransaction(false, ontid, password, abiFunction, vmtype);
    }

    /**
     * @param ontid
     * @param password
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    public Object invokeTransactionPreExec(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        return invokeTransaction(true, ontid, password, abiFunction, vmtype);
    }

    /**
     * @param preExec
     * @param ontid
     * @param password
     * @param abiFunction
     * @param vmtype
     * @return
     * @throws Exception
     */
    private String invokeTransaction(boolean preExec, String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
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
                throw new SDKException("type error");
            }
        }
        list.add(tmp);
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        params = Helper.addBytes(params, new byte[]{0x69});
        params = Helper.addBytes(params, Helper.hexToBytes(codeAddress));

        Transaction tx = null;
        if (ontid == null && password == null) {
            Fee[] fees = new Fee[0];
            tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, vmtype, fees);
        } else {
            Fee[] fees = new Fee[1];
            AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid, password,sdk.keyType,sdk.curveParaSpec);
            fees[0] = new Fee(0, Address.addressFromPubKey(info.pubkey));
            tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, vmtype, fees);
            sdk.signTx(tx, new Account[][]{{sdk.getWalletMgr().getAccount(ontid, password,sdk.keyType,sdk.curveParaSpec)}});
        }
        boolean b = false;
        if (preExec) {
            return (String) sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        } else {
            b = sdk.getConnectMgr().sendRawTransaction(wsSessionId, tx.toHexString());
        }
        if (!b) {
            throw new SDKException("sendRawTransaction error");
        }
        return tx.hash().toString();
    }

    /**
     * @param codeHexStr
     * @param needStorage
     * @param name
     * @param codeVersion
     * @param author
     * @param email
     * @param desp
     * @param vmtype
     * @return
     * @throws Exception
     */
    public String DeployCodeTransaction(String codeHexStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, byte vmtype) throws Exception {
        Transaction tx = makeDeployCodeTransaction(codeHexStr, needStorage, name, codeVersion, author, email, desp, vmtype);
        String txHex = tx.toHexString();//sdk.getWalletMgr().signatureData(tx);
        System.out.println(txHex);
        boolean b = sdk.getConnectMgr().sendRawTransaction(wsSessionId, txHex);
        if (!b) {
            throw new SDKException("sendRawTransaction error");
        }
        return tx.hash().toString();
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
                if (val instanceof BigInteger) {
                    builder.push((BigInteger) val);
                } else if (val instanceof byte[]) {
                    builder.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    builder.push((Boolean) val);
                } else if (val instanceof Integer) {
                    builder.push(new BigInteger(String.valueOf(val)));
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
                if (val instanceof BigInteger) {
                    sb.push((BigInteger) val);
                } else if (val instanceof byte[]) {
                    sb.push((byte[]) val);
                } else if (val instanceof Boolean) {
                    sb.push((Boolean) val);
                } else if (val instanceof Integer) {
                    sb.push(new BigInteger(String.valueOf(val)));
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
    public DeployCode makeDeployCodeTransaction(String codeStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, byte vmtype) throws SDKException {
        DeployCode tx = new DeployCode();
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
        tx.description = desp;
        return tx;
    }

    /**
     * @param paramsHexStr
     * @param vmtype
     * @param fees
     * @return
     * @throws SDKException
     */
    public InvokeCode makeInvokeCodeTransaction(byte[] paramsHexStr, byte vmtype, Fee[] fees) throws SDKException {
        InvokeCode tx = new InvokeCode();
        tx.attributes = new Attribute[1];
        tx.attributes[0] = new Attribute();
        tx.attributes[0].usage = AttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = paramsHexStr;
        tx.gasLimit = 0;
        tx.vmType = vmtype;
        tx.fee = fees;
        return tx;
    }
}
