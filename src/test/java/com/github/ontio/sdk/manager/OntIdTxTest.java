package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class OntIdTxTest {
    OntSdk ontSdk;

    Identity id;

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://127.0.0.1:20334");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("OntIdDemoSSS.json");
        ontSdk.setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");

        if(ontSdk.getWalletMgr().getIdentitys().size() < 1){
            ontSdk.getOntIdTx().sendRegister("passwordtest");
        }
        id = ontSdk.getWalletMgr().getIdentitys().get(0);
    }

    @Test
    public void setCodeAddress() {

        ontSdk.getOntIdTx().setCodeAddress("test");
        Assert.assertEquals("test",ontSdk.getOntIdTx().getCodeAddress());
    }

    @Test
    public void getCodeAddress() {
        String codeAddress = ontSdk.getOntIdTx().getCodeAddress();
        Assert.assertEquals("80b0cc71bda8653599c5666cae084bff587e2de1",codeAddress);
    }

    @Test
    public void sendRegister() throws Exception {
        Identity id = ontSdk.getOntIdTx().sendRegister("passwordtest");
        Assert.assertNull(id);

    }

    @Test
    public void makeRegister() throws Exception {

        IdentityInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        Transaction tx = ontSdk.getOntIdTx().makeRegister(acctinfo);
        Assert.assertNull(tx);
    }

    @Test
    public void sendRegisterWithAttribute() throws Exception {

        String attri = "attri";
        Map recordMap = new HashMap();
        recordMap.put("key0", "world0");
        //recordMap.put("key1", i);
        recordMap.put("keyNum", 1234589);
        recordMap.put("key2", false);
        Identity id = ontSdk.getOntIdTx().sendRegister("passwordtest",recordMap);
        Assert.assertNull(id);
    }

    @Test
    public void sendRegisterWithIdentity() throws Exception {

        Identity id = ontSdk.getWalletMgr().createIdentity("passwordtest");

        Identity did = ontSdk.getOntIdTx().sendRegister(id,"passwordtest");
        Assert.assertNull(did);
    }

    @Test
    public void sendRegisterByGuardian() {
    }

    @Test
    public void sendAddPubKey() throws Exception {
        IdentityInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        String res = ontSdk.getOntIdTx().sendAddPubKey(id.ontid.replace(Common.didont,""),"passwordtest",acctinfo.pubkey);
        Assert.assertNull(res);

    }

    @Test
    public void sendAddPubKeyWithRecovery() throws Exception {
        IdentityInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        String res = ontSdk.getOntIdTx().sendAddPubKey("passwordtest",id.ontid.replace(Common.didont,""),acctinfo.pubkey,acctinfo.addressU160);
        Assert.assertNull(res);
    }

    @Test
    public void sendRemovePubKey() throws Exception {

        String res = ontSdk.getOntIdTx().sendRemovePubKey(id.ontid.replace(Common.didont,""),"passwordtest","120202f0f26fd0f1f3792d55c870db4f83d38373a0f9ec29fb44b49687bb01cd81ea08");
        Assert.assertNull(res);
    }

    @Test
    public void sendRemovePubKeyWithRecovery() throws Exception {

        String res = ontSdk.getOntIdTx().sendRemovePubKey(id.ontid.replace(Common.didont,""),"passwordtest","".getBytes(),"");
        Assert.assertNull(res);
    }

    @Test
    public void sendAddRecovery() throws Exception {
        IdentityInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        String res = ontSdk.getOntIdTx().sendAddRecovery(id.ontid.replace(Common.didont,""),"passwordtest",acctinfo.addressU160);
        Assert.assertNull(res);
    }

    @Test
    public void sendChangeRecovery() throws Exception {
        IdentityInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        String res = ontSdk.getOntIdTx().sendChangeRecovery(id.ontid.replace(Common.didont,""),"passwordtest",acctinfo.addressU160,"f27a8d0c6e5b856a8c3b3e4900510f7d669ca9a3");
        Assert.assertNull(res);
    }

    @Test
    public void sendUpdateAttribute() throws Exception {
        String res = ontSdk.getOntIdTx().sendUpdateAttribute(id.ontid,"passwordtest","key".getBytes(),"byte".getBytes(),"value".getBytes());
        Assert.assertNull(res);
    }

    @Test
    public void makeUpdateAttribute() throws Exception {

        Transaction tx = ontSdk.getOntIdTx().makeUpdateAttribute(id.ontid,"passwordtest","key".getBytes(),"byte".getBytes(),"value".getBytes());
        Assert.assertNull(tx);
    }

    @Test
    public void sendUpdateAttributeArray() {
    }

    @Test
    public void sendGetDDO() throws Exception {
        String res = ontSdk.getOntIdTx().sendGetDDO(id.ontid);
        Assert.assertNull(res);
    }

    @Test
    public void getPubKeys() throws Exception {
        ontSdk.getOntIdTx().getPubKeys(id.ontid);
    }

    @Test
    public void createOntIdClaim() throws Exception {

        List<Identity> dids = ontSdk.getWalletMgr().getIdentitys();
        if (dids.size() < 2) {
            ontSdk.getOntIdTx().sendRegister("passwordtest");
            ontSdk.getOntIdTx().sendRegister("passwordtest");
            dids = ontSdk.getWalletMgr().getIdentitys();
            Thread.sleep(6000);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Issuer", dids.get(0).ontid);
        map.put("Subject", dids.get(1).ontid);
        String claim = ontSdk.getOntIdTx().createOntIdClaim(dids.get(0).ontid,"passwordtest", "claim:context", map, map);
        Assert.assertNull(claim);
    }

    @Test
    public void verifyOntIdClaim() throws Exception {
        String claim = "{\"Context\":\"claim:context\",\"Content\":{\"Issuer\":\"did:ont:TA7uH85yyLoBWiMGR8c9cC13zzHG5PNSro\",\"Subject\":\"did:ont:TA7UzGUwrw2Rn2Pt8VoZMmRA5C9QdMWx8R\"},\"Signature\":{\"Format\":\"pgp\",\"Value\":\"ARhUI9WWVbwoThqXGhWErRkUKm7epnq849y/xIJ4IE4Y+1FRSIBmF6NG4mv2R0dYBVni1Q9UGtACzBfXE6gj7gw=\",\"Algorithm\":\"ECDSAwithSHA256\"},\"Metadata\":{\"Issuer\":\"did:ont:TA7uH85yyLoBWiMGR8c9cC13zzHG5PNSro\",\"CreateTime\":\"2018-04-04T11:03:22Z\",\"Subject\":\"did:ont:TA7UzGUwrw2Rn2Pt8VoZMmRA5C9QdMWx8R\"},\"Id\":\"8c5511aa47adf621ce1cd9ca23b87c154d78a0b35e99ad8116774802ebafbe4b\"}";
        boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(claim);
        Assert.assertTrue(b);
    }


    @Test
    public void sendRemoveAttribute() throws Exception {

        String res = ontSdk.getOntIdTx().sendRemoveAttribute(id.ontid,"passwordtest","key".getBytes());
        Assert.assertNotNull(res);
    }

    @Test
    public void makeRemoveAttribute() throws Exception {
        Transaction tx = ontSdk.getOntIdTx().makeRemoveAttribute(id.ontid,"passwordtest","key".getBytes());
        Assert.assertNotNull(tx);
    }

    @Test
    public void makeInvokeTransaction() throws Exception {
        List list = new ArrayList();
        list.add("test");
        IdentityInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        Transaction tx = ontSdk.getOntIdTx().makeInvokeTransaction(list,acctinfo);
        Assert.assertNotNull(tx);
    }

    @Test
    public void makeInvokeTransactionWithAddr() throws Exception {
        List list = new ArrayList();
        list.add("test");
        Transaction tx = ontSdk.getOntIdTx().makeInvokeTransaction(list,id.ontid,"passwordtest");
        Assert.assertNotNull(tx);
    }

    @Test
    public void sendGetPublicKeyId() throws Exception {
        String res = ontSdk.getOntIdTx().sendGetPublicKeyId(id.ontid,"passwordtest");
        Assert.assertNotNull(res);
    }

    @Test
    public void sendGetPublicKeyStatus() throws Exception {
        AccountInfo accountInfo = ontSdk.getWalletMgr().getAccountInfo(id.ontid.replace(Common.didont,""),"passwordtest");
        String res = ontSdk.getOntIdTx().sendGetPublicKeyStatus(id.ontid,"passwordtest", Helper.hexToBytes("01"));
        Assert.assertNotNull(res);
    }

    @Test
    public void getProof() throws Exception {
        Object obj = ontSdk.getOntIdTx().getProof("237d65f620f241c41ee80da348ebdd530d35dfb8c1662050d5c7fbda531c29eb");
        Assert.assertNotNull(obj);
    }

    @Test
    public void verifyMerkleProof() throws Exception {
        boolean b = ontSdk.getOntIdTx().verifyMerkleProof("{\"Proof\":{\"Type\":\"MerkleProof\",\"TxnHash\":\"237d65f620f241c41ee80da348ebdd530d35dfb8c1662050d5c7fbda531c29eb\",\"BlockHeight\":804}}");
        Assert.assertTrue(b);
    }

}