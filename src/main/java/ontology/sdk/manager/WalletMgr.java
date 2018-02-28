package ontology.sdk.manager;

import ontology.account.Acct;
import ontology.account.KeyType;
import ontology.common.Common;
import ontology.common.Helper;
import ontology.common.UInt160;
import ontology.core.DeployCodeTransaction;
import ontology.core.InvokeCodeTransaction;
import ontology.core.SignatureContext;
import ontology.crypto.ECC;
import ontology.crypto.sm.SM2Utils;
import ontology.core.DataSignature;
import ontology.sdk.exception.SDKException;
import ontology.sdk.exception.ParamCheck;
import ontology.sdk.info.account.AccountInfo;
import ontology.sdk.wallet.*;
import ontology.sdk.exception.Error;
import ontology.sdk.exception.AccountException;
import ontology.sdk.wallet.Account;
import ontology.sdk.wallet.Contract;
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

    public Identity importIdentity(String encrypted, String password) throws Exception {
        String prikey = Acct.getPrivateKey(encrypted, password);
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

    private AccountInfo createIdentity(String password, byte[] prikey) {
        Acct acct = createAccount(password, prikey, false);
        AccountInfo info = new AccountInfo();
        info.address = ontology.core.contract.Contract.createSignatureContract(acct.publicKey).address();
        info.pubkey = Helper.toHexString(acct.publicKey.getEncoded(true));
        info.setPrikey(Helper.toHexString(acct.privateKey));
        info.setPriwif(acct.export());
        info.encryptedprikey = acct.export(password);
        info.pkhash = acct.scriptHash.toString();
        storePrivateKey(identityPriKeyMap, "did:ont:" + info.address, password, Helper.toHexString(prikey));
        return info;
    }

    public Account importAccount(String encrypted, String password) throws Exception {
        String prikey = Acct.getPrivateKey(encrypted, password);
        AccountInfo info = createAccount(password, Helper.hexToBytes(prikey));
        storePrivateKey(acctPriKeyMap, info.address, password, prikey);
        return getAccount(info.address);
    }

    public Account createAccount(String password) {
        AccountInfo info = createAccount(password, ECC.generateKey());
        return getAccount(info.address);
    }

    public AccountInfo createAccountInfo(String password) {
        AccountInfo info = createAccount(password, ECC.generateKey());
        return info;
    }

    private AccountInfo createAccount(String password, byte[] prikey) {
        Acct acct = createAccount(password, prikey, true);
        AccountInfo info = new AccountInfo();
        info.address = ontology.core.contract.Contract.createSignatureContract(acct.publicKey).address();
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
    public String signatureData(String password, ontology.core.Transaction tx) throws SDKException {
        SignatureContext context = new SignatureContext(tx);
        if (tx instanceof DeployCodeTransaction) {
            tx.scripts = context.getScripts();
            return Helper.toHexString(tx.toArray());
        }
        boolean sign = sign(password, context);
        if (sign && context.isCompleted()) {
            tx.scripts = context.getScripts();
        } else {
            throw new SDKException(Error.getDescSigIncomplete("Signature incompleted"));
        }
        return Helper.toHexString(tx.toArray());
    }

    // 交易签名
    public String signatureData(ontology.core.Transaction tx) throws SDKException {
        SignatureContext context = new SignatureContext(tx);
        if (tx instanceof DeployCodeTransaction) {
            tx.scripts = context.getScripts();
            return Helper.toHexString(tx.toArray());
        }
        boolean sign = sign(null, context);
        if (sign && context.isCompleted()) {
            tx.scripts = context.getScripts();
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

    public Acct getAccount(String password, String address) throws SDKException {
        if (!ParamCheck.isValidAddress(address)) {
            throw new SDKException(Error.getDescAddrError(String.format("%s=%s", "address", address)));
        }
        return getAccountByScriptHash(password, Common.toScriptHash(address));
    }

    public Acct createAccount(String password, String prikey) {
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
    public AccountInfo getAccountInfo(String password, String address) throws SDKException {
        address = address.replace("did:ont:", "");
        if (!ParamCheck.isValidAddress(address)) {
            throw new SDKException(Error.getDescAddrError(String.format("%s=%s", "address", address)));
        }
        AccountInfo info = new AccountInfo();
        ontology.core.contract.Contract con = getContract(password, address);
        Acct acc = getAccountByScriptHash(password, Common.toScriptHash(address));
        info.address = con.address();
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
        at.address = ontology.core.contract.Contract.createSignatureContract(account.publicKey).address();
        ontology.core.contract.Contract c = ontology.core.contract.Contract.createSignatureContract(account.publicKey);
        at.contract = new Contract();
        at.contract.script = Helper.toHexString(c.redeemScript);
        at.contract.parameters = c.parameterList;
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


//    private List<ontology.core.contract.Contract> getContracts() {
//        try {
//            List<ontology.core.contract.Contract> contracts = new ArrayList<ontology.core.contract.Contract>();
//            for (Account e : wallet.getAccounts()) {
//                 //contracts.add(e.contract);
//            }
//            return contracts;
//        } catch (Exception e) {
//            throw new AccountException(Error.getDescDatabaseError("getContract(s) err"), e);
//        }
//    }

    private ontology.core.contract.Contract getContract(String password, String addr) {
        try {
            for (Account e : wallet.getAccounts()) {
                if (addr.equals(e.address)) {
                    return ontology.core.contract.Contract.create(new UInt160(), e.contract.parameters, Helper.hexToBytes(e.contract.script));
                }
            }
            for (Identity e : wallet.getIdentities()) {
                if (("did:ont:" + addr).equals(e.ontid)) {
                    String prikey = (String) identityPriKeyMap.get(e.ontid+","+password);
                    if (prikey == null ) {
                        prikey = Acct.getPrivateKey(e.controls.get(0).key, password);
                        storePrivateKey(identityPriKeyMap, e.ontid, password, prikey);
                    }
                    Acct account = new Acct(Helper.hexToBytes(prikey), Algrithem);
                    ontology.core.contract.Contract c = ontology.core.contract.Contract.createSignatureContract(account.publicKey);
                    return ontology.core.contract.Contract.create(new UInt160(), c.parameterList, Helper.hexToBytes(Helper.toHexString(c.redeemScript)));
                }
            }
        } catch (Exception e) {
            throw new AccountException(Error.getDescArgError("getContract err"), e);
        }
        throw new AccountException(Error.getDescArgError("getContract err"));
    }

    private ontology.core.contract.Contract getContract(String password, UInt160 scriptHash) {
        try {
            return getContract(password, Common.toAddress(scriptHash));
        } catch (Exception e) {
            throw new AccountException(Error.getDescDatabaseError("getContract err"), e);
        }
    }

//    private Acct getAccountByScriptHash(UInt160 scriptHash) {
//        try {
//            for (Account e : wallet.getAccounts()) {
//                if (e.address.equals(Common.toAddress(scriptHash))) {
//                    return new Acct(Helper.hexToBytes(e.key), getAlgrithem());
//                }
//            }
//
//            for (Identity e : wallet.getIdentities()) {
//                if (e.ontid.equals("did:ont:" + Common.toAddress(scriptHash))) {
//                    return new Acct(Helper.hexToBytes(e.controls.get(0).key), getAlgrithem());
//                }
//            }
//        } catch (Exception e) {
//            throw new AccountException(Error.getDescDatabaseError("getAccountByScriptHash err"), e);
//        }
//        return null;
//    }

    private Acct getAccountByScriptHash(String password, UInt160 scriptHash) {
        try {
            for (Account e : wallet.getAccounts()) {
                if (e.address.equals(Common.toAddress(scriptHash))) {
                    String prikey = (String) acctPriKeyMap.get(e.address+","+password);
                    if (prikey == null ) {
                        prikey = Acct.getPrivateKey(e.key, password);
                        storePrivateKey(acctPriKeyMap, e.address, password, prikey);
                    }
                    return new Acct(Helper.hexToBytes(prikey), getAlgrithem());
                }
            }

            for (Identity e : wallet.getIdentities()) {
                if (e.ontid.equals("did:ont:" + Common.toAddress(scriptHash))) {
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

    // 签名
    public boolean sign(String password, SignatureContext context) {
        boolean fSuccess = false;
        for (UInt160 scriptHash : context.scriptHashes) {
            ontology.core.contract.Contract contract = getContract(password, scriptHash);
            if (contract == null) {
                continue;
            }
            Acct account = null;
            if (password != null) {
                account = getAccountByScriptHash(password, scriptHash);
            }
            if (account == null) {
                continue;
            }
            byte[] signature = context.signable.sign(account, Algrithem);
            fSuccess |= context.add(contract, account.publicKey, signature);
        }
        return fSuccess;
    }

}
