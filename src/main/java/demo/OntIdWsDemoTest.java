package demo;

import ontology.common.Helper;
import ontology.core.InvokeCodeTransaction;
import ontology.OntSdk;
import ontology.sdk.info.RecordInfo;
import ontology.sdk.info.account.AccountInfo;
import ontology.sdk.wallet.Identity;
import ontology.sdk.wallet.Wallet;
import ontology.sdk.websocket.MsgQueue;
import ontology.sdk.websocket.Result;
import ontology.sdk.websocket.WsProcess;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ontology.common.Common.print;


/**
 * Created by zx on 2018/1/9.
 */

public class OntIdWsDemoTest {
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();
            Identity accountInfo = ontSdk.getWalletMgr().importIdentity("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
           // System.out.println(accountInfo);
          //  System.exit(0);
//            System.out.println(ontSdk.getWalletMgr().getWallet().getIdentities());
            ontSdk.getWalletMgr().getWallet().setDefaultIdentity(0);
            ontSdk.getWalletMgr().writeWallet();

//            String wsUrl = "ws://127.0.0.1:20335";
//            String wsUrl = "ws://54.222.182.88:22335";
            String wsUrl = "ws://101.132.193.149:21335";

            Object lock = new Object();
            WsProcess.startWebsocketThread(lock,wsUrl);

            //等待ws 的session uuid，发送请求后，可以指定 推送给websocket客户端
            String wsUUID = waitUserid(ontSdk,lock);
            System.out.println("wsSessionID:"+wsUUID);
            ontSdk.setWsSessionId(wsUUID);

            Wallet oep6 = ontSdk.getWalletMgr().getWallet();
            System.out.println("oep6:"+ JSON.toJSONString(oep6));
            //System.exit(0);

            System.out.println("================register=================");
            //注册ontid
            Identity ident = ontSdk.getOntIdTx().register("passwordtest");

            String ontid = ident.ontid;

            //System.exit(0);
            //等待推送结果
            waitResult(ontSdk,lock);
            //Thread.sleep(6000);
            System.out.println("===============updateAttribute==================");

            String attri = "attri";
            //String ontid = "did:ont:APoFQzsESEZZ2LzCtZZ4GyAdp8zLwBZQcA";
            //for (int i = 0; i < 1; i++) {
                Map recordMap = new HashMap();
                recordMap.put("key0", "world0");
                //recordMap.put("key1", i);
                recordMap.put("keyNum", 1234589);
                recordMap.put("key2", false);


                System.out.println(JSON.toJSONString(recordMap));
                System.out.println(ontid);
                String hash = ontSdk.getOntIdTx().updateAttribute("passwordtest", ontid, attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
                System.out.println("hash:" + hash);

                //等待推送结果
                waitResult(ontSdk, lock);
                Thread.sleep(1000);
           // }
           // System.exit(0);

            System.out.println("===============getDDO==================");
            //查询ontid 的 ddo内容
            String ddo = ontSdk.getOntIdTx().getDDO("passwordtest", ontid,ontid);
            System.out.println("Ddo内容:"+ddo);

            //解析ontid 中Attributes 内容
            String rcd = JSON.parseObject(ddo).getJSONObject("Attributes").getString(attri);

            System.out.println("属性:"+attri);
            System.out.println("属性类型:"+ JSON.parseObject(rcd).get("Type"));
            System.out.println("属性值:"+ JSON.parseObject(rcd).get("Value"));

            System.out.println();
            System.out.println("===============get Transaction, parse Attribute==================");
            InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getRawTransaction(hash);
            RecordInfo info = ontSdk.getOntIdTx().parseAttribute(t.code);
            System.out.println(info);
            System.exit(0);

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
    public static void waitResult(OntSdk ontSdk, Object lock){
        try {
            synchronized (lock) {
                System.out.println("\nwait begin " + new Date().toString());
                boolean flag = false;
                while(true) {
                    lock.wait();
//                    if(MsgQueue.getChangeFlag()){
//                        String wsSessionId = waitUserid(ontSdk,lock);
//                        ontSdk.getOntIdTx().setWsSessionId(wsSessionId);
//                    }
                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println(e);
                        Result rt = JSON.parseObject(e, Result.class);
                        //TODO
                        MsgQueue.removeResult(e);
                        if(rt.Action.equals("Notify")) {
                            flag = true;
                            List<Map<String,Object>> list = (List<Map<String,Object>>)((Map)rt.Result).get("State");
                            for(Map m:(List<Map<String,Object>>)(list.get(0).get("Value"))){
                                String value = (String)m.get("Value");
                                String val = new String(Helper.hexToBytes(value));
                                System.out.print(val+" ");
                            }
                            System.out.println();
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
    }

    public static OntSdk getOntSdk() throws Exception {
//        String url = "http://54.222.182.88:22334";
//        String url = "http://127.0.0.1:20334";
        String url = "http://101.132.193.149:21334";
        OntSdk wm = OntSdk.getInstance();
        wm.setBlockChainConfig(url, "");
        //配置 ontid 文件
        wm.openWalletFile("OntIdWsDemo.json");

        print(String.format("ConnectParam=[%s, %s]", url, ""));

        //设置 ontid合约hash
        wm.setCodeHash("89ff0f39193ddaeeeab9de4873b549f71bbe809c");

        //System.exit(0);
        return wm;
    }
}
