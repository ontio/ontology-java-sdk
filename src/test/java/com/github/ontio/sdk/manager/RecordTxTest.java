package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class RecordTxTest {

    OntSdk ontSdk;
    String codeAddress = "806256c36653d4091a3511d308aac5c414b2a444";
    String password = "111111";
    String claimId = "7f2075f54450432e7582b58088f269d778206be3af08c650af35a61d894a6d35";

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        String restUrl = "http://127.0.0.1:20334";
        String codeHex = "5ec56b6c766b00527ac46c766b51527ac4616c766b00c306436f6d6d6974876c766b52527ac46c766b52c3645d00616c766b51c3c0529c009c6c766b55527ac46c766b55c3640e00006c766b56527ac4621e016c766b51c300c36c766b53527ac46c766b51c351c36c766b54527ac46c766b53c36c766b54c3617c65fc006c766b56527ac462e9006c766b00c3065265766f6b65876c766b57527ac46c766b57c3645d00616c766b51c3c0529c009c6c766b5a527ac46c766b5ac3640e00006c766b56527ac462a8006c766b51c300c36c766b58527ac46c766b51c351c36c766b59527ac46c766b58c36c766b59c3617c65d7016c766b56527ac46273006c766b00c309476574537461747573876c766b5b527ac46c766b5bc3644900616c766b51c3c0519c009c6c766b5d527ac46c766b5dc3640e00006c766b56527ac4622f006c766b51c300c36c766b5c527ac46c766b5cc36165b8036c766b56527ac4620e00006c766b56527ac46203006c766b56c3616c756656c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b52527ac46c766b52c3640e006c766b52c3c000a0620400006c766b54527ac46c766b54c364410061616c766b00c309206578697374656421617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac462a0000231236c766b53527ac46c766b53c36c766b51c37e6c766b53527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b53c3615272680f4e656f2e53746f726167652e50757461616c766b51c31320637265617465206e657720636c61696e3a206c766b00c3615272045075736854c168124e656f2e52756e74696d652e4e6f7469667961516c766b55527ac46203006c766b55c3616c756658c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b52527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e476574756c766b52c3630e006c766b52c3c0009c620400006c766b54527ac46c766b54c364450061616c766b00c30d206e6f74206578697374656421617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac4625f016c766b52c300517f01309c6c766b56527ac46c766b56c3644a0061616c766b00c312206861732062656564207265766f6b65642e617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac462fe006c766b52c300517f01319c009c6c766b57527ac46c766b57c364490061616c766b00c3112076616c756520696e76616c696465642e617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac4629c000230236c766b53527ac46c766b53c36c766b51c37e6c766b53527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b53c3615272680f4e656f2e53746f726167652e50757461616c766b51c30f207265766f6b6520636c61696d3a206c766b00c3615272045075736854c168124e656f2e52756e74696d652e4e6f7469667961516c766b55527ac46203006c766b55c3616c756653c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b51527ac4616c766b00c309207374617475733a206c766b51c3615272045075736854c168124e656f2e52756e74696d652e4e6f74696679616c766b51c36c766b52527ac46203006c766b52c3616c7566";
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("RecordTxTest.json");
        ontSdk.neovm().claimRecord().setCodeAddress(codeAddress);
        Identity id = ontSdk.getWalletMgr().createIdentity(password);

        ontSdk.vm().setCodeAddress(codeAddress);

        Transaction tx = ontSdk.vm().makeDeployCodeTransaction(codeHex, true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value(),id.ontid,0);
        ontSdk.signTx(tx,id.ontid,password);
        String txHex = Helper.toHexString(tx.toArray());
        ontSdk.getConnectMgr().sendRawTransaction(txHex);
        Thread.sleep(6000);
    }

    @Test
    public void sendCommit() throws Exception {
        Account payer = ontSdk.getWalletMgr().createAccount(password);
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);
        ontSdk.nativevm().ontId().sendRegister(identity,password,payer.address,password,0);

        Thread.sleep(6000);

        String commitRes = ontSdk.neovm().claimRecord().sendCommit(identity.ontid,password,identity.ontid,claimId,0);
        Assert.assertNotNull(commitRes);
        Thread.sleep(6000);

        String getstatusRes = ontSdk.neovm().claimRecord().sendGetStatus(identity.ontid,password,claimId);
        Assert.assertTrue(getstatusRes.contains("0"));

        String revokeRes = ontSdk.neovm().claimRecord().sendRevoke(identity.ontid,password,claimId,0);
        Assert.assertNotNull(revokeRes);
        Thread.sleep(6000);

        String getstatusRes2 = ontSdk.neovm().claimRecord().sendGetStatus(identity.ontid,password,claimId);
        Assert.assertTrue(getstatusRes2.contains("1"));

    }
}