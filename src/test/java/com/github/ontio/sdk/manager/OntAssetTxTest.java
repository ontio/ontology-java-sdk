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

    Account payer;
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

        payer = ontSdk.getWalletMgr().createAccount(password);
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
        com.github.ontio.account.Account sendAcct = ontSdk.getWalletMgr().getAccount(info1.address,password,info1.getSalt());
        com.github.ontio.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
        String res= ontSdk.nativevm().ont().sendTransfer(sendAcct,info2.address,100L,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);


        Assert.assertNotNull(res);
    }

    @Test
    public void makeTransfer() throws Exception {

        Transaction tx = ontSdk.nativevm().ont().makeTransfer(info1.address,info2.address,100L,payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        Assert.assertNotNull(tx);
    }

    @Test
    public void sendApprove() throws Exception {
        com.github.ontio.account.Account sendAcct1 = ontSdk.getWalletMgr().getAccount(info1.address,password,info1.getSalt());
        com.github.ontio.account.Account sendAcct2 = ontSdk.getWalletMgr().getAccount(info2.address,password,info2.getSalt());
        com.github.ontio.account.Account payerAcct = ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt());
        ontSdk.nativevm().ont().sendApprove(sendAcct1,sendAcct2.getAddressU160().toBase58(),10L,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        long info1balance = ontSdk.nativevm().ont().queryBalanceOf(sendAcct1.getAddressU160().toBase58());
        long info2balance = ontSdk.nativevm().ont().queryBalanceOf(sendAcct2.getAddressU160().toBase58());
        Thread.sleep(6000);

        long allo = ontSdk.nativevm().ont().queryAllowance(sendAcct1.getAddressU160().toBase58(),sendAcct2.getAddressU160().toBase58());
        Assert.assertTrue(allo == 10);
        ontSdk.nativevm().ont().sendTransferFrom(sendAcct2,info1.address,sendAcct2.getAddressU160().toBase58(),10L,payerAcct,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long info1balance2 = ontSdk.nativevm().ont().queryBalanceOf(info1.address);
        long info2balance2 = ontSdk.nativevm().ont().queryBalanceOf(info2.address);

        Assert.assertTrue((info1balance - info1balance2) == 10);
        Assert.assertTrue((info2balance2 - info2balance) == 10);


    }

    @Test
    public void sendOngTransferFrom() throws Exception {
        String unboundOngStr = ontSdk.nativevm().ong().unboundOng(info1.address);
        long unboundOng = Long.parseLong(unboundOngStr);
        String res = ontSdk.nativevm().ong().withdrawOng(ontSdk.getWalletMgr().getAccount(info1.address,password,info1.getSalt()),info2.address,unboundOng/100,ontSdk.getWalletMgr().getAccount(payer.address,password,payer.getSalt()),ontSdk.DEFAULT_GAS_LIMIT,0);
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