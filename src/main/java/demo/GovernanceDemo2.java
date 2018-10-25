package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.governance.*;
import com.github.ontio.crypto.SignatureScheme;

import java.util.Base64;
import java.util.Map;

public class GovernanceDemo2 {

    public static void main(String[] args) throws Exception {
        String privateKey = Account.getGcmDecodedPrivateKey("8p2q0vLRqyfKmFHhnjUYVWOm12kPm78JWqzkTOi9rrFMBz624KjhHQJpyPmiSSOa","111111","AHX1wzvdw9Yipk7E9MuLY4GGX4Ym9tHeDe",Base64.getDecoder().decode("KbiCUr53CZUfKG1M3Gojjw=="),16384,SignatureScheme.SHA256WITHECDSA);
        Account account = new Account(Helper.hexToBytes(privateKey), SignatureScheme.SHA256WITHECDSA);
        //梦航
        Account account1 = getAccount("wR9S/JYwMDfCPWFGEy5DEvWfU14k9suZuL4+woGtfhZJf5+KyL9VJqMi/wGTOd1i","passwordtest","AZqk4i7Zhfhc1CRUtZYKrLw4YTSq4Y9khN","ZaIL8DxNaQ91fkMHAdiBjQ==");
        Account account2 = getAccount("PCj/a4zUgYnOBNZUVEaXBK61Sq4due8w2RUzrumO3Bm0hZ/3v4mlDiXYYvmmBZUk","passwordtest","ARpjnrnHEjXhg4aw7vY6xsY6CfQ1XEWzWC","wlz1h439j0GwsWhGBByMxg==");
        Account account3 = getAccount("4U6qYhRUxGYTcvDvBKKCu2C1xUyd0A+pHXsK1YVY1Hbxd8TcbyvmfOcqx7N+f+BH","passwordtest","AQs2BmzzFVk7pQPfTQQi9CTEz43ejSyBnt","AFDFoZAlLGJdB4yVQqYVhw==");
        Account account4 = getAccount("i6n+FTACzRF5y0oeo6Wm3Zbv68bfjmyRyNfKB5IArK76RCG8b/JgRqnHgMtHixFx","passwordtest","AKBSRLbFNvUrWEGtKxNTpe2ZdkepQjYKfM","FkTZ6czRPAqHnSpEqVEWwA==");
        Account account5 = getAccount("IoEbJXMPlxNLrAsDYKGD4I6oFYgJl1j603c8oHQl+82yET+ibKgJdZjgdw39pr2K","passwordtest","AduX7odaWGipkdvzBwyaTgsumRbRzhhiwe","lc7ofKCBkNUmjTLrZYmStA==");
        Account account6 = getAccount("6hynBJVTAhmMJt9bIYSDoz+GL5EFaUGhn3Pd6HsF+RQ1tFyZoFRhT+JNMGAb+B6a","passwordtest","ANFfWhk3A5iFXQrVBHKrerjDDapYmLo5Bi","DTmbW9wzGA8pi4Dcj3/Cpg==");
        Account account7 = getAccount("EyXxszzKh09jszQXMIFJTmbujnojOzYzPU4cC0wOpuegDgVcRFllATQ81zD0Rp8s","passwordtest","AK3YRcRvKrASQ6nTfW48Z4iMZ2sDTDRiMC","jbwUF7JxgsiJq5QAy5dfug==");
        Address multiAddress = Address.addressFromMultiPubKeys(5,account1.serializePublicKey(),account2.serializePublicKey(),account3.serializePublicKey(),account4.serializePublicKey(),account5.serializePublicKey(),account6.serializePublicKey(),account7.serializePublicKey());
        Account[] accounts = new Account[]{account1,account2,account3,account4,account5,account6,account7};
        Account[] accounts1 = new Account[]{account1,account2,account3,account4,account5};
        byte[][] pks = new byte[accounts.length][];
        for(int i=0;i<pks.length;i++){
            pks[i] = accounts[i].serializePublicKey();
        }


        try {
            OntSdk sdk = getOntSdk();
            if(false){
                String txhash = sdk.nativevm().governance().commitDpos(multiAddress,5,new Account[]{account1,account2,account3,account4,account5},new byte[][]{account6.serializePublicKey(),account7.serializePublicKey()},
                        account1,sdk.DEFAULT_GAS_LIMIT,0);

                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
            }
            if(false){
                GovernanceView view = sdk.nativevm().governance().getGovernanceView();
                System.out.println(JSON.toJSONString(view));
            }
            if(true){
                GlobalParam1 param1 = sdk.nativevm().governance().getGlobalParam1();
                GlobalParam2 param2 = sdk.nativevm().governance().getGlobalParam2();
                System.out.println(JSON.toJSONString(param1));
                System.out.println(JSON.toJSONString(param2));
//                param1.A = 60;
//                param1.B = 40;
//                String txhash = sdk.nativevm().governance().updateGlobalParam1(accounts1,pks,5,param1,account,20000,0);
//                Thread.sleep(6000);
//                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                return;
            }


            if(false){
                SplitCurve curve = sdk.nativevm().governance().getSplitCurve();
                System.out.println(JSON.toJSONString(curve));
                curve.Yi[0] = 0;
                String txhash = sdk.nativevm().governance().updateSplitCurve(accounts, pks,5,curve,account,20000,0);
                System.out.println(txhash);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                return;
            }
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
            if(false){
                GovernanceView view = sdk.nativevm().governance().getGovernanceView();
                System.out.println(view);
                return;
            }
            if(false){
                InputPeerPoolMapParam param = sdk.nativevm().sideChainGovernance().getInputPeerPoolMapParam("123456");
                System.out.println(param);
            }
            if(false){
                Configuration c = sdk.nativevm().governance().getConfiguration();
                System.out.println(JSON.toJSONString(c));

            }
            if(false){

                Configuration c = new Configuration(7,2,7,112,10000,10000,10,10000);
                String txhash = sdk.nativevm().governance().updateConfig(accounts1,pks,5,c,account1,20000,0);
                System.out.println(txhash);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                return;
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
        String ip = "http://127.0.0.1";
//        String ip = "http://polaris1.ont.io";
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
