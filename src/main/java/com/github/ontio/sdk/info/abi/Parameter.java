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

package com.github.ontio.sdk.info.abi;

import com.github.ontio.sdk.exception.Error;
import com.github.ontio.sdk.exception.SDKException;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Array;

/**
 * Created by zx on 2018/1/31.
 */
public class Parameter {
    public String name;
    public String type;
    public String value;

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
            if(value == null) {
                this.value = null;
            }else if ("ByteArray".equals(type)) {
                byte[] tmp = (byte[]) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("String".equals(type)) {
                this.value = (String) value;
            } else if ("Boolean".equals(type)) {
                boolean tmp = (boolean) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Integer".equals(type)) {
                int tmp = (int) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Array".equals(type)) {
                Array tmp = (Array) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("InteropInterface".equals(type)) {
                Object tmp = (Object) value;
                this.value = JSON.toJSONString(tmp);
            } else if ("Void".equals(type)) {
            } else {
                throw new SDKException(Error.getDescArgError("type error"));
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
