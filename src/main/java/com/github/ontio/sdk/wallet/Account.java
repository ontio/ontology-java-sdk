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

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Account {
    public String label = "";
    public String address = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public String algorithm = "";
    public Map parameters = new HashMap() ;
    public String key = "";
    @JSONField(name = "enc-alg")
    public String encAlg = "aes-256-ctr";
    public String hash = "sha256";
    public String signatureScheme = "SHA256withECDSA";
    public String passwordHash = "";
    public Object extra = null;
    public Account(){

    }
    public Account(String alg,Object[] params,String encAlg,String scheme,String hash){
        this.algorithm = alg;
        this.parameters.put("curve",params[0]);
        this.signatureScheme = scheme;
        this.encAlg = encAlg;
        this.hash = hash;
        this.extra = null;
    }
    public void setKey(String key){
        this.key = key;
    }
    public String getKey(){
        return key;
    }
//    public void setEncAlg(String encAlg){
//        this.encAlg = encAlg;
//    }
//    public String getEncAlg(){
//        return encAlg;
//    }
    public void setHash(String hash){
        this.hash = hash;
    }
    public String getHash(){
        return hash;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
    public Object getPasswordHash(){
        return passwordHash;
    }
    public void setExtra(Object extra){
        this.extra = extra;
    }
    public Object getExtra(){
        return extra;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}

