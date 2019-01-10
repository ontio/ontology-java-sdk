package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class QuitSideChainParam {
    public long sideChainID;
    public Address address;
    public QuitSideChainParam(long sideChainID, Address address){
        this.sideChainID = sideChainID;
        this.address = address;
    }

}
