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
import com.github.ontio.smartcontract.nativevm.OntAssetTx;
import com.github.ontio.smartcontract.neovm.BuildParams;
import com.github.ontio.smartcontract.neovm.ClaimRecordTx;
import com.github.ontio.smartcontract.neovm.Nep5Tx;
import com.github.ontio.smartcontract.neovm.RecordTx;

/**
 * @Description:
 * @date 2018/5/17
 */
public class NeoVm {
    private Nep5Tx nep5Tx = null;
    private RecordTx recordTx = null;
    private ClaimRecordTx claimRecordTx = null;

    private OntSdk sdk;
    public NeoVm(OntSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get OntAsset Tx
     * @return instance
     */
    public Nep5Tx nep5() {
        if(nep5Tx == null){
            nep5Tx = new Nep5Tx(sdk);
        }
        return nep5Tx;
    }

    /**
     * RecordTx
     * @return instance
     */
    public RecordTx record() {
        if(recordTx == null){
            recordTx = new RecordTx(sdk);
        }
        return recordTx;
    }

    public ClaimRecordTx claimRecord(){
        if (claimRecordTx == null){
            claimRecordTx = new ClaimRecordTx(sdk);
        }
        return claimRecordTx;
    }
    public String sendTransaction(String contractAddr,String payer, String password, long gas, AbiFunction func, boolean preExec) throws Exception {
        byte[] params = BuildParams.serializeAbiFunction(func);
        if (preExec) {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, VmType.NEOVM.value(), null, 0);
            Object obj = sdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
            String result = ((JSONObject) obj).getString("Result");
            if (Integer.parseInt(result) == 0) {
                throw new SDKException(ErrorCode.OtherError("sendRawTransaction PreExec error"));
            }
            return result;
        } else {
            Transaction tx = sdk.vm().makeInvokeCodeTransaction(contractAddr, null, params, VmType.NEOVM.value(), payer, gas);
            sdk.signTx(tx, payer, password);
            boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
            if (!b) {
                throw new SDKException(ErrorCode.SendRawTxError);
            }
            return tx.hash().toHexString();
        }
    }
}
