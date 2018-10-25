package com.github.ontio.core.sidechaingovernance;

import com.github.ontio.common.Address;

public class NodeToSideChainParams {
    public String peerPubkey;
    public Address address;
    public String sideChainId;
    public NodeToSideChainParams(String peerPubkey, Address address, String sideChainId){
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.sideChainId = sideChainId;
    }
}
