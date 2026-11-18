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
import com.github.ontio.smartcontract.nativevm.*;

public class NativeVm {
    private Ont ont = null;
    private OntV2 ontV2 = null;
    private Ong ong = null;
    private OngV2 ongV2 = null;
    private OntId ontId = null;
    private GlobalParams globalParams = null;
    private Auth auth = null;
    private Governance governance = null;
    private SideChainGovernance sideChainGovernance = null;
    private OntSdk sdk;

    public NativeVm(OntSdk sdk) {
        this.sdk = sdk;
    }

    /**
     * get OntAsset Tx
     *
     * @return instance
     */
    public Ont ont() {
        if (ont == null) {
            ont = new Ont(sdk);
        }
        return ont;
    }
    public OntV2 ontV2() {
        if (ontV2 == null) {
            ontV2 = new OntV2(sdk);
        }
        return ontV2;
    }

    public Ong ong() {
        if (ong == null) {
            ong = new Ong(sdk);
        }
        return ong;
    }
    public OngV2 ongV2() {
        if (ongV2 == null) {
            ongV2 = new OngV2(sdk);
        }
        return ongV2;
    }

    public OntId ontId() {
        if (ontId == null) {
            ontId = new OntId(sdk);
        }
        return ontId;
    }

    public GlobalParams gParams() {
        if (globalParams == null) {
            globalParams = new GlobalParams(sdk);
        }
        return globalParams;
    }

    public Auth auth() {
        if (auth == null) {
            auth = new Auth(sdk);
        }
        return auth;
    }

    public Governance governance() {
        if (governance == null) {
            governance = new Governance(sdk);
        }
        return governance;
    }

    public SideChainGovernance sideChainGovernance() {
        if (sideChainGovernance == null) {
            sideChainGovernance = new SideChainGovernance(sdk);
        }
        return sideChainGovernance;
    }
}
