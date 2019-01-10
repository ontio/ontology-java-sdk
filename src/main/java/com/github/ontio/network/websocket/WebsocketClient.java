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

package com.github.ontio.network.websocket;

import com.alibaba.fastjson.JSON;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.exception.ConnectorException;
import okhttp3.*;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class WebsocketClient extends AbstractConnector {
    private WebSocket mWebSocket = null;
    private Object lock;
    private boolean logFlag;
    private long reqId = 0;
    public static String wsUrl = "";
    private WebsocketClient wsClient = null;

    public WebsocketClient(String url,Object lock) {
        wsUrl = url;
        this.lock = lock;
        wsClient = this;
    }


    public void setLog(boolean b) {
        logFlag = b;
    }

    public void startWebsocketThread(boolean log) {
        this.logFlag = log;
        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        wsClient.wsStart();
                    }
                });
        thread.start();
    }
    @Override
    public String getUrl(){
        return wsUrl;
    }

    public void sendHeartBeat() {
        Map map = new HashMap<>();
        map.put("Action", "heartbeat");
        map.put("Version", "V1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
    }
    public void sendSubscribe(Map map) {
        map.put("Action", "subscribe");
        map.put("Version", "V1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
    }
    public void send(Map map) {
        mWebSocket.send(JSON.toJSONString(map));
    }
    public void setReqId(long reqId){
        this.reqId = reqId;
    }
    private long generateReqId(){
        if(reqId == 0) {
            return new Random().nextInt() & Integer.MAX_VALUE;
        }
        return reqId;
    }
    @Override
    public Object sendRawTransaction(boolean preExec,String userid,String hexData) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "sendrawtransaction");
        map.put("Version", "1.0.0");
        map.put("Data", hexData);
        map.put("Id", generateReqId());
        if(preExec){
            map.put("PreExec", "1");
        }
        mWebSocket.send(JSON.toJSONString(map));
        if(preExec){
            return "0";
        }
        return "";
    }
    @Override
    public Object sendRawTransaction(String hexData) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "sendrawtransaction");
        map.put("Version", "1.0.0");
        map.put("Data", hexData);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public Transaction getRawTransaction(String txhash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "gettransaction");
        map.put("Version", "1.0.0");
        map.put("Hash", txhash);
        map.put("Raw", "1");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Object getRawTransactionJson(String txhash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "gettransaction");
        map.put("Version", "1.0.0");
        map.put("Hash", txhash);
        map.put("Raw", "0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public int getNodeCount() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getconnectioncount");
        map.put("Version", "1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }
    @Override
    public int getBlockHeight() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockheight");
        map.put("Version", "1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }
    @Override
    public Block getBlock(int height) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockbyheight");
        map.put("Version", "1.0.0");
        map.put("Height",height);
        map.put("Raw","1");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Block getBlock(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockbyhash");
        map.put("Version", "1.0.0");
        map.put("Hash",hash);
        map.put("Raw","1");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }

    @Override
    public String getBlockBytes(int height) throws ConnectorException, IOException {
        Map map = new HashMap<>();
        map.put("Action", "getblockbyheight");
        map.put("Version", "1.0.0");
        map.put("Height",height);
        map.put("Raw","1");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }

    @Override
    public String getBlockBytes(String hash) throws ConnectorException, IOException {
        Map map = new HashMap<>();
        map.put("Action", "getblockbyhash");
        map.put("Version", "1.0.0");
        map.put("Hash",hash);
        map.put("Raw","1");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }

    @Override
    public Block getBlockJson(int height) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockbyheight");
        map.put("Version", "1.0.0");
        map.put("Height",height);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Block getBlockJson(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockbyhash");
        map.put("Version", "1.0.0");
        map.put("Hash",hash);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Object getBalance(String address) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getbalance");
        map.put("Version", "1.0.0");
        map.put("Addr",address);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Object getContract(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getcontract");
        map.put("Version", "1.0.0");
        map.put("Raw","1");
        map.put("Hash", hash);
        map.put("Id", generateReqId());
        return mWebSocket.send(JSON.toJSONString(map));
    }
    @Override
    public Object getContractJson(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getcontract");
        map.put("Version", "1.0.0");
        map.put("Raw","0");
        map.put("Hash", hash);
        map.put("Id", generateReqId());
        return mWebSocket.send(JSON.toJSONString(map));
    }
    @Override
    public Object getSmartCodeEvent(int height) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getsmartcodeeventbyheight");
        map.put("Version", "1.0.0");
        map.put("Height", height);
        map.put("Id", generateReqId());
        return mWebSocket.send(JSON.toJSONString(map));
    }
    @Override
    public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getsmartcodeeventbyhash");
        map.put("Version", "1.0.0");
        map.put("Hash", hash);
        return mWebSocket.send(JSON.toJSONString(map));
    }
    @Override
    public int getBlockHeightByTxHash(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockheightbytxhash");
        map.put("Version", "1.0.0");
        map.put("Hash", hash);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }

    @Override
    public String getStorage(String codehash, String key) throws ConnectorException, IOException {
        Map map = new HashMap<>();
        map.put("Action", "getstorage");
        map.put("Version", "1.0.0");
        map.put("Hash", codehash);
        map.put("Key", key);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public Object getMerkleProof(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getmerkleproof");
        map.put("Version", "1.0.0");
        map.put("Hash", hash);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public String getAllowance(String asset,String from,String to) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getallowance");
        map.put("Version", "1.0.0");
        map.put("Asset", asset);
        map.put("From", from);
        map.put("To", to);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public Object getMemPoolTxCount() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getmempooltxcount");
        map.put("Version", "1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public Object getMemPoolTxState(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getmempooltxstate");
        map.put("Version", "1.0.0");
        map.put("Hash", hash);
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public String getVersion() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getversion");
        map.put("Version", "1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }

    @Override
    public String getGrantOng(String address) throws ConnectorException, IOException {
        Map map = new HashMap<>();
        map.put("Action", "getgrantong");
        map.put("Version", "1.0.0");
        map.put("Id", generateReqId());
        map.put("Addr", address);
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }

    @Override
    public int getNetworkId() throws ConnectorException, IOException {
        Map map = new HashMap<>();
        map.put("Action", "getnetworkid");
        map.put("Version", "1.0.0");
        map.put("Id", generateReqId());
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }

    @Override
    public String getSideChainData(int sideChainID) throws ConnectorException, IOException {
        return null;
    }

    public void wsStart() {
        //request = new Request.Builder().url(WS_URL).build();
        String httpUrl = null;
        if (wsUrl.contains("wss")) {
            httpUrl = "https://" + wsUrl.split("://")[1];
        } else {
            httpUrl = "http://" + wsUrl.split("://")[1];
        }
        Request request = new Request.Builder().url(wsUrl).addHeader("Origin", httpUrl).build();
        OkHttpClient mClient = new OkHttpClient.Builder().build();
        mWebSocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("opened websocket connection");
                sendHeartBeat();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        sendHeartBeat();
                    }
                }, 1000, 30000);
            }

            @Override
            public void onMessage(WebSocket webSocket, String s) {
                if (logFlag) {
                    System.out.println("websoket onMessage:" + s);
                }
                Result result = JSON.parseObject(s, Result.class);
                try {
                    synchronized (lock) {
                        MsgQueue.addResult(result);
                        lock.notify();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                System.out.println(reason);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                System.out.println("close:" + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.out.println("onFailure:" + response);
                wsStart();
            }
        });

    }
}

