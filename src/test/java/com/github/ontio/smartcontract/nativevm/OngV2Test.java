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

public class OngV2Test {

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
        BigInteger amount = BigInteger.valueOf(10L).multiply(ONG_DECIMAL);
        BigInteger accountOng = ontSdk.nativevm().ongV2().queryBalanceOf(account.getAddressU160().toBase58());
        BigInteger receiveAccOng = ontSdk.nativevm().ongV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ongV2().sendTransfer(account, receiveAcc.getAddressU160().toBase58(), amount, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        BigInteger accountOng2 = ontSdk.nativevm().ongV2().queryBalanceOf(account.getAddressU160().toBase58());
        BigInteger receiveAccOng2 = ontSdk.nativevm().ongV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(accountOng.subtract(accountOng2).equals(amount));
        Assert.assertTrue(receiveAccOng2.subtract(receiveAccOng).equals(amount));
    }


    @Test
    public void sendApprove() throws Exception {
        BigInteger amount = BigInteger.valueOf(10).multiply(ONG_DECIMAL);
        BigInteger allowance = ontSdk.nativevm().ongV2().queryAllowance(account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ongV2().sendApprove(account, receiveAcc.getAddressU160().toBase58(), amount, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        BigInteger allowance2 = ontSdk.nativevm().ongV2().queryAllowance(account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58());
        System.out.println(allowance2 + ";" + amount);
        Assert.assertTrue(allowance2.equals(amount));

        BigInteger acctbalance = ontSdk.nativevm().ongV2().queryBalanceOf(account.getAddressU160().toBase58());
        BigInteger reciebalance = ontSdk.nativevm().ongV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ongV2().sendTransferFrom(receiveAcc, account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58(), amount, receiveAcc, ontSdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        BigInteger acctbalance2 = ontSdk.nativevm().ongV2().queryBalanceOf(account.getAddressU160().toBase58());
        BigInteger reciebalance2 = ontSdk.nativevm().ongV2().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(acctbalance.subtract(acctbalance2).equals(amount));
        Assert.assertTrue(reciebalance2.subtract(reciebalance).equals(amount));
        BigInteger allowance3 = ontSdk.nativevm().ongV2().queryAllowance(account.getAddressU160().toBase58(), receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(allowance3.signum() == 0);

        ontSdk.nativevm().ongV2().sendApprove(account, receiveAcc2.getAddressU160().toBase58(), amount, account, ontSdk.DEFAULT_GAS_LIMIT, 0);
        BigInteger allowance4 = ontSdk.nativevm().ongV2().queryTotalAllowance(account.getAddressU160().toBase58());
        Assert.assertTrue(allowance4.equals(amount));
    }

    @Test
    public void queryName() throws Exception {
        String name = ontSdk.nativevm().ongV2().queryName();
        Assert.assertTrue(name.contains("ONG"));
        String symbol = ontSdk.nativevm().ongV2().querySymbol();
        Assert.assertTrue(symbol.contains("ONG"));
        long decimals = ontSdk.nativevm().ongV2().queryDecimals();
        Assert.assertTrue(decimals == 18);
        BigInteger total = ontSdk.nativevm().ongV2().queryTotalSupply();
        Assert.assertFalse(total.signum() == 0);
    }
}