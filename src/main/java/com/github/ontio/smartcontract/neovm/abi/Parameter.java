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

package com.github.ontio.smartcontract.neovm.abi;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.sdk.exception.SDKException;
import com.alibaba.fastjson.JSON;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Parameter {

    public enum Type {ByteArray, String, Boolean, Integer, Array, InteropInterface, Void, Main, Struct}

    public String name;
    public String type;
    public String value;

    public Parameter(String name, Type type, Object value) {
        this.name = name;
        this.type = type.name();
        setValue(value);
    }

    public Parameter() {
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean setValue(Object value) {
        try {
            if (value == null) {
                this.value = null;
            } else if ("ByteArray".equals(type)) {
                byte[] tmp = (byte[]) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("String".equals(type)) {
                this.value = (String) value;
            } else if ("Boolean".equals(type)) {
                boolean tmp = (boolean) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Integer".equals(type)) {
                long tmp = Long.parseLong(value.toString());
                this.value = JSON.toJSONString(tmp);
            } else if ("Array".equals(type)) {
                List tmp = (List) value;
                for (int i = 0; i < tmp.size(); i++) {
                    if (tmp.get(i) instanceof String) {
                        tmp.set(i, ((String) tmp.get(i)).getBytes());
                    }
                }
                this.value = JSON.toJSONString(tmp);
            } else if ("InteropInterface".equals(type)) {
                Object tmp = (Object) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Void".equals(type)) {
            } else if ("Map".equals(type)) {
                Map tmp = (Map) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Struct".equals(type)) {
                Struct tmp = (Struct) value;
                this.value = JSON.toJSONString(tmp);
            } else {
                throw new SDKException(ErrorCode.TypeError);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
