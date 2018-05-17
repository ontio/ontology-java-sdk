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
import com.github.ontio.smartcontract.nativevm.NativeOntIdTx;
import com.github.ontio.smartcontract.nativevm.OntAssetTx;

/**
 * @Description:
 * @date 2018/5/17
 */
public class NativeVm {
    private OntAssetTx ont = null;
    private NativeOntIdTx nativeOntIdTx = null;
    private OntSdk sdk;
    public NativeVm(OntSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get OntAsset Tx
     * @return instance
     */
    public OntAssetTx ont() {
        if(ont == null){
            ont = new OntAssetTx(sdk);
        }
        return ont;
    }
    public NativeOntIdTx ontId(){
        if (nativeOntIdTx == null){
            nativeOntIdTx = new NativeOntIdTx(sdk);
        }
        return nativeOntIdTx;
    }
}
