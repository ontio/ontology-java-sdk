package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.crypto.ECC;
import com.github.ontio.sdk.info.AccountInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @date 2018/3/28
 */
public class AccountDemo {
    public static void main(String[] args) {

        try {
            OntSdk ontSdk = getOntSdk();
            if (false){
                AccountInfo info0 = ontSdk.getWalletMgr().createAccountInfo("passwordtest");
                AccountInfo info = ontSdk.getWalletMgr().createAccountInfoFromPriKey("passwordtest","e467a2a9c9f56b012c71cf2270df42843a9d7ff181934068b4a62bcdd570e8be");
                System.out.println(info.addressBase58);
                Account accountInfo = ontSdk.getWalletMgr().importAccount("3JZLD/X45qSFjmRRvRVhcEjKgCJQDPWOsjx2dcTEj58=", "passwordtest",info.addressBase58);

                com.github.ontio.account.Account acct0 = ontSdk.getWalletMgr().getAccount(info.addressBase58, "passwordtest");
            }


            if(true){

                byte[] salt = new byte[]{(byte)251,(byte)155,(byte)65,(byte)228,(byte)3,(byte)251,(byte)77,(byte)136,(byte)106,(byte)44,(byte)2,(byte)255,(byte)194,(byte)185,(byte)234,(byte)196};
                salt = ECC.generateKey(16);
                com.github.ontio.account.Account acct = new com.github.ontio.account.Account(Helper.hexToBytes("3e47428fd73f915a7937bf1f8d3bffc27a45dbb6ef4e57bd9513c1a8bfbcbfd4"),ontSdk.defaultSignScheme);
                String key = acct.exportGcmEncryptedPrikey("passwordtest",salt,16384);
                String prikey = com.github.ontio.account.Account.getGcmDecodedPrivateKey(key, "passwordtest",acct.getAddressU160().toBase58(),salt,16384,ontSdk.defaultSignScheme);
                System.out.println(prikey);
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

        String ip = "http://127.0.0.1";
//        String ip = "http://54.222.182.88;
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20384";
        String rpcUrl = ip + ":" + "20386";
        String wsUrl = ip + ":" + "20385";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRestful());

        wm.openWalletFile("AccountDemo.json");

        return wm;
    }
}
