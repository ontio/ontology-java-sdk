package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RecordTxTest {

    OntSdk ontSdk;
    Identity id;

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://127.0.0.1:20384");
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("RecordTxTest.json");
        ontSdk.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");


        if(ontSdk.getWalletMgr().getIdentitys().size() < 1) {

            ontSdk.getWalletMgr().createIdentity("passwordtest");
            ontSdk.getWalletMgr().writeWallet();
        }

        id = ontSdk.getWalletMgr().getIdentitys().get(0);
    }

    @Test
    public void sendPut() throws Exception {

        String res = ontSdk.getRecordTx().sendPut(id.ontid.replace("did:ont:",""),"passwordtest","key","value");
        Assert.assertNotNull(res);
    }

    @Test
    public void sendGet() throws Exception {

        String res = ontSdk.getRecordTx().sendGet(id.ontid.replace("did:ont:",""),"passwordtest","key");
        Assert.assertNotNull(res);
    }
}