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

package com.github.ontio.network.rest;

import java.io.IOException;

import com.github.ontio.common.ErrorCode;
import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.core.transaction.Transaction;

import com.alibaba.fastjson.JSON;
import com.github.ontio.network.exception.RestfulException;

public class RestClient extends AbstractConnector {
    private Interfaces api;
    private String version = "v1.0.0", action = "sendrawtransaction";

    public RestClient(String restUrl) {
        api = new Interfaces(restUrl);
    }

    @Override
    public String getUrl() {
        return api.getUrl();
    }

    @Override
    public String sendRawTransaction(String hexData) throws RestfulException {
        String rs = api.sendTransaction(false, null, action, version, hexData);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rs;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public String sendRawTransaction(boolean preExec, String userid, String hexData) throws RestfulException {
        String rs = api.sendTransaction(preExec, userid, action, version, hexData);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rs;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Transaction getRawTransaction(String txhash) throws RestfulException {
        String rs = api.getTransaction(txhash, true);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            try {
                return Transaction.deserializeFrom(Helper.hexToBytes((String) rr.Result));
            } catch (IOException e) {
                throw new RestfulException(ErrorCode.TxDeserializeError, e);
            }
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public int getNodeCount() throws RestfulException {
        String rs = api.getNodeCount();
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (int) rr.Result;
        }
        throw new RestfulException(to(rr));

    }

    @Override
    public int getBlockHeight() throws RestfulException {
        String rs = api.getBlockHeight();
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (int) rr.Result;
        }
        throw new RestfulException(to(rr));

    }

    @Override
    public Block getBlock(int height) throws RestfulException {
        String rs = api.getBlock(height, "1");
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            try {
                return Serializable.from(Helper.hexToBytes((String) rr.Result), Block.class);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RestfulException(ErrorCode.BlockDeserializeError, e);
            }
        }
        throw new RestfulException(to(rr));
    }


    @Override
    public Block getBlock(String hash) throws RestfulException {
        String rs = api.getBlock(hash, "1");
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error != 0) {
            throw new RestfulException(to(rr));
        }
        try {
            return Serializable.from(Helper.hexToBytes((String) rr.Result), Block.class);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RestfulException(ErrorCode.BlockDeserializeError, e);
        }
    }
    @Override
    public String getBlockBytes(int height) throws ConnectorException, IOException {
        String rs = api.getBlock(height, "1");
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error != 0) {
            throw new RestfulException(to(rr));
        }
        return (String) rr.Result;
    }

    @Override
    public String getBlockBytes(String hash) throws ConnectorException, IOException {
        String rs = api.getBlock(hash, "1");
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error != 0) {
            throw new RestfulException(to(rr));
        }
        return (String) rr.Result;
    }

    @Override
    public Object getBalance(String address) throws RestfulException {
        String rs = api.getBalance(address);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Object getRawTransactionJson(String txhash) throws RestfulException,IOException {
        String rs = api.getTransaction(txhash, true);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return JSON.toJSONString(Transaction.deserializeFrom(Helper.hexToBytes((String) rr.Result)).json());
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Object getBlockJson(int height) throws RestfulException {
        String rs = api.getBlock(height, "0");
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Object getBlockJson(String hash) throws RestfulException {
        String rs = api.getBlock(hash, "0");
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));

    }

    @Override
    public Object getContract(String hash) throws RestfulException {
        String rs = api.getContract(hash);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Object getContractJson(String hash) throws RestfulException {
        String rs = api.getContractJson(hash);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Object getSmartCodeEvent(int height) throws ConnectorException, IOException {
        String rs = api.getSmartCodeEvent(height);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));

    }

    @Override
    public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException {
        String rs = api.getSmartCodeEvent(hash);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public int getBlockHeightByTxHash(String hash) throws ConnectorException, IOException {
        String rs = api.getBlockHeightByTxHash(hash);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (int)rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public String getStorage(String codehash,String key) throws ConnectorException, IOException {
        String rs = api.getStorage(codehash,key);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (String)rr.Result;
        }
        throw new RestfulException(to(rr));
    }
    @Override
    public Object getMerkleProof(String hash) throws ConnectorException, IOException{
        String rs = api.getMerkleProof(hash);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }
    @Override
    public String getAllowance(String asset,String from,String to) throws ConnectorException, IOException{
        String rs = api.getAllowance(asset,from,to);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (String)rr.Result;
        }
        throw new RestfulException(to(rr));
    }
    @Override
    public Object getMemPoolTxCount() throws ConnectorException, IOException{
        String rs = api.getMemPoolTxCount();
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public Object getMemPoolTxState(String hash) throws ConnectorException, IOException{
        String rs = api.getMemPoolTxState(hash);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return rr.Result;
        }
        throw new RestfulException(to(rr));
    }
    @Override
    public String getVersion() throws ConnectorException, IOException{
        String rs = api.getVersion();
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (String)rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public String getGrantOng(String address) throws ConnectorException, IOException {
        String rs = api.getGrantOng(address);
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (String)rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public int getNetworkId() throws ConnectorException, IOException {
        String rs = api.getNetworkId();
        Result rr = JSON.parseObject(rs, Result.class);
        if (rr.Error == 0) {
            return (int)rr.Result;
        }
        throw new RestfulException(to(rr));
    }

    @Override
    public String getSideChainData(int sideChainID) throws ConnectorException, IOException {
        return null;
    }

    private String to(Result rr) {
        return JSON.toJSONString(rr);
    }
}




