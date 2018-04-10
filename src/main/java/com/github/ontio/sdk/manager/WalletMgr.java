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
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.*;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.crypto.KeyType;
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
    private SignatureScheme scheme = null;
    private String filePath = null;
    private KeyType keyType = null;
    private Object[] curveParaSpec = null;
    public WalletMgr(String path,KeyType type, Object[] curveParaSpec) {
        try {
            this.keyType = type;
            this.curveParaSpec = curveParaSpec;
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

    private WalletMgr(String path, String password,KeyType type, Object[] params) {
        try {
            this.filePath = path;
            File file = new File(filePath);
            if (!file.exists()) {
                wallet = new Wallet();
                walletFile = new Wallet();
                file.createNewFile();
                createIdentity(password,type,params);
                writeWallet();
            }
            InputStream inputStream = new FileInputStream(filePath);
            String text = IOUtils.toString(inputStream);
            wallet = JSON.parseObject(text, Wallet.class);
            walletFile = JSON.parseObject(text, Wallet.class);
            if (getIdentitys().size() == 0) {
                createIdentity(password,type,params);
                writeWallet();
                return;
            }
            Identity identity = getDefaultIdentity();
            if (identity != null) {
                String prikey = com.github.ontio.account.Account.getPrivateKey(identity.controls.get(0).key, password,type,params);
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

    public SignatureScheme getSignatureScheme() {
        return scheme;
    }

    public void setSignatureScheme(SignatureScheme scheme) {
        this.scheme = scheme;
    }

    private void storePrivateKey(Map map, String key, String password, String prikey) {
        map.put(key + "," + password, prikey);
    }
    public Identity importIdentity(String encryptedPrikey, String password) throws Exception {
        return importIdentity(encryptedPrikey,password,keyType,curveParaSpec);
    }
    public Identity importIdentity(String encryptedPrikey, String password, KeyType type, Object[] params) throws Exception {
        String prikey = com.github.ontio.account.Account.getPrivateKey(encryptedPrikey, password, type, params);
        IdentityInfo info = createIdentity(password, Helper.hexToBytes(prikey), type, params);
        storePrivateKey(identityPriKeyMap, info.ontid, password, prikey);
        return getIdentity(info.ontid);
    }
    public Identity createIdentity(String password) throws Exception {
        return createIdentity(password,keyType,curveParaSpec);
    }
    public Identity createIdentityFromPriKey(String password, String prikey) throws Exception{
        IdentityInfo info = createIdentity(password, Helper.hexToBytes(prikey), keyType,curveParaSpec);
        return getIdentity(info.ontid);
    }
    public Identity createIdentity(String password, KeyType type, Object[] params) throws Exception {
        IdentityInfo info = createIdentity(password, ECC.generateKey(), type, params);
        return getIdentity(info.ontid);
    }

    public IdentityInfo createIdentityInfo(String password, KeyType type, Object[] params) throws Exception {
        IdentityInfo info = createIdentity(password, ECC.generateKey(), type, params);
        return info;
    }

    public IdentityInfo getIdentityInfo(String ontid, String password, KeyType type, Object[] params) throws Exception {
        String prikeyStr = (String) identityPriKeyMap.get(ontid + "," + password);
        if (prikeyStr == null) {
            return null;
        }
        byte[] prikey = Helper.hexToBytes(prikeyStr);
        com.github.ontio.account.Account acct = createAccount(password, prikey, false, type, params);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont+Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportEncryptedPrikey(password);
        info.addressU160 = acct.getAddressU160().toString();
        storePrivateKey(identityPriKeyMap, info.ontid, password, Helper.toHexString(prikey));
        return info;
    }

    private IdentityInfo createIdentity(String password, byte[] prikey, KeyType type, Object[] params) throws Exception {
        com.github.ontio.account.Account acct = createAccount(password, prikey, false,type,params);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportEncryptedPrikey(password);
        info.addressU160 = acct.getAddressU160().toHexString();
        storePrivateKey(identityPriKeyMap, info.ontid, password, Helper.toHexString(prikey));
        return info;
    }

    public Account importAccount(String encryptedPrikey, String password, KeyType type, Object[] params) throws Exception {
        String prikey = com.github.ontio.account.Account.getPrivateKey(encryptedPrikey, password, type, params);
        AccountInfo info = createAccount(password, Helper.hexToBytes(prikey), type, params);
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, prikey);
        return getAccount(info.addressBase58);
    }
    public Account createAccount(String password) throws Exception {
        return createAccount(password,keyType,curveParaSpec);
    }

    public Account createAccount(String password, KeyType type, Object[] params) throws Exception {
        AccountInfo info = createAccount(password, ECC.generateKey(), type, params);
        return getAccount(info.addressBase58);
    }
    public Account createAccountFromPriKey(String password, String prikey) throws Exception {
        return createAccountFromPriKey(password,prikey,keyType,curveParaSpec);
    }
    public Account createAccountFromPriKey(String password, String prikey, KeyType type, Object[] params) throws Exception {
        AccountInfo info = createAccount(password, Helper.hexToBytes(prikey), type, params);
        return getAccount(info.addressBase58);
    }

    public AccountInfo createAccountInfo(String password, KeyType type, Object[] params) throws Exception {
        AccountInfo info = createAccount(password, ECC.generateKey(), type, params);
        return info;
    }

    private AccountInfo createAccount(String password, byte[] prikey, KeyType type, Object[] params) throws Exception {
        com.github.ontio.account.Account acct = createAccount(password, prikey, true, type, params);
        AccountInfo info = new AccountInfo();
        info.addressBase58 = Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportEncryptedPrikey(password);
        info.addressU160 = acct.getAddressU160().toHexString();
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, Helper.toHexString(prikey));
        return info;
    }
    public AccountInfo createAccountInfoFromPriKey(String password,String prikey)throws Exception {
        return createAccountFromInfoPriKey(password,prikey,keyType,curveParaSpec);
    }
    public AccountInfo createAccountFromInfoPriKey(String password, String prikey,KeyType type, Object[] params)throws Exception {
        return createAccount(password, Helper.hexToBytes(prikey), type, params);
    }

    public IdentityInfo createIdentityInfoFromPriKey(String password, String prikey) throws Exception{
        return createIdentity(password, Helper.hexToBytes(prikey), keyType,curveParaSpec);
    }

    public IdentityInfo createIdentityInfoFromPriKey(String password, String prikey,KeyType type, Object[] params) throws Exception{
        return createIdentity(password, Helper.hexToBytes(prikey), type, params);
    }

    public String privateKeyToWif(String privateKey, KeyType type, Object[] params) throws Exception {
        com.github.ontio.account.Account act = new com.github.ontio.account.Account(Helper.hexToBytes(privateKey), type, params);
        return act.exportWif();
    }

//    public ECPoint getPubkey(String pubkeyHexStr) {
//        ECPoint pubkey;
//        byte[] pubkeyBys = Helper.hexToBytes(pubkeyHexStr);
//        if (getAlgrithem().equals(KeyType.SM2.name())) {
//            pubkey = SM2Utils.decodePoint(pubkeyBys);
//        } else {
//            pubkey = ECC.secp256r1.getCurve().decodePoint(pubkeyBys);
//        }
//        return pubkey;
//    }

    public byte[] signatureData(com.github.ontio.account.Account acct, String str) throws SDKException {
        DataSignature sign = null;
        try {
            sign = new DataSignature(getSignatureScheme(), acct, str);
            return sign.signature();
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }

    public boolean verifySign(String pubkeyStr, byte[] data, byte[] signature) throws SDKException {
        DataSignature sign = null;
        try {
            sign = new DataSignature();
            return sign.verifySignature(new com.github.ontio.account.Account(false, Helper.hexToBytes(pubkeyStr)), data, signature);
        } catch (Exception e) {
            throw new SDKException(e);
        }
    }
    public com.github.ontio.account.Account getAccount(String address, String password) throws Exception {
        return getAccount(address,password,keyType,curveParaSpec);
    }
    public com.github.ontio.account.Account getAccount(String address, String password, KeyType type, Object[] params) throws Exception {
        address = address.replace(Common.didont, "");
        return getAccountByAddress(Address.decodeBase58(address), password, type, params);
    }

    private com.github.ontio.account.Account createAccount(String password, String prikey, KeyType type, Object[] params) throws Exception {
        return createAccount(password, Helper.hexToBytes(prikey), true, type, params);
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

    public AccountInfo getAccountInfo(String address, String password) throws Exception{
        return getAccountInfo(address,password,keyType,curveParaSpec);
    }
    public AccountInfo getAccountInfo(String address, String password, KeyType type, Object[] params) throws Exception {
        address = address.replace(Common.didont, "");
        AccountInfo info = new AccountInfo();
        com.github.ontio.account.Account acc = getAccountByAddress(Address.decodeBase58(address), password, type, params);
        info.addressBase58 = address;
        info.pubkey = Helper.toHexString(acc.serializePublicKey());
        info.setPrikey(Helper.toHexString(acc.serializePrivateKey()));
        info.encryptedPrikey = acc.exportEncryptedPrikey(password);
        info.setPriwif(acc.exportWif());
        info.addressU160 = acc.getAddressU160().toString();
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

    private com.github.ontio.account.Account createAccount(String password, byte[] privateKey, boolean saveAccountFlag, KeyType type, Object[] params) throws Exception {
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(privateKey, type, params);

        Account at = new Account();
        if (password != null) {
            at.key = account.exportEncryptedPrikey(password);
        } else {
            at.key = Helper.toHexString(account.serializePrivateKey());
        }
        at.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();
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

    private com.github.ontio.account.Account getAccountByAddress(Address address, String password, KeyType type, Object[] params) {
        try {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(address.toBase58())) {
                    String prikey = (String) acctPriKeyMap.get(e.address + "," + password);
                    if (prikey == null) {
                        prikey = com.github.ontio.account.Account.getPrivateKey(e.key, password, type, params);
                        storePrivateKey(acctPriKeyMap, e.address, password, prikey);
                    }
                    return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), type, params);
                }
            }

            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals(Common.didont + address.toBase58())) {
                    String prikey = (String) identityPriKeyMap.get(e.ontid + "," + password);
                    if (prikey == null) {
                        prikey = com.github.ontio.account.Account.getPrivateKey(e.controls.get(0).key, password, type, params);
                        storePrivateKey(identityPriKeyMap, e.ontid, password, prikey);
                    }
                    return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), type, params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SDKRuntimeException("getAccountByAddress err", e);
        }
        return null;
    }

}
