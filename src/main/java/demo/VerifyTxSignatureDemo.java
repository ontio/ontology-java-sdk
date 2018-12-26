package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Digest;
import com.github.ontio.sdk.exception.SDKException;

/**
 *
 *
 */
public class VerifyTxSignatureDemo {
    private static OntSdk ontSdk = null;

    public static void main(String[] args) {

        try {
            ontSdk = getOntSdk();
            Transaction tx = ontSdk.getConnect().getTransaction("c2a917a928f15ba6b30bb2af0f12884df4d81ab917b6ca82ce395a33000f7ba2");
            //System.out.println(tx.toHexString());
            System.out.println(ontSdk.verifyTransaction(tx));

            // test error signature
            tx.sigs[0].sigData[0] = Helper.hexToBytes("011b2cb837a373ee8ac9f9ed1cbdf03517006d30df8d1c0a379e21d833bcbeeb853b4fa182c9868052a6604d9d46a20bfd2028a7a709e75cac7aaa7421d2de0ffd");
            System.out.println(false == ontSdk.verifyTransaction(tx));

            System.out.println();
            // multi sign
            String data = "00d16853423c000000000000000030750000000000004756c9dd829b2142883adbe1ae4f8689a1f673e97400c66b144756c9dd829b2142883adbe1ae4f8689a1f673e96a7cc81426aff63b171726ac95969887b2478dc2bc5b96f46a7cc803a086016a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b650002424101c886c23654063b9bb8e6e7d7103cd98128eddaf5c309f82050f3c3eff7875c5536e63a4c2e670b24186067f145a8de9d2e3a3f7aaa8741f74a15683cf41177b5232103036c12be3726eb283d078dff481175e96224f0b0c632c7a37e10eb40fe6be889ac8441011b74eef9f06c393768ec1be11401138e48ec936e61cd51711013268da2b4bfdae904abd24cb212e9885737d6fa34336d3688252ac6ad133eab2980836cff941d41012106ff283e156b22b777a48a525018a87d130b32d1e3fb313ec9ce1b98234eb8d59f551718437d375e523d3e088cbe400f05b8853db2f9e65ba5226b61d6ec2869522103036c12be3726eb283d078dff481175e96224f0b0c632c7a37e10eb40fe6be88921022dce1383ab72dc421732f772171a9ef867b5d36a82c7c4daabdb083d0e710ac62102df6f28e327352a44720f2b384e55034c1a7f54ba31785aa3a338f613a5b7cc2653ae";
            tx = Transaction.deserializeFrom(Helper.hexToBytes(data));
            for (int i = 0; i < tx.sigs.length; i++) {
                if (tx.sigs[i].M == 1) {
                    System.out.println(i + " " + Address.addressFromPubKey(tx.sigs[i].pubKeys[0]).toBase58() + "  " + tx.sigs[i].json());
                } else if (tx.sigs[i].M > 1) {
                    System.out.println(i + " " + Address.addressFromMultiPubKeys(tx.sigs[i].M, tx.sigs[i].pubKeys).toBase58() + "  " + tx.sigs[i].json());
                }
            }
            System.out.println( ontSdk.verifyTransaction(tx));

            System.out.println();
            // test error signature in multi sign
            data = "00d10157e29d000000000000000030750000000000004756c9dd829b2142883adbe1ae4f8689a1f673e97400c66b144756c9dd829b2142883adbe1ae4f8689a1f673e96a7cc81426aff63b171726ac95969887b2478dc2bc5b96f46a7cc803a086016a7cc86c51c1087472616e736665721400000000000000000000000000000000000000010068164f6e746f6c6f67792e4e61746976652e496e766f6b650002424101ddb49f40c7d8ee3efb9a3b4104e76b9bba5d5567ba7328bc723f897298cbdef2511af83a6d5170001c3c5917e89e640b65aaa7bff82c5eb3b36083d1fb986c64232103036c12be3726eb283d078dff481175e96224f0b0c632c7a37e10eb40fe6be889ac8441011d158ef87d04a9eaad579f5317a77561bef1035db9d9dd787f1c971a383fef92e4d71cd6931516ff5084793f074222c54e982157653966ba68758806993dfc1e4101e2a2fbf0e77a88161139be7e8d15fb455503cfa26b89fa2f94ff6f6ee6495f47168c75830f4219a3b75126ae74df3315680fd8325f67537f37eb08bdf73b09f169522103036c12be3726eb283d078dff481175e96224f0b0c632c7a37e10eb40fe6be88921022dce1383ab72dc421732f772171a9ef867b5d36a82c7c4daabdb083d0e710ac62102df6f28e327352a44720f2b384e55034c1a7f54ba31785aa3a338f613a5b7cc2653ae";
            tx = Transaction.deserializeFrom(Helper.hexToBytes(data));
            for (int i = 0; i < tx.sigs.length; i++) {
                if (tx.sigs[i].M == 1) {
                    System.out.println(i + " " + Address.addressFromPubKey(tx.sigs[i].pubKeys[0]).toBase58() + "  " + tx.sigs[i].json());
                } else if (tx.sigs[i].M > 1) {
                    System.out.println(i + " " + Address.addressFromMultiPubKeys(tx.sigs[i].M, tx.sigs[i].pubKeys).toBase58() + "  " + tx.sigs[i].json());
                }
            }
            System.out.println(false == ontSdk.verifyTransaction(tx));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static OntSdk getOntSdk() throws Exception {
        String ip = "http://dappnode1.ont.io";//"http://polaris1.ont.io";//
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());
        wm.openWalletFile("wallet.json");

        return wm;
    }
}
