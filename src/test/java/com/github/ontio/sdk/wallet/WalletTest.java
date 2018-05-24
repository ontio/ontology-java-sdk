package com.github.ontio.sdk.wallet;

import com.github.ontio.OntSdk;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class WalletTest {

    OntSdk ontSdk;
    Identity id1;
    Identity id2;
    Account acct1;
    Account acct2;

    String walletFile = "WalletTest.json";

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.openWalletFile(walletFile);


        id1 = ontSdk.getWalletMgr().createIdentity("passwordtest");
        id2 = ontSdk.getWalletMgr().createIdentity("passwordtest");

        acct1 = ontSdk.getWalletMgr().createAccount("passwordtest");
        acct2 = ontSdk.getWalletMgr().createAccount("passwordtest");
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
    public void removeAccount() {

        boolean b = ontSdk.getWalletMgr().getWallet().removeAccount(acct1.address);
        Assert.assertTrue(b);
    }

    @Test
    public void getAccount() {
        Account acct = ontSdk.getWalletMgr().getAccount(acct1.address);
        Assert.assertNotNull(acct);

    }

    @Test
    public void removeIdentity() {

        boolean b = ontSdk.getWalletMgr().getWallet().removeIdentity(id1.ontid);
        Assert.assertTrue(b);


    }

    @Test
    public void getIdentity() {

        Identity did = ontSdk.getWalletMgr().getIdentity(id1.ontid);
        Assert.assertNotNull(did);
    }

    @Test
    public void setDefaultAccount() throws Exception {

        ontSdk.getWalletMgr().getWallet().setDefaultAccount(1);


    }

    @Test
    public void setDefaultAccountByAddress() {
        ontSdk.getWalletMgr().getWallet().setDefaultAccount(acct1.address);
    }

    @Test
    public void setDefaultIdentity() throws Exception {
        ontSdk.getWalletMgr().getWallet().setDefaultIdentity(1);
    }

    @Test
    public void setDefaultIdentityByOntid() {
        ontSdk.getWalletMgr().getWallet().setDefaultIdentity(id1.ontid);
    }
}