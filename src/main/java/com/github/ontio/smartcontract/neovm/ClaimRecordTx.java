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

import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;

import java.util.ArrayList;
import java.util.List;

public class ClaimRecordTx {
    private OntSdk sdk;
    private String codeAddress = null;


    public ClaimRecordTx(OntSdk sdk) {
        this.sdk = sdk;
    }

    public void setCodeAddress(String codeHash) {
        this.codeAddress = codeHash.replace("0x", "");
    }

    public String getCodeAddress() {
        return codeAddress;
    }

    public String sendCommit(String ontid,String password,String claimId,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("Commit".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes(claimId));
        tmp.add(did);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info.addressBase58,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendRevoke(String ontid,String password,String claimId,long gas) throws Exception {
        if(gas < 0){
            throw new SDKException(ErrorCode.ParamErr("gas is less than 0"));
        }
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        byte[] did = (Common.didont + addr).getBytes();
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("Revoke".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes(claimId));
        tmp.add(did);
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,info.addressBase58,gas);
        sdk.signTx(tx, addr, password);
        boolean b = sdk.getConnectMgr().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return null;
    }
    public String sendGetStatus(String ontid,String password,String claimId) throws Exception {
        if (codeAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }
        if (claimId == null || claimId == ""){
            throw new SDKException(ErrorCode.NullKeyOrValue);
        }
        String addr = ontid.replace(Common.didont,"");
        AccountInfo info = sdk.getWalletMgr().getAccountInfo(addr, password);
        List list = new ArrayList<Object>();
        list.add("GetStatus".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes(claimId));
        list.add(tmp);
        Transaction tx = makeInvokeTransaction(list,null,0);
        sdk.signTx(tx, addr, password);
        Object obj = sdk.getConnectMgr().sendRawTransactionPreExec(tx.toHexString());
        if (obj != null ) {
            return (String) obj;
        }
        return null;
    }
    public Transaction makeInvokeTransaction(List<Object> list,String addr,long gas) throws Exception {
        byte[] params = sdk.vm().createCodeParamsScript(list);
        Transaction tx = sdk.vm().makeInvokeCodeTransaction(codeAddress,null,params, VmType.NEOVM.value(), addr,gas);
        return tx;
    }
}
