package com.github.ontio.sidechain.smartcontract.ongx;

import com.github.ontio.common.Address;

public class Swap {
    public Address address;
    public long value;
    public Swap(Address address, long value){
        this.address = address;
        this.value = value;
    }
}
