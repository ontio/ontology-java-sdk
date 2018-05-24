package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.smartcontract.nativevm.OntId;
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
    private OntId ontIdTx;

    String password = "111111";

    Account payer;

    String walletFile = "wallet.json";

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(OntSdkTest.URL);
        ontSdk.openWalletFile(walletFile);
        walletMgr = ontSdk.getWalletMgr();
        wallet = walletMgr.getWallet();
        ontIdTx = ontSdk.nativevm().ontId();

        payer = ontSdk.getWalletMgr().createAccount(password);

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
         Identity identity = walletMgr.createIdentity(password);
         assertNotNull(identity);
         assertNotNull(identity.ontid);
         assertNotEquals(identity.ontid,"");
    }

    @Test
    public void getAccount() throws Exception {
        Identity identity =  walletMgr.createIdentity(password);
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,password);
        assertNotNull(account);
    }

    @Test
    public void importIdentity() throws Exception {
        List<Identity> identities = wallet.getIdentities();
        identities.clear();
        walletMgr.writeWallet();
        assertEquals(identities.size(), 0);

        Identity identity = walletMgr.createIdentity(password);
        com.github.ontio.account.Account account = walletMgr.getAccount(identity.ontid,password);
        String prikeyStr = account.exportCtrEncryptedPrikey(password,16384);
        assertTrue(identities.size() == 1);
        identities.clear();
        walletMgr.writeWallet();
        assertTrue(identities.size() == 0);


        String addr = identity.ontid.substring(8);
        walletMgr.importIdentity(prikeyStr,password,addr);
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

        Account account = walletMgr.createAccount(password);
        com.github.ontio.account.Account accountDiff = walletMgr.getAccount(account.address,password);
        String prikeyStr = accountDiff.exportCtrEncryptedPrikey(password,16384);
       assertTrue(accounts.size() == 1);
       accounts.clear();
       assertTrue(accounts.size() == 0);
       walletMgr.writeWallet();

       Account account1 = walletMgr.importAccount(prikeyStr,password,account.address);
       assertTrue(accounts.size() == 1);
       assertEquals(account.address, account1.address);

    }
}