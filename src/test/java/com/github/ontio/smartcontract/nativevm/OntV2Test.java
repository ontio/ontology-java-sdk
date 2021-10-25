package com.github.ontio.smartcontract.nativevm;

import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

public class OntV2Test {

    public String password = "111111";
    OntSdk ontSdk;
    Account account;
    Account receiveAcc;
    Account receiveAcc2;

    private BigInteger ONG_DECIMAL = BigInteger.valueOf(1000000000000000000L);
    private long ONT_DECIMAL = 1000000000L;

    @Before
    public void setUp() throws Exception {
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(OntSdkTest.URL);
        account = new Account(Helper.hexToBytes(OntSdkTest.PRIVATEKEY), SignatureScheme.SHA256WITHECDSA);
        System.out.println(account.getAddressU160().toBase58());
        receiveAcc = new Account(Helper.hexToBytes(OntSdkTest.PRIVATEKEY2), SignatureScheme.SHA256WITHECDSA);
        receiveAcc2 = new Account(Helper.hexToBytes(OntSdkTest.PRIVATEKEY3), SignatureScheme.SHA256WITHECDSA);
        System.out.println(receiveAcc2.getAddressU160().toBase58());
        ontSdk.nativevm().ontV2().sendTransfer(account, receiveAcc.getAddressU160().toBase58(), 10L * ONT_DECIMAL, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
    }

    @Test
    public void sendTransfer() throws Exception {
        long amount = 10L * ONT_DECIMAL;
        long accountOng = ontSdk.nativevm().ontV2().queryBalanceOf(account.getAddressU160().toBase58());
        long receiveAccOng = ontSdk.nativevm().ontV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ontV2().sendTransfer(account, receiveAcc.getAddressU160().toBase58(), amount, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        long accountOng2 = ontSdk.nativevm().ontV2().queryBalanceOf(account.getAddressU160().toBase58());
        long receiveAccOng2 = ontSdk.nativevm().ontV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(accountOng - accountOng2 == amount);
        Assert.assertTrue(receiveAccOng2 - receiveAccOng == amount);
    }


    @Test
    public void sendApprove() throws Exception {
        long amount = 10L * ONT_DECIMAL;
        long allowance = ontSdk.nativevm().ontV2().queryAllowance(account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ontV2().sendApprove(account, receiveAcc.getAddressU160().toBase58(), amount, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        long allowance2 = ontSdk.nativevm().ontV2().queryAllowance(account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58());
        System.out.println(allowance2 + ";" + amount);
        Assert.assertTrue(allowance2 == amount);

        long acctbalance = ontSdk.nativevm().ontV2().queryBalanceOf(account.getAddressU160().toBase58());
        long reciebalance = ontSdk.nativevm().ontV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ontV2().sendTransferFrom(receiveAcc, account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58(), amount, receiveAcc, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        long acctbalance2 = ontSdk.nativevm().ontV2().queryBalanceOf(account.getAddressU160().toBase58());
        long reciebalance2 = ontSdk.nativevm().ontV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(acctbalance - acctbalance2 == amount);
        Assert.assertTrue(reciebalance2 - reciebalance == amount);
        long allowance3 = ontSdk.nativevm().ontV2().queryAllowance(account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(allowance3 == 0);

        ontSdk.nativevm().ontV2().sendApprove(account, receiveAcc2.getAddressU160().toBase58(), amount, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        long allowance4 = ontSdk.nativevm().ontV2().queryTotalAllowance(account.getAddressU160().toBase58());
        System.out.println(allowance4);
        System.out.println(amount);
        Assert.assertTrue(allowance4 == amount);
    }

    @Test
    public void queryName() throws Exception {
        String name = ontSdk.nativevm().ontV2().queryName();
        Assert.assertTrue(name.contains("ONT"));
        String symbol = ontSdk.nativevm().ontV2().querySymbol();
        Assert.assertTrue(symbol.contains("ONT"));
        long decimals = ontSdk.nativevm().ontV2().queryDecimals();
        Assert.assertTrue(decimals == 9);
        long total = ontSdk.nativevm().ontV2().queryTotalSupply();
        Assert.assertFalse(total == 0);
    }
}