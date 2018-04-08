package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.UInt256;
import com.github.ontio.merkle.MerkleVerifier;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/4/8
 */
public class merkleProofDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            //System.out.println(ontSdk.getConnectMgr().getMerkleProof(""));

            UInt256 txroot = UInt256.parse("4b74e15973ce3964ba4a33ddaf92efbff922ea2225bca7676f62eab05829f11f");
            UInt256 curBlk = UInt256.parse("a5094c1daeeceab46319ce62b600c68a7accc806bd9fe2fdb869560bf66b5251");
            UInt256[] targetHashes = new UInt256[]{
                    UInt256.parse("c7ac8087b4ce292d654001b1ab1bfe5e68fa6f7b8492a5b2f83560f8ac28f5fa"),
                    UInt256.parse("5205a22b07c6072d60d28b41f1321ab993799d70693a3bb70bab7e58b49acc30"),
                    UInt256.parse("c0de7f3035a7960450ec9a64e7835b958b0fec1ddb90cbeb0779073c0a9a8f53")
            };
            boolean b = MerkleVerifier.VerifyLeafHashInclusion(txroot,2,targetHashes,curBlk,6);
            System.out.println(b);
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("ClaimDemo.json");

        return wm;
    }
}
