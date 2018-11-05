package com.github.ontio.sidechain;

import com.github.ontio.OntSdk;
import com.github.ontio.sidechain.smartcontract.governance.Governance;
import com.github.ontio.sidechain.smartcontract.ongx.OngX;

public class SidechainVm {
    private Governance governance;
    private OngX ongX;
    private OntSdk sdk;
    public SidechainVm(OntSdk sdk){
        this.sdk = sdk;
    }

    public Governance governance() {
        if (governance == null){
            governance = new Governance(sdk);
        }
        return governance;
    }

    public OngX ongX() {
        if (ongX == null){
            ongX = new OngX(sdk);
        }
        return ongX;
    }
}
