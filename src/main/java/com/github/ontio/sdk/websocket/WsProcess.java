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
import okhttp3.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class WsProcess {

    private OkHttpClient mOkHttpClient;
    private Request request;
    private WebSocket mWebSocket = null;
    private Object lock;
    private boolean logFlag;
    private static boolean broadcastFlag;
    public static String wsUrl = "";

    public WsProcess(Object lock,String url,boolean logFlag){
        wsUrl  = url;
        this.lock = lock;
        this.logFlag = logFlag;
    }
    public static void startWebsocketThread(final Object lock,final String url){
        startWebsocketThread(lock,url,false);
    }
    public static void setBroadcast(final boolean b){
        broadcastFlag = b;
    }
    public static void startWebsocketThread(final Object lock,final String url,final boolean eventLog) {

        Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        new WsProcess(lock,url,eventLog).wsStart();
                    }
                });
        thread.start();
    }
    public  void wsStart() {
        //request = new Request.Builder().url(WS_URL).build();
        String httpUrl = null;
        if(wsUrl.contains("wss")){
            httpUrl = "https://" + wsUrl.split("://")[1];
        }else {
            httpUrl = "http://" + wsUrl.split("://")[1];
        }
        request = new Request.Builder().url(wsUrl).addHeader("Origin", httpUrl).build();
        mOkHttpClient = new OkHttpClient.Builder().build();
        mWebSocket = mOkHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                System.out.println("opened websocket connection");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(broadcastFlag) {
                            mWebSocket.send("{\"Action\":\"heartbeat\",\"Broadcast\":true}");
                            return;
                        }
                        mWebSocket.send("{\"Action\":\"heartbeat\"}");
                    }
                }, 1000, 30000);
            }

            @Override
            public void onMessage(WebSocket webSocket, String s) {
                if(logFlag) {
                    System.out.println("websoket onMessage:" + s);
                }
                Result result = JSON.parseObject(s, Result.class);
                try {
                    //TODO
                    synchronized (lock) {
                        if(result.Action.equals("heartbeat")) {
                            if(MsgQueue.addHeartBeat(result)){
                                lock.notify();
                            }
                        }else {
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
                System.out.println("close:"+reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                System.out.println("fail:"+response);
                wsStart();
            }
        });

    }
}

