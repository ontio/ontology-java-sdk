package com.github.ontio.smartcontract;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.network.exception.RestfulException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.neovm.abi.AbiFunction;
import com.github.ontio.smartcontract.neovm.abi.AbiInfo;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import com.github.ontio.smartcontract.neovm.abi.Struct;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static demo.NeoVmDemo.abi;
import static org.junit.Assert.*;

public class NeoVmTest {

    OntSdk ontSdk;

    @Before
    public void setUp() throws SDKException {

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(OntSdkTest.URL);
        ontSdk.setDefaultConnect(ontSdk.getRestful());
        ontSdk.openWalletFile("NeoVmTest.json");
    }

    @Test
    public void sendTransaction() throws Exception {

        Account account = new Account(Helper.hexToBytes(OntSdkTest.PRIVATEKEY),SignatureScheme.SHA256WITHECDSA);

        String codeHex = "57c56b6c766b00527ac46c766b51527ac4616c766b00c307546573744d6170876c766b52527ac46c766b52c3641200616165c7006c766b53527ac462b4006c766b00c30e446573657269616c697a654d6170876c766b54527ac46c766b54c3641900616c766b51c300c3616511026c766b53527ac4627a006c766b00c30a54657374537472756374876c766b55527ac46c766b55c3641200616165e9026c766b53527ac4624b006c766b00c311446573657269616c697a65537472756374876c766b56527ac46c766b56c3641900616c766b51c300c36165cc036c766b53527ac4620e00006c766b53527ac46203006c766b53c3616c756658c56b6161681953797374656d2e53746f726167652e476574436f6e746578746c766b00527ac4c76c766b51527ac401646c766b52527ac46c766b51c3036b65796c766b52c3c4616c766b51c361681853797374656d2e52756e74696d652e53657269616c697a656c766b53527ac46c766b00c30274786c766b53c3615272681253797374656d2e53746f726167652e507574616c766b00c3027478617c681253797374656d2e53746f726167652e4765746c766b54527ac46c766b54c361681a53797374656d2e52756e74696d652e446573657269616c697a656c766b55527ac46c766b55c36416006c766b55c3036b6579c36c766b52c39c620400006c766b56527ac46c766b56c3643c00616c766b00c306726573756c740474727565615272681253797374656d2e53746f726167652e507574616c766b53c36c766b57527ac46238006c766b00c306726573756c740566616c7365615272681253797374656d2e53746f726167652e50757461006c766b57527ac46203006c766b57c3616c756656c56b6c766b00527ac4616c766b00c361681a53797374656d2e52756e74696d652e446573657269616c697a656c766b51527ac461681953797374656d2e53746f726167652e476574436f6e746578746c766b52527ac401646c766b53527ac46c766b51c36416006c766b51c3036b6579c36c766b53c39c620400006c766b54527ac46c766b54c3643800616c766b52c306726573756c740474727565615272681253797374656d2e53746f726167652e50757461516c766b55527ac46241006c766b52c306726573756c740566616c7365615272681253797374656d2e53746f726167652e507574616c766b51c3036b6579c36c766b55527ac46203006c766b55c3616c756656c56b6161681953797374656d2e53746f726167652e476574436f6e746578746c766b00527ac46152c56c766b51527ac46c766b51c307636c61696d6964517cc46c766b51c30164007cc46c766b51c361681853797374656d2e52756e74696d652e53657269616c697a656c766b52527ac46c766b00c30274786c766b52c3615272681253797374656d2e53746f726167652e507574616c766b00c3027478617c681253797374656d2e53746f726167652e4765746c766b53527ac46c766b52c300a06c766b54527ac46c766b54c3641300616c766b52c36c766b55527ac46238006c766b00c306726573756c740566616c7365615272681253797374656d2e53746f726167652e50757461006c766b55527ac46203006c766b55c3616c756656c56b6c766b00527ac4616c766b00c361681a53797374656d2e52756e74696d652e446573657269616c697a656c766b51527ac461681953797374656d2e53746f726167652e476574436f6e746578746c766b52527ac401646c766b53527ac46c766b51c36413006c766b51c300c36c766b53c39c620400006c766b54527ac46c766b54c3643800616c766b52c306726573756c740474727565615272681253797374656d2e53746f726167652e50757461516c766b55527ac4623e006c766b52c306726573756c740566616c7365615272681253797374656d2e53746f726167652e507574616c766b51c300c36c766b55527ac46203006c766b55c3616c7566";
        String abi = "{\"hash\":\"0xd97f2d441f82f132d1904d521f93f8e51d354d7c\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"TestMap\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeMap\",\"parameters\":[{\"name\":\"param\",\"type\":\"ByteArray\"}],\"returntype\":\"Any\"},{\"name\":\"TestStruct\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeStruct\",\"parameters\":[{\"name\":\"param\",\"type\":\"ByteArray\"}],\"returntype\":\"Any\"}],\"events\":[]}";
        abi = "{\"hash\":\"0x3c341335540c51c03bdef0f460994f99ea4659e8\",\"entrypoint\":\"Main\",\"functions\":[{\"name\":\"Main\",\"parameters\":[{\"name\":\"operation\",\"type\":\"String\"},{\"name\":\"args\",\"type\":\"Array\"}],\"returntype\":\"Any\"},{\"name\":\"TestMap\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeMap\",\"parameters\":[{\"name\":\"param\",\"type\":\"ByteArray\"}],\"returntype\":\"Any\"},{\"name\":\"TestStruct\",\"parameters\":[],\"returntype\":\"Any\"},{\"name\":\"DeserializeStruct\",\"parameters\":[{\"name\":\"param\",\"type\":\"ByteArray\"}],\"returntype\":\"Any\"}],\"events\":[]}";
        String codeAddress = Address.AddressFromVmCode(codeHex).toHexString();

        if(true){
            Transaction tx = ontSdk.vm().makeDeployCodeTransaction(codeHex, true, "name",
                    "v1.0", "author", "email", "desp", account.getAddressU160().toBase58(),30000000,0);
            ontSdk.signTx(tx, new Account[][]{{account}});
            Object result = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
            Thread.sleep(6000);
            Object obj = ontSdk.getConnect().getContract(codeAddress);
            assertTrue(obj != null);
        }
        if(true){

            AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
            String name = "TestMap";
            AbiFunction func = abiinfo.getFunction(name);
            func.name = name;
            func.setParamsValue();
            Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(codeAddress),null,null,0,0,func, true);
            assertTrue( ((JSONObject)obj).getString("State").equals("1"));
        }
        if(true){
            AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
            String name = "DeserializeMap";
            AbiFunction func = abiinfo.getFunction(name);
            func.name = name;
            Map map = new HashMap<>();
            map.put("key",100);
            func.setParamsValue(BuildParams.getMapBytes(map));
            Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(codeAddress),null,null,0,0,func, true);
            assertTrue(((JSONObject)obj).getString("State").equals("1"));
        }

        //80 02 00 0164 00 07 636c61696d6964
        if(true){
            AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
            String name = "TestStruct";
            AbiFunction func = abiinfo.getFunction(name);

            System.out.println(func);
            func.setParamsValue();

            Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(codeAddress),null,null,0,0,func, true);
            assertTrue(((JSONObject)obj).getString("State").equals("1"));

        }
        //80 02 00 0164 00 07 636c61696d6964
        //80 02 02 0164 00 07 636c61696d6964
        if(true){
            AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
            String name = "DeserializeStruct";
            AbiFunction func = abiinfo.getFunction(name);

            System.out.println(func);
            func.setParamsValue(BuildParams.getStructBytes(new Struct().add(100,"claimid")));
            Object obj =  ontSdk.neovm().sendTransaction(Helper.reverse(codeAddress),null,null,0,0,func, true);
            assertTrue(((JSONObject)obj).getString("State").equals("1"));
        }
    }
}