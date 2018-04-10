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
import com.github.ontio.common.Address;
import com.github.ontio.common.Helper;
import com.github.ontio.core.VmType;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.websocket.WebsocketClient;
import com.github.ontio.sdk.abi.AbiFunction;
import com.github.ontio.sdk.abi.AbiInfo;
import com.github.ontio.sdk.wallet.Account;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.sdk.wallet.Wallet;
import com.github.ontio.network.websocket.MsgQueue;
import com.github.ontio.network.websocket.Result;
import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;
import java.io.InputStream;
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


            ontSdk.getWebSocket().startWebsocketThread(false);
//            ontSdk.getWebSocket().setBroadcast(true);

            Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            waitResult(lock);
                        }
                    });
            thread.start();
            Thread.sleep(5000);

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


            for (int i = 0; i >= 0; i++) {

                //System.out.println(ontid);
                //String hash = ontSdk.getOntIdTx().updateAttribute(ontid, "passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
                //System.out.println("hash:" + hash);


                String password = "passwordtest";

                if (false) {
                    Account info1 = null;
                    Account info2 = null;
                    Account info3 = null;
                    if (ontSdk.getWalletMgr().getAccounts().size() < 3) {
                        info1 = ontSdk.getWalletMgr().createAccountFromPriKey("passwordtest", "9a31d585431ce0aa0aab1f0a432142e98a92afccb7bcbcaff53f758df82acdb3");
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

                if (false) {
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

                if (false) {
                    ontSdk.getConnectMgr().getBalance("TA63xZXqdPLtDeznWQ6Ns4UsbqprLrrLJk");
                    ontSdk.getConnectMgr().getBlockJson("c8c165bf0ac6107f7f324b0badb60af4dc4e1157b5eb9d3163c8f332a8612c98");
                    ontSdk.getConnectMgr().getNodeCount();
                    ontSdk.getConnectMgr().getGenerateBlockTime();
                    ontSdk.getConnectMgr().getContractJson("80e7d2fc22c24c466f44c7688569cc6e6d6c6f92");
                    ontSdk.getConnectMgr().getSmartCodeEvent("7c3e38afb62db28c7360af7ef3c1baa66aeec27d7d2f60cd22c13ca85b2fd4f3");
                    ontSdk.getConnectMgr().getBlockHeightByTxHash("7c3e38afb62db28c7360af7ef3c1baa66aeec27d7d2f60cd22c13ca85b2fd4f3");
                    ontSdk.getConnectMgr().getStorage("ff00000000000000000000000000000000000001", Address.decodeBase58("TA63xZXqdPLtDeznWQ6Ns4UsbqprLrrLJk").toHexString());
                    ontSdk.getConnectMgr().getTransactionJson("7c3e38afb62db28c7360af7ef3c1baa66aeec27d7d2f60cd22c13ca85b2fd4f3");
                }
                if (false) {
                    ontSdk.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");
                    InputStream is = new FileInputStream("C:\\ZX\\huguanjun.abi.json");//IdContract
                    byte[] bys = new byte[is.available()];
                    is.read(bys);
                    is.close();
                    String abi = new String(bys);

                    AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
                    //System.out.println("Entrypoint:" + abiinfo.getEntrypoint());
                    //System.out.println("Functions:" + abiinfo.getFunctions());

                    AbiFunction func0 = abiinfo.getFunction("Put");
                    Identity did0 = ontSdk.getWalletMgr().getIdentitys().get(0);
                    func0.setParamsValue("key".getBytes(), "value".getBytes());
                    String hash0 = ontSdk.getSmartcodeTx().sendInvokeSmartCodeWithSign(did0.ontid, "passwordtest", func0, (byte) VmType.NEOVM.value());

                    System.out.println(hash0);
                }
                if(true){
                    Map map = new HashMap();
                    if(i >0) {
                        map.put("SubscribeEvent", true);
                        map.put("SubscribeRawBlock", false);
                    }else{
                        map.put("SubscribeJsonBlock", false);
                        map.put("SubscribeRawBlock", true);
                    }
                    //System.out.println(map);
                    ontSdk.getWebSocket().setReqId(i);
                    ontSdk.getWebSocket().sendHeartBeat(map);
//                    ontSdk.getWebSocket().getBlockHeight();
                }
                Thread.sleep(6000);
            }

            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitResult(Object lock) {
        try {
            synchronized (lock) {
                while (true) {
                    lock.wait();
                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println("RECV: " + e);
                        Result rt = JSON.parseObject(e, Result.class);
                        //TODO
                        MsgQueue.removeResult(e);
                        if (rt.Action.equals("getblockbyheight")) {
                            Block bb = Serializable.from(Helper.hexToBytes((String) rt.Result), Block.class);
                            //System.out.println(bb.json());
                        }
                    }
                }
            }
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
        String wsUrl = ip + ":" + "20335";

        OntSdk wm = OntSdk.getInstance();
        wm.setRpc(rpcUrl);
        wm.setRestful(restUrl);
        wm.setWesocket(wsUrl, lock);
        wm.setDefaultConnect(wm.getWebSocket());

        wm.openWalletFile("OntAssetDemo.json");
        return wm;
    }
}
