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
import com.github.ontio.OntSdk;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.exception.ConnectorException;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class WebsocketClient extends AbstractConnector {
    private WebSocket mWebSocket = null;
    private Object lock;
    private boolean logFlag;
    private boolean broadcastFlag;
    public static String wsUrl = "";
    private WebsocketClient wsClient = null;

    public WebsocketClient(String url,Object lock) {
        wsUrl = url;
        this.lock = lock;
        wsClient = this;
    }

    public void setBroadcast(boolean b) {
        broadcastFlag = b;
    }

    public void setLog(boolean b) {
        logFlag = b;
    }

    public void startWebsocketThread(boolean log,boolean broadcastFlag) {
        this.logFlag = log;
        this.broadcastFlag = broadcastFlag;
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
    @Override
    public Object sendRawTransaction(boolean preExec,String userid,String hexData) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "sendrawtransaction");
        map.put("Version", "1.0.0");
        map.put("Userid", userid);
        map.put("Data", hexData);
        if(preExec){
            map.put("PreExec", "1");
        }
        mWebSocket.send(JSON.toJSONString(map));
        return "";
    }
    @Override
    public Object sendRawTransaction(String hexData) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "sendrawtransaction");
        map.put("Version", "1.0.0");
        map.put("Data", hexData);
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
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public int getGenerateBlockTime() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getgenerateblocktime");
        map.put("Version", "1.0.0");
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }
    @Override
    public int getNodeCount() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getconnectioncount");
        map.put("Version", "1.0.0");
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }
    @Override
    public int getBlockHeight() throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblockcount");
        map.put("Version", "1.0.0");
        mWebSocket.send(JSON.toJSONString(map));
        return 0;
    }
    @Override
    public Block getBlock(int height) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblock");
        map.put("Version", "1.0.0");
        map.put("Height",height);
        map.put("Raw","1");
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Block getBlock(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblock");
        map.put("Version", "1.0.0");
        map.put("Hash",hash);
        map.put("Raw","1");
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Block getBlockJson(int height) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblock");
        map.put("Version", "1.0.0");
        map.put("Height",height);
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Block getBlockJson(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblock");
        map.put("Version", "1.0.0");
        map.put("Hash",hash);
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }
    @Override
    public Object getBalance(String address) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getblock");
        map.put("Version", "1.0.0");
        map.put("Addr",address);
        mWebSocket.send(JSON.toJSONString(map));
        return null;
    }

    @Override
    public Object getContractJson(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getcontract");
        map.put("Version", "1.0.0");
        map.put("Hash", hash);
        return mWebSocket.send(JSON.toJSONString(map));
    }
    @Override
    public Object getSmartCodeEvent(int height) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getsmartcodeevent");
        map.put("Version", "1.0.0");
        map.put("Height", height);
        return mWebSocket.send(JSON.toJSONString(map));
    }
    @Override
    public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException{
        Map map = new HashMap<>();
        map.put("Action", "getsmartcodeevent");
        map.put("Version", "1.0.0");
        map.put("Hash", hash);
        return mWebSocket.send(JSON.toJSONString(map));
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
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (broadcastFlag) {
                            mWebSocket.send("{\"Action\":\"heartbeat\",\"Broadcast\":true}");
                            return;
                        }
                        mWebSocket.send("{\"Action\":\"heartbeat\"}");
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
                    //TODO
                    synchronized (lock) {
                        if (result.Action.equals("heartbeat")) {
                            if (MsgQueue.addHeartBeat(result)) {
                                lock.notify();
                                MsgQueue.setChangeFlag(false);
                            }
                        } else {
                            //System.out.println("onMessage:"+s);
                            MsgQueue.addResult(result);
                            lock.notify();
                        }

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

