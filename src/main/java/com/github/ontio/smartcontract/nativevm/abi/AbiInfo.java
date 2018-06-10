/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.github.ontio.smartcontract.nativevm.abi;

import com.alibaba.fastjson.JSON;

import java.util.List;

/**
 * smartcode abi information
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
