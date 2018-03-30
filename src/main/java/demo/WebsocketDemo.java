/*
 * Copyright (C) 2018 The ontology Authors
 * This file is part of The ontology library.
 *
 *  The ontology is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The ontology is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with The ontology.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package demo;

import com.github.ontio.OntSdk;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.websocket.WebsocketClient;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.network.websocket.MsgQueue;
import com.github.ontio.network.websocket.Result;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;


/**
 *
 */

public class WebsocketDemo {
    public static Object lock = new Object();
    public static void main(String[] args) {
        try {
            OntSdk ontSdk = getOntSdk();



            ontSdk.getWebSocket().startWebsocketThread(false,false);
//            ontSdk.getWebSocket().setBroadcast(true);

            Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            waitResult(ontSdk, lock);
                        }
                    });
            thread.start();

            Wallet oep6 = ontSdk.getWalletMgr().getWallet();
            System.out.println("oep6:" + JSON.toJSONString(oep6));
            //System.exit(0);

            //System.out.println("================register=================");
            Identity ident = null;
            if (ontSdk.getWalletMgr().getIdentitys().size() == 0) {
                ident = ontSdk.getOntIdTx().sendRegister("passwordtest");
            } else {
                ident = ontSdk.getWalletMgr().getIdentitys().get(0);
            }

            String ontid = ident.ontid;


            for (int i = 0; i < 1000; i++) {


                //System.out.println(ontid);
                //String hash = ontSdk.getOntIdTx().updateAttribute(ontid, "passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
                //System.out.println("hash:" + hash);


                String password = "passwordtest";

                if(true) {
                    Account info1 = null;
                    Account info2 = null;
                    Account info3 = null;
                    if (ontSdk.getWalletMgr().getAccounts().size() < 3) {
                        info1 = ontSdk.getWalletMgr().createAccountFromPrikey("passwordtest", "9a31d585431ce0aa0aab1f0a432142e98a92afccb7bcbcaff53f758df82acdb3");
                        info2 = ontSdk.getWalletMgr().createAccount("passwordtest");
                        info3 = ontSdk.getWalletMgr().createAccount("passwordtest");
                        ontSdk.getWalletMgr().writeWallet();
                    }
                    info1 = ontSdk.getWalletMgr().getAccounts().get(0);
                    info2 = ontSdk.getWalletMgr().getAccounts().get(1);
                    Transaction tx = ontSdk.getOntAssetTx().makeTransfer("ont", info1.address, "passwordtest", info2.address, 100L);
                    ontSdk.signTx(tx, info1.address, password);
                    System.out.println(tx.toHexString());
                    ontSdk.getConnectMgr().sendRawTransaction(tx.toHexString());
                }

                if(false) {
                    String attri = "attri";
                    Map recordMap = new HashMap();
                    recordMap.put("key0", "world0");
                    recordMap.put("key1", i);
                    recordMap.put("keyNum", 1234589);
                    recordMap.put("key2", false);
                    ontSdk.setCodeAddress("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92");
                    Transaction tx = ontSdk.getOntIdTx().makeUpdateAttribute(ontid, "passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
                    ontSdk.signTx(tx, ontid, password);
                    ontSdk.getWebSocket().sendRawTransaction(tx.toHexString());
                }
                //waitResult(ontSdk, lock);
                Thread.sleep(5000);
            }

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitResult(OntSdk ontSdk, Object lock) {
        try {
            synchronized (lock) {
                //System.out.println("\nwait begin " + new Date().toString());
                boolean flag = false;
                while (true) {
                    lock.wait();
                    if (MsgQueue.getChangeFlag()) {
                        System.out.println(MsgQueue.getHeartBeat());
                    }

                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println("RECV: " + e);
                        Result rt = JSON.parseObject(e, Result.class);
                        //TODO
                        MsgQueue.removeResult(e);
                        if (rt.Action.equals("InvokeTransaction")) {
                        }
                    }
                }
                //System.out.println("wait end  " +  new Date().toString()+"\n");
            }
        } catch (InterruptedException e) {
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
        wm.setWesocket(wsUrl,lock);
        wm.setDefaultConnect(wm.getWebSocket());

        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
