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

package com.github.ontio.smartcontract.neovm;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
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


    public String sendPut(String addr,String password,String key,String value,long gas) throws Exception {
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (key == null || value == null || key == "" || value == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        addr = addr.replace(Common.didont,"");
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("Put".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(key.getBytes());
        tmp.add(JSON.toJSONString(constructRecord(value)).getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info.addressBase58,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendGet(String addr,String password,String key) throws Exception {
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (key == null || key == ""){
            throw new SDKException(ErrorCode.NullKey);
        }
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("Get".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(key.getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,null,0);
        sdk.signTx(tx, addr, password);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        return new String(Helper.hexToBytes((String)obj));
    }

    public Transaction makeInvokeTransaction(List<Object> list,String addr,long gas) throws Exception {
        byte[] params = BuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(codeAddress,null,params, VmType.NEOVM.value(), addr,gas);
        return tx;
    }

    public LinkedHashMap<String, Object> constructRecord(String text) {
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
