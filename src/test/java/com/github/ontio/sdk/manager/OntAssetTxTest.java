package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.github.ontio.OntSdk;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class OntAssetTxTest {

    OntSdk ontSdk;
    Account info1 = null;
    Account info2 = null;
    Account info3 = null;
    String password = "111111";

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        String restUrl = "http://127.0.0.1:20334";
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("OntAssetTxTest.json");
        if (ontSdk.getWalletMgr().getAccounts().size() < 3) {
            info1 = ontSdk.getWalletMgr().createAccountFromPriKey(password, "54ca4db481966046b15f8d15ff433e611c49ab8e68a279ebf579e4cfd108196d");
            info2 = ontSdk.getWalletMgr().createAccount(password);
            info3 = ontSdk.getWalletMgr().createAccount(password);
            ontSdk.getWalletMgr().writeWallet();
        }

        info1 = ontSdk.getWalletMgr().getAccounts().get(0);
        info2 = ontSdk.getWalletMgr().getAccounts().get(1);
        info3 = ontSdk.getWalletMgr().getAccounts().get(2);
    }

    @Test
    public void sendTransfer() throws Exception {

        String res= ontSdk.nativevm().ont().sendTransfer("ont",info1.address,password,info2.address,100L,0);
        Assert.assertNotNull(res);
    }

    @Test
    public void makeTransfer() throws Exception {

        Transaction tx = ontSdk.nativevm().ont().makeTransfer("ont",info1.address,password,info2.address,100L,0);
        Assert.assertNotNull(tx);
    }

    @Test
    public void sendTransferToMany() throws Exception {
        String hash1 = ontSdk.nativevm().ont().sendTransferToMany("ont",info1.address,password,new String[]{info2.address,info3.address},new long[]{100L,200L},0);
        Assert.assertNotNull(hash1);
    }

    @Test
    public void sendTransferFromMany() throws Exception {

        String hash2 = ontSdk.nativevm().ont().sendTransferFromMany("ont", new String[]{info1.address, info2.address}, new String[]{password, password}, info3.address, new long[]{1L, 2L},0);
        Assert.assertNotNull(hash2);
    }

    @Test
    public void sendApprove() throws Exception {
        ontSdk.nativevm().ont().sendApprove("ont",info1.address,password,info2.address,10L,0);
        long info1balance = ontSdk.nativevm().ont().queryBalanceOf("ont",info1.address);
        long info2balance = ontSdk.nativevm().ont().queryBalanceOf("ont",info2.address);
        Thread.sleep(6000);

System.out.println(info1.address);
        ontSdk.nativevm().ont().sendTransferFrom("ont",info2.address,password,info1.address,info2.address,10L,0);
        Thread.sleep(6000);
        long info1balance2 = ontSdk.nativevm().ont().queryBalanceOf("ont",info1.address);
        long info2balance2 = ontSdk.nativevm().ont().queryBalanceOf("ont",info2.address);

        Assert.assertTrue((info1balance - info1balance2) == 10);
        Assert.assertTrue((info2balance2 - info2balance) == 10);


    }

    @Test
    public void sendOngTransferFrom() throws Exception {

        String res = ontSdk.nativevm().ont().claimOng(info1.address,password,info2.address,10L,0);
        Assert.assertNotNull(res);
    }

    @Test
    public void queryTest() throws Exception {

        long decimal = ontSdk.nativevm().ont().queryDecimals("ont");
        long decimal2 = ontSdk.nativevm().ont().queryDecimals("ong");
        Assert.assertNotNull(decimal);
        Assert.assertNotNull(decimal2);

        String ontname = ontSdk.nativevm().ont().queryName("ont");
        String ongname = ontSdk.nativevm().ont().queryName("ong");
        Assert.assertNotNull(ontname);
        Assert.assertNotNull(ongname);

        String ontsym = ontSdk.nativevm().ont().querySymbol("ont");
        String ongsym = ontSdk.nativevm().ont().querySymbol("ong");
        Assert.assertNotNull(ontsym);
        Assert.assertNotNull(ongsym);

        long onttotal = ontSdk.nativevm().ont().queryTotalSupply("ont");
        long ongtotal = ontSdk.nativevm().ont().queryTotalSupply("ong");
        Assert.assertNotNull(onttotal);
        Assert.assertNotNull(ongtotal);
    }
}