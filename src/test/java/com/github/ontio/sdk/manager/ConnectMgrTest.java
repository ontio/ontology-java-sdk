package com.github.ontio.sdk.manager;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.asset.Fee;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.dsig.TransformService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ConnectMgrTest {

    OntSdk ontSdk;
    public static Object lock = new Object();

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful("http://127.0.0.1:20334");
        ontSdk.setRpc("http://127.0.0.1:20336");
        ontSdk.setWesocket("http://127.0.0.1:20335",lock);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("ConnectMgrTest.json");
        if(ontSdk.getWalletMgr().getIdentitys().size() < 1){

        }

    }

    @Test
    public void sendRawTransaction() throws ConnectorException, IOException {
        boolean b = ontSdk.getConnectMgr().sendRawTransaction("00d1bc262d2f0000000000000000801f036b657951c10367657469803ca638069742da4b6871fe3d7f78718eeee78a01002439316431643534652d613238612d343239612d623630622d37376231363637616239353401000000000000000001f2cb16173f2a5a2cfe73d42f1450c4410f528c000000000000000001012312020347492d5c43297b42f9c9a6814526d91e3196096802bc574e3d0138f8ec655a7f0101410145a95c7c5a194dc63415bf684b6bd718b92662e2b73ffe13352a418c8640e4e65242b5675d31bb8dd6c6a9e762b5771791fd294b26eeeaf666030927fabc3906");
        Assert.assertEquals(true,b);
    }

    @Test
    public void sendRawTransactionByTx() throws Exception {
        IdentityInfo info = ontSdk.getWalletMgr().createIdentityInfo("passwordtest",ontSdk.keyType,ontSdk.curveParaSpec);
        ontSdk.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");

        byte[] pk = Helper.hexToBytes(info.pubkey);
        List list = new ArrayList<Object>();
        list.add("get".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add("key".getBytes());
        list.add(tmp);
        Fee[] fees = new Fee[1];
        fees[0] = new Fee(0, Address.addressFromPubKey(info.pubkey));
        byte[] params = ontSdk.getSmartcodeTx().createCodeParamsScript(list);
        Transaction tx = ontSdk.getSmartcodeTx().makeInvokeCodeTransaction("803ca638069742da4b6871fe3d7f78718eeee78a",null,params, VmType.NEOVM.value(),fees);
        boolean b = ontSdk.getConnectMgr().sendRawTransaction(tx);
        Assert.assertEquals(true,b);
    }

    @Test
    public void sendRawTransactionPreExec() throws ConnectorException, IOException {
        Object obj = ontSdk.getConnectMgr().sendRawTransactionPreExec("00d1bc262d2f0000000000000000801f036b657951c10367657469803ca638069742da4b6871fe3d7f78718eeee78a01002439316431643534652d613238612d343239612d623630622d37376231363637616239353401000000000000000001f2cb16173f2a5a2cfe73d42f1450c4410f528c000000000000000001012312020347492d5c43297b42f9c9a6814526d91e3196096802bc574e3d0138f8ec655a7f0101410145a95c7c5a194dc63415bf684b6bd718b92662e2b73ffe13352a418c8640e4e65242b5675d31bb8dd6c6a9e762b5771791fd294b26eeeaf666030927fabc3906");
        Assert.assertNotEquals(null,obj);
    }

    @Test
    public void getTransaction() throws ConnectorException, IOException {
        Transaction tx = ontSdk.getConnectMgr().getTransaction("2f1405a979a913a565fee3408a9d63086fb385034f9acd8a673a9444cbdec0cb");
        Assert.assertNotEquals(null,tx);

    }

    @Test
    public void getTransactionJson() throws ConnectorException, IOException {
        Object obj = ontSdk.getConnectMgr().getTransactionJson("2f1405a979a913a565fee3408a9d63086fb385034f9acd8a673a9444cbdec0cb");
        Assert.assertNotEquals(null,obj);
    }

    @Test
    public void getGenerateBlockTime() throws ConnectorException, IOException {
        int res = ontSdk.getConnectMgr().getGenerateBlockTime();
        Assert.assertNotEquals(0,res);
    }

    @Test
    public void getNodeCount() throws ConnectorException, IOException {
        int nodecount = ontSdk.getConnectMgr().getNodeCount();
        Assert.assertNotEquals(0,nodecount);
    }

    @Test
    public void getBlockHeight() throws ConnectorException, IOException {

        int res = ontSdk.getConnectMgr().getBlockHeight();
        Assert.assertTrue(res > 0);
    }

    @Test
    public void getBlock() throws ConnectorException, IOException {
        Block b = ontSdk.getConnectMgr().getBlock(20);
        Assert.assertNotNull(b);

    }

    @Test
    public void getBlockByBlockhash() throws ConnectorException, IOException {
//
        Block b = ontSdk.getConnectMgr().getBlock("cb0334e23a91c5cd65825499adbde6261b756d35943e78d624c124c85701b68d");
        Assert.assertNotNull(b);
    }

    @Test
    public void getBalance() throws ConnectorException, IOException {

        Object obj = ontSdk.getConnectMgr().getBalance("TA7QoDue4QFKaSpCE9eoGqEEm2odrDc2Hs");
        Assert.assertNotNull(obj);
    }

    @Test
    public void getBlockJson() throws ConnectorException, IOException {

        Object obj = ontSdk.getConnectMgr().getBlockJson(20);
        Assert.assertNotNull(obj);
    }

    @Test
    public void getBlockJsonbyHash() throws ConnectorException, IOException {
        Object obj = ontSdk.getConnectMgr().getBlockJson("cb0334e23a91c5cd65825499adbde6261b756d35943e78d624c124c85701b68d");
        Assert.assertNotNull(obj);
    }

    @Test
    public void getContract() throws ConnectorException, IOException {
        Object obj = ontSdk.getConnectMgr().getContract("803ca638069742da4b6871fe3d7f78718eeee78a");
        Assert.assertNotNull(obj);
    }

    @Test
    public void getContractJson() throws ConnectorException, IOException {

        Object obj = ontSdk.getConnectMgr().getContractJson("803ca638069742da4b6871fe3d7f78718eeee78a");
        Assert.assertNotNull(obj);
    }

    @Test
    public void getSmartCodeEvent() {
    }

    @Test
    public void getSmartCodeEventByHash() throws ConnectorException, IOException {
//
        Object obj = ontSdk.getConnectMgr().getSmartCodeEvent("0fb357df989120fbe1e80e840c83eb69472c19812baed3c71eda346f832f169f");
        Assert.assertNotNull(obj);
    }

    @Test
    public void getBlockHeightByTxHash() throws ConnectorException, IOException {
        int h = ontSdk.getConnectMgr().getBlockHeightByTxHash("0fb357df989120fbe1e80e840c83eb69472c19812baed3c71eda346f832f169f");
        Assert.assertTrue(h > 0);
    }

    @Test
    public void getStorage() throws ConnectorException, IOException {

        String res = ontSdk.getConnectMgr().getStorage("ff00000000000000000000000000000000000001","018d0e5a6af8bb8d43b48212c79afa6df850b5b8");
        Assert.assertNull(res);
    }

    @Test
    public void getMerkleProof() throws ConnectorException, IOException {

        Object obj = ontSdk.getConnectMgr().getMerkleProof("08adac23f6e0b440507ca78a679c63677a8df568708ce7f2cf26a539fdcdfe71");
        Assert.assertNotNull(obj);
    }
}