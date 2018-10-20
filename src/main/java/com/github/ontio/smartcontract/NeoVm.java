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

package com.github.ontio.smartcontract;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.smartcontract.neovm.*;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

public class NeoVm {
    private Nep5 nep5Tx = null;
    private Oep4 oep4Tx = null;
    private Oep5 oep5Tx = null;
    private Oep8 oep8Tx = null;
    private Record recordTx = null;
    private ClaimRecord claimRecordTx = null;

    private OntSdk sdk;
    public NeoVm(OntSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get OntAsset Tx
     * @return instance
     */
    public Oep4 oep4(){
        if(oep4Tx == null) {
            oep4Tx = new Oep4(sdk);
        }
        return oep4Tx;
    }
    public Oep5 oep5(){
        if(oep5Tx == null) {
            oep5Tx = new Oep5(sdk);
        }
        return oep5Tx;
    }
    public Oep8 oep8(){
        if(oep8Tx == null) {
            oep8Tx = new Oep8(sdk);
        }
        return oep8Tx;
    }
    public Nep5 nep5() {
        if(nep5Tx == null){
            nep5Tx = new Nep5(sdk);
        }
        return nep5Tx;
    }

    /**
     * RecordTx
     * @return instance
     */
    public Record record() {
        if(recordTx == null){
            recordTx = new Record(sdk);
        }
        return recordTx;
    }

    public ClaimRecord claimRecord(){
        if (claimRecordTx == null){
            claimRecordTx = new ClaimRecord(sdk);
        }
        return claimRecordTx;
    }
    public Object sendTransaction(String contractAddr, Account acct,Account payerAcct, long gaslimit, long gasprice, AbiFunction func, boolean preExec) throws Exception {
        byte[] params;
        if (func != null) {
            params = BuildParams.serializeAbiFunction(func);
        } else {
            params = new byte[]{};
        }
        if (preExec) {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, null,0, 0);
            if (acct != null) {
                sdk.signTx(tx, new Account[][]{{acct}});
            }
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            return obj;
        } else {
            String payer = payerAcct.getAddressU160().toBase58();
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, payer,gaslimit, gasprice);
            sdk.signTx(tx, new Account[][]{{acct}});
            if(!acct.equals(payerAcct.getAddressU160().toBase58())){
                sdk.addSign(tx,payerAcct);
            }
            boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
            return tx.hash().toHexString();
        }
    }
}
