package com.github.ontio.smartcontract.nativevm;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OngTest {

    public String password = "111111";
    OntSdk ontSdk;
    Account account;
    Account receiveAcc;

    @Before
    public void setUp() throws Exception {
        ontSdk=OntSdk.getInstance();
        ontSdk.setRestful(OntSdkTest.URL);
        account = new Account(Helper.hexToBytes(OntSdkTest.PRIVATEKEY),SignatureScheme.SHA256WITHECDSA);
        receiveAcc = new Account(SignatureScheme.SHA256WITHECDSA);
        ontSdk.nativevm().ont().sendTransfer(account,receiveAcc.getAddressU160().toBase58(),10L,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);

        String accountOng = ontSdk.nativevm().ong().unboundOng(account.getAddressU160().toBase58());
        ontSdk.nativevm().ong().withdrawOng(account,account.getAddressU160().toBase58(),1000,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        Object obj = ontSdk.getConnect().getBalance(account.getAddressU160().toBase58());
        System.out.println(obj);
    }
    @Test
    public void sendTransfer() throws Exception {
        long accountOng = ontSdk.nativevm().ong().queryBalanceOf(account.getAddressU160().toBase58());
        long receiveAccOng = ontSdk.nativevm().ong().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ong().sendTransfer(account,receiveAcc.getAddressU160().toBase58(),10L,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long accountOng2 = ontSdk.nativevm().ong().queryBalanceOf(account.getAddressU160().toBase58());
        long receiveAccOng2 = ontSdk.nativevm().ong().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(accountOng-accountOng2 == 10);
        Assert.assertTrue(receiveAccOng2-receiveAccOng == 10);
    }


    @Test
    public void sendApprove() throws Exception {
        long allowance = ontSdk.nativevm().ong().queryAllowance(account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ong().sendApprove(account,receiveAcc.getAddressU160().toBase58(),10,account,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long allowance2 = ontSdk.nativevm().ong().queryAllowance(account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(allowance2-allowance == 10);

        long acctbalance = ontSdk.nativevm().ong().queryBalanceOf(account.getAddressU160().toBase58());
        long reciebalance = ontSdk.nativevm().ong().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        ontSdk.nativevm().ong().sendTransferFrom(receiveAcc,account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58(),10,receiveAcc,ontSdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        long acctbalance2 = ontSdk.nativevm().ong().queryBalanceOf(account.getAddressU160().toBase58());
        long reciebalance2 = ontSdk.nativevm().ong().queryBalanceOf(receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(acctbalance-acctbalance2 == 10);
        Assert.assertTrue(reciebalance2 - reciebalance == 10);
        long allowance3 = ontSdk.nativevm().ong().queryAllowance(account.getAddressU160().toBase58(),receiveAcc.getAddressU160().toBase58());
        Assert.assertTrue(allowance3 == allowance);
    }

    @Test
    public void queryName() throws Exception {
        String name = ontSdk.nativevm().ong().queryName();
        Assert.assertTrue(name.contains("ONG"));
        String symbol = ontSdk.nativevm().ong().querySymbol();
        Assert.assertTrue(symbol.contains("ONG"));
        long decimals = ontSdk.nativevm().ong().queryDecimals();
        Assert.assertTrue(decimals == 9);
        long total = ontSdk.nativevm().ong().queryTotalSupply();
        Assert.assertFalse(total < 0);
    }
}