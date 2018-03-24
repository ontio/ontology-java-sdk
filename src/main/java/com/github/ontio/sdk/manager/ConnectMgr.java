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

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Helper;
import com.github.ontio.core.Block;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.rpc.RpcClient;
import com.github.ontio.core.Blockchain;
import com.github.ontio.core.Transaction;
import com.github.ontio.network.connect.ConnectorException;
import com.github.ontio.network.connect.IConnector;
import com.github.ontio.network.rest.RestBlockchain;
import com.github.ontio.network.rest.RestClient;
import com.github.ontio.network.rest.Result;

/**
 * 连接管理器
 *
 * @author 12146
 */
public class ConnectMgr {
    private IConnector connector;

    public ConnectMgr(String url) {
        setConnector(new RestClient(url));
        Blockchain.register(new RestBlockchain(new RestClient(url)));
    }

    public ConnectMgr(String url, boolean rpc) {
        if (rpc) {
            setConnector(new RpcClient(url));
        } else {
            setConnector(new RestClient(url));
            Blockchain.register(new RestBlockchain(new RestClient(url)));
        }
    }

    public ConnectMgr(String url, String token) {
        setConnector(new RestClient(url, token));
        Blockchain.register(new RestBlockchain(new RestClient(url, token)));
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
        String rs = connector.sendRawTransaction(Helper.toHexString(tx.toArray()));
        if (connector instanceof RpcClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public boolean sendRawTransaction(String hexData) throws ConnectorException, IOException {
        String rs = connector.sendRawTransaction(hexData);
        if (connector instanceof RpcClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public boolean sendRawTransaction(String uuid, String hexData) throws ConnectorException, IOException {
        String rs = connector.sendRawTransaction(false, uuid, hexData);
        if (connector instanceof RpcClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return true;
        }
        return false;
    }

    public Object sendRawTransactionPreExec(String hexData) throws ConnectorException, IOException {
        String rs = connector.sendRawTransaction(true, null, hexData);
        if (connector instanceof RpcClient) {
            return true;
        }
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        return null;
    }

    public Transaction getRawTransaction(String txhash) throws ConnectorException, IOException {
        return connector.getRawTransaction(txhash);
    }

    public String getTransaction(String txhash) throws ConnectorException, IOException {
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
//	public long getBalances(String address) throws ConnectorException, IOException {
//		return connector.getBalances(address);
//	}

//	public String getDDO(String codehash, String ontid) throws ConnectorException, IOException {
//		return connector.getDDO(codehash, ontid);
//	}

//	public String getRawTransactionJson(String txhash) throws ConnectorException, IOException {
//		return connector.getRawTransactionJson(txhash);
//	}

    public String getBlockJson(int height) throws ConnectorException, IOException {
        return connector.getBlockJson(height);
    }

    public String getBlockJson(String hash) throws ConnectorException, IOException {
        return connector.getBlockJson(hash);
    }

    public Object getSmartCodeEvent(int height) throws ConnectorException, IOException {
        return connector.getSmartCodeEvent(height);
    }

    public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException {
        return connector.getSmartCodeEvent(hash);
    }
}


