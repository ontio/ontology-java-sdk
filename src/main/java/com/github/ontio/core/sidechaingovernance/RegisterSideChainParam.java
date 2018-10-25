package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class RegisterSideChainParam {
    public String sideChainID;
    public Address address;
    public int ratio;
    public long deposit;
    public long ongPool;
    public byte[] caller;
    public int keyNo;
    public RegisterSideChainParam(String sideChainID, Address address, int ratio, long deposit, long ongPool, byte[] caller, int keyNo){
        this.sideChainID = sideChainID;
        this.address = address;
        this.ratio = ratio;
        this.deposit = deposit;
        this.ongPool = ongPool;
        this.caller = caller;
        this.keyNo = keyNo;
    }
}
