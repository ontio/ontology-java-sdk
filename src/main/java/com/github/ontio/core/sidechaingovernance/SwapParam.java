package com.github.ontio.core.sidechaingovernance;


import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Address;

import java.util.HashMap;
import java.util.Map;

public class SwapParam {
    public long sideChainId;
    public Address address;
    public long ongXAccount;
    public SwapParam(long sideChainId, Address address, long ongXAccount){
        this.sideChainId = sideChainId;
        this.address = address;
        this.ongXAccount = ongXAccount;
    }
    public String toJson(){
        Map map = new HashMap<>();
        map.put("sideChainId", sideChainId);
        map.put("address", address.toBase58());
        map.put("ongXAccount",ongXAccount);
        return JSON.toJSONString(map);
    }
}
