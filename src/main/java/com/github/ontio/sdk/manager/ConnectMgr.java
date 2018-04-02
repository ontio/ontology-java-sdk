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

package com.github.ontio.sdk.manager;

import java.io.IOException;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.network.connect.IConnector;
import com.github.ontio.network.rest.RestClient;
import com.github.ontio.network.rest.Result;
import com.github.ontio.network.websocket.WebsocketClient;

/**
 *
 */
public class ConnectMgr {
    private IConnector connector;
    public ConnectMgr(String url, String type, Object lock) {
        if (type.equals("websocket")){
            setConnector(new WebsocketClient(url,lock));
        }
    }
    public ConnectMgr(String url, String type) {
        if (type.equals("rpc")) {
            setConnector(new RpcClient(url));
        } else if (type.equals("restful")){
            setConnector(new RestClient(url));
        }
    }
    public void startWebsocketThread(boolean log){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).startWebsocketThread(log);
        }
    }
    public void send(Map map){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).send(map);
        }
    }
    public void sendHeartBeat(Map map){
        if(connector instanceof WebsocketClient){
            ((WebsocketClient) connector).sendHeartBeat(map);
        }
    }
    public ConnectMgr(IConnector connector) {
        setConnector(connector);
    }

    public void setConnector(IConnector connector) {
        this.connector = connector;
    }

    private String getUrl() {
        return connector.getUrl();
    }

    public boolean sendRawTransaction(Transaction tx) throws ConnectorException, IOException {
        String rs = (String) connector.sendRawTransaction(Helper.toHexString(tx.toArray()));
        if (connector instanceof RpcClient) {
            return true;
        }
        if (connector instanceof WebsocketClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public boolean sendRawTransaction(String hexData) throws ConnectorException, IOException {
        String rs = (String) connector.sendRawTransaction(hexData);
        if (connector instanceof RpcClient) {
            return true;
        }
        if (connector instanceof WebsocketClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public Object sendRawTransactionPreExec(String hexData) throws ConnectorException, IOException {
        Object rs = connector.sendRawTransaction(true, null, hexData);
        if (connector instanceof RpcClient) {
            return rs;
        }
        if (connector instanceof WebsocketClient) {
            return rs;
        }
        Result rr = JSON.parseObject((String) rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        return null;
    }

    public Transaction getTransaction(String txhash) throws ConnectorException, IOException {
        txhash = txhash.replace("0x","");
        return connector.getRawTransaction(txhash);
    }

    public Object getTransactionJson(String txhash) throws ConnectorException, IOException {
        txhash = txhash.replace("0x","");
        return connector.getRawTransactionJson(txhash);
    }

    public int getGenerateBlockTime() throws ConnectorException, IOException {
        return connector.getGenerateBlockTime();
    }

    public int getNodeCount() throws ConnectorException, IOException {
        return connector.getNodeCount();
    }

    public int getBlockHeight() throws ConnectorException, IOException {
        return connector.getBlockHeight();
    }

    public Block getBlock(int height) throws ConnectorException, IOException {
        return connector.getBlock(height);
    }

    public Block getBlock(String hash) throws ConnectorException, IOException {
        return connector.getBlock(hash);

    }

    public Object getBalance(String address) throws ConnectorException, IOException {
        return connector.getBalance(address);
    }

    public Object getBlockJson(int height) throws ConnectorException, IOException {
        return connector.getBlockJson(height);
    }

    public Object getBlockJson(String hash) throws ConnectorException, IOException {
        return connector.getBlockJson(hash);
    }

    public Object getContract(String hash) throws ConnectorException, IOException {
        hash = hash.replace("0x","");
        return connector.getContract(hash);
    }
    public Object getContractJson(String hash) throws ConnectorException, IOException {
        hash = hash.replace("0x","");
        return connector.getContractJson(hash);
    }

    public Object getSmartCodeEvent(int height) throws ConnectorException, IOException {
        return connector.getSmartCodeEvent(height);
    }

    public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException {
        return connector.getSmartCodeEvent(hash);
    }

    public int getBlockHeightByTxHash(String hash) throws ConnectorException, IOException {
        hash = hash.replace("0x", "");
        return connector.getBlockHeightByTxHash(hash);
    }

    public String getStorage(String codehash, String key) throws ConnectorException, IOException {
        codehash = codehash.replace("0x", "");
        return connector.getStorage(codehash, key);
    }
}


