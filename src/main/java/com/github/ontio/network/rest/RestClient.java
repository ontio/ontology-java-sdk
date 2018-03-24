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
import java.util.Map;

import com.github.ontio.common.Helper;
import com.github.ontio.core.block.Block;
import com.github.ontio.io.Serializable;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.connect.ConnectorException;
import com.github.ontio.core.transaction.Transaction;

import com.alibaba.fastjson.JSON;

public class RestClient extends AbstractConnector {
	private Interfaces api;
	private String version = "v1.0.0",  action = "sendrawtransaction";
	
	public RestClient(String restUrl) {
		api = new Interfaces(restUrl);
	}

	@Override
	public String getUrl() {
		return api.getUrl();
	}
	public String delete(String url, Map<String, String> params, Map<String, Object> body) throws RestfulException {
		return api.delete(url,params,body);
	}
	public String post(String url, Map<String, String> params, Map<String, String> body) throws RestfulException {
		return api.post(url,params,body);
	}

	public String postObject(String url, Map<String, String> params, Map<String, Object> body) throws RestfulException {
		return api.postObject(url, params, body);
	}
	public String get(String url, Map<String, String> params) throws RestfulException {
		return api.get(url,params);
	}

	@Override
	public String sendRawTransaction(Transaction tx) throws RestfulException {
		return sendRawTransaction(false,null, Helper.toHexString(tx.toArray()));
	}
	@Override
	public String sendRawTransaction(boolean preExec,String userid,Transaction tx) throws RestfulException {
		return sendRawTransaction(preExec,userid,Helper.toHexString(tx.toArray()));
	}
	@Override
	public String sendRawTransaction(String hexData) throws RestfulException {
		String rs = api.sendTransaction(false,null,action, version, hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rs;
		}
		throw new RestfulException(to(rr));
	}
	@Override
	public String sendRawTransaction(boolean preExec,String userid,String hexData) throws RestfulException {
		String rs = api.sendTransaction(preExec,userid,action, version, hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rs;
		}
		throw new RestfulException(to(rr));
	}
	@Override
	public Transaction getRawTransaction(String txhash) throws RestfulException {
		String rs = api.getTransaction(txhash,true);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Transaction.deserializeFrom(Helper.hexToBytes(rr.Result));
			} catch (IOException e) {
				throw new RestfulException("Transaction.deserializeFrom(txhash) failed", e);
			}
		}
		throw new RestfulException(to(rr));
	}
	@Override
	public int getGenerateBlockTime() throws RestfulException {
		String rs = api.getGenerateBlockTime();
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Integer.valueOf(rr.Result).intValue();
		}
		throw new RestfulException(to(rr));

	}
	@Override
	public int getNodeCount() throws RestfulException {
		String rs = api.getNodeCount();
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Integer.valueOf(rr.Result).intValue();
		}
		throw new RestfulException(to(rr));

	}

	@Override
	public int getBlockHeight() throws RestfulException {
		String rs = api.getBlockHeight();
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Integer.valueOf(rr.Result).intValue();
		}
		throw new RestfulException(to(rr));
		
	}
	@Override
	public Block getBlock(int height) throws RestfulException {
		String rs = api.getBlock( height,"1");
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				System.out.println(rr.Result);
				return Serializable.from(Helper.hexToBytes(rr.Result), Block.class);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RestfulException("Block.deserializeFrom(height) failed", e);
			}
		}
		throw new RestfulException(to(rr));
	}


	@Override
	public Block getBlock(String hash) throws RestfulException {
		String rs = api.getBlock( hash,"1");
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Serializable.from(Helper.hexToBytes(rr.Result), Block.class);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RestfulException("Block.deserializeFrom(hash) failed", e);
			}
		}
		throw new RestfulException(to(rr));
		
	}

	@Override
	public Object getBalance(String address) throws RestfulException {
		String rs = api.getBalance( address);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestfulException(to(rr));
	}

	@Override
	public String getRawTransactionJson(String txhash) throws RestfulException {
		String rs = api.getTransaction( txhash,false);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestfulException(to(rr));
	}
	@Override
	public String getBlockJson(int height) throws RestfulException {
		String rs = api.getBlock(height,"0");
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestfulException(to(rr));
	}
	@Override
	public String getBlockJson(String hash) throws RestfulException {
		String rs = api.getBlock(hash,"0");
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestfulException(to(rr));
		
	}
	@Override
	public Object getSmartCodeEvent(int height) throws ConnectorException, IOException {
		String rs = api.getSmartCodeEvent(height);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestfulException(to(rr));

	}
	@Override
	public Object getSmartCodeEvent(String hash) throws ConnectorException, IOException {
		String rs = api.getSmartCodeEvent(hash);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestfulException(to(rr));

	}

	private String to(Result rr) {
		return JSON.toJSONString(rr);
	}
}




