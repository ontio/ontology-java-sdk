package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class InflationParam {
    public String sideChainId;
    public Address address;
    public long depositAdd;
    public long ongPoolAdd;
    public InflationParam(String sideChainId, Address address, long depositAdd, long ongPoolAdd){
        this.sideChainId = sideChainId;
        this.address = address;
        this.depositAdd = depositAdd;
        this.ongPoolAdd = ongPoolAdd;
    }
}
