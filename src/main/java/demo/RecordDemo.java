package demo;

import com.alibaba.fastjson.JSON;
import ontology.OntSdk;
import ontology.account.KeyType;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static ontology.common.Common.print;

/**
 * Created by zx on 2018/1/25.
 */
public class RecordDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();

            Map recordMap = new HashMap();
            recordMap.put("key0", "world0");
            //recordMap.put("key1", i);
            recordMap.put("keyNum", 12345678);
            recordMap.put("key2", false);
            Map recordData = constructRecord(JSON.toJSONString(recordMap));
            System.out.println(recordData);
            String hash0 = ontSdk.getRecordTx().recordTransaction(JSON.toJSONString(recordData));

            //等待出块
            Thread.sleep(6000);

            String result = ontSdk.getRecordTx().queryRecord(hash0);
            System.out.println(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static LinkedHashMap<String, Object> constructRecord(String text){
        LinkedHashMap<String, Object> recordData = new LinkedHashMap<String, Object> ();
        LinkedHashMap<String, Object> data = new  LinkedHashMap<String, Object>();
        data.put("Algrithem", KeyType.SM2.name());
        data.put("Hash","");
        data.put("Text",text);
        data.put("Signature","");

        recordData.put("Data",data);
        recordData.put("CAkey","");
        recordData.put("SeqNo","");
        recordData.put("Timestamp",0);
        return  recordData;
    }
    public static OntSdk getOntSdk() throws Exception {

//        String url = "http://127.0.0.1:20334";
        String url = "http://101.132.193.149:21334";
        OntSdk wm = OntSdk.getInstance();
        wm.setBlockChainConfig(url, "");
        wm.openWalletFile("RecordDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        return wm;
    }
}
