package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class SwapParam {
    public String sideChainId;
    public Address address;
    public int ongXAccount;
    public SwapParam(String sideChainId, Address address, int ongXAccount){
        this.sideChainId = sideChainId;
        this.address = address;
        this.ongXAccount = ongXAccount;
    }
}
