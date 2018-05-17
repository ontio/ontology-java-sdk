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
import com.github.ontio.smartcontract.nativevm.OntAssetTx;
import com.github.ontio.smartcontract.neovm.ClaimRecordTx;
import com.github.ontio.smartcontract.neovm.Nep5Tx;
import com.github.ontio.smartcontract.neovm.OntIdTx;
import com.github.ontio.smartcontract.neovm.RecordTx;

/**
 * @Description:
 * @date 2018/5/17
 */
public class NeoVm {
    private Nep5Tx nep5Tx = null;
    private OntIdTx ontIdTx = null;
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
     * OntId
     * @return instance
     */
    public OntIdTx ontId() {
        if(ontIdTx == null){
            ontIdTx = new OntIdTx(sdk);
        }
        return ontIdTx;
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
}
