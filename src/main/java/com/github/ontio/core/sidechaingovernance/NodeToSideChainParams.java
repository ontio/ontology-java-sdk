package com.github.ontio.core.sidechaingovernance;

import com.github.ontio.common.Address;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;
import com.github.ontio.io.utils;

import java.io.IOException;

public class NodeToSideChainParams implements Serializable {
    public String peerPubkey;
    public Address address;
    public long sideChainId;
    public NodeToSideChainParams(){}
    public NodeToSideChainParams(String peerPubkey, Address address, long sideChainId){
        this.peerPubkey = peerPubkey;
        this.address = address;
        this.sideChainId = sideChainId;
    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.peerPubkey = reader.readVarString();
        this.address = utils.readAddress(reader);
        this.sideChainId = reader.readLong();
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }
}
