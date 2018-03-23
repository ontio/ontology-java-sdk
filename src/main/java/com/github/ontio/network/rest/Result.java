package com.github.ontio.network.rest;

import com.alibaba.fastjson.JSON;

public class Result {
    public String Action;
    public long Error;
    public String Desc;
    public String Result;
    public String Version;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}