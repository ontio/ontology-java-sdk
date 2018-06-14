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
    public void getAccount() throws Exception {
        Account acct = ontSdk.getWalletMgr().getWallet().getAccount(acct1.address);
        Assert.assertNotNull(acct);

        ontSdk.getWalletMgr().getWallet().setDefaultIdentity(id1.ontid);
        ontSdk.getWalletMgr().getWallet().setDefaultIdentity(1);
        ontSdk.getWalletMgr().getWallet().setDefaultAccount(acct1.address);
        ontSdk.getWalletMgr().getWallet().setDefaultAccount(1);
        Identity did = ontSdk.getWalletMgr().getWallet().getIdentity(id1.ontid);
        Assert.assertNotNull(did);
        boolean b = ontSdk.getWalletMgr().getWallet().removeIdentity(id1.ontid);
        Assert.assertTrue(b);

        boolean b2 = ontSdk.getWalletMgr().getWallet().removeAccount(acct1.address);
        Assert.assertTrue(b2);


    }


}