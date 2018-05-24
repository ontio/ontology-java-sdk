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
import com.github.ontio.smartcontract.nativevm.Ong;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.nativevm.Ont;

/**
 * @Description:
 * @date 2018/5/17
 */
public class NativeVm {
    private Ont ont = null;
    private Ong ong = null;
    private OntId nativeOntIdTx = null;
    private OntSdk sdk;
    public NativeVm(OntSdk sdk){
        this.sdk = sdk;
    }
    /**
     *  get OntAsset Tx
     * @return instance
     */
    public Ont ont() {
        if(ont == null){
            ont = new Ont(sdk);
        }
        return ont;
    }
    public Ong ong() {
        if(ong == null){
            ong = new Ong(sdk);
        }
        return ong;
    }
    public OntId ontId(){
        if (nativeOntIdTx == null){
            nativeOntIdTx = new OntId(sdk);
        }
        return nativeOntIdTx;
    }
}
