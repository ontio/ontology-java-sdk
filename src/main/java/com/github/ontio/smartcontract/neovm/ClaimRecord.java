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

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class ClaimRecord {
    private OntSdk sdk;
    private String contractAddress = null;


    public ClaimRecord(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setContractAddress(String codeHash) {
        this.contractAddress = codeHash.replace("0x", "");
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String sendCommit(String issuerOntid, String password, String subjectOntid, String claimId, Account payerAcct, long gaslimit, long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = issuerOntid.replace(Common.didont,"");
        List list = new ArrayList<Object>();
        list.add("Commit".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(claimId.getBytes());
        tmp.add(issuerOntid.getBytes());
        tmp.add(subjectOntid.getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,payerAcct.getAddressU160().toBase58(),gaslimit,gas);
        sdk.signTx(tx, addr, password);
        sdk.addSign(tx,payerAcct);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendRevoke(String ontid,String password,String claimId,Account payerAcct,long gaslimit,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("Revoke".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(claimId.getBytes());
        tmp.add(ontid.getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info.addressBase58,gaslimit,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendGetStatus(String claimId) throws Exception {
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        List list = new ArrayList<Object>();
        list.add("GetStatus".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(claimId.getBytes());
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,null,0,0);
        Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        if (obj != null ) {
            String res = ((JSONObject)obj).getString("Result");
            String temp = new String(Helper.hexToBytes(res));
            ByteArrayInputStream bais = new ByteArrayInputStream(Helper.hexToBytes(res));
            BinaryReader br = new BinaryReader(bais);
            byte[] status = br.readVarBytes();
            byte[] claimIdBytes = br.readVarBytes();
            byte[] isserOntid = br.readVarBytes();
            byte[] subjectOnid = br.readVarBytes();
            return new String(claimIdBytes)+new String(status)+new String(isserOntid)+new String(subjectOnid);
        }
        return null;
    }
    public Transaction makeInvokeTransaction(List<Object> list,String payer,long gaslimit,long gas) throws Exception {
        byte[] params = BuildParams.createCodeParamsScript(list);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddress,null,params, VmType.NEOVM.value(), payer,gaslimit,gas);
        return tx;
    }
}
