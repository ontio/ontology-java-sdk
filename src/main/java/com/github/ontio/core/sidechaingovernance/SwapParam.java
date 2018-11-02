package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class SwapParam {
    public String sideChainId;
    public Address address;
    public long ongXAccount;
    public SwapParam(String sideChainId, Address address, long ongXAccount){
        this.sideChainId = sideChainId;
        this.address = address;
        this.ongXAccount = ongXAccount;
    }
}
