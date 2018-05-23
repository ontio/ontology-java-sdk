package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class NativeOntIdTxTest {
    OntSdk ontSdk;
    String password = "111111";
    Account payerAcc;
    Identity identity;
    @Before
    public void setUp() throws Exception {
//        String ip = "http://polaris1.ont.io";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("NativeOntIdTxTest.json");
        ontSdk.setSignatureScheme(SignatureScheme.SM3WITHSM2);
        payerAcc = ontSdk.getWalletMgr().createAccount(password);
        if(ontSdk.getWalletMgr().getIdentitys().size() < 1){
            identity = ontSdk.getWalletMgr().createIdentity(password);
            ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,0);
            Thread.sleep(6000);
        }else{
            identity = ontSdk.getWalletMgr().getIdentitys().get(0);
        }

    }

    @Test
    public void sendRegister() throws Exception {
        IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo(password);
        Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(identity.ontid));
    }
    @Test
    public void sendAddPubkey() throws Exception {
        IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo(password);
        Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(identity.ontid,password,info.pubkey,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.pubkey));


        Transaction tx2 = ontSdk.nativevm().ontId().makeRemovePubKey(identity.ontid,password,info.pubkey,payerAcc.address,0);
        ontSdk.signTx(tx2,identity.ontid,password);
        ontSdk.addSign(tx2,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx2);
        Thread.sleep(6000);
        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertFalse(ddo2.contains(info.pubkey));
    }

    @Test
    public void sendAddAttributes() throws Exception {
        Map attrsMap = new HashMap<>();
        attrsMap.put("key1","value1");
        Transaction tx = ontSdk.nativevm().ontId().makeAddAttributes(identity.ontid,password,attrsMap,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains("key1"));



        Transaction tx2= ontSdk.nativevm().ontId().makeRemoveAttribute(identity.ontid,password,"key1",payerAcc.address,0);
        ontSdk.signTx(tx2,identity.ontid,password);
        ontSdk.addSign(tx2,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx2);
        Thread.sleep(6000);

        String ddo2 = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertFalse(ddo2.contains("key1"));


    }

    @Test
    public void sendAddRecovery() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,0);
        Thread.sleep(6000);

        AccountInfo info = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(identity.ontid,password,info.addressBase58,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);
        Thread.sleep(6000);
        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info.addressBase58));
    }

    @Test
    public void sendChangeRecovery() throws Exception {

        Identity identity = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.nativevm().ontId().sendRegister(identity,password,payerAcc.address,password,0);
        Thread.sleep(6000);

        AccountInfo info = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(identity.ontid,password,info.addressBase58,payerAcc.address,0);
        ontSdk.signTx(tx, identity.ontid,password);
        ontSdk.addSign(tx,payerAcc.address,password);
        ontSdk.getConnect().sendRawTransaction(tx);
        Thread.sleep(6000);

        AccountInfo info2 = ontSdk.getWalletMgr().createAccountInfo(password);

        Transaction tx2 = ontSdk.nativevm().ontId().makeChangeRecovery(identity.ontid,info2.addressBase58,info.addressBase58,password,0);
        ontSdk.signTx(tx2, identity.ontid,password);
        ontSdk.addSign(tx2,info.addressBase58,password);
        ontSdk.getConnect().sendRawTransaction(tx2);

        Thread.sleep(6000);

        String ddo = ontSdk.nativevm().ontId().sendGetDDO(identity.ontid);
        Assert.assertTrue(ddo.contains(info2.addressBase58));
        Assert.assertFalse(ddo.contains(info.addressBase58));
    }

}