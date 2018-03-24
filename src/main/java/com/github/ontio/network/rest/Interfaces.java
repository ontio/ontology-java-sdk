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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
	public String delete(String url, Map<String, String> params, Map<String, Object> body) throws RestfulException {
		try {
			return Methods.delete(url, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String post(String url, Map<String, String> params, Map<String, String> body) throws RestfulException {
		try {
			return Methods.post(url, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String postObject(String url, Map<String, String> params, Map<String, Object> body) throws RestfulException {
		try {
			return Methods.postObject(url, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String get(String url, Map<String, String> params) throws RestfulException {
		try {
			return Methods.get(url, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String sendTransaction(boolean preExec,String userid,String action, String version, String data) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		if(userid != null) {
			params.put("userid", userid);
		}
		if(preExec) {
			params.put("preExec", "1");
		}
		Map<String, String> body = new HashMap<String, String>();
		body.put("Action", action);
		body.put("Version", version);
		body.put("Data", data);
		try {
			return Methods.post(url + Consts.Url_send_transaction, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getTransaction(String txhash,boolean raw) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		if(raw) {
			params.put("raw", "1");
		}
		try {
			return Methods.get(url + Consts.Url_get_transaction + txhash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getGenerateBlockTime() throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_GenerateBlockTime, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getNodeCount() throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_node_count, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String getBlockHeight() throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_block_height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getBlock(int height,String raw) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("raw", raw);
		try {
			return Methods.get(url + Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getBlock( String hash,String raw) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("raw", raw);
		try {
			return Methods.get(url + Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String getSmartCodeEvent(int height) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_smartcodeevent_by_height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String getSmartCodeEvent(String hash) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_smartcodeevent_by_txhash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getBalance( String address) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_account_balance + address, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getTransactionJson(String txhash) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_transaction + txhash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
	public String getBlockJson(int height) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}

	public String getBlockJson(String hash) throws RestfulException {
		Map<String, String> params = new HashMap<String, String>();
		try {
			return Methods.get(url + Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestfulException("Invalid url:"+url + ",errMsg:"+e.getMessage(), e);
		}
	}
}
