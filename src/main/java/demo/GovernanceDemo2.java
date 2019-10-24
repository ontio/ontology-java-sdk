package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.common.NotifyEventInfo;
import com.github.ontio.core.governance.*;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
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
                NotifyEventInfo info = new NotifyEventInfo();
                info.setContractAddress("0200000000000000000000000000000000000000");
                List<Object> list = new ArrayList<>();
                list.add("hahaha");
                info.setStates(list);
                System.out.println(info);
                System.out.println(info.toJson());
                return;
            }



            if(false){
                sdk.setRpc("http://139.219.128.220:20336");
//                System.out.println(multiAddress.toBase58());
//                System.out.println(sdk.getConnect().getBalance(multiAddress.toBase58()));
                System.out.println(sdk.getConnect().getSideChainData(123456));
                return;
            }


            if(false){
                String txhash = sdk.nativevm().governance().commitDpos(multiAddress,5,new Account[]{account1,account2,account3,account4,account5},new byte[][]{account6.serializePublicKey(),account7.serializePublicKey()},
                        account1,sdk.DEFAULT_GAS_LIMIT,0);

                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                return;
            }
            if(false){
                GovernanceView view = sdk.nativevm().governance().getGovernanceView();
                System.out.println(JSON.toJSONString(view));
            }
            if(false){
//                success
                GlobalParam1 param1 = sdk.nativevm().governance().getGlobalParam1();
                GlobalParam2 param2 = sdk.nativevm().governance().getGlobalParam2();
                System.out.println(JSON.toJSONString(param1));
                System.out.println(JSON.toJSONString(param2));
                System.out.println(Long.MAX_VALUE);
                param1.candidateFee = (long)1000*1000000000;
                String txhash = sdk.nativevm().governance().updateGlobalParam1(accounts1,pks,5,param1,account,20000,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                GlobalParam1 param11 = sdk.nativevm().governance().getGlobalParam1();
                GlobalParam2 param22 = sdk.nativevm().governance().getGlobalParam2();
                System.out.println(JSON.toJSONString(param11));
                System.out.println(JSON.toJSONString(param22));
                return;
            }
            if(false){
//                success
                GlobalParam2 param2 = sdk.nativevm().governance().getGlobalParam2();
                if(param2 == null){
                    param2 = new GlobalParam2(1000,1,Helper.hexToBytes("00"),Helper.hexToBytes("00"),
                            Helper.hexToBytes("00"),Helper.hexToBytes("00"),Helper.hexToBytes("00"),Helper.hexToBytes("00"));
                }
                System.out.println(JSON.toJSONString(param2));
                String txhash = sdk.nativevm().governance().updateGlobalParam2(accounts1,pks,5,param2,account,20000,0);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                GlobalParam2 param22 = sdk.nativevm().governance().getGlobalParam2();
                System.out.println(JSON.toJSONString(param22));
            }

            if(false){
//                success
                InputPeerPoolMapParam param= sdk.nativevm().governance().getInputPeerPoolMapParam(123456);
                System.out.println(JSON.toJSONString(param));
                return;
            }

            if(false){
//                success
                SplitCurve curve = sdk.nativevm().governance().getSplitCurve();
                System.out.println(JSON.toJSONString(curve));
                curve.Yi[0] = 0;
                String txhash = sdk.nativevm().governance().updateSplitCurve(accounts, pks,5,curve,account,20000,0);
                System.out.println(txhash);
                Thread.sleep(6000);
                System.out.println(sdk.getConnect().getSmartCodeEvent(txhash));
                SplitCurve curve2 = sdk.nativevm().governance().getSplitCurve();
                System.out.println(JSON.toJSONString(curve2));
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
                InputPeerPoolMapParam param = sdk.nativevm().governance().getInputPeerPoolMapParam(123456);
                System.out.println(param);
            }
            if(true){
                Configuration c = sdk.nativevm().governance().getConfiguration();
                System.out.println(JSON.toJSONString(c));
                return;

            }
            if(true){
                sdk.setRpc("http://139.219.128.220:20336");
                Configuration c = new Configuration(8,3,8,128,10000,10000,10,10000);
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
