package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.wallet.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OntAssetTxTest {

    OntSdk ontSdk;
    Account info1 = null;
    Account info2 = null;
    Account info3 = null;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://127.0.0.1:20384");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("OntAssetTxTest.json");

        if (ontSdk.getWalletMgr().getAccounts().size() < 3) {
            info1 = ontSdk.getWalletMgr().createAccountFromPriKey("passwordtest", "be4400a0b15e8b15cc3c8103839f766b8b1dd05b4b41885bab8eb06867262cfe");
            info2 = ontSdk.getWalletMgr().createAccount("passwordtest");
            info3 = ontSdk.getWalletMgr().createAccount("passwordtest");
            ontSdk.getWalletMgr().writeWallet();
        }

        info1 = ontSdk.getWalletMgr().getAccounts().get(0);
        info2 = ontSdk.getWalletMgr().getAccounts().get(1);
        info3 = ontSdk.getWalletMgr().getAccounts().get(2);
    }

    @Test
    public void sendTransfer() throws Exception {

        String res= ontSdk.getOntAssetTx().sendTransfer("ont",info1.address,"passwordtest",info2.address,100L);
        Assert.assertNotNull(res);
    }

    @Test
    public void makeTransfer() throws Exception {

        Transaction tx = ontSdk.getOntAssetTx().makeTransfer("ont",info1.address,"passwordtest",info2.address,100L);
        Assert.assertNotNull(tx);
    }

    @Test
    public void sendTransferToMany() throws Exception {
        String hash1 = ontSdk.getOntAssetTx().sendTransferToMany("ont",info1.address,"passwordtest",new String[]{info2.address,info3.address},new long[]{100L,200L});
        Assert.assertNotNull(hash1);
    }

    @Test
    public void sendTransferFromMany() throws Exception {

        String hash2 = ontSdk.getOntAssetTx().sendTransferFromMany("ont", new String[]{info1.address, info2.address}, new String[]{"passwordtest", "passwordtest"}, info3.address, new long[]{1L, 2L});
        Assert.assertNotNull(hash2);
    }

    @Test
    public void sendOngTransferFrom() throws Exception {

        String res = ontSdk.getOntAssetTx().sendOngTransferFrom(info1.address,"passwordtest",info2.address,10L);
        Assert.assertNotNull(res);
    }


}