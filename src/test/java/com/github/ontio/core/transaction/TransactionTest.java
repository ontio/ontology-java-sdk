package com.github.ontio.core.transaction;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.Vm;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class TransactionTest {

    OntSdk ontSdk;
    Vm vm;
    String ontContract = "0000000000000000000000000000000000000001";

    @Before
    public void setUp(){
        ontSdk = OntSdk.getInstance();
        vm = new Vm(ontSdk);
    }

    @Test
    public void serialize() throws Exception {
        Transaction tx = vm.buildNativeParams(Address.parse(ontContract),"init","1".getBytes(),null,0,0);
        Account account = new Account(Helper.hexToBytes("0bc8c1f75a028672cd42c221bf81709dfc7abbbaf0d87cb6fdeaf9a20492c194"),SignatureScheme.SHA256WITHECDSA);
        ontSdk.signTx(tx,new Account[][]{{account}});

        String t = tx.toHexString();
        System.out.println(t);

        Transaction tx2 = Transaction.deserializeFrom(Helper.hexToBytes(t));
        System.out.println(tx2.json());


    }
}