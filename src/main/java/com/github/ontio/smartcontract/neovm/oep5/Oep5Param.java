package com.github.ontio.smartcontract.neovm.oep5;

public class Oep5Param {
    public byte[] toAcct;
    public byte[] tokenId;
    public Oep5Param(byte[] toAcct, byte[] tokenId){
        this.toAcct = toAcct;
        this.tokenId = tokenId;
    }
}