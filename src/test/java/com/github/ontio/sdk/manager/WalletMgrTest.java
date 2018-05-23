package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.smartcontract.neovm.OntIdTx;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class WalletMgrTest {
    private OntSdk ontSdk;
    private WalletMgr walletMgr;
    private Wallet wallet;
    private OntIdTx ontIdTx;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://polaris1.ont.io:20334");
        ontSdk.openWalletFile("wallet.json");
        ontSdk.vm().setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        ontIdTx = ontSdk.neovm().ontId();

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void openWallet() {
        ontSdk.openWalletFile("wallet.json");
        walletMgr = ontSdk.getWalletMgr();
        assertNotNull(walletMgr);
    }

    @Test
    public void getWallet() {
    }

    @Test
    public void writeWallet() throws Exception {
        walletMgr.writeWallet();
        File f = new File("wallet.json");
        boolean isExist = f.exists() && !f.isDirectory();
        assertTrue(isExist);
    }

    @Test
    public void createIdentity() throws Exception {
         Identity identity = walletMgr.createIdentity("123456");
         assertNotNull(identity);
         assertNotNull(identity.ontid);
         assertNotEquals(identity.ontid,"");
    }

    @Test
    public void getAccount() throws Exception {
        Identity identity =  walletMgr.createIdentity("123456");
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,"12345");
        assertNotNull(account);
    }

    @Test
    public void sendRegisterPreExec() throws Exception {
        Identity identity = walletMgr.createIdentity("123456");
        Identity identity1 = ontIdTx.sendRegisterPreExec(identity,"123456",0);
        assertNotNull(identity1);
        assertEquals(identity.ontid,identity1.ontid);
    }

    @Test
    public void importIdentity() throws Exception {
        List<Identity> identities = wallet.getIdentities();
        identities.clear();
        walletMgr.writeWallet();
        assertEquals(identities.size(), 0);

        Identity identity = walletMgr.createIdentity("123456");
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,"123456");
        String prikeyStr = account.exportCtrEncryptedPrikey("123456",16384);
        assertTrue(identities.size() == 1);
        identities.clear();
        walletMgr.writeWallet();
        assertTrue(identities.size() == 0);


        String addr = identity.ontid.substring(8);
        walletMgr.importIdentity(prikeyStr,"123456",addr);
        assertTrue(identities.size() == 1);
        Identity identity1 = identities.get(0);
        assertEquals(identity.ontid,identity1.ontid);
    }

    @Test
    public void importAccount() throws Exception {
        List<Account> accounts = walletMgr.getAccounts();
        accounts.clear();
        assertEquals(accounts.size(), 0);
        walletMgr.writeWallet();

        Account account = walletMgr.createAccount("123456");
        com.github.ontio.account.Account accountDiff = walletMgr.getAccount(account.address,"123456");
        String prikeyStr = accountDiff.exportCtrEncryptedPrikey("123456",16384);
       assertTrue(accounts.size() == 1);
       accounts.clear();
       assertTrue(accounts.size() == 0);
       walletMgr.writeWallet();

       Account account1 = walletMgr.importAccount(prikeyStr,"123456",account.address);
       assertTrue(accounts.size() == 1);
       assertEquals(account.address, account1.address);

    }
}