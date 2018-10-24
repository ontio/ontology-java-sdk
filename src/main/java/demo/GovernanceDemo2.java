package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.governance.Configuration;
import com.github.ontio.core.governance.GlobalParam;
import com.github.ontio.core.governance.InputPeerPoolMapParam;
import com.github.ontio.core.governance.SplitCurve;
import com.github.ontio.crypto.SignatureScheme;

import java.util.Base64;
import java.util.Map;

public class GovernanceDemo2 {

    public static void main(String[] args) throws Exception {



        try {
            OntSdk sdk = getOntSdk();
            if(false){
                System.out.println(sdk.nativevm().governance().getPeerInfoAll());
                return;
            }

            if(false){
                Map m = sdk.nativevm().governance().getPeerPoolMap();
                System.out.println(m);
            }
            if(false){
                Configuration c = sdk.nativevm().governance().getConfiguration();
                System.out.println(c);
            }
            if(false){
                GlobalParam param = sdk.nativevm().governance().getGlobalParam();
                System.out.println(param);
            }
            if(false){
                SplitCurve curve = sdk.nativevm().governance().getSplitCurve();
                System.out.println(curve);
            }
            if(true){
                InputPeerPoolMapParam param = sdk.nativevm().governance().getInputPeerPoolMapParam("123456");
                System.out.println(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Account getAccount(String enpri,String password,String address,String salt) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey(enpri,password,address,Base64.getDecoder().decode(salt),16384,SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey),SignatureScheme.SHA256WITHECDSA);
//        System.out.println(Helper.toHexString(account.serializePublicKey()));
        return account;
    }


    public static OntSdk getOntSdk() throws Exception {
//        String ip = "http://127.0.0.1";
        String ip = "http://polaris1.ont.io";
        ip= "http://139.219.128.220";
//        String ip = "http://139.219.128.60";
//        String ip = "http://139.219.129.55";
//        String ip = "http://101.132.193.149";
        String restUrl = ip + ":" + "20334";
        String rpcUrl = ip + ":" + "20336";
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setDefaultConnect(wm.getRpc());
        wm.openWalletFile("GovernanceDemo.json");
        return wm;
    }
}
