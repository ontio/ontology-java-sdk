package com.github.ontio.merkle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.OntSdkTest;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MerkleVerifierTest {
    OntSdk ontSdk;
    String password = "111111";
    String walletFile = "MerkleVerifierTest.json";

    @Before
    public void setUp() throws SDKException {
        String restUrl = OntSdkTest.URL;

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());

        ontSdk.openWalletFile(walletFile);
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
    public void verifyLeafHashInclusion() throws Exception {
        UInt256 txroot = UInt256.parse("f332c8ede11799137f28b10e40200063353dfc3233da6cea689e0637231ad1a7");
        UInt256 curBlkRoot = UInt256.parse("ba64746f650b7be0ac89fbf8defeceeb63821272d8096d83d3764b7ae9eb4a21");
        UInt256[] targetHashes = new UInt256[]{
                UInt256.parse("0000000000000000000000000000000000000000000000000000000000000000"),
                UInt256.parse("e14172c8a6e193943465648e1c586a9186a3784ee7ee29db9edbf6afe04f5390"),
                UInt256.parse("f440531999c547db08f516677c152215475a69dccb82176e4bca1b726261a1be"),

                UInt256.parse("ef4d3c0debb66bb15af8b82e1b9463d3039f6a95bf91c349e7df1b34ef5f7630"),
                UInt256.parse("d6dd5266af7407b89d00b1a11044cd4eb30f94dabbdf440b03f9173384b16d67"),
                UInt256.parse("17100c09ef2d19689c85cc7038b6654037045fc83b3951439634e5d2074998c6"),

                UInt256.parse("584aa3a421020a07710a2bc16e7865ea9ee0860692ad509515fb8caa837d27df"),
                UInt256.parse("192eed54084fbe94a8c34c1274168f38ac02abe01c94f9b425e58f26fe93d598")
        };
        int blockHeight = 1277;
        int curBlockHeight = 1277;
        boolean b = MerkleVerifier.VerifyLeafHashInclusion(txroot, blockHeight, targetHashes, curBlkRoot, curBlockHeight+1);
        assertTrue(b);
    }

    @Test
    public void getProof() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);
        Account payer = ontSdk.getWalletMgr().createAccount(password);
        byte[] salt = identity.controls.get(0).getSalt();
        Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,new byte[]{},"",null,payer.address,ontSdk.DEFAULT_GAS_LIMIT,0);
        ontSdk.signTx(tx,identity.ontid,password,salt);
        ontSdk.addSign(tx,payer.address,password,payer.getSalt());
        ontSdk.getConnect().sendRawTransaction(tx);
        Thread.sleep(6000);

        String hash = tx.hash().toHexString();
        System.out.println(hash);
        Map proof = new HashMap();
        Map map = new HashMap();
        int height = ontSdk.getConnect().getBlockHeightByTxHash(hash);
        map.put("Type", "MerkleProof");
        map.put("TxnHash", hash);
        map.put("BlockHeight", height);
        System.out.println(hash);
        Map tmpProof = (Map) ontSdk.getConnect().getMerkleProof(hash);
        System.out.println(JSONObject.toJSONString(tmpProof));
        UInt256 txroot = UInt256.parse((String) tmpProof.get("TransactionsRoot"));
        int blockHeight = (int) tmpProof.get("BlockHeight");
        UInt256 curBlockRoot = UInt256.parse((String) tmpProof.get("CurBlockRoot"));
        int curBlockHeight = (int) tmpProof.get("CurBlockHeight");
        List hashes = (List) tmpProof.get("TargetHashes");
        UInt256[] targetHashes = new UInt256[hashes.size()];
        for (int i = 0; i < hashes.size(); i++) {
            targetHashes[i] = UInt256.parse((String) hashes.get(i));
        }
        map.put("MerkleRoot", curBlockRoot.toHexString());
        map.put("Nodes", MerkleVerifier.getProof(txroot, blockHeight, targetHashes, curBlockHeight + 1));
        proof.put("Proof", map);
        boolean b =  MerkleVerifier.Verify(txroot,  MerkleVerifier.getProof(txroot, blockHeight, targetHashes, curBlockHeight + 1), curBlockRoot);
        assertTrue(b);
    }
}