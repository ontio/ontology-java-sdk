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
import com.github.ontio.common.ErrorCode;
import com.github.ontio.sdk.exception.SDKException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class Wallet implements Cloneable {
    private String name = "com.github.ontio";
    private String version = "1.0";
    private String createTime = "";
    private String defaultOntid = "";
    private String defaultAccountAddress = "";
    private Scrypt scrypt = new Scrypt();
    private Object extra = null;
    private List<Identity> identities = new ArrayList<Identity>();
    private List<Account> accounts = new ArrayList<Account>();

    public Wallet() {
        identities.clear();
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultOntid() {
        return defaultOntid;
    }

    public void setDefaultOntid(String defaultOntid) {
        this.defaultOntid = defaultOntid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDefaultAccountAddress() {
        return defaultAccountAddress;
    }

    public void setDefaultAccountAddress(String defaultAccountAddress) {
        this.defaultAccountAddress = defaultAccountAddress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Scrypt getScrypt() {
        return scrypt;
    }

    public void setScrypt(Scrypt scrypt) {
        this.scrypt = scrypt;
    }

    public List<Identity> getIdentities() {
        return identities;
    }

    public void setIdentities(List<Identity> identityList) {
        this.identities = identityList;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accountList) {
        this.accounts = accountList;
    }

    public boolean removeAccount(String address) {
        for (Account e : accounts) {
            if (e.address.equals(address)) {
                accounts = new ArrayList(accounts);
                accounts.remove(e);
                return true;
            }
        }
        return false;
    }
    public boolean clearAccount() {
        accounts = new ArrayList<Account>();
        return true;
    }

    public Account getAccount(String address) {
        for (Account e : accounts) {
            if (e.address.equals(address)) {
                return e;
            }
        }
        return null;
    }

    public boolean removeIdentity(String ontid) {
        for (Identity e : identities) {
            if (e.ontid.equals(ontid)) {
                identities = new ArrayList(identities);
                identities.remove(e);
                return true;
            }
        }
        return false;
    }

    public boolean clearIdentity() {
        identities = new ArrayList<Identity>();
        return true;
    }

    public Identity getIdentity(String ontid) {
        for (Identity e : identities) {
            if (e.ontid.equals(ontid)) {
                return e;
            }
        }
        return null;
    }

    public void setDefaultAccount(int index) throws Exception {
        if (index >= accounts.size()) {
            throw new SDKException(ErrorCode.ParamError);
        }
        for (Account e : accounts) {
            e.isDefault = false;
        }
        accounts.get(index).isDefault = true;
        defaultAccountAddress = accounts.get(index).address;
    }

    public void setDefaultAccount(String address) {
        for (Account e : accounts) {
            if (e.address.equals(address)) {
                e.isDefault = true;
                defaultAccountAddress = address;
            } else {
                e.isDefault = false;
            }
        }
    }

    public void setDefaultIdentity(int index) throws Exception {
        if (index >= identities.size()) {
            throw new SDKException(ErrorCode.ParamError);
        }
        for (Identity e : identities) {
            e.isDefault = false;
        }
        identities.get(index).isDefault = true;
        defaultOntid = identities.get(index).ontid;
    }

    public void setDefaultIdentity(String ontid) {
        for (Identity e : identities) {
            if (e.ontid.equals(ontid)) {
                e.isDefault = true;
                defaultOntid = ontid;
            } else {
                e.isDefault = false;
            }
        }
    }

    private Identity addIdentity(String ontid) {
        for (Identity e : getIdentities()) {
            if (e.ontid.equals(ontid)) {
                return e;
            }
        }
        Identity identity = new Identity();
        identity.ontid = ontid;
        identity.controls = new ArrayList<Control>();
        getIdentities().add(identity);
        return identity;
    }

    private void addIdentity(Identity idt) {
        for (Identity e : getIdentities()) {
            if (e.ontid.equals(idt.ontid)) {
                return;
            }
        }
        getIdentities().add(idt);
    }
    public Identity addOntIdController(String ontid, String key, String id,String pubkey) {
        Identity identity = getIdentity(ontid);
        if (identity == null) {
            identity = addIdentity(ontid);
        }
        for (Control e : identity.controls) {
            if (e.key.equals(key)) {
                return identity;
            }
        }
        Control control = new Control(key, id,pubkey);
        identity.controls.add(control);
        return identity;
    }
    @Override
    public Wallet clone() {
        Wallet o = null;
        try {
            o = (Wallet) super.clone();
            Account[] srcAccounts = o.accounts.toArray(new Account[0]);
            Account[] destAccounts = new Account[srcAccounts.length];
            System.arraycopy(srcAccounts, 0, destAccounts, 0, srcAccounts.length);
            o.accounts = Arrays.asList(destAccounts);

            Identity[] srcIdentitys = o.identities.toArray(new Identity[0]);
            Identity[] destIdentitys = new Identity[srcIdentitys.length];
            System.arraycopy(srcIdentitys, 0, destIdentitys, 0, srcIdentitys.length);
            o.identities = Arrays.asList(destIdentitys);

            o.scrypt = (Scrypt)o.scrypt.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return o;
    }
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
