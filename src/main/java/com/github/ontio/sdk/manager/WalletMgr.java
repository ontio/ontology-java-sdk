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

package com.github.ontio.sdk.manager;

import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.crypto.sm.SM2Utils;
import com.github.ontio.sdk.exception.*;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.account.Acct;
import com.github.ontio.account.KeyType;
import com.github.ontio.common.Common;
import com.github.ontio.crypto.ECC;
import com.github.ontio.core.DataSignature;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import org.bouncycastle.math.ec.ECPoint;

import java.io.*;
import java.util.*;

/**
 *
 */
public class WalletMgr {
    private Wallet wallet;
    private Map acctPriKeyMap = new HashMap();
    private Map identityPriKeyMap = new HashMap();
    private Wallet walletFile;
    private String Algrithem = "P256R1";
    private String filePath = null;

    public WalletMgr(String path) {
        try {
            this.filePath = path;
            File file = new File(filePath);
            if (!file.exists()) {
                wallet = new Wallet();
                walletFile = new Wallet();
                file.createNewFile();
                writeWallet();
            }
            InputStream inputStream = new FileInputStream(filePath);
            String text = IOUtils.toString(inputStream);
            wallet = JSON.parseObject(text, Wallet.class);
            walletFile = JSON.parseObject(text, Wallet.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WalletMgr(String path, String password) {
        try {
            this.filePath = path;
            File file = new File(filePath);
            if (!file.exists()) {
                wallet = new Wallet();
                walletFile = new Wallet();
                file.createNewFile();
                createIdentity(password);
                writeWallet();
            }
            InputStream inputStream = new FileInputStream(filePath);
            String text = IOUtils.toString(inputStream);
            wallet = JSON.parseObject(text, Wallet.class);
            walletFile = JSON.parseObject(text, Wallet.class);
            if (getIdentitys().size() == 0) {
                createIdentity(password);
                writeWallet();
                return;
            }
            Identity identity = getDefaultIdentity();
            if (identity != null) {
                String prikey = Acct.getPrivateKey(identity.controls.get(0).key, password);
                storePrivateKey(identityPriKeyMap, identity.ontid, password, prikey);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Wallet openWallet() {
        return walletFile;
    }

    public Wallet getWallet() {
        return wallet;
    }

    private static void writeFile(String filePath, String sets) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    public Wallet writeWallet() throws Exception {
        writeFile(filePath, JSON.toJSONString(wallet));
        walletFile = wallet;
        return walletFile;
    }

    public String getAlgrithem() {
        return Algrithem;
    }

    public void setAlgrithem(String alg) {
        if (alg.equals(KeyType.SM2.name())) {
            Algrithem = alg;
        }
    }

    private void storePrivateKey(Map map, String key, String password, String prikey) {
        map.put(key + "," + password, prikey);
    }

    public Identity importIdentity(String encryptedPrikey, String password) throws Exception {
        String prikey = Acct.getPrivateKey(encryptedPrikey, password);
        AccountInfo info = createIdentity(password, Helper.hexToBytes(prikey));
        storePrivateKey(identityPriKeyMap, Common.didont + info.address, password, prikey);
        return getIdentity(Common.didont + info.address);
    }

    public Identity createIdentity(String password) {
        AccountInfo info = createIdentity(password, ECC.generateKey());
        return getIdentity(Common.didont + info.address);
    }

    public AccountInfo createIdentityInfo(String password) {
        AccountInfo info = createIdentity(password, ECC.generateKey());
        return info;
    }

    public AccountInfo getIdentityInfo(String ontid, String password) {
        String prikeyStr = (String) identityPriKeyMap.get(ontid + "," + password);
        if (prikeyStr == null) {
            return null;
        }
        byte[] prikey = Helper.hexToBytes(prikeyStr);
        Acct acct = createAccount(password, prikey, false);
        AccountInfo info = new AccountInfo();
        info.address = Address.addressFromPubKey(acct.publicKey).toBase58();
        info.pubkey = Helper.toHexString(acct.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acct.privateKey));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportEncryptedPrikey(password);
        info.pkHash = acct.addressU160.toString();
        storePrivateKey(identityPriKeyMap, Common.didont + info.address, password, Helper.toHexString(prikey));
        return info;
    }

    private AccountInfo createIdentity(String password, byte[] prikey) {
        Acct acct = createAccount(password, prikey, false);
        AccountInfo info = new AccountInfo();
        info.address = Address.addressFromPubKey(acct.publicKey).toBase58();
        info.pubkey = Helper.toHexString(acct.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acct.privateKey));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportEncryptedPrikey(password);
        info.pkHash = acct.addressU160.toString();
        storePrivateKey(identityPriKeyMap, Common.didont + info.address, password, Helper.toHexString(prikey));
        return info;
    }

    public Account importAccount(String encryptedPrikey, String password) throws Exception {
        String prikey = Acct.getPrivateKey(encryptedPrikey, password);
        AccountInfo info = createAccount(password, Helper.hexToBytes(prikey));
        storePrivateKey(acctPriKeyMap, info.address, password, prikey);
        return getAccount(info.address);
    }

    public Account createAccount(String password) {
        AccountInfo info = createAccount(password, ECC.generateKey());
        return getAccount(info.address);
    }

    public Account createAccountFromPrikey(String password, String prikey) {
        AccountInfo info = createAccount(password, Helper.hexToBytes(prikey));
        return getAccount(info.address);
    }

    public AccountInfo createAccountInfo(String password) {
        AccountInfo info = createAccount(password, ECC.generateKey());
        return info;
    }

    private AccountInfo createAccount(String password, byte[] prikey) {
        Acct acct = createAccount(password, prikey, true);
        AccountInfo info = new AccountInfo();
        info.address = Address.addressFromPubKey(acct.publicKey).toBase58();
        info.pubkey = Helper.toHexString(acct.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acct.privateKey));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportEncryptedPrikey(password);
        info.pkHash = acct.addressU160.toString();
        storePrivateKey(acctPriKeyMap, info.address, password, Helper.toHexString(prikey));
        return info;
    }

    public AccountInfo createAccountFromPrivateKey(String password, String prikey) {
        return createAccount(password, Helper.hexToBytes(prikey));
    }

    public AccountInfo createIdentityFromPrivateKey(String password, String prikey) {
        return createIdentity(password, Helper.hexToBytes(prikey));
    }

    public String privateKeyToWif(String privateKey) {
        Acct act = new Acct(Helper.hexToBytes(privateKey), getAlgrithem());
        return act.exportWif();
    }

    public ECPoint getPubkey(String pubkeyHexStr) {
        ECPoint pubkey;
        byte[] pubkeyBys = Helper.hexToBytes(pubkeyHexStr);
        if (getAlgrithem().equals(KeyType.SM2.name())) {
            pubkey = SM2Utils.decodePoint(pubkeyBys);
        } else {
            pubkey = ECC.secp256r1.getCurve().decodePoint(pubkeyBys);
        }
        return pubkey;
    }

    public byte[] signatureData(Acct acct, String str) throws SDKException {
        DataSignature sign = null;
        try {
            sign = new DataSignature(getAlgrithem(), acct, str);
            return sign.signature();
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }

    public boolean verifySignature(String pubkeyHexStr, byte[] data, byte[] signature) throws SDKException {
        DataSignature sign = null;
        ECPoint pubkey = getPubkey(pubkeyHexStr);
        try {
            sign = new DataSignature();
            return sign.verifySignature(getAlgrithem(), pubkey, data, signature);
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }

    public Acct getAccount(String address, String password) throws SDKException {
        address = address.replace(Common.didont, "");
        if (!ParamCheck.isValidAddress(address)) {
            throw new SDKException(String.format("%s=%s", "address:", address));
        }
        return getAccountByAddress(Address.decodeBase58(address), password);
    }

    private Acct createAccount(String password, String prikey) {
        return createAccount(password, Helper.hexToBytes(prikey), true);
    }

    private Identity addIdentity(String ontid) {
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(ontid)) {
                return e;
            }
        }
        Identity identity = new Identity();
        identity.ontid = ontid;
        identity.controls = new ArrayList<Control>();
        wallet.getIdentities().add(identity);
        return identity;
    }

    private void addIdentity(Identity idt) {
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(idt.ontid)) {
                return;
            }
        }
        wallet.getIdentities().add(idt);
    }

    public List<Account> getAccounts() {
        return wallet.getAccounts();
    }

    public AccountInfo getAccountInfo(String address, String password) throws SDKException {
        address = address.replace(Common.didont, "");
        if (!ParamCheck.isValidAddress(address)) {
            throw new SDKException(String.format("%s=%s", "address:", address));
        }
        AccountInfo info = new AccountInfo();
        Acct acc = getAccountByAddress(Address.decodeBase58(address), password);
        info.address = address;
        info.pubkey = Helper.toHexString(acc.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acc.privateKey));
        info.encryptedPrikey = acc.exportEncryptedPrikey(password);
        info.setPriwif(acc.exportWif());
        info.pkHash = acc.addressU160.toString();
        return info;
    }

    public List<Identity> getIdentitys() {
        return wallet.getIdentities();
    }

    public Identity getIdentity(String ontid) {
        for (Identity e : wallet.getIdentities()) {
            if (e.ontid.equals(ontid)) {
                return e;
            }
        }
        return null;
    }

    public Identity getDefaultIdentity() {
        for (Identity e : wallet.getIdentities()) {
            if (e.isDefault) {
                return e;
            }
        }
        return null;
    }

    public Account getAccount(String address) {
        for (Account e : wallet.getAccounts()) {
            if (e.address.equals(address)) {
                return e;
            }
        }
        return null;
    }

    public Identity addOntIdController(String ontid, String key, String id) {
        Identity identity = getIdentity(ontid);
        if (identity == null) {
            identity = addIdentity(ontid);
        }
        for (Control e : identity.controls) {
            if (e.key.equals(key)) {
                return identity;
            }
        }
        Control control = new Control(key, id);
        identity.controls.add(control);
        return identity;
    }

    private Acct createAccount(String password, byte[] privateKey, boolean saveAccountFlag) {
        Acct account = new Acct(privateKey, Algrithem);

        Account at = new Account();
        if (password != null) {
            at.key = account.exportEncryptedPrikey(password);
        } else {
            at.key = Helper.toHexString(account.privateKey);
        }
        at.address = Address.addressFromPubKey(account.publicKey).toBase58();
        if (saveAccountFlag) {
            for (Account e : wallet.getAccounts()) {
                if (e.equals(at)) {
                    return account;
                }
            }
            if (wallet.getAccounts().size() == 0) {
                at.isDefault = true;
            }
            wallet.getAccounts().add(at);
        } else {
            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals(Common.didont + at.address)) {
                    return account;
                }
            }
            Identity idt = new Identity();
            if (wallet.getIdentities().size() == 0) {
                idt.isDefault = true;
            }
            idt.ontid = Common.didont + at.address;
            idt.controls = new ArrayList<Control>();
            Control ctl = new Control(at.key, "");
            idt.controls.add(ctl);
            wallet.getIdentities().add(idt);
        }
        return account;
    }

    private Acct getAccountByAddress(Address address, String password) {
        try {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(address.toBase58())) {
                    String prikey = (String) acctPriKeyMap.get(e.address + "," + password);
                    if (prikey == null) {
                        prikey = Acct.getPrivateKey(e.key, password);
                        storePrivateKey(acctPriKeyMap, e.address, password, prikey);
                    }
                    return new Acct(Helper.hexToBytes(prikey), getAlgrithem());
                }
            }

            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals(Common.didont + address.toBase58())) {
                    String prikey = (String) identityPriKeyMap.get(e.ontid + "," + password);
                    if (prikey == null) {
                        prikey = Acct.getPrivateKey(e.controls.get(0).key, password);
                        storePrivateKey(identityPriKeyMap, e.ontid, password, prikey);
                    }
                    return new Acct(Helper.hexToBytes(prikey), getAlgrithem());
                }
            }
        } catch (Exception e) {
            throw new SDKRuntimeException("getAccountByScriptHash err", e);
        }
        return null;
    }

}
