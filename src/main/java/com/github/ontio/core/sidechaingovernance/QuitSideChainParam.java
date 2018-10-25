package com.github.ontio.core.sidechaingovernance;

import sun.jvm.hotspot.debugger.Address;

public class QuitSideChainParam {
    public String sideChainID;
    public Address address;
    public QuitSideChainParam(String sideChainID, Address address){
        this.sideChainID = sideChainID;
        this.address = address;
    }

}
