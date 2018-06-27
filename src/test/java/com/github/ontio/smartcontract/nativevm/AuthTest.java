package com.github.ontio.smartcontract.nativevm;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.core.payload.DeployCode;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class AuthTest {

    OntSdk sdk;
    String codeHex;
    String codeAddress;
    String abi;
    Account account;
    String password;
    String walletFile = "AuthTest.json";
    Identity adminIdentity;
    Identity identity;
    Identity identity2;

    @Before
    public void setUp() throws Exception {
        sdk = OntSdk.getInstance();
        sdk.setRestful(OntSdkTest.URL);
        sdk.setDefaultConnect(sdk.getRestful());
        sdk.openWalletFile(walletFile);
        codeHex = "57c56b6c766b00527ac46c766b51527ac4616c766b00c304696e6974876c766b52527ac46c766b52c3641100616509016c766b53527ac46294006c766b00c30a717565727961646d696e876c766b54527ac46c766b54c3641100616598006c766b53527ac46266006c766b00c303666f6f876c766b55527ac46c766b55c3644200616c766b00c36c766b51c3617c655d01009c6c766b56527ac46c766b56c3640e00006c766b53527ac46221006c766b51c3616521006c766b53527ac4620e00006c766b53527ac46203006c766b53c3616c756652c56b6c766b00527ac461516c766b51527ac46203006c766b51c3616c756651c56b61612a6469643a6f6e743a41617a457666515063513247454646504c46315a4c7751374b356a446e38316876656c766b00527ac46203006c766b00c3616c756653c56b611400000000000000000000000000000000000000066c766b00527ac4006c766b00c311696e6974436f6e747261637441646d696e612a6469643a6f6e743a41617a457666515063513247454646504c46315a4c7751374b356a446e383168766561537951795572755172755279527954727552727568164f6e746f6c6f67792e4e61746976652e496e766f6b656c766b51527ac46c766b51c300517f519c6c766b52527ac46203006c766b52c3616c756657c56b6c766b00527ac46c766b51527ac461556154c66c766b527a527ac46c766b55c36c766b52527ac46c766b52c361682d53797374656d2e457865637574696f6e456e67696e652e476574457865637574696e6753637269707448617368007cc46c766b52c36c766b00c3527cc46c766b52c36c766b51c300c3517cc46c766b52c36c766b51c351c3537cc41400000000000000000000000000000000000000066c766b53527ac4006c766b53c30b766572696679546f6b656e6c766b52c361537951795572755172755279527954727552727568164f6e746f6c6f67792e4e61746976652e496e766f6b656c766b54527ac46c766b54c300517f519c6c766b56527ac46203006c766b56c3616c7566";
        codeAddress = Address.AddressFromVmCode(codeHex).toHexString();
        abi = "{\"hash\":\"0x3acea0d75537d14762b692dc7e3c62b98975fa50\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"foo\",\"parameters\":[{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Boolean\"},{\"name\":\"queryadmin\",\"parameters\":[],\"returntype\":\"ByteArray\"},{\"name\":\"init\",\"parameters\":[],\"returntype\":\"Boolean\"}],\"events\":[]}";
        account = new Account(Helper.hexToBytes(OntSdkTest.PRIVATEKEY),SignatureScheme.SHA256WITHECDSA);
        password = "111111";
        adminIdentity  = sdk.getWalletMgr().createIdentityFromPriKey(password,OntSdkTest.PRIVATEKEY);
        identity  = sdk.getWalletMgr().createIdentityFromPriKey(password,OntSdkTest.PRIVATEKEY2);
        identity2 = sdk.getWalletMgr().createIdentityFromPriKey(password,OntSdkTest.PRIVATEKEY3);

    }

    @After
    public void removeWallet(){
        File file = new File(walletFile);
        if(file.exists()){
            if(file.delete()){
                System.out.println("delete wallet file success");
            }
        }
    }

    @Test
    public void test() throws Exception {
        sdk.nativevm().ontId().sendRegister(adminIdentity,password,account,sdk.DEFAULT_GAS_LIMIT,0);
        sdk.nativevm().ontId().sendRegister(identity,password,account,sdk.DEFAULT_GAS_LIMIT,0);
        sdk.nativevm().ontId().sendRegister(identity2,password,account,sdk.DEFAULT_GAS_LIMIT,0);
        Transaction tx = sdk.vm().makeDeployCodeTransaction(codeHex, true, "name",
                "v1.0", "author", "email", "desp", account.getAddressU160().toBase58(),20000000,0);
        sdk.vm().setCodeAddress(codeAddress);
        sdk.signTx(tx, new Account[][]{{account}});
        String txHex = Helper.toHexString(tx.toArray());
        Object result = sdk.getConnect().sendRawTransaction(txHex);
        Thread.sleep(6000);
        DeployCode t = (DeployCode) sdk.getConnect().getTransaction(tx.hash().toHexString());
        assertNotNull(t);
    }

    @Test
    public void sendTransfer() throws Exception {
        String txhash = sdk.nativevm().auth().sendTransfer(identity.ontid,password,identity.controls.get(0).getSalt(),1,codeAddress,adminIdentity.ontid,account,sdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
        assertTrue(((JSONObject)obj).getString("State").equals("1"));
    }

    @Test
    public void initTest() throws Exception {

        AbiInfo abiInfo = JSON.parseObject(abi,AbiInfo.class);
        String name = "init";
        AbiFunction function = abiInfo.getFunction(name);
        function.setParamsValue();
        String txhash = (String) sdk.neovm().sendTransaction(Helper.reverse(codeAddress),account,account,sdk.DEFAULT_GAS_LIMIT,0,function,false);
        Thread.sleep(6000);
        Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
        System.out.println(obj);
    }

    @Test
    public void queryAdmin() throws Exception {
        AbiInfo abiInfo = JSON.parseObject(abi,AbiInfo.class);
        String name = "queryadmin";
        AbiFunction function = abiInfo.getFunction(name);
        function.setParamsValue();
        Object obj = sdk.neovm().sendTransaction(Helper.reverse(codeAddress),account,account,sdk.DEFAULT_GAS_LIMIT,0,function,true);
        String res = ((JSONObject)obj).getString("Result");
        String aa = new String(Helper.hexToBytes(res));
        assertTrue("did:ont:AazEvfQPcQ2GEFFPLF1ZLwQ7K5jDn81hve".equals(aa));
    }


    @Test
    public void assignFuncsToRole() throws Exception {
        String txhash = sdk.nativevm().auth().assignFuncsToRole(adminIdentity.ontid, password, adminIdentity.controls.get(0).getSalt(), 1, Helper.reverse(codeAddress), "role", new String[]{"foo"}, account, sdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        Object obj = sdk.getConnect().getSmartCodeEvent(txhash);
        assertTrue(((JSONObject) obj).getString("State").equals("1"));
        txhash = sdk.nativevm().auth().assignOntIdsToRole(adminIdentity.ontid, password, adminIdentity.controls.get(0).getSalt(), 1, Helper.reverse(codeAddress), "role", new String[]{identity2.ontid}, account, sdk.DEFAULT_GAS_LIMIT, 0);
        Thread.sleep(6000);
        obj = sdk.getConnect().getSmartCodeEvent(txhash);
        assertTrue(((JSONObject) obj).getString("State").equals("1"));
        String result = sdk.nativevm().auth().verifyToken(identity2.ontid, password, identity2.controls.get(0).getSalt(), 1, Helper.reverse(codeAddress), "foo");
        assertTrue(result.equals("01"));
    }

    @Test
    public void delegate() throws Exception {
        sdk.nativevm().auth().delegate(identity2.ontid,password,identity2.controls.get(0).getSalt(),1,Helper.reverse(codeAddress),identity.ontid,"role",60*5,1,account,sdk.DEFAULT_GAS_LIMIT,0);
        Thread.sleep(6000);
        String result = sdk.nativevm().auth().verifyToken(identity2.ontid, password, identity2.controls.get(0).getSalt(), 1, Helper.reverse(codeAddress), "foo");
        String result2 = sdk.nativevm().auth().verifyToken(identity.ontid, password, identity.controls.get(0).getSalt(), 1, Helper.reverse(codeAddress), "foo");
        assertTrue(result2.equals("01"));
    }

    @Test
    public void verifyToken() throws Exception {
        String result2 = sdk.nativevm().auth().verifyToken(identity.ontid, password, identity.controls.get(0).getSalt(), 1, Helper.reverse(codeAddress), "foo");
        assertTrue(result2.equals("01"));
    }
}