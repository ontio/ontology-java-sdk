package demo;

import com.github.ontio.common.Helper;
import com.github.ontio.OntSdk;
import com.github.ontio.sdk.info.abi.AbiInfo;
import com.github.ontio.sdk.info.abi.AbiFunction;
import com.github.ontio.sdk.info.account.AccountInfo;
import com.github.ontio.sdk.wallet.Identity;
import com.alibaba.fastjson.JSON;
import com.github.ontio.sdk.websocket.MsgQueue;
import com.github.ontio.sdk.websocket.Result;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import static com.github.ontio.common.Common.print;

/**
 * Created by zx on 2018/1/31.
 */
public class InvokeSmartCodeDemo {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            System.out.println(ontSdk.getWalletMgr().getWallet());

             String wsUrl = "ws://127.0.0.1:20335";
//            String wsUrl = "ws://54.222.182.88:22335";
//            String wsUrl = "ws://101.132.193.149:21335";
            print(String.format("ConnectParam wsUrl=[%s, %s]", wsUrl, ""));

            Object lock = new Object();
//            WsProcess.startWebsocketThread(lock,wsUrl,true);

            //等待ws 的session uuid，发送请求后，可以指定 推送给websocket客户端
//            String wsUUID = waitUserid(ontSdk,lock);
//            System.out.println("wsSessionID:"+wsUUID);
//            ontSdk.setWsSessionId(wsUUID);

            InputStream is = new FileInputStream("C:\\ZX\\NeoContract1.abi.json");
            byte[] bys = new byte[is.available()];
            is.read(bys);
            is.close();
            String abi = new String(bys);

            //System.out.println(abi);
            AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
            System.out.println("codeHash:"+abiinfo.getHash());
            System.out.println("Entrypoint:"+abiinfo.getEntrypoint());
            System.out.println("Functions:"+abiinfo.getFunctions());
            System.out.println("Events"+abiinfo.getEvents());


            ontSdk.setCodeHash(abiinfo.getHash());
            if(ontSdk.getWalletMgr().getIdentitys().size() == 0){
                Map map = new HashMap<>();
                map.put("test","value00");
                ontSdk.getOntIdTx().register("passwordtest");
                //waitResult(ontSdk, lock);
            }
            Identity did = ontSdk.getWalletMgr().getIdentitys().get(0);

//            String ddo = ontSdk.getOntIdTx().getDDO(did.ontid,"passwordtest",did.ontid);
//            System.out.println("Ddo内容:"+ddo);
//            System.exit(0);
            AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(did.ontid,"passwordtest");

            AbiFunction func = abiinfo.getFunction("AddAttribute");
            System.out.println(func.getParameters());
            func.setParamsValue(did.ontid.getBytes(),"key".getBytes(),"bytes".getBytes(),"values02".getBytes(),Helper.hexToBytes(info.pubkey));
            System.out.println(func);

            ontSdk.setCodeHash(abiinfo.getHash());
            String hash = ontSdk.getSmartcodeTx().invokeTransaction(did.ontid,"passwordtest",func,(byte)0x80);

            System.out.println("invokeTransaction hash:"+hash);
//            List listResult = waitResult(ontSdk, lock);
//            System.out.println(listResult);

            //Transaction tx = ontSdk.getConnectManager().getRawTransaction(hash);
            Thread.sleep(6000);
//            String ddo = ontSdk.getOntIdTx().getDDO(did.ontid,"passwordtest",did.ontid);
//            System.out.println("Ddo内容:"+ddo);
            //System.out.println(tx);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String waitUserid(OntSdk ontSdk, Object lock){
        try {
            synchronized (lock) {
                while(true) {
                    lock.wait();
                    System.out.println(MsgQueue.getHeartBeat());
                    Result rt = JSON.parseObject(MsgQueue.getHeartBeat(), Result.class);
                    MsgQueue.setChangeFlag(false);
                    if (rt.Action.equals("heartbeat")) {
                        return (String) rt.Result;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static List waitResult(OntSdk ontSdk, Object lock){
        try {
            synchronized (lock) {
                System.out.println("\nwait begin " + new Date().toString());
                boolean flag = false;
                while(true) {
                    lock.wait();
                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println(e);
                        Result rt = JSON.parseObject(e, Result.class);
                        //TODO
                        MsgQueue.removeResult(e);
                        if(rt.Action.equals("Notify")) {
                            flag = true;
                            List<Map<String,Object>> list = (List<Map<String,Object>>)((Map)rt.Result).get("State");
                            List listResult = new ArrayList();
                            for(Map m:(List<Map<String,Object>>)(list.get(0).get("Value"))){
                                String value = (String)m.get("Value");
                                String val = new String(Helper.hexToBytes(value));
                                System.out.print(val+" ");
                                listResult.add(val);
                            }
                            System.out.println();
                            return listResult;

                        }
                    }
                    if(flag){
                        break;
                    }
                }
                System.out.println("wait end  " +  new Date().toString()+"\n");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static OntSdk getOntSdk() throws Exception {
//        String url = "http://54.222.182.88:22334";
        String url = "http://127.0.0.1:20384";
//        String url = "http://101.132.193.149:21334";
        OntSdk wm = OntSdk.getInstance();
        wm.setRpcConnection(url);
        wm.openWalletFile("InvokeSmartCodeDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));
        //设置 ontid合约hash
        wm.setCodeHash("263dbc0ca10aec184ceced7a998106733852c28a");
        return wm;
    }
}
