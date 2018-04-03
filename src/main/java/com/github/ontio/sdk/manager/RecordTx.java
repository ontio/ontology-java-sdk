package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.KeyType;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RecordTx {
    private OntSdk sdk;
    private String codeAddress = null;


    public RecordTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.codeAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return codeAddress;
    }


    public String sendPut(String addr,String password,String key,String value) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        if (key == null || value == null || key == "" || value == ""){
            throw new SDKException("null key or value");
        }
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("put".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(key.getBytes());
        tmp.add(JSON.toJSONString(constructRecord(value)).getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendGet(String addr,String password,String key) throws Exception {
        if (codeAddress == null) {
            throw new SDKException("null codeHash");
        }
        if (key == null || key == ""){
            throw new SDKException("null  key");
        }
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password,sdk.keyType,sdk.curveParaSpec);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("get".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(key.getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info);
        sdk.signTx(tx, addr, password);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        List listResult = (List) obj;
        byte[] res = Helper.hexToBytes((String)listResult.get(0));
        return new String(res);
    }

    public Transaction makeInvokeTransaction(List<Object> list,AccountInfo acctinfo) throws Exception {
        Fee[] fees = new Fee[1];
        fees[0] = new Fee(0, Address.addressFromPubKey(acctinfo.pubkey));
        byte[] params = sdk.getSmartcodeTx().createCodeParamsScript(list);
        params = Helper.addBytes(params, new byte[]{0x69});
        params = Helper.addBytes(params, Helper.hexToBytes(codeAddress));
        Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(params, VmType.NEOVM.value(), fees);
        return tx;
    }

    private LinkedHashMap<String, Object> constructRecord(String text) {
        LinkedHashMap<String, Object> recordData = new LinkedHashMap<String, Object>();
        LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("Algrithem", KeyType.SM2.name());
        data.put("Hash", "");
        data.put("Text", text);
        data.put("Signature", "");

        recordData.put("Data", data);
        recordData.put("CAkey", "");
        recordData.put("SeqNo", "");
        recordData.put("Timestamp", 0);
        return recordData;
    }
}
