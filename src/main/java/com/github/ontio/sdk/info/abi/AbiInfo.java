package com.github.ontio.sdk.info.abi;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * Created by zx on 2018/1/31.
 */
public class AbiInfo {
    public String hash;
    public String entrypoint;
    public List<AbiFunction> functions;
    public List<AbiEvent> events;

    public String getHash() {
        return hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getEntrypoint() {
        return entrypoint;
    }
    public void setEntrypoint(String entrypoint) {
        this.entrypoint = entrypoint;
    }

    public List<AbiFunction> getFunctions() {
        return functions;
    }
    public void getFunctions(List<AbiFunction> functions) {
        this.functions = functions;
    }

    public List<AbiEvent> getEvents() {
        return events;
    }
    public void getEvents(List<AbiEvent> events) {
        this.events = events;
    }
    public AbiFunction getFunction(String name) {
        for (AbiFunction e : functions) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }
    public AbiEvent getEvent(String name) {
        for (AbiEvent e : events) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }
    public void clearFunctionsParamsValue() {
        for (AbiFunction e : functions) {
            e.clearParamsValue();
        }
    }
    public void clearEventsParamsValue() {
        for (AbiEvent e : events) {
            e.clearParamsValue();
        }
    }
    public void removeFunctionParamsValue(String name) {
        for (AbiFunction e : functions) {
            if (e.getName().equals(name)) {
                e.clearParamsValue();
            }
        }
    }
    public void removeEventParamsValue(String name) {
        for (AbiEvent e : events) {
            if (e.getName().equals(name)) {
                e.clearParamsValue();
            }
        }
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
