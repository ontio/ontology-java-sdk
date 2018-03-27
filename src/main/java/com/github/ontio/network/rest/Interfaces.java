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


import com.github.ontio.network.exception.RestfulException;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
class Interfaces {
    private String url;

    public Interfaces(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String sendTransaction(boolean preExec, String userid, String action, String version, String data) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        if (userid != null) {
            params.put("userid", userid);
        }
        if (preExec) {
            params.put("preExec", "1");
        }
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("Action", action);
        body.put("Version", version);
        body.put("Data", data);
        try {
            return http.post(url + UrlConsts.Url_send_transaction, params, body);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getTransaction(String txhash, boolean raw) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        if (raw) {
            params.put("raw", "1");
        }
        try {
            return http.get(url + UrlConsts.Url_get_transaction + txhash, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getGenerateBlockTime() throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_generate_block_time, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getNodeCount() throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_node_count, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getBlockHeight() throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_block_height, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getBlock(int height, String raw) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("raw", raw);
        try {
            return http.get(url + UrlConsts.Url_get_block_by_height + height, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getBlock(String hash, String raw) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("raw", raw);
        try {
            return http.get(url + UrlConsts.Url_get_block_by_hash + hash, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getContract(String hash) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_contract_state + hash, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getSmartCodeEvent(int height) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_smartcodeevent_txs_by_height + height, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getSmartCodeEvent(String hash) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_smartcodeevent_by_txhash + hash, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getBalance(String address) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_account_balance + address, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getTransactionJson(String txhash) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_transaction + txhash, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getBlockJson(int height) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_block_by_height + height, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }

    public String getBlockJson(String hash) throws RestfulException {
        Map<String, String> params = new HashMap<String, String>();
        try {
            return http.get(url + UrlConsts.Url_get_block_by_hash + hash, params);
        } catch (Exception e) {
            throw new RestfulException("Invalid url:" + url + "," + e.getMessage(), e);
        }
    }
}
