package demo;

import ontology.core.InvokeCodeTransaction;
import ontology.OntSdk;
import ontology.sdk.info.RecordInfo;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import static ontology.common.Common.print;

/**
 * Created by zx on 2018/1/25.
 */
public class SmartCodeRecordDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();

            Map recordMap = new HashMap();
            recordMap.put("key0", "world0");
            //recordMap.put("key1", i);
            recordMap.put("keyNum", 12345678);
            recordMap.put("key2", false);
            Map recordData = ontSdk.getOntIdTx().constructRecord(JSON.toJSONString(recordMap));
            System.out.println(recordData);

            //smartcode record
            if(ontSdk.getWalletMgr().getIdentitys().size() == 0){
                ontSdk.getWalletMgr().createIdentity("password");
                System.out.println(ontSdk.getWalletMgr().openWallet());
                ontSdk.getWalletMgr().writeWallet();
            }


            String ontid = ontSdk.getWalletMgr().getIdentitys().get(0).ontid;

            String hash = ontSdk.getOntIdTx().addRecord("password", ontid, "attri".getBytes(), JSON.toJSONString(recordData).getBytes());

            System.out.println("waiting...");
            Thread.sleep(6000);

            InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getRawTransaction(hash);
            RecordInfo info = ontSdk.getOntIdTx().parseRecord(t.code);
            System.out.println(info);
            System.out.println(info.value);

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static OntSdk getOntSdk() throws Exception {

//        String url = "http://127.0.0.1:20334";
        String url = "http://101.132.193.149:20334";
        OntSdk wm = OntSdk.getInstance();
        wm.setBlockChainConfig(url, "");
        wm.openWalletFile("RecordDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));
        //设置 ontid合约hash
        wm.setCodeHash("89ff0f39193ddaeeeab9de4873b549f71bbe809c");
        return wm;
    }
}
