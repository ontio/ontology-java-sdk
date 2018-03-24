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
import com.github.ontio.sdk.exception.Error;
import com.github.ontio.sdk.exception.SDKException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zx on 2018/1/11.
 */
public class Wallet {
    private String name = "com/github/ontio";
    private String version = "1.0";
    private Scrypt scrypt = new Scrypt();
    private Object extra = null;
    private List<Identity> identities = new ArrayList<Identity>();
    private List<Account> accounts = new ArrayList<>();
    public Wallet(){
        identities.clear();
    }
    public void setExtra(Object extra){
        this.extra = extra;
    }
    public Object getExtra(){
        return extra;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public void setVersion(String version){
        this.version = version;
    }
    public String getVersion(){
        return version;
    }
    public void setScrypt(Scrypt scrypt){
        this.scrypt = scrypt;
    }
    public Scrypt getScrypt(){
        return scrypt;
    }
    public void setIdentities(List<Identity> identityList){
        this.identities = identityList;
    }
    public List<Identity> getIdentities(){
        return identities;
    }

    public void setAccounts(List<Account> accountList){
        this.accounts = accountList;
    }
    public List<Account> getAccounts(){
        return accounts;
    }
    public boolean removeAccount(String address){
        for(Account e:accounts){
            if(e.address.equals(address)){
                accounts.remove(e);
                return true;
            }
        }
        return false;
    }
    public Account getAccount(String address){
        for(Account e:accounts){
            if(e.address.equals(address)){
                return e;
            }
        }
        return null;
    }
    public boolean removeIdentity(String ontid){
        for(Identity e:identities){
            if(e.ontid.equals(ontid)){
                identities.remove(e);
                return true;
            }
        }
        return false;
    }
    public Identity getIdentity(String ontid){
        for(Identity e:identities){
            if(e.ontid.equals(ontid)){
                return e;
            }
        }
        return null;
    }
    public void setDefaultAccount(int index) throws Exception{
        if(index >= accounts.size()){
            throw new SDKException(Error.getDescArgError("index error"));
        }
        for(Account e:accounts){
            e.isDefault = false;
        }
        accounts.get(index).isDefault = true;
    }
    public void setDefaultAccount(String address){
        for(Account e:accounts){
            if(e.address.equals(address)){
                e.isDefault = true;
            }else {
                e.isDefault = false;
            }
        }
    }
    public void setDefaultIdentity(int index) throws Exception{
        if(index >= identities.size()){
            throw new SDKException(Error.getDescArgError("index error"));
        }
        for(Identity e:identities){
            e.isDefault = false;
        }
        identities.get(index).isDefault = true;
    }
    public void setDefaultIdentity(String ontid){
        for(Identity e:identities){
            if(e.ontid.equals(ontid)){
                e.isDefault = true;
            }else {
                e.isDefault = false;
            }
        }
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
