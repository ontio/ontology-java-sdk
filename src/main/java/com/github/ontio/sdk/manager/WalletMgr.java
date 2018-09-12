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

import java.io.*;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 */
public class WalletMgr {
    private Wallet walletInMem;
    private Wallet walletFile;
    private SignatureScheme scheme = null;
    private String filePath = null;
    public WalletMgr(Wallet wallet,SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.walletInMem = wallet;
        this.walletFile = wallet;
    }
    public WalletMgr(String path, SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.filePath = path;
        File file = new File(filePath);
        if (!file.exists()) {
            walletInMem = new Wallet();
            walletInMem.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
            walletFile = new Wallet();
            file.createNewFile();
            writeWallet();
        }
        InputStream inputStream = new FileInputStream(filePath);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String text = new String(bytes);
        walletInMem = JSON.parseObject(text, Wallet.class);
        walletFile = JSON.parseObject(text, Wallet.class);
        if (walletInMem.getIdentities() == null) {
            walletInMem.setIdentities(new ArrayList<Identity>());
        }
        if (walletInMem.getAccounts() == null) {
            walletInMem.setAccounts(new ArrayList<Account>());
        }
        writeWallet();
    }

    private WalletMgr(String path, String label, String password, SignatureScheme scheme) throws Exception {
        this.scheme = scheme;
        this.filePath = path;
        File file = new File(filePath);
        if (!file.exists()) {
            walletInMem = new Wallet();
            walletInMem.setCreateTime(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date()));
            walletFile = new Wallet();
            file.createNewFile();
            createIdentity(label, password);
            writeWallet();
        }
        InputStream inputStream = new FileInputStream(filePath);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        String text = new String(bytes);
        walletInMem = JSON.parseObject(text, Wallet.class);
        walletFile = JSON.parseObject(text, Wallet.class);
        if (walletInMem.getIdentities() == null) {
            walletInMem.setIdentities(new ArrayList<Identity>());
        }
        if (walletInMem.getAccounts() == null) {
            walletInMem.setAccounts(new ArrayList<Account>());
        }
        if (walletInMem.getIdentities().size() == 0) {
            createIdentity(label, password);
            writeWallet();
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

    /**
     *
     * @return wallet file data
     */
    public Wallet getWalletFile() {
        return walletFile;
    }

    /**
     *
     * @return wallet in memory
     */
    public Wallet getWallet() {
        return walletInMem;
    }

    /**
     *  wallet in memory = wallet file data
     * @return
     */
    public Wallet resetWallet() {
        walletInMem = walletFile.clone();
        return walletInMem;
    }
    public Wallet saveWallet() throws Exception {
        return writeWallet();
    }
    public Wallet writeWallet() throws Exception {
        writeFile(filePath, JSON.toJSONString(walletInMem));
        walletFile = walletInMem.clone();
        return walletFile;
    }

    public SignatureScheme getSignatureScheme() {
        return scheme;
    }

    public void setSignatureScheme(SignatureScheme scheme) {
        this.scheme = scheme;
    }

    public Identity importIdentity(String encryptedPrikey, String password,byte[] salt, String address) throws Exception {
        return importIdentity("",encryptedPrikey,password,salt,address);
    }
    public Identity importIdentity(String label,String encryptedPrikey, String password,byte[] salt, String address) throws Exception {
        String prikey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(encryptedPrikey, password, address,salt, walletFile.getScrypt().getN(), scheme);
        IdentityInfo info = createIdentity(label,password,salt, Helper.hexToBytes(prikey));
        prikey = null;
        return getWallet().getIdentity(info.ontid);
    }


    public Identity createIdentity(String password) throws Exception {
        return createIdentity("",password);
    }
    public Identity createIdentity(String label,String password) throws Exception {
        IdentityInfo info = createIdentity(label,password, ECC.generateKey());
        return getWallet().getIdentity(info.ontid);
    }

    public Identity createIdentityFromPriKey(String label,String password, String prikey) throws Exception {
        IdentityInfo info = createIdentity(label,password, Helper.hexToBytes(prikey));
        return getWallet().getIdentity(info.ontid);
    }

    public Identity createIdentityFromPriKey(String password, String prikey) throws Exception {
        IdentityInfo info = createIdentity("", password, Helper.hexToBytes(prikey));
        prikey = null;
        return getWallet().getIdentity(info.ontid);
    }

    public IdentityInfo createIdentityInfo(String password) throws Exception {
        return createIdentityInfo("",password);
    }
    public IdentityInfo createIdentityInfo(String label,String password) throws Exception {
        IdentityInfo info = createIdentity(label,password, ECC.generateKey());
        return info;
    }


    public IdentityInfo getIdentityInfo(String ontid, String password,byte[] salt) throws Exception {
        com.github.ontio.account.Account acct = getAccountByAddressOrOntId(ontid, password,salt);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportGcmEncryptedPrikey(password, salt,walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toString();
        return info;
    }

    private IdentityInfo createIdentity(String label,String password, byte[] prikey) throws Exception {
        byte[] salt = ECC.generateKey(16);
        return createIdentity(label,password,salt,prikey);
    }
    private IdentityInfo createIdentity(String label,String password,byte[] salt, byte[] prikey) throws Exception {
        com.github.ontio.account.Account acct = createAccount(label,password,salt, prikey, false);
        IdentityInfo info = new IdentityInfo();
        info.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportGcmEncryptedPrikey(password, salt,walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toHexString();
        return info;
    }
    public Account importAccount(String encryptedPrikey, String password, String address,byte[] salt) throws Exception {
        return importAccount("",encryptedPrikey,password,address,salt);
    }

    public Account importAccount(String label,String encryptedPrikey, String password, String address,byte[] salt) throws Exception {
        String prikey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(encryptedPrikey, password, address,salt, walletFile.getScrypt().getN(), scheme);
        AccountInfo info = createAccountInfo(label,password, salt,Helper.hexToBytes(prikey));
        prikey = null;
        password = null;
        return getWallet().getAccount(info.addressBase58);
    }

    public void createAccounts(int count, String password) throws Exception {
        for (int i = 0; i < count; i++) {
            createAccount("", password);
        }
    }
    public Account createAccount(String password) throws Exception {
        Account account = createAccount("", password);
        return account;
    }
    public Account createAccount(String label,String password) throws Exception {
        AccountInfo info = createAccountInfo(label,password, ECC.generateKey());
        return getWallet().getAccount(info.addressBase58);
    }
    private AccountInfo createAccountInfo(String label,String password,byte[] prikey) throws Exception {
        byte[] salt = ECC.generateKey(16);
        return createAccountInfo(label,password,salt,prikey);
    }

    private AccountInfo createAccountInfo(String label,String password,byte[] salt, byte[] prikey) throws Exception {
        com.github.ontio.account.Account acct = createAccount(label,password,salt, prikey, true);
        new SecureRandom().nextBytes(prikey);
        AccountInfo info = new AccountInfo();
        info.addressBase58 = Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        info.pubkey = Helper.toHexString(acct.serializePublicKey());
        info.setPrikey(Helper.toHexString(acct.serializePrivateKey()));
        info.setPriwif(acct.exportWif());
        info.encryptedPrikey = acct.exportGcmEncryptedPrikey(password, salt,walletFile.getScrypt().getN());
        info.addressU160 = acct.getAddressU160().toHexString();
        return info;
    }

    public Account createAccountFromPriKey(String password, String prikey) throws Exception {
        AccountInfo info = createAccountInfo("",password, Helper.hexToBytes(prikey));
        return getWallet().getAccount(info.addressBase58);
    }
    public Account createAccountFromPriKey(String label,String password, String prikey) throws Exception {
        AccountInfo info = createAccountInfo(label,password, Helper.hexToBytes(prikey));
        return getWallet().getAccount(info.addressBase58);
    }
    public AccountInfo createAccountInfo(String password) throws Exception {
        return createAccountInfo("",password);
    }
    public AccountInfo createAccountInfo(String label,String password) throws Exception {
        AccountInfo info = createAccountInfo(label,password, ECC.generateKey());
        return info;
    }
    public AccountInfo createAccountInfoFromPriKey(String password, String prikey) throws Exception {
        return createAccountInfo("",password, Helper.hexToBytes(prikey));
    }
    public AccountInfo createAccountInfoFromPriKey(String label,String password, String prikey) throws Exception {
        return createAccountInfo(label,password, Helper.hexToBytes(prikey));
    }

    public IdentityInfo createIdentityInfoFromPriKey(String label,String password, String prikey) throws Exception {
        return createIdentity(label,password, Helper.hexToBytes(prikey));
    }

    public String privateKeyToWif(String privateKey) throws Exception {
        com.github.ontio.account.Account act = new com.github.ontio.account.Account(Helper.hexToBytes(privateKey), scheme);
        return act.exportWif();
    }
    public com.github.ontio.account.Account getAccount(String address, String password) throws Exception {
        return getAccount(address, password,getWallet().getAccount(address).getSalt());
    }
    public com.github.ontio.account.Account getAccount(String address, String password,byte[] salt) throws Exception {
        return getAccountByAddressOrOntId(address, password,salt);
    }

    public AccountInfo getAccountInfo(String address, String password,byte[] salt) throws Exception {
        address = address.replace(Common.didont, "");
        AccountInfo info = new AccountInfo();
        com.github.ontio.account.Account acc = getAccountByAddressOrOntId(address, password,salt);
        info.addressBase58 = address;
        info.pubkey = Helper.toHexString(acc.serializePublicKey());
        info.setPrikey(Helper.toHexString(acc.serializePrivateKey()));
        info.encryptedPrikey = acc.exportGcmEncryptedPrikey(password,salt, walletFile.getScrypt().getN());
        info.setPriwif(acc.exportWif());
        info.addressU160 = acc.getAddressU160().toString();
        return info;
    }


    private com.github.ontio.account.Account createAccount(String label, String password, byte[] salt,byte[] privateKey, boolean accountFlag) throws Exception {
        com.github.ontio.account.Account account = new com.github.ontio.account.Account(privateKey, scheme);
        Account acct;
        switch (scheme) {
            case SHA256WITHECDSA:
                acct = new Account("ECDSA", new Object[]{Curve.P256.toString()}, "aes-256-gcm", "SHA256withECDSA", "sha256");
                break;
            case SM3WITHSM2:
                acct = new Account("SM2", new Object[]{Curve.SM2P256V1.toString()}, "aes-256-gcm", "SM3withSM2", "sha256");
                break;
            default:
                throw new SDKException(ErrorCode.OtherError("scheme type error"));
        }
        if (password != null) {
            acct.key = account.exportGcmEncryptedPrikey(password,salt, walletFile.getScrypt().getN());
            password = null;
        } else {
            acct.key = Helper.toHexString(account.serializePrivateKey());
        }
        acct.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();
        if (label == null || label.equals("")) {
            String uuidStr = UUID.randomUUID().toString();
            label = uuidStr.substring(0, 8);
        }
        if (accountFlag) {
            for (Account e : walletInMem.getAccounts()) {
                if (e.address.equals(acct.address)) {
                    throw new SDKException(ErrorCode.ParamErr("wallet account exist"));
                }
            }
            if (walletInMem.getAccounts().size() == 0) {
                acct.isDefault = true;
                walletInMem.setDefaultAccountAddress(acct.address);
            }
            acct.label = label;
            acct.setSalt(salt);
            acct.setPublicKey(Helper.toHexString(account.serializePublicKey()));
            walletInMem.getAccounts().add(acct);
        } else {
            for (Identity e : walletInMem.getIdentities()) {
                if (e.ontid.equals(Common.didont + acct.address)) {
                    throw new SDKException(ErrorCode.ParamErr("wallet Identity exist"));
                }
            }
            Identity idt = new Identity();
            idt.ontid = Common.didont + acct.address;
            idt.label = label;
            if (walletInMem.getIdentities().size() == 0) {
                idt.isDefault = true;
                walletInMem.setDefaultOntid(idt.ontid);
            }
            idt.controls = new ArrayList<Control>();
            Control ctl = new Control(acct.key, "keys-1",Helper.toHexString(account.serializePublicKey()));
            ctl.setSalt(salt);
            ctl.setAddress(acct.address);
            idt.controls.add(ctl);
            walletInMem.getIdentities().add(idt);
        }
        return account;
    }

    private com.github.ontio.account.Account getAccountByAddressOrOntId(String addressOrOntId,String password,byte[] salt) throws Exception {
        try {
            if(addressOrOntId.startsWith(Common.didont)){
                for (Identity e : walletInMem.getIdentities()) {
                    if (e.ontid.equals(addressOrOntId)) {
                        String addr = e.ontid.replace(Common.didont, "");
                        String prikey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(e.controls.get(0).key, password, addr,salt, walletFile.getScrypt().getN(), scheme);
                        return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), scheme);
                    }
                }
            }else {
                for (Account e : walletInMem.getAccounts()) {
                    if (e.address.equals(addressOrOntId)) {
                        String prikey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(e.key, password, e.address,salt, walletFile.getScrypt().getN(), scheme);
                        return new com.github.ontio.account.Account(Helper.hexToBytes(prikey), scheme);
                    }
                }
            }

        } catch (Exception e) {
            throw new SDKException(ErrorCode.GetAccountByAddressErr);
        }
        throw new SDKException(ErrorCode.OtherError("Account null"));
    }


    public Identity getDefaultIdentity() {
        for (Identity e : getWallet().getIdentities()) {
            if (e.isDefault) {
                return e;
            }
        }
        return null;
    }
    public Account getDefaultAccount() {
        for (Account e : getWallet().getAccounts()) {
            if (e.isDefault) {
                return e;
            }
        }
        return null;
    }
}
