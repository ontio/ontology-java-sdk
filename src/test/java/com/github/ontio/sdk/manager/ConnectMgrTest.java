package com.github.ontio.sdk.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.IOUtils;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.info.IdentityInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.BuildParams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.crypto.dsig.TransformService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;

public class ConnectMgrTest {

    OntSdk ontSdk;
    String codeAddress;
    String blockHash;
    String  address = "TA7yeU2TTwmwZaBy9DV2uG1PdXJ9187pVH";
    String password = "111111";
    Identity identity;
    private final String ontContract = "ff00000000000000000000000000000000000001";

    String wallet = "ConnectMgrTest.json";

    @Before
    public void setUp() throws Exception {

        ontSdk = OntSdk.getInstance();
        String restUrl = OntSdkTest.URL;
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile(wallet);

        if(ontSdk.getWalletMgr().getIdentitys().size() < 1){
            identity = ontSdk.getWalletMgr().createIdentity(password);
        }else{
            identity = ontSdk.getWalletMgr().getIdentitys().get(0);
        }
    }


    @After
    public void removeWallet(){
        File file = new File(wallet);
        if(file.exists()){
            if(file.delete()){
                System.out.println("delete wallet file success");
            }
        }
    }

    @Test
    public void sendRawTransaction() throws Exception {
        //部署交易
        String codeHex = "5ec56b6c766b00527ac46c766b51527ac4616c766b00c306436f6d6d6974876c766b52527ac46c766b52c3645d00616c766b51c3c0529c009c6c766b55527ac46c766b55c3640e00006c766b56527ac4621e016c766b51c300c36c766b53527ac46c766b51c351c36c766b54527ac46c766b53c36c766b54c3617c65fc006c766b56527ac462e9006c766b00c3065265766f6b65876c766b57527ac46c766b57c3645d00616c766b51c3c0529c009c6c766b5a527ac46c766b5ac3640e00006c766b56527ac462a8006c766b51c300c36c766b58527ac46c766b51c351c36c766b59527ac46c766b58c36c766b59c3617c65d7016c766b56527ac46273006c766b00c309476574537461747573876c766b5b527ac46c766b5bc3644900616c766b51c3c0519c009c6c766b5d527ac46c766b5dc3640e00006c766b56527ac4622f006c766b51c300c36c766b5c527ac46c766b5cc36165b8036c766b56527ac4620e00006c766b56527ac46203006c766b56c3616c756656c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b52527ac46c766b52c3640e006c766b52c3c000a0620400006c766b54527ac46c766b54c364410061616c766b00c309206578697374656421617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac462a0000231236c766b53527ac46c766b53c36c766b51c37e6c766b53527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b53c3615272680f4e656f2e53746f726167652e50757461616c766b51c31320637265617465206e657720636c61696e3a206c766b00c3615272045075736854c168124e656f2e52756e74696d652e4e6f7469667961516c766b55527ac46203006c766b55c3616c756658c56b6c766b00527ac46c766b51527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b52527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e476574756c766b52c3630e006c766b52c3c0009c620400006c766b54527ac46c766b54c364450061616c766b00c30d206e6f74206578697374656421617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac4625f016c766b52c300517f01309c6c766b56527ac46c766b56c3644a0061616c766b00c312206861732062656564207265766f6b65642e617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac462fe006c766b52c300517f01319c009c6c766b57527ac46c766b57c364490061616c766b00c3112076616c756520696e76616c696465642e617c084572726f724d736753c168124e656f2e52756e74696d652e4e6f7469667961006c766b55527ac4629c000230236c766b53527ac46c766b53c36c766b51c37e6c766b53527ac46168164e656f2e53746f726167652e476574436f6e746578746c766b00c36c766b53c3615272680f4e656f2e53746f726167652e50757461616c766b51c30f207265766f6b6520636c61696d3a206c766b00c3615272045075736854c168124e656f2e52756e74696d652e4e6f7469667961516c766b55527ac46203006c766b55c3616c756653c56b6c766b00527ac4616168164e656f2e53746f726167652e476574436f6e746578746c766b00c3617c680f4e656f2e53746f726167652e4765746c766b51527ac4616c766b00c309207374617475733a206c766b51c3615272045075736854c168124e656f2e52756e74696d652e4e6f74696679616c766b51c36c766b52527ac46203006c766b52c3616c7566";
        codeAddress = "806256c36653d4091a3511d308aac5c414b2a444";
        Transaction tx = ontSdk.vm().makeDeployCodeTransaction(codeHex, true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value(),identity.ontid,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx,identity.ontid,password);

        String txHex = Helper.toHexString(tx.toArray());
        boolean b = ontSdk.getConnect().sendRawTransaction(txHex);
        Thread.sleep(6000);


        AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(identity.ontid.replace(Common.didont,""), password);
        List list = new ArrayList<Object>();
        list.add("Commit".getBytes());
        List tmp = new ArrayList<Object>();
        tmp.add(Helper.hexToBytes("dde1a09571bf98e04e62b1a8778b2d413747408f4594c946577965fa571de8e5"));
        tmp.add(identity.ontid.getBytes());
        list.add(tmp);
        byte[] params = BuildParams.createCodeParamsScript(list);
        Transaction tx2 = ontSdk.vm().makeInvokeCodeTransaction(codeAddress,null,params, VmType.NEOVM.value(), identity.ontid,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx2, identity.ontid, password);
        boolean b2 = ontSdk.getConnect().sendRawTransaction(tx2.toHexString());
        Assert.assertEquals(true,b);

        Thread.sleep(6000);

        Transaction txres = ontSdk.getConnect().getTransaction(tx2.hash().toHexString());
        Assert.assertNotNull(txres);
        Object obj = ontSdk.getConnect().getTransactionJson(tx2.hash().toHexString());
        Assert.assertNotNull(obj);

        Object obj2 = ontSdk.getConnect().getSmartCodeEvent(tx2.hash().toHexString());
        Assert.assertNotNull(obj2);

        int blockheight = ontSdk.getConnect().getBlockHeightByTxHash(tx2.hash().toHexString());
        Assert.assertNotNull(blockheight);
//        ontSdk.getConnectMgr().getStorage(codeAddress,)

    }

    @Test
    public void sendRawTransactionPreExec() throws Exception {

        byte[] parabytes = com.github.ontio.smartcontract.nativevm.BuildParams.buildParams(Address.decodeBase58(address).toArray());
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(ontContract,"balanceOf", parabytes, VmType.Native.value(), null,ontSdk.DEFAULT_GAS_LIMIT,0);
        Object obj = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        Assert.assertNotEquals(null,obj);
    }

    @Test
    public void getGenerateBlockTime() throws ConnectorException, IOException {
        int res = ontSdk.getConnect().getGenerateBlockTime();
        Assert.assertNotEquals(0,res);
    }

    @Test
    public void getBlockHeight() throws ConnectorException, IOException {

        int res = ontSdk.getConnect().getBlockHeight();
        Assert.assertTrue(res > 0);
    }

    @Test
    public void getBlock() throws ConnectorException, IOException,SDKException {
        int blockHeight =  ontSdk.getConnect().getBlockHeight();
        Block b = ontSdk.getConnect().getBlock(blockHeight);
        Assert.assertNotNull(b);

    }

    @Test
    public void getBlockByBlockhash() throws ConnectorException, IOException,SDKException {
        int blockHeight =  ontSdk.getConnect().getBlockHeight();
        Block b2 = ontSdk.getConnect().getBlock(blockHeight);
        blockHash = b2.hash().toString();
        Block b = ontSdk.getConnect().getBlock(blockHash);
        Assert.assertNotNull(b);
    }

    @Test
    public void getBalance() throws ConnectorException, IOException {
        Object obj = ontSdk.getConnect().getBalance(address);
        Assert.assertNotNull(obj);
    }

    @Test
    public void getBlockJson() throws ConnectorException, IOException {
        int blockHeight =  ontSdk.getConnect().getBlockHeight();
        Object obj = ontSdk.getConnect().getBlockJson(blockHeight);
        Assert.assertNotNull(obj);
    }

    @Test
    public void getContract() throws ConnectorException, IOException {
        Object obj = ontSdk.getConnect().getContract("ff00000000000000000000000000000000000001");
        Assert.assertNotNull(obj);
    }

    @Test
    public void getContractJson() throws ConnectorException, IOException {

        Object obj = ontSdk.getConnect().getContractJson("ff00000000000000000000000000000000000001");
        Assert.assertNotNull(obj);
    }
}