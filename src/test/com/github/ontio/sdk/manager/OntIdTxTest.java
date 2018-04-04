package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;
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
        ontSdk.setRestful("http://127.0.0.1:20384");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("OntIdTxTest.json");
        ontSdk.setCodeAddress("80a45524f3f6a5b98d633e5c7a7458472ec5d625");

        if(ontSdk.getWalletMgr().getIdentitys().size() < 1){
            ontSdk.getOntIdTx().sendRegister("passwordtest");
        }

        id = ontSdk.getWalletMgr().getIdentitys().get(0);
    }

    @Test
    public void setCodeAddress() {

        ontSdk.getOntIdTx().setCodeAddress("test");
    }

    @Test
    public void getCodeAddress() {
        System.out.println(ontSdk.getOntIdTx().getCodeAddress());
    }

    @Test
    public void sendRegister() throws Exception {

        System.out.println(ontSdk.getOntIdTx().sendRegister("passwordtest"));

    }

    @Test
    public void makeRegister() throws Exception {

        AccountInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        ontSdk.getOntIdTx().makeRegister(acctinfo);
    }

    @Test
    public void sendRegisterWithAttribute() throws Exception {

        String attri = "attri";
        Map recordMap = new HashMap();
        recordMap.put("key0", "world0");
        //recordMap.put("key1", i);
        recordMap.put("keyNum", 1234589);
        recordMap.put("key2", false);

        System.out.println(ontSdk.getOntIdTx().sendRegister("passwordtest",recordMap));
    }

    @Test
    public void sendRegisterWithIdentity() throws Exception {

        Identity id = ontSdk.getWalletMgr().createIdentity("passwordtest");

        System.out.println(ontSdk.getOntIdTx().sendRegister(id,"passwordtest"));
    }

    @Test
    public void sendRegisterByGuardian() {
    }

    @Test
    public void sendAddPubKey() throws Exception {
        AccountInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        String res = ontSdk.getOntIdTx().sendAddPubKey(id.ontid.replace(Common.didont,""),"passwordtest",acctinfo.pubkey);

    }

    @Test
    public void sendAddPubKeyWithRecovery() throws Exception {
        AccountInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        String res = ontSdk.getOntIdTx().sendAddPubKey("passwordtest",id.ontid.replace(Common.didont,""),acctinfo.pubkey,acctinfo.addressU160);
    }

    @Test
    public void sendAddPubKeyByGuardian() {

    }

    @Test
    public void sendRemovePubKey() throws Exception {

        String res = ontSdk.getOntIdTx().sendRemovePubKey(id.ontid.replace(Common.didont,""),"passwordtest","120202f0f26fd0f1f3792d55c870db4f83d38373a0f9ec29fb44b49687bb01cd81ea08");
    }

    @Test
    public void sendRemovePubKeyWithRecovery() throws Exception {

        ontSdk.getOntIdTx().sendRemovePubKey(id.ontid.replace(Common.didont,""),"passwordtest","".getBytes(),"");
    }

    @Test
    public void sendAddRecovery() throws Exception {
        AccountInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        ontSdk.getOntIdTx().sendAddRecovery(id.ontid.replace(Common.didont,""),"passwordtest",acctinfo.addressU160);
    }

    @Test
    public void sendChangeRecovery() throws Exception {
        AccountInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        ontSdk.getOntIdTx().sendChangeRecovery(id.ontid.replace(Common.didont,""),"passwordtest",acctinfo.addressU160,"f27a8d0c6e5b856a8c3b3e4900510f7d669ca9a3");
    }

    @Test
    public void sendUpdateAttribute() throws Exception {
        ontSdk.getOntIdTx().sendUpdateAttribute(id.ontid,"passwordtest","key".getBytes(),"byte".getBytes(),"value".getBytes());
    }

    @Test
    public void makeUpdateAttribute() throws Exception {

        ontSdk.getOntIdTx().makeUpdateAttribute(id.ontid,"passwordtest","key".getBytes(),"byte".getBytes(),"value".getBytes());
    }

    @Test
    public void sendUpdateAttributeArray() {
    }

    @Test
    public void sendGetDDO() throws Exception {
        System.out.println("DDO:" + ontSdk.getOntIdTx().sendGetDDO(id.ontid));
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
        String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest", "claim:context", map, map);
        System.out.println(claim);
    }

    @Test
    public void verifyOntIdClaim() throws Exception {
        String claim = "{\"Context\":\"claim:context\",\"Content\":{\"Issuer\":\"did:ont:TA7uH85yyLoBWiMGR8c9cC13zzHG5PNSro\",\"Subject\":\"did:ont:TA7UzGUwrw2Rn2Pt8VoZMmRA5C9QdMWx8R\"},\"Signature\":{\"Format\":\"pgp\",\"Value\":\"ARhUI9WWVbwoThqXGhWErRkUKm7epnq849y/xIJ4IE4Y+1FRSIBmF6NG4mv2R0dYBVni1Q9UGtACzBfXE6gj7gw=\",\"Algorithm\":\"ECDSAwithSHA256\"},\"Metadata\":{\"Issuer\":\"did:ont:TA7uH85yyLoBWiMGR8c9cC13zzHG5PNSro\",\"CreateTime\":\"2018-04-04T11:03:22Z\",\"Subject\":\"did:ont:TA7UzGUwrw2Rn2Pt8VoZMmRA5C9QdMWx8R\"},\"Id\":\"8c5511aa47adf621ce1cd9ca23b87c154d78a0b35e99ad8116774802ebafbe4b\"}";
        boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(claim);
        System.out.println(b);
    }


    @Test
    public void sendRemoveAttribute() throws Exception {

        ontSdk.getOntIdTx().sendRemoveAttribute(id.ontid,"passwordtest","key".getBytes());
    }

    @Test
    public void makeRemoveAttribute() throws Exception {
        ontSdk.getOntIdTx().makeRemoveAttribute(id.ontid,"passwordtest","key".getBytes());
    }

    @Test
    public void makeInvokeTransaction() throws Exception {
        List list = new ArrayList();
        list.add("test");
        AccountInfo acctinfo = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        ontSdk.getOntIdTx().makeInvokeTransaction(list,acctinfo);
    }

    @Test
    public void makeInvokeTransactionWithAddr() throws Exception {
        List list = new ArrayList();
        list.add("test");
        ontSdk.getOntIdTx().makeInvokeTransaction(list,id.ontid,"passwordtest");
    }
}