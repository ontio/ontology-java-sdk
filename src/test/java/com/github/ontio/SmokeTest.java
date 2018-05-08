package com.github.ontio;

import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.manager.ConnectMgr;
import com.github.ontio.sdk.manager.OntAssetTx;
import com.github.ontio.sdk.manager.OntIdTx;
import com.github.ontio.sdk.manager.WalletMgr;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SmokeTest {
    private OntSdk ontSdk;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private OntIdTx ontIdTx;
    private ConnectMgr connectMgr;
    private OntAssetTx ontAssetTx;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        ontSdk.openWalletFile("wallet.json");
        ontSdk.setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        ontIdTx = ontSdk.getOntIdTx();
        connectMgr = ontSdk.getConnectMgr();
        ontAssetTx = ontSdk.getOntAssetTx();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sendUpdateAttribute() throws Exception {
        Identity identity = ontIdTx.sendRegister("123456");
        Account account = walletMgr.getAccount(identity.ontid,"123456");
        String prikey = account.exportCtrEncryptedPrikey("123456", 16384);
        Thread.sleep(6000);
        String string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains(identity.ontid));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Context", "claimlalala");
        jsonObject.put("Issuer", "issuerlalala");
        String txnId = ontIdTx.sendUpdateAttribute(identity.ontid,"123456",prikey.getBytes(),"Json".getBytes(), jsonObject.toJSONString().getBytes());
        assertNotNull(txnId);
        assertNotEquals(txnId,"");
        Thread.sleep(6000);
        string = ontIdTx.sendGetDDO(identity.ontid);
        assertTrue(string.contains("claimlalala"));
        assertTrue(string.contains("issuerlalala"));
    }

    @Test
    public void getBalance() throws Exception {
//        TA6qWdLo14aEve5azrYWWvMoGPrpczFfeW---1/gEPy/Uz3Eyl/sjoZ8JDymGX6hU/gi1ufUIg6vDURM= rich
//        TA4pSdTKm4hHtQJ8FbrCk9LZn7Uo96wrPC---Vz0CevSaI9/VNLx03XNEQ4Lrnnkkjo5aM5hdCuicsOE= poor1
//        TA5F9QefsyKvn5cH37VnP5snSru5ZCYHHC---OGaD13Sn/q9gIZ8fmOtclMi4yy34qq963wzpidYDX5k= poor2

        JSONObject balanceObj = (JSONObject) connectMgr.getBalance("TA5F9QefsyKvn5cH37VnP5snSru5ZCYHHC");
        assertNotNull(balanceObj);
        int ontBalance = balanceObj.getIntValue("ont");
        assertTrue(ontBalance >= 0);

    }

    @Test
    public void sendTransfer() throws Exception {
        final int amount = 1;
//        TA6qWdLo14aEve5azrYWWvMoGPrpczFfeW---1/gEPy/Uz3Eyl/sjoZ8JDymGX6hU/gi1ufUIg6vDURM= rich
//        TA4pSdTKm4hHtQJ8FbrCk9LZn7Uo96wrPC---Vz0CevSaI9/VNLx03XNEQ4Lrnnkkjo5aM5hdCuicsOE= poor
        final String richAddr = "TA6qWdLo14aEve5azrYWWvMoGPrpczFfeW";
        final String richKey = "1/gEPy/Uz3Eyl/sjoZ8JDymGX6hU/gi1ufUIg6vDURM=";
        final String poorAddr = "TA4pSdTKm4hHtQJ8FbrCk9LZn7Uo96wrPC";
        final String poorKey = "Vz0CevSaI9/VNLx03XNEQ4Lrnnkkjo5aM5hdCuicsOE=";
        JSONObject richBalanceObj = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObj = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalance = richBalanceObj.getIntValue("ont");
        int poorBalance = poorBalanceObj.getIntValue("ont");
        assertTrue(richBalance > 0);
        assertTrue(poorBalance >= 0);

        com.github.ontio.sdk.wallet.Account accountRich = walletMgr.importAccount(richKey,"123456",richAddr);
        com.github.ontio.sdk.wallet.Account accountPoor = walletMgr.importAccount(poorKey,"123456",poorAddr);

        String txnId = ontAssetTx.sendTransfer("ont",richAddr,"123456",poorAddr,amount);
        assertNotNull(txnId);
        assertNotEquals(txnId,"");

        Thread.sleep(6000);

        JSONObject richBalanceObjAfter = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObjAfter = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalanceAfter = richBalanceObjAfter.getIntValue("ont");
        int poorBalanceAfter = poorBalanceObjAfter.getIntValue("ont");

        assertTrue(richBalanceAfter == richBalance -amount);
        assertTrue(poorBalanceAfter == poorBalance +amount);

        String txnIdback = ontAssetTx.sendTransfer("ont",poorAddr,"123456",richAddr,amount);
        assertNotNull(txnIdback);
        assertNotEquals(txnIdback,"");

        Thread.sleep(6000);
        JSONObject richBalanceObjBack = (JSONObject) connectMgr.getBalance(richAddr);
        JSONObject poorBalanceObjBack = (JSONObject) connectMgr.getBalance(poorAddr);
        int richBalanceBack = richBalanceObjBack.getIntValue("ont");
        int poorBalanceBack = poorBalanceObjBack.getIntValue("ont");
        assertEquals(richBalanceBack,richBalance);
        assertEquals(poorBalanceBack,poorBalance);

    }
}