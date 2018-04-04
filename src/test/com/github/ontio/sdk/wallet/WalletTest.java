package com.github.ontio.sdk.wallet;

import com.github.ontio.OntSdk;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WalletTest {

    OntSdk ontSdk;
    Identity id1;
    Identity id2;
    Account acct1;
    Account acct2;

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://127.0.0.1:20384");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("WalletTest.json");

        if(ontSdk.getWalletMgr().getIdentitys().size() < 2){
            ontSdk.getWalletMgr().createIdentity("passwordtest");
            ontSdk.getWalletMgr().createIdentity("passwordtest");

            ontSdk.getWalletMgr().createAccount("passwordtest");
            ontSdk.getWalletMgr().createAccount("passwordtest");

            ontSdk.getWalletMgr().writeWallet();
        }

        id1 = ontSdk.getWalletMgr().getIdentitys().get(0);
        id2 = ontSdk.getWalletMgr().getIdentitys().get(1);
        acct1 = ontSdk.getWalletMgr().getAccounts().get(0);
        acct2 = ontSdk.getWalletMgr().getAccounts().get(1);

    }


    @Test
    public void removeAccount() {
        System.out.println(ontSdk.getWalletMgr().getWallet().removeAccount(acct1.address));
    }

    @Test
    public void getAccount() {
        System.out.println(ontSdk.getWalletMgr().getAccount(acct1.address));
    }

    @Test
    public void removeIdentity() {

        System.out.println(ontSdk.getWalletMgr().getWallet().removeIdentity(id1.ontid));


    }

    @Test
    public void getIdentity() {
        System.out.println(ontSdk.getWalletMgr().getIdentity(id1.ontid));
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