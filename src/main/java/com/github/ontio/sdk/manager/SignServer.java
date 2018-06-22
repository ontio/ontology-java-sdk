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

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.ErrorCode;
import com.github.ontio.network.exception.RpcException;
import com.github.ontio.network.rpc.Interfaces;
import com.github.ontio.network.rpc.RpcClient;

import java.util.HashMap;
import java.util.Map;

public class SignServer {
    private Interfaces rpcClient;
    private String url = "";

    public SignServer(String url) throws Exception {
        this.url = url;
        rpcClient = new Interfaces(url);
    }

    public Object sendSigRawTx(String rawTx) throws Exception {
        Map req = new HashMap();
        req.put("jsonrpc", "2.0");
        req.put("method", "sigrawtx");
        Map params = new HashMap();
        params.put("raw_tx", rawTx);
        req.put("params", params);
        req.put("id", 1);
        return send(req);
    }

    public Object sendMultiSigRawTx(String rawTx, int m, String[] pubkeys) throws Exception {
        Map req = new HashMap();
        req.put("jsonrpc", "2.0");
        req.put("method", "sigmutilrawtx");
        Map params = new HashMap();
        params.put("raw_tx", rawTx);
        params.put("m", m);
        params.put("pub_keys", pubkeys);
        req.put("params", params);
        req.put("id", 1);
        return send(req);
    }

    public Object sendSigTransferTx(String asset, String from, String to, long amount, long gasLimit, long gasPrice) throws Exception {
        Map req = new HashMap();
        req.put("jsonrpc", "2.0");
        req.put("method", "sigtransfertx");
        Map params = new HashMap();
        params.put("asset", asset);
        params.put("from", from);
        params.put("to", to);
        params.put("amount", amount);
        params.put("gas_limit", gasLimit);
        params.put("gas_price", gasPrice);
        req.put("params", params);
        req.put("id", 1);
        return send(req);
    }

    public Object sendSigNativeInvokeTx(String contractAddr, String method, int version, long gasLimit, long gasPrice, Map parameters) throws Exception {
        Map req = new HashMap();
        req.put("jsonrpc", "2.0");
        req.put("method", "sigtransfertx");
        Map params = new HashMap();
        params.put("address", contractAddr);
        params.put("method", method);
        params.put("version", version);
        params.put("gas_limit", gasLimit);
        params.put("gas_price", gasPrice);
        params.put("params", parameters);
        req.put("params", params);
        req.put("id", 1);
        return send(req);
    }

    public Object sendSigNeoInvokeTx(String contractAddr, int version, long gasLimit, long gasPrice, Map parameters) throws Exception {
        Map req = new HashMap();
        req.put("jsonrpc", "2.0");
        req.put("method", "sigtransfertx");
        Map params = new HashMap();
        params.put("address", contractAddr);
        params.put("version", version);
        params.put("gas_limit", gasLimit);
        params.put("gas_price", gasPrice);
        params.put("params", parameters);
        req.put("params", params);
        req.put("id", 1);
        return send(req);
    }

    private Object send(Map req) throws Exception {
        Map response = (Map) rpcClient.send(req);
        System.out.println(response);
        if (response == null) {
            throw new RpcException(0, ErrorCode.ConnectUrlErr(url + "response is null. maybe is connect error"));
        } else if ((int) response.get("error_code") == 0) {
            return response.get("result");
        } else {
            throw new RpcException(0, JSON.toJSONString(response));
        }
    }
}
