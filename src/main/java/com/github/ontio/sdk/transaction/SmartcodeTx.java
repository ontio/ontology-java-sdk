package com.github.ontio.sdk.transaction;

import com.github.ontio.common.Address;
import com.github.ontio.core.Transaction;
import com.github.ontio.core.TransactionAttribute;
import com.github.ontio.core.TransactionAttributeUsage;
import com.github.ontio.common.Helper;
import com.github.ontio.core.*;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.payload.DeployCodeTransaction;
import com.github.ontio.core.payload.InvokeCodeTransaction;
import com.github.ontio.core.scripts.ScriptBuilder;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.exception.Error;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.abi.AbiFunction;
import com.github.ontio.sdk.info.abi.Parameter;
import com.github.ontio.sdk.info.account.AccountInfo;
import com.alibaba.fastjson.JSON;
import org.bouncycastle.math.ec.ECPoint;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zx on 2018/1/9.
 */
public class SmartcodeTx {
    public OntSdk sdk;
    private String codeHash = null;
    private String wsSessionId = "";

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash.replace("0x", "");
    }

    public SmartcodeTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setWsSessionId(String sessionId) {
        if (!this.wsSessionId.equals(sessionId)) {
            this.wsSessionId = sessionId;
        }
    }

    public String getWsSessionId() {
        return wsSessionId;
    }

    public String invokeTransaction(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        return (String) invokeTransaction(false, ontid, password, abiFunction, vmtype);
    }

    public Object invokeTransactionPreExec(String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        return invokeTransaction(true, ontid, password, abiFunction, vmtype);
    }

    public Object invokeTransaction(boolean preExec, String ontid, String password, AbiFunction abiFunction, byte vmtype) throws Exception {
        if (codeHash == null) {
            throw new SDKException(Error.getDescArgError("null codeHash"));
        }
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(ontid, password);
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
                throw new SDKException(Error.getDescArgError("type error"));
            }
        }
        list.add(tmp);
        Fee[] fees = new Fee[1];
        ECPoint publicKey = sdk.getWalletMgr().getPubkey(info.pubkey);
        fees[0] = new Fee(0, Address.addressFromPubKey(publicKey));
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        params = Helper.addBytes(params, new byte[]{0x69});
        params = Helper.addBytes(params, Helper.hexToBytes(codeHash));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, info.pubkey, vmtype, fees);
        String txHex = sdk.getWalletMgr().signatureData(password, tx);
        System.out.println("sigData[0]:" + Helper.toHexString(tx.sigs[0].pubKeys[0].getEncoded(true)));
        System.out.println(txHex);
        boolean b = false;
        if (preExec) {
            return sdk.getConnectMgr().sendRawTransactionPreExec(txHex);
        } else {
            b = sdk.getConnectMgr().sendRawTransaction(wsSessionId, txHex);
        }
        if (!b) {
            throw new SDKException(Error.getDescArgError("sendRawTransaction error"));
        }
        return tx.hash().toString();
    }

    public String DeployCodeTransaction(String codeHexStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, byte vmtype) throws Exception {
        Transaction tx = makeDeployCodeTransaction(codeHexStr, needStorage, name, codeVersion, author, email, desp, vmtype);
        String txHex = sdk.getWalletMgr().signatureData(tx);
        System.out.println(txHex);
        boolean b = sdk.getConnectMgr().sendRawTransaction(wsSessionId, txHex);
        if (!b) {
            throw new SDKException(Error.getDescArgError("sendRawTransaction error"));
        }
        return tx.hash().toString();
    }

    public byte[] createCodeParamsScript(ScriptBuilder sb, List<Object> list) {
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

    public DeployCodeTransaction makeDeployCodeTransaction(String codeStr, boolean needStorage, String name, String codeVersion, String author, String email, String desp, byte vmtype) throws SDKException {
        DeployCodeTransaction tx = new DeployCodeTransaction();
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Nonce;
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

    public InvokeCodeTransaction makeInvokeCodeTransaction(byte[] paramsHexStr, String pubkey, byte vmtype, Fee[] fees) throws SDKException {
        ECPoint publicKey = null;
        if (pubkey != null) {
            sdk.getWalletMgr().getPubkey(pubkey);
        }
        InvokeCodeTransaction tx = new InvokeCodeTransaction(publicKey);
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Nonce;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.code = paramsHexStr;
        tx.gasLimit = 0;
        tx.vmType = vmtype;
        tx.fee = fees;
        return tx;
    }
}
