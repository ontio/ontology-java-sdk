package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class OntAssetTxTest {

    OntSdk ontSdk;
    Account info1 = null;
    Account info2 = null;
    Account info3 = null;
    String password = "111111";
    String wallet = "OntAssetTxTest.json";
    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        String restUrl = OntSdkTest.URL;
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile(wallet);
        info1 = ontSdk.getWalletMgr().createAccountFromPriKey(OntSdkTest.PASSWORD, OntSdkTest.PRIVATEKEY);
        info2 = ontSdk.getWalletMgr().createAccount(password);
        info3 = ontSdk.getWalletMgr().createAccount(password);
    }
    @After
    public void removeWallet(){
        File file = new File(wallet);
        if(file.exists()){
            if(file.delete()){
                System.out.println("delete wallet file success");
            }
        }
    }
    @Test
    public void sendTransfer() throws Exception {

        String res= ontSdk.nativevm().ont().sendTransfer(info1.address,password,info2.address,100L,ontSdk.DEFAULT_GAS_LIMIT,0);

        Assert.assertNotNull(res);
    }

    @Test
    public void makeTransfer() throws Exception {

        Transaction tx = ontSdk.nativevm().ont().makeTransfer(info1.address,password,info2.address,100L,ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(tx);
    }

    @Test
    public void sendTransferToMany() throws Exception {
        String hash1 = ontSdk.nativevm().ont().sendTransferToMany(info1.address,password,new String[]{info2.address,info3.address},new long[]{100L,200L},ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(hash1);
    }

    @Test
    public void sendTransferFromMany() throws Exception {

        String hash2 = ontSdk.nativevm().ont().sendTransferFromMany( new String[]{info1.address, info2.address}, new String[]{password, password}, info3.address, new long[]{1L, 2L},ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(hash2);
    }

    @Test
    public void sendApprove() throws Exception {
        ontSdk.nativevm().ont().sendApprove(info1.address,password,info2.address,10L,ontSdk.DEFAULT_GAS_LIMIT,0);
        long info1balance = ontSdk.nativevm().ont().queryBalanceOf(info1.address);
        long info2balance = ontSdk.nativevm().ont().queryBalanceOf(info2.address);
        Thread.sleep(6000);

        long allo = ontSdk.nativevm().ont().queryAllowance(info1.address,info2.address);
        Assert.assertTrue(allo > 0);
        ontSdk.nativevm().ont().sendTransferFrom(info2.address,password,info1.address,info2.address,10L,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long info1balance2 = ontSdk.nativevm().ont().queryBalanceOf(info1.address);
        long info2balance2 = ontSdk.nativevm().ont().queryBalanceOf(info2.address);

        Assert.assertTrue((info1balance - info1balance2) == 10);
        Assert.assertTrue((info2balance2 - info2balance) == 10);


    }

    @Test
    public void sendOngTransferFrom() throws Exception {

        String res = ontSdk.nativevm().ong().claimOng(info1.address,password,info2.address,10L,ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(res);
    }

    @Test
    public void queryTest() throws Exception {

        long decimal = ontSdk.nativevm().ont().queryDecimals();
        long decimal2 = ontSdk.nativevm().ong().queryDecimals();
        Assert.assertNotNull(decimal);
        Assert.assertNotNull(decimal2);

        String ontname = ontSdk.nativevm().ont().queryName();
        String ongname = ontSdk.nativevm().ong().queryName();
        Assert.assertNotNull(ontname);
        Assert.assertNotNull(ongname);

        String ontsym = ontSdk.nativevm().ont().querySymbol();
        String ongsym = ontSdk.nativevm().ong().querySymbol();
        Assert.assertNotNull(ontsym);
        Assert.assertNotNull(ongsym);

        long onttotal = ontSdk.nativevm().ont().queryTotalSupply();
        long ongtotal = ontSdk.nativevm().ong().queryTotalSupply();
        Assert.assertNotNull(onttotal);
        Assert.assertNotNull(ongtotal);
    }
}