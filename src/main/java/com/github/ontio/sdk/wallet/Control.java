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

package com.github.ontio.sdk.wallet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Control {
    public String algorithm = "ECDSA";
    public Map parameters = new HashMap() ;
    public String id = "";
    public String key = "";
    public String salt = "";
    @JSONField(name = "enc-alg")
    public String encAlg = "aes-256-gcm";
    public String address = "";
    public Control(){

    }
    public Control(String key,String id){
        this.key = key;
        this.algorithm = "ECDSA";
        this.id = id;
        this.parameters.put("curve","secp256r1");
    }
    public String getEncAlg(){
        return encAlg;
    }
    public void setEncAlg(String encAlg){
        this.encAlg = encAlg;
    }
    public byte[] getSalt(){
        return Base64.getDecoder().decode(salt);
    }
    public void setSalt(byte[] salt){
        this.salt = new String(Base64.getEncoder().encode(salt));
    }
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}