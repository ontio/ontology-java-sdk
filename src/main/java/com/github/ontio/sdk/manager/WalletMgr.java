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

import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.crypto.*;
import com.github.ontio.sdk.exception.*;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.common.Common;
import com.github.ontio.core.DataSignature;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import org.bouncycastle.math.ec.ECPoint;

import java.io.*;
import java.text.SimpleDateFormat;
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
    public static boolean priKeyStoreInMem = true;//for dont need decode every time
    public WalletMgr(Wallet wallet,SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.wallet = wallet;
        this.walletFile = wallet;
    }
    public WalletMgr(String path, SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.filePath = path;
        File file = new File(filePath);
        if (!file.exists()) {
            wallet = new Wallet();
            wallet.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
            walletFile = new Wallet();
            file.createNewFile();
            writeWallet();
        }
        InputStream inputStream = new FileInputStream(filePath);
        String text = IOUtils.toString(inputStream);
        wallet = JSON.parseObject(text, Wallet.class);
        walletFile = JSON.parseObject(text, Wallet.class);
        if (wallet.getIdentities() == null) {
            wallet.setIdentities(new ArrayList<>());
        }
        if (wallet.getAccounts() == null) {
            wallet.setAccounts(new ArrayList<>());
        }
        writeWallet();
    }

    private WalletMgr(String path, String label, String password, SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.filePath = path;
        File file = new File(filePath);
        if (!file.exists()) {
            wallet = new Wallet();
            wallet.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
            walletFile = new Wallet();
            file.createNewFile();
            createIdentity(label, password);
            writeWallet();
        }
        InputStream inputStream = new FileInputStream(filePath);
        String text = IOUtils.toString(inputStream);
        wallet = JSON.parseObject(text, Wallet.class);
        walletFile = JSON.parseObject(text, Wallet.class);
        if (wallet.getIdentities() == null) {
            wallet.setIdentities(new ArrayList<>());
        }
        if (wallet.getAccounts() == null) {
            wallet.setAccounts(new ArrayList<>());
        }
        if (getIdentitys().size() == 0) {
            createIdentity(label, password);
            writeWallet();
            return;
        }
        Identity identity = getDefaultIdentity();
        if (identity != null) {
            String addr = identity.ontid.replace(Common.didont, "");
            String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(identity.controls.get(0).key, password, addr, walletFile.getScrypt().getN(), scheme);
            storePrivateKey(identityPriKeyMap, identity.ontid, password, prikey);
        }
    }

    private static void writeFile(String filePath, String sets) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    public Wallet openWallet() {
        return walletFile;
    }

    public Wallet getWallet() {
        return wallet;
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
        if (priKeyStoreInMem) {
            map.put(key + "," + password, prikey);
        }
    }
    public Identity importIdentity(String encryptedPrikey, String password, String address) throws Exception {
        return importIdentity(encryptedPrikey,password,address);
    }
    public Identity importIdentity(String label,String encryptedPrikey, String password, String address) throws Exception {
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(encryptedPrikey, password, address, walletFile.getScrypt().getN(), scheme);
        IdentityInfo info = createIdentity(label,password, Helper.hexToBytes(prikey));
        storePrivateKey(identityPriKeyMap, info.ontid, password, prikey);
        return getIdentity(info.ontid);
    }

    public Identity createIdentity(String password) throws Exception {
        return createIdentity("",password);
    }
    public Identity createIdentity(String label,String password) throws Exception {
        IdentityInfo info = createIdentity(label,password, ECC.generateKey());
        return getIdentity(info.ontid);
    }

    public Identity createIdentityFromPriKey(String label,String password, String prikey) throws Exception {
        IdentityInfo info = createIdentity(label,password, Helper.hexToBytes(prikey));
        return getIdentity(info.ontid);
    }
    public IdentityInfo createIdentityInfo(String password) throws Exception {
        return createIdentityInfo("",password);
    }
    public IdentityInfo createIdentityInfo(String label,String password) throws Exception {
        IdentityInfo info = createIdentity(label,password, ECC.generateKey());
        return info;
    }


    public IdentityInfo getIdentityInfo(String ontid, String password) throws Exception {
        com.github.ontio.account.Account acct = getAccountByAddress(Address.decodeBase58(ontid.replace(Common.didont, "")), password);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toString();
        return info;
    }

    private IdentityInfo createIdentity(String label,String password, byte[] prikey) throws Exception {
        com.github.ontio.account.Account acct = createAccount(label,password, prikey, false);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toHexString();
        storePrivateKey(identityPriKeyMap, info.ontid, password, Helper.toHexString(prikey));
        return info;
    }
    public Account importAccount(String encryptedPrikey, String password, String address) throws Exception {
        return importAccount("",encryptedPrikey,password,address);
    }

    public Account importAccount(String label,String encryptedPrikey, String password, String address) throws Exception {
        String prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(encryptedPrikey, password, address, walletFile.getScrypt().getN(), scheme);
        AccountInfo info = createAccount(label,password, Helper.hexToBytes(prikey));
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, prikey);
        return getAccount(info.addressBase58);
    }

    public Account createAccount(String password) throws Exception {
        AccountInfo info = createAccount("",password, ECC.generateKey());
        return getAccount(info.addressBase58);
    }
    public Account createAccount(String label,String password) throws Exception {
        AccountInfo info = createAccount(label,password, ECC.generateKey());
        return getAccount(info.addressBase58);
    }


    private AccountInfo createAccount(String label,String password, byte[] prikey) throws Exception {
        com.github.ontio.account.Account acct = createAccount(label,password, prikey, true);
        AccountInfo info = new AccountInfo();
        info.addressBase58 = Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toHexString();
        storePrivateKey(acctPriKeyMap, info.addressBase58, password, Helper.toHexString(prikey));
        return info;
    }

    public Account getDefaultAccount() {
        for (Account e : wallet.getAccounts()) {
            if (e.isDefault) {
                return e;
            }
        }
        return null;
    }
    public Account createAccountFromPriKey(String password, String prikey) throws Exception {
        AccountInfo info = createAccount("",password, Helper.hexToBytes(prikey));
        return getAccount(info.addressBase58);
    }
    public Account createAccountFromPriKey(String label,String password, String prikey) throws Exception {
        AccountInfo info = createAccount(label,password, Helper.hexToBytes(prikey));
        return getAccount(info.addressBase58);
    }
    public AccountInfo createAccountInfo(String password) throws Exception {
        return createAccountInfo(password);
    }
    public AccountInfo createAccountInfo(String label,String password) throws Exception {
        AccountInfo info = createAccount(label,password, ECC.generateKey());
        return info;
    }
    public AccountInfo createAccountInfoFromPriKey(String password, String prikey) throws Exception {
        return createAccount("",password, Helper.hexToBytes(prikey));
    }
    public AccountInfo createAccountInfoFromPriKey(String label,String password, String prikey) throws Exception {
        return createAccount(label,password, Helper.hexToBytes(prikey));
    }

    public IdentityInfo createIdentityInfoFromPriKey(String label,String password, String prikey) throws Exception {
        return createIdentity(label,password, Helper.hexToBytes(prikey));
    }

    public String privateKeyToWif(String privateKey) throws Exception {
        com.github.ontio.account.Account act = new com.github.ontio.account.Account(Helper.hexToBytes(privateKey), scheme);
        return act.exportWif();
    }


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
        address = address.replace(Common.didont, "");
        return getAccountByAddress(Address.decodeBase58(address), password);
    }

    private com.github.ontio.account.Account createAccount(String label,String password, String prikey) throws Exception {
        return createAccount(label,password, Helper.hexToBytes(prikey), true);
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


    public AccountInfo getAccountInfo(String address, String password) throws Exception {
        address = address.replace(Common.didont, "");
        AccountInfo info = new AccountInfo();
        com.github.ontio.account.Account acc = getAccountByAddress(Address.decodeBase58(address), password);
        info.addressBase58 = address;
        info.pubkey = Helper.toHexString(acc.serializePublicKey());
        info.setPrikey(Helper.toHexString(acc.serializePrivateKey()));
        info.encryptedPrikey = acc.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
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

    private com.github.ontio.account.Account createAccount(String label, String password, byte[] privateKey, boolean saveAccountFlag) throws Exception {
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(privateKey, scheme);
        Account acct;
        switch (scheme) {
            case SHA256WITHECDSA:
                acct = new Account("ECDSA", new Object[]{Curve.P256.toString()}, "aes-256-ctr", "SHA256withECDSA", "sha256");
                break;
            case SM3WITHSM2:
                acct = new Account("SM2", new Object[]{Curve.SM2P256V1.toString()}, "aes-256-ctr", "SM3withSM2", "sha256");
                break;
            default:
                throw new SDKException(ErrorCode.OtherError("scheme type error"));
        }
        if (password != null) {
            acct.key = account.exportCtrEncryptedPrikey(password, walletFile.getScrypt().getN());
        } else {
            acct.key = Helper.toHexString(account.serializePrivateKey());
        }
        acct.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();
        if (label == null || label.equals("")) {
            String uuidStr = UUID.randomUUID().toString();
            label = uuidStr.substring(0, 8);
        }
        if (saveAccountFlag) {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(acct.address)) {
                    throw new SDKException(ErrorCode.ParamErr("wallet account exist"));
                }
            }
            if (wallet.getAccounts().size() == 0) {
                acct.isDefault = true;
                wallet.setDefaultAccountAddress(acct.address);
            }
            acct.label = label;
            acct.passwordHash = Helper.toHexString(Digest.sha256(password.getBytes()));
            wallet.getAccounts().add(acct);
        } else {
            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals(Common.didont + acct.address)) {
                    return account;
                }
            }
            Identity idt = new Identity();
            idt.ontid = Common.didont + acct.address;
            idt.label = label;
            if (wallet.getIdentities().size() == 0) {
                idt.isDefault = true;
                wallet.setDefaultOntid(idt.ontid);
            }
            idt.controls = new ArrayList<Control>();
            Control ctl = new Control(acct.key, "");
            idt.controls.add(ctl);
            wallet.getIdentities().add(idt);
        }
        return account;
    }

    private com.github.ontio.account.Account getAccountByAddress(Address address, String password) throws Exception {
        try {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(address.toBase58())) {
                    String prikey = (String) acctPriKeyMap.get(e.address + "," + password);
                    if (prikey == null) {
                        prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(e.key, password, e.address, walletFile.getScrypt().getN(), scheme);
                        storePrivateKey(acctPriKeyMap, e.address, password, prikey);
                    }
                    return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), scheme);
                }
            }

            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals(Common.didont + address.toBase58())) {
                    String prikey = (String) identityPriKeyMap.get(e.ontid + "," + password);
                    if (prikey == null) {
                        String addr = e.ontid.replace(Common.didont, "");
                        prikey = com.github.ontio.account.Account.getCtrDecodedPrivateKey(e.controls.get(0).key, password, addr, walletFile.getScrypt().getN(), scheme);
                        storePrivateKey(identityPriKeyMap, e.ontid, password, prikey);
                    }
                    return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), scheme);
                }
            }
        } catch (Exception e) {
            throw e;
        }
        throw new SDKRuntimeException(ErrorCode.GetAccountByAddressErr);
    }

}
