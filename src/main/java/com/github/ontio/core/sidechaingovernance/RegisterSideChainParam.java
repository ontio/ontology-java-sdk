package com.github.ontio.core.sidechaingovernance;


import com.github.ontio.common.Address;

public class RegisterSideChainParam {
    public long sideChainID;
    public Address address;
    public int ratio;
    public long deposit;
    public long ongPool;
    public byte[] genesisBlock;
    public byte[] caller;
    public int keyNo;
    public RegisterSideChainParam(long sideChainID, Address address, int ratio, long deposit, long ongPool,byte[] genesisBlock, byte[] caller, int keyNo){
        this.sideChainID = sideChainID;
        this.address = address;
        this.ratio = ratio;
        this.deposit = deposit;
        this.ongPool = ongPool;
        this.genesisBlock = genesisBlock;
        this.caller = caller;
        this.keyNo = keyNo;
    }
}
