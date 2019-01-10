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

package com.github.ontio.network.rpc;

import java.io.IOException;
import java.net.MalformedURLException;

import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.block.Block;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.network.exception.RpcException;

public class RpcClient extends AbstractConnector {
    private Interfaces rpc;

    public RpcClient(String url) {
        try {
            this.rpc = new Interfaces(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getUrl() {
        return rpc.getHost();
    }

    @Override
    public Object getBalance(String address) throws ConnectorException,IOException {
        Object result = null;
        result = rpc.call("getbalance", address);
        return result;
    }

    @Override
    public String sendRawTransaction(String sData) throws RpcException, IOException {
        Object result = rpc.call("sendrawtransaction", sData);
        return (String) result;
    }

    @Override
    public Object sendRawTransaction(boolean preExec, String userid, String sData) throws RpcException, IOException {
        Object result = null;
        if(preExec){
            result = rpc.call("sendrawtransaction", sData,1);
        }else {
            result = rpc.call("sendrawtransaction", sData);
        }
        return result;
    }

    @Override
    public Transaction getRawTransaction(String txhash) throws RpcException, IOException {
        Object result = rpc.call("getrawtransaction", txhash.toString());
        return Transaction.deserializeFrom(Helper.hexToBytes((String) result));
    }

    @Override
    public Object getRawTransactionJson(String txhash) throws RpcException, IOException {
        Object result = null;
        result = rpc.call("getrawtransaction", txhash.toString());
        return Transaction.deserializeFrom(Helper.hexToBytes((String) result)).json();
    }

    @Override
    public int getNodeCount() throws RpcException, IOException {
        Object result = rpc.call("getconnectioncount");
        return (int) result;
    }

    @Override
    public int getBlockHeight() throws RpcException, IOException {
        Object result = rpc.call("getblockcount");
        return (int) result - 1;
    }

    @Override
    public Object getBlockJson(int index) throws RpcException,IOException {
        Object result = null;
        result = rpc.call("getblock", index, 1);
        return result;
    }

    @Override
    public Object getBlockJson(String hash) throws RpcException,IOException {
        Object result = null;
        result = rpc.call("getblock", hash, 1);
        return result;
    }

    @Override
    public Object getContract(String hash) throws RpcException,IOException {
        Object result = null;
        result = rpc.call("getcontractstate", hash);
        return result;
    }

    @Override
    public Object getContractJson(String hash) throws RpcException,IOException {
        Object result = null;
        result = rpc.call("getcontractstate", hash,1);
        return result;
    }

    public String getRawTransaction(UInt256 txhash) throws RpcException, IOException {
        Object result = rpc.call("getrawtransaction", txhash.toString());
        return (String) result;
    }


    public Block getBlock(UInt256 hash) throws RpcException, IOException {
        Object result = rpc.call("getblock", hash.toString());
        try {
            Block bb = Serializable.from(Helper.hexToBytes((String) result), Block.class);
            return bb;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Block getBlock(int index) throws RpcException, IOException {
        Object result = rpc.call("getblock", index);
        try {
            Block bb = Serializable.from(Helper.hexToBytes((String) result), Block.class);
            return bb;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int getBlockCount() throws RpcException, IOException {
        Object result = rpc.call("getblockcount");
        return (int) result;
    }

    @Override
    public Block getBlock(String hash) throws ConnectorException, IOException {
        Object result = rpc.call("getblock", hash.toString());
        try {
            Block bb = Serializable.from(Helper.hexToBytes((String) result), Block.class);
            return bb;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBlockBytes(int height) throws ConnectorException, IOException {
        Object result = rpc.call("getblock",height);
        return (String) result;
    }

    @Override
    public String getBlockBytes(String hash) throws ConnectorException, IOException {
        Object result = rpc.call("getblock",hash);
        return (String) result;
    }

    @Override
    public Object getSmartCodeEvent(int height) throws ConnectorException, IOException {
        Object result = rpc.call("getsmartcodeevent", height);
        try {
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException {
        Object result = rpc.call("getsmartcodeevent", hash.toString());
        try {
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getBlockHeightByTxHash(String hash) throws ConnectorException, IOException {
        Object result = rpc.call("getblockheightbytxhash", hash.toString());
        try {
            return (int)result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getStorage(String codehash, String key) throws ConnectorException, IOException {
        Object result = rpc.call("getstorage", codehash, key);
        try {
            return (String)result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Object getMerkleProof(String hash) throws ConnectorException, IOException {
        Object result = rpc.call("getmerkleproof", hash);
        try {
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getAllowance(String asset,String from,String to) throws ConnectorException, IOException {
        Object result = rpc.call("getallowance", asset,from,to);
        try {
            return (String)result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Object getMemPoolTxCount() throws ConnectorException, IOException {
        Object result = rpc.call("getmempooltxcount");
        try {
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Object getMemPoolTxState(String hash) throws ConnectorException, IOException {
        Object result = rpc.call("getmempooltxstate", hash);
        try {
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getVersion() throws ConnectorException, IOException {
        Object result = rpc.call("getversion");
        try {
            return (String)result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getGrantOng(String address) throws ConnectorException, IOException {
        Object result = rpc.call("getgrantong",address);
        try {
            return (String) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getNetworkId() throws ConnectorException, IOException {
        Object result = rpc.call("getnetworkid");
        try {
            return (int)result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSideChainData(int sideChainID) throws ConnectorException, IOException {
        Object result = rpc.call("getsidechaindata", sideChainID);
        try {
            return (String) result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

