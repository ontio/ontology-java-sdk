package com.github.ontio.core.governance;

import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.io.Serializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SideChainNodeInfo implements Serializable {
    public String sideChainId;
    public Map<String, NodeToSideChainParams> nodeInfoMap;

    public SideChainNodeInfo(){

    }

    @Override
    public void deserialize(BinaryReader reader) throws IOException {
        this.sideChainId = reader.readVarString();
        this.nodeInfoMap = new HashMap<>();
        int n = reader.readInt();
        for(int i=0; i<n; i++){
            NodeToSideChainParams params = new NodeToSideChainParams();
            params.deserialize(reader);
            this.nodeInfoMap.put(params.peerPubkey, params);
        }
    }

    @Override
    public void serialize(BinaryWriter writer) throws IOException {

    }
}
