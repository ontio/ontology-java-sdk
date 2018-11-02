package com.github.ontio.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import demo.vmtest.types.StackItems;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotifyEventInfo {
    public List<Object> States;
    public String ContractAddress;
    public List<Object> getStates() {
        return States;
    }

    public void setStates(List<Object> states) {
        States = states;
    }

    public String getContractAddress() {
        return ContractAddress;
    }

    public void setContractAddress(String contractAddress) {
        ContractAddress = contractAddress;
    }

    public String toJson(){
        Map map = new HashMap();
        map.put("States", States);
        map.put("ContractAddress", ContractAddress);
        return JSON.toJSONString(map);
    }

}
