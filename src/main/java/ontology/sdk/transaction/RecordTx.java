package ontology.sdk.transaction;

import ontology.common.Helper;
import ontology.core.*;
import ontology.core.payload.RecordTransaction;
import ontology.core.payload.TransferTransaction;
import ontology.core.scripts.Program;
import ontology.OntSdk;
import ontology.sdk.exception.Error;
import ontology.sdk.exception.ParamCheck;
import ontology.sdk.exception.SDKException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * Created by zx on 2018/1/10.
 */
public class RecordTx {
    public OntSdk sdk;
    public RecordTx (OntSdk sdk) {
        this.sdk =sdk;
    }
    //存证
    public String recordTransaction(String data) throws Exception {
        RecordTransaction tx = makeRecordTransaction(data,null);
        boolean b = sdk.getConnectMgr().sendRawTransaction(Helper.toHexString(tx.toArray()));
        if(b) {
            return tx.hash().toString();
        }
        return null;
    }
    //存证
    public  String recordTransaction0(String recordDataStr)throws Exception {
        String txid = null;
        String recordDataTimeStr = "";

        long time = System.currentTimeMillis();
        String nowTimeStamp = String.valueOf(time / 1000L);

        Map innerMap = new HashMap<>();
        innerMap.put("InnerTimestamp",nowTimeStamp);
        recordDataTimeStr = JSON.toJSONString(innerMap);

        TransferTransaction conTx = new TransferTransaction();
        //attri length limit
        int limit = 252;

        int sc = recordDataStr.getBytes("utf-8").length;
        int sc1 = recordDataTimeStr.getBytes("utf-8").length;

        int cc = sc / limit;
        int yu = sc % limit;
        int ac = yu == 0 ? cc : cc + 1;

        //make txn
        conTx.attributes = new ontology.core.TransactionAttribute[ac + 1];

        conTx.attributes[0] = new ontology.core.TransactionAttribute();
        conTx.attributes[0].usage = ontology.core.TransactionAttributeUsage.Description;
        conTx.attributes[0].data = recordDataTimeStr.getBytes("utf-8");
        for (int i = 0; i < ac; ++i) {
            int from = i * limit, to = (i + 1) * limit < sc ? (i + 1) * limit : sc;
            conTx.attributes[i + 1] = new ontology.core.TransactionAttribute();
            conTx.attributes[i + 1].usage = ontology.core.TransactionAttributeUsage.Description;
            conTx.attributes[i + 1].data = Arrays.copyOfRange(recordDataStr.getBytes("utf-8"), from, to);
        }
        conTx.inputs = new ontology.core.TransactionInput[0];
        conTx.outputs = new ontology.core.TransactionOutput[0];
        conTx.scripts = new ontology.core.scripts.Program[0];

        boolean b = sdk.getConnectMgr().sendRawTransaction(conTx);
        if (b) {
            txid = conTx.hash().toString();
        }
        return txid;
    }
    public RecordTransaction makeRecordTransaction(String data, String desc) throws Exception {
        RecordTransaction tx = makeRecordTx(data, desc);
        return tx;
    }
    private RecordTransaction makeRecordTx(String data, String txDesc) {
        RecordTransaction tx = new RecordTransaction();
        tx.recordType = "txt";
        tx.recordData = data.getBytes();
        tx.outputs = new TransactionOutput[0];
        tx.inputs = new TransactionInput[0];
        tx.attributes = new TransactionAttribute[1];
        tx.attributes[0] = new TransactionAttribute();
        tx.attributes[0].usage = TransactionAttributeUsage.Description;
        tx.attributes[0].data = UUID.randomUUID().toString().getBytes();
        tx.scripts = new Program[0];
        return tx;
    }
    //取证
    public String queryRecord(String txid) throws SDKException, IOException {
        if (!ParamCheck.isValidTxid(txid)) {
            throw new SDKException(Error.getDescArgError(String.format("%s=%s", "txid", txid)));
        }
        Transaction tx = sdk.getConnectMgr().getRawTransaction(txid);
        if (tx instanceof RecordTransaction) {
            RecordTransaction t = (RecordTransaction) tx;
            return new String(t.recordData);
        }else if(tx instanceof TransferTransaction) {
            TransferTransaction t = (TransferTransaction) tx;
            int attrlen = tx.attributes.length;
            byte attri[] = new byte[0];
            //attribute[0]放时间戳，attribute[1]开始放存证内容
            //String innerTimeStamp = (String)JSON.parseObject(new String(tx.attributes[0].data),Map.class).get("InnerTimestamp");
            for (int i = 1; i < attrlen; i++) {
                attri = Helper.addBytes(attri, tx.attributes[i].data);
            }
            String record = new String(attri, "utf-8");
            JSONObject result = JSON.parseObject(record);
            JSONObject Data = result.getJSONObject("Data");
            String text = Data.getString("Text");
            return text;
        }
        return null;
    }
}
