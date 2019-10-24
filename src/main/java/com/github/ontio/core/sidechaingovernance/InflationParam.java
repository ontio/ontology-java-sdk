package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class InflationParam {
    public long sideChainId;
    public Address address;
    public long depositAdd;
    public long ongPoolAdd;
    public InflationParam(long sideChainId, Address address, long depositAdd, long ongPoolAdd){
        this.sideChainId = sideChainId;
        this.address = address;
        this.depositAdd = depositAdd;
        this.ongPoolAdd = ongPoolAdd;
    }
}
