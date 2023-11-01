package com.github.ontio.account;

import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountTest {


    @Test
    public void generateSignature() throws Exception {
        Account account = new Account(SignatureScheme.SM3WITHSM2);
        byte[] signature = account.generateSignature("hello".getBytes(), SignatureScheme.SM3WITHSM2, null);
        boolean b = account.verifySignature("hello".getBytes(), signature);
        assertTrue(b);
    }

    @Test
    public void testSHA256WITHECDSA() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        byte[] signature = account.generateSignature("hello".getBytes(), SignatureScheme.SHA256WITHECDSA, null);
        boolean b = account.verifySignature("hello".getBytes(), signature);
        assertTrue(b);
    }

    @Test
    public void serializePublicKey() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        byte[] publickey = account.serializePublicKey();
        assertNotNull(publickey);
    }

    @Test
    public void serializePrivateKey() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        byte[] privateKey = account.serializePrivateKey();
        assertNotNull(privateKey);
    }

    @Test
    public void compareTo() throws Exception {
        Account account1 = new Account(SignatureScheme.SHA256WITHECDSA);
        Account account2 = new Account(SignatureScheme.SHA256WITHECDSA);
        int res = account1.compareTo(account2);
        assertNotNull(res);
    }

    @Test
    public void exportCtrEncryptedPrikey1() throws Exception {
        Account account = new Account(SignatureScheme.SHA256WITHECDSA);
        String encruPri = account.exportCtrEncryptedPrikey("111111", 16384);
        String privateKey = Account.getCtrDecodedPrivateKey(encruPri, "111111", account.getAddressU160().toBase58(), 16384, SignatureScheme.SHA256WITHECDSA);
        assertEquals(privateKey, Helper.toHexString(account.serializePrivateKey()));
    }

}