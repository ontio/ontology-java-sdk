package com.github.ontio.smartcontract.nativevm;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NativeOntIdTxTest {
    OntSdk ontSdk;
    String password = "111111";
    Account payerAcc;
    Identity identity;
    String walletFile = "NativeOntIdTxTest.json";
    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(OntSdkTest.URL);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile(walletFile);
        ontSdk.setSignatureScheme(SignatureScheme.SM3WITHSM2);
        payerAcc = ontSdk.getWalletMgr().createAccount(password);

        identity = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

    }

    @After
    public void removeWallet(){
        File file = new File(walletFile);
        if(file.exists()){
            if(file.delete()){
                System.out.println("delete wallet file success");
            }
        }
    }

    @Test
    public void sendRegister() throws Exception {
        Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);

        Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Identity identity3 = ontSdk.getWalletMgr().createIdentity(password);
        Map  attributeMap = new HashMap();
        attributeMap.put("key2","value2");
        ontSdk.nativevm().ontId().sendRegisterWithAttrs(identity3,password,attributeMap,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(identity.ontid));

        String dd02 = ontSdk.nativevm().ontId().sendGetDDO(identity3.ontid);
        Assert.assertTrue(dd02.contains("key2"));

        String keystate = ontSdk.nativevm().ontId().sendGetKeyState(identity.ontid,1);
        Assert.assertNotNull(keystate);

        //merkleproof
        Object merkleproof = ontSdk.nativevm().ontId().getMerkleProof(tx.hash().toHexString());
        boolean b = ontSdk.nativevm().ontId().verifyMerkleProof(JSONObject.toJSONString(merkleproof));
        Assert.assertTrue(b);

        //claim
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Issuer", identity.ontid);
        map.put("Subject", identity2.ontid);

        Map clmRevMap = new HashMap();
        clmRevMap.put("typ","AttestContract");
        clmRevMap.put("addr",identity.ontid.replace(Common.didont,""));

        String claim = ontSdk.nativevm().ontId().createOntIdClaim(identity.ontid,password, "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
        boolean b2 = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
        Assert.assertTrue(b2);
    }
    @Test
    public void sendAddPubkey() throws Exception {
        IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo(password);
        IdentityInfo info2 = ontSdk.getWalletMgr().createIdentityInfo(password);
        Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(identity.ontid,password,info.pubkey,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);

        ontSdk.nativevm().ontId().sendAddPubKey(identity.ontid,password,info2.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.pubkey));
        Assert.assertTrue(ddo.contains(info2.pubkey));

        String publikeys = ontSdk.nativevm().ontId().sendGetPublicKeys(identity.ontid);
        Assert.assertNotNull(publikeys);

        Transaction tx2 = ontSdk.nativevm().ontId().makeRemovePubKey(identity.ontid,password,info.pubkey,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx2,identity.ontid,password);
        ontSdk.addSign(tx2,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx2);

        ontSdk.nativevm().ontId().sendRemovePubKey(identity.ontid,password,info2.pubkey,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        String ddo3 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertFalse(ddo3.contains(info.pubkey));
        Assert.assertFalse(ddo3.contains(info2.pubkey));
    }

    @Test
    public void sendAddAttributes() throws Exception {
        Map attrsMap = new HashMap<>();
        attrsMap.put("key1","value1");
        Transaction tx = ontSdk.nativevm().ontId().makeAddAttributes(identity.ontid,password,attrsMap,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);

        Map attrsMap2 = new HashMap<>();
        attrsMap2.put("key99","value99");
        ontSdk.nativevm().ontId().sendAddAttributes(identity.ontid,password,attrsMap2,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains("key1"));
        Assert.assertTrue(ddo.contains("key99"));

        String attribute = ontSdk.nativevm().ontId().sendGetAttributes(identity.ontid);
        Assert.assertTrue(attribute.contains("key1"));

        Transaction tx2= ontSdk.nativevm().ontId().makeRemoveAttribute(identity.ontid,password,"key1",payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx2,identity.ontid,password);
        ontSdk.addSign(tx2,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx2);

        ontSdk.nativevm().ontId().sendRemoveAttribute(identity.ontid,password,"key99",payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertFalse(ddo2.contains("key1"));
        Assert.assertFalse(ddo2.contains("key99"));

    }

    @Test
    public void sendAddRecovery() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Identity identity2 = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity2,password,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);

        AccountInfo info = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(identity.ontid,password,info.addressBase58,payerAcc.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);

        ontSdk.nativevm().ontId().sendAddRecovery(identity2.ontid,password,info.addressBase58,payerAcc.address,password,ontSdk.DEFAULT_GAS_LIMIT,0);

        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.addressBase58));
        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity2.ontid);
        Assert.assertTrue(ddo2.contains(info.addressBase58));

        AccountInfo info2 = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx2 = ontSdk.nativevm().ontId().makeChangeRecovery(identity.ontid,info2.addressBase58,info.addressBase58,password,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx2,info.addressBase58,password);

        ontSdk.nativevm().ontId().sendChangeRecovery(identity2.ontid,info2.addressBase58,info.addressBase58,password,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

        String ddo3 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo3.contains(info.addressBase58));
        String ddo4 = ontSdk.nativevm().ontId().sendGetDDO(identity2.ontid);
        Assert.assertTrue(ddo4.contains(info2.addressBase58));
    }
}