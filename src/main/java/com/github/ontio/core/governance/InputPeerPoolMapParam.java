package com.github.ontio.core.governance;

import java.util.Map;

public class InputPeerPoolMapParam {
    public Map<String, PeerPoolItem> peerPoolMap;
    public Map<String, NodeToSideChainParams> nodeInfoMap;
    public InputPeerPoolMapParam(Map<String, PeerPoolItem> peerPoolMap, Map<String, NodeToSideChainParams> nodeInfoMap){
        this.peerPoolMap = peerPoolMap;
        this.nodeInfoMap = nodeInfoMap;
    }
}
