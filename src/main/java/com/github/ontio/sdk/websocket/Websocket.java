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

package com.github.ontio.sdk.websocket;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import okhttp3.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class Websocket {

    private OkHttpClient mClient;
    private Request request;
    private WebSocket mWebSocket = null;
    private Object lock;
    private boolean logFlag;
    private boolean broadcastFlag;
    private OntSdk sdk = null;
    public static String wsUrl = "";
    private Websocket wsProcess = null;

    public Websocket(Object lock, String url, OntSdk sdk) {
        wsUrl = url;
        this.lock = lock;
        this.sdk = sdk;
        wsProcess = this;
    }

    public void setBroadcast(boolean b) {
        broadcastFlag = b;
    }

    public void setLog(boolean b) {
        logFlag = b;
    }

    public void startWebsocketThread() {

        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        wsProcess.wsStart();
                    }
                });
        thread.start();
    }

    public void sendRawTransaction(String data) {
        Map map = new HashMap<>();
        map.put("Action", "sendrawtransaction");
        map.put("Version", "1.0.0");
        map.put("Data", data);
        mWebSocket.send(JSON.toJSONString(map));
    }

    public void wsStart() {
        //request = new Request.Builder().url(WS_URL).build();
        String httpUrl = null;
        if (wsUrl.contains("wss")) {
            httpUrl = "https://" + wsUrl.split("://")[1];
        } else {
            httpUrl = "http://" + wsUrl.split("://")[1];
        }
        request = new Request.Builder().url(wsUrl).addHeader("Origin", httpUrl).build();
        mClient = new OkHttpClient.Builder().build();
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

