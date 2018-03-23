package com.github.ontio.sdk.manager;

import com.github.ontio.common.Helper;
import com.github.ontio.common.Address;
import com.github.ontio.core.SignatureContext;
import com.github.ontio.core.Transaction;
import com.github.ontio.core.payload.DeployCodeTransaction;
import com.github.ontio.crypto.sm.SM2Utils;
import com.github.ontio.sdk.exception.AccountException;
import com.github.ontio.sdk.exception.Error;
import com.github.ontio.sdk.info.account.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.account.Acct;
import com.github.ontio.account.KeyType;
import com.github.ontio.common.Common;
import com.github.ontio.crypto.ECC;
import com.github.ontio.core.DataSignature;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.exception.ParamCheck;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IOUtils;
import org.bouncycastle.math.ec.ECPoint;

import java.io.*;
import java.util.*;

/**
 * Created by zx on 2018/1/11.
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
    public WalletMgr(String path,String password) {
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
            if(getIdentitys().size() == 0){
                createIdentity(password);
                writeWallet();
                return;
            }
            Identity identity = getDefaultIdentity();
            if(identity != null) {
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

    //TODO synchronized
    public Wallet writeWallet() throws Exception {
        Common.writeFile(filePath, JSON.toJSONString(wallet));
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
        storePrivateKey(identityPriKeyMap, "did:ont:" + info.address, password, prikey);
        return getIdentity("did:ont:" + info.address);
    }

    public Identity createIdentity(String password) {
        AccountInfo info = createIdentity(password, ECC.generateKey());
        return getIdentity("did:ont:" + info.address);
    }

    public AccountInfo createIdentityInfo(String password) {
        AccountInfo info = createIdentity(password, ECC.generateKey());
        return info;
    }
    public AccountInfo getIdentityInfo(String ontid,String password) {
        String prikeyStr = (String)identityPriKeyMap.get(ontid+","+password);
        if(prikeyStr == null){
            return null;
        }
        byte[] prikey = Helper.hexToBytes(prikeyStr);
        Acct acct = createAccount(password, prikey, false);
        AccountInfo info = new AccountInfo();
        info.address = Address.addressFromPubKey(acct.publicKey).toBase58();
        info.pubkey = Helper.toHexString(acct.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acct.privateKey));
        info.setPriwif(acct.export());
        info.encryptedprikey = acct.export(password);
        info.pkhash = acct.scriptHash.toString();
        storePrivateKey(identityPriKeyMap, "did:ont:" + info.address, password, Helper.toHexString(prikey));
        return info;
    }
    private AccountInfo createIdentity(String password, byte[] prikey) {
        Acct acct = createAccount(password, prikey, false);
        AccountInfo info = new AccountInfo();
        info.address = Address.addressFromPubKey(acct.publicKey).toBase58();
        info.pubkey = Helper.toHexString(acct.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acct.privateKey));
        info.setPriwif(acct.export());
        info.encryptedprikey = acct.export(password);
        info.pkhash = acct.scriptHash.toString();
        storePrivateKey(identityPriKeyMap, "did:ont:" + info.address, password, Helper.toHexString(prikey));
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
    public Account createAccountFromPrikey(String password,String prikey) {
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
        info.setPriwif(acct.export());
        info.encryptedprikey = acct.export(password);
        info.pkhash = acct.scriptHash.toString();
        storePrivateKey(acctPriKeyMap, info.address, password, Helper.toHexString(prikey));
        return info;
    }

    //根据私钥创建账户
    public AccountInfo createAccountFromPrivateKey(String password, String prikey) {
        return createAccount(password, Helper.hexToBytes(prikey));
    }

    public AccountInfo createIdentityFromPrivateKey(String password, String prikey) {
        return createIdentity(password, Helper.hexToBytes(prikey));
    }

    public String privateKeyToWif(String privateKey) {
        Acct act = new Acct(Helper.hexToBytes(privateKey), getAlgrithem());
        return act.export();
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

    // 交易签名
    public String signatureData(String password, Transaction tx) throws SDKException {
        SignatureContext context = new SignatureContext(tx);
        if (tx instanceof DeployCodeTransaction) {
            tx.sigs = context.getSigs();
            return Helper.toHexString(tx.toArray());
        }
        boolean sign = sign(password, context);
        if (sign && context.isCompleted()) {
            tx.sigs = context.getSigs();
        } else {
            throw new SDKException(Error.getDescSigIncomplete("Signature incompleted"));
        }
        return Helper.toHexString(tx.toArray());
    }
    // 交易签名
    public String signatureData(String[] password, Transaction tx) throws SDKException {
        SignatureContext context = new SignatureContext(tx);
        if (tx instanceof DeployCodeTransaction) {
            tx.sigs = context.getSigs();
            return Helper.toHexString(tx.toArray());
        }
        boolean sign = sign(password, context);
        if (sign && context.isCompleted()) {
            tx.sigs = context.getSigs();
        } else {
            throw new SDKException(Error.getDescSigIncomplete("Signature incompleted"));
        }
        return Helper.toHexString(tx.toArray());
    }

    // 交易签名
    public String signatureData(Transaction tx) throws SDKException {
        SignatureContext context = new SignatureContext(tx);
        if (tx instanceof DeployCodeTransaction) {
            tx.sigs = context.getSigs();;
            return Helper.toHexString(tx.toArray());
        }
        boolean sign = sign("", context);
        if (sign && context.isCompleted()) {
            tx.sigs = context.getSigs();
        } else {
            throw new SDKException(Error.getDescSigIncomplete("Signature incompleted"));
        }
        return Helper.toHexString(tx.toArray());
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

    public Acct getAccount(String address,String password) throws SDKException {
        if (!ParamCheck.isValidAddress(address)) {
            throw new SDKException(Error.getDescAddrError(String.format("%s=%s", "address", address)));
        }
        return getAccountByScriptHash( Address.decodeBase58(address),password);
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

    // 获取账户信息
    public AccountInfo getAccountInfo(String address,String password) throws SDKException {
        address = address.replace("did:ont:", "");
        if (!ParamCheck.isValidAddress(address)) {
            throw new SDKException(Error.getDescAddrError(String.format("%s=%s", "address", address)));
        }
        AccountInfo info = new AccountInfo();
        Acct acc = getAccountByScriptHash(Address.decodeBase58(address),password);
        info.address = address;
        info.pubkey = Helper.toHexString(acc.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acc.privateKey));
        info.encryptedprikey = acc.export(password);
        info.setPriwif(acc.export());
        info.pkhash = acc.scriptHash.toString();
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
            at.key = account.export(password);
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
                if (e.ontid.equals("did:ont:" + at.address)) {
                    return account;
                }
            }
            Identity idt = new Identity();
            if (wallet.getIdentities().size() == 0) {
                idt.isDefault = true;
            }
            idt.ontid = "did:ont:" + at.address;
            idt.controls = new ArrayList<Control>();
            Control ctl = new Control(at.key, "");
            idt.controls.add(ctl);
            wallet.getIdentities().add(idt);
        }
        return account;
    }

    private Acct getAccountByScriptHash(Address scriptHash, String password) {
        try {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(scriptHash.toBase58())) {
                    String prikey = (String) acctPriKeyMap.get(e.address+","+password);
                    if (prikey == null ) {
                        prikey = Acct.getPrivateKey(e.key, password);
                        storePrivateKey(acctPriKeyMap, e.address, password, prikey);
                    }
                    return new Acct(Helper.hexToBytes(prikey), getAlgrithem());
                }
            }

            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals("did:ont:" + scriptHash.toBase58())) {
                    String prikey = (String) identityPriKeyMap.get(e.ontid+","+password);
                    if (prikey == null ) {
                        prikey = Acct.getPrivateKey(e.controls.get(0).key, password);
                        storePrivateKey(identityPriKeyMap, e.ontid, password, prikey);
                    }
                    return new Acct(Helper.hexToBytes(prikey), getAlgrithem());
                }
            }
        } catch (Exception e) {
            throw new AccountException(Error.getDescDatabaseError("getAccountByScriptHash err"), e);
        }
        return null;
    }

    public boolean sign(String password, SignatureContext context) {
        boolean fSuccess = false;
        int i = 0;
        for (Address scriptHash : context.addressU160) {
            Acct account = null;
            if (password != null) {
                account = getAccountByScriptHash(scriptHash,password);
            }
            if (account == null) {
                continue;
            }
            byte[] signature = context.signable.sign(account, Algrithem);
            fSuccess |= context.add(scriptHash, account.publicKey, signature);
        }
        return fSuccess;
    }

    public boolean sign(String[] password, SignatureContext context) {
        boolean fSuccess = false;
        for (Address scriptHash : context.addressU160) {
            for (int i = 0; i < password.length; i++) {
                Acct account = getAccountByScriptHash(scriptHash, password[i]);
                if (account == null) {
                    continue;
                }
                byte[] signature = context.signable.sign(account, Algrithem);
                fSuccess |= context.add(scriptHash, account.publicKey, signature);
            }
        }
        return fSuccess;
    }

}
