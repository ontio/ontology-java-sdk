package com.github.ontio.merkle;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MerkleVerifierTest {
    OntSdk ontSdk;
    String password = "111111";

    @Before
    public void setUp() throws SDKException {
        String ip = "http://127.0.0.1";
        String restUrl = ip + ":" + "20334";

        ontSdk = OntSdk.getInstance();
        ontSdk.setRestful(restUrl);
        ontSdk.setDefaultConnect(ontSdk.getRestful());

        ontSdk.openWalletFile("MerkleVerifierTest.json");
    }

    @Test
    public void verifyLeafHashInclusion() throws Exception {
        UInt256 txroot = UInt256.parse("731be6e82cfe0382bdf04e891fdab2fd1a3cd1b97628ef8498c85789f8c798ba");
        UInt256 curBlkRoot = UInt256.parse("34aad54259addee02df4636b3d6b57cbe3398725847f05f94ea35e8356ab46b3");
        UInt256[] targetHashes = new UInt256[]{
                UInt256.parse("b4295f52004d01be2a459b297aaa3ddc397560dddc13a917b047d8daa6bf5b2b"),
                UInt256.parse("6b8e9b09ac23b532f9391f57047cc226eaafcf7531fba63714ba2aa7f0ffc291"),
                UInt256.parse("863c0210cedf57a9f999f36d34b86760054234aab72eba67433ee51f6504e63a"),

                UInt256.parse("47025ef9977449b90ea58ddf820852a04a35f0827c691365a97122141e201305"),
                UInt256.parse("712a75cd6759c69e704353a8b3735274dcdc9026fdce7dc3f1320123c49d6938"),
                UInt256.parse("d6a3f1cb1270c5ef4e721aabc45d4894315b268875ca850653164e31e4fe64e2"),

                UInt256.parse("cb93a80f22bda0563e317b96332f989b0cc6764b84a41f7e22eb6025d3c63e94")
        };
        int blockHeight = 3970;
        int curBlockHeight = 3971;
        boolean b = MerkleVerifier.VerifyLeafHashInclusion(txroot, blockHeight, targetHashes, curBlkRoot, curBlockHeight+1);
        assertTrue(b);
    }

    @Test
    public void getProof() throws Exception {
        Identity identity = ontSdk.getWalletMgr().createIdentity(password);
        Account payer = ontSdk.getWalletMgr().createAccount(password);

        Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,payer.address,0);
        ontSdk.signTx(tx,identity.ontid,password);
        ontSdk.addSign(tx,payer.address,password);
        ontSdk.getConnectMgr().sendRawTransaction(tx);
        Thread.sleep(6000);

        String hash = tx.hash().toHexString();
        Map proof = new HashMap();
        Map map = new HashMap();
        int height = ontSdk.getConnectMgr().getBlockHeightByTxHash(hash);
        map.put("Type", "MerkleProof");
        map.put("TxnHash", hash);
        map.put("BlockHeight", height);

        Map tmpProof = (Map) ontSdk.getConnectMgr().getMerkleProof(hash);
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
        MerkleVerifier.Verify(txroot,  MerkleVerifier.getProof(txroot, blockHeight, targetHashes, curBlockHeight + 1), curBlockRoot);
    }
}