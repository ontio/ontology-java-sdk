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

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.BuildParams;
import com.github.ontio.smartcontract.neovm.ClaimRecord;
import com.github.ontio.smartcontract.neovm.Nep5;
import com.github.ontio.smartcontract.neovm.Record;

/**
 * @Description:
 * @date 2018/5/17
 */
public class NeoVm {
    private Nep5 nep5Tx = null;
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
    public Object sendTransaction(String contractAddr,String payer, String password,long gaslimit, long gas, AbiFunction func, boolean preExec) throws Exception {
        byte[] params = BuildParams.serializeAbiFunction(func);
        if (preExec) {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, VmType.NEOVM.value(), payer,gaslimit, gas);
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            if (((JSONObject) obj).getInteger("State") != 1){
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
            return obj;
        } else {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, VmType.NEOVM.value(), payer,gaslimit, gas);
            sdk.signTx(tx, payer, password);
            boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
            return tx.hash().toHexString();
        }
    }
}
