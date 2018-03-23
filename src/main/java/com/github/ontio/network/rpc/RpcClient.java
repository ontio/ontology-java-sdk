package com.github.ontio.network.rpc;

import java.io.IOException;
import java.net.MalformedURLException;

import com.alibaba.fastjson.JSON;
import com.github.ontio.common.Helper;
import com.github.ontio.common.UInt256;
import com.github.ontio.core.Block;
import com.github.ontio.io.JsonReader;
import com.github.ontio.core.Transaction;
import com.github.ontio.io.JsonSerializable;
import com.github.ontio.io.json.JNumber;
import com.github.ontio.io.json.JObject;
import com.github.ontio.io.json.JString;
import com.github.ontio.network.connect.AbstractConnector;
import com.github.ontio.network.connect.ConnectorException;

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
	public Object getBalance(String address) throws ConnectorException {
		JObject result = null;
		try {
			result = rpc.call("getbalance", new JString(address));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	@Override
	public String sendRawTransaction(String sData) throws RpcException, IOException {
		JObject result = rpc.call("sendrawtransaction", new JString(sData));
		return result.asString();
	}
	@Override
	public String sendRawTransaction(boolean preExec,String userid,String sData) throws RpcException, IOException {
		JObject result = rpc.call("sendrawtransaction", new JString(sData));
		return result.asString();
	}
	@Override
	public Transaction getRawTransaction(String txid) throws RpcException, IOException {
		JObject result = rpc.call("getrawtransaction", new JString(txid.toString()));
		return Transaction.deserializeFrom(Helper.hexToBytes(result.asString()));
	}
	@Override
	public String getRawTransactionJson(String txid) throws RpcException {
		JObject result = null;
		try {
			result = rpc.call("getrawtransaction", new JString(txid.toString()),new JNumber(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	@Override
	public int getGenerateBlockTime() throws RpcException, IOException {
		JObject result = rpc.call("getgenerateblocktime");
		return new Double(result.asNumber()).intValue();
	}
	@Override
	public int getNodeCount() throws RpcException, IOException {
		JObject result = rpc.call("getconnectioncount");
		return (int)result.asNumber();
	}
	@Override
	public int getBlockHeight() throws RpcException, IOException {
		JObject result = rpc.call("getblockcount");
		return (int)result.asNumber();
	}
	@Override
	public String getBlockJson(int index) throws RpcException {
		JObject result = null;
		try {
			result = rpc.call("getblock", new JNumber(index),new JNumber(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	@Override
	public String getBlockJson(String hash) throws RpcException {
		JObject result = null;
		try {
			result = rpc.call("getblock", new JString(hash),new JNumber(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

	/// ************************************************************ ///
	@Override
	public String sendRawTransaction(Transaction tx) throws RpcException, IOException {
		JObject result = rpc.call("sendrawtransaction", new JString(Helper.toHexString(tx.toArray())));
		System.out.println("rs:"+result);
		return result.asString();
	}

	@Override
	public String sendRawTransaction(boolean preExec,String userid,Transaction tx) throws RpcException, IOException {
		JObject result = rpc.call("sendrawtransaction", new JString(Helper.toHexString(tx.toArray())));
		System.out.println("rs:"+result);
		return result.asString();
	}
	public String getRawTransaction(UInt256 txid) throws RpcException, IOException {
		JObject result = rpc.call("getrawtransaction", new JString(txid.toString()));
		System.out.println("rs:"+result);
		return result.toString();
	}
	
	public Transaction getTxn(UInt256 txid) throws RpcException, IOException {
		JObject result = rpc.call("getTxn", new JString(txid.toString()));
		System.out.println("rs:"+result);
		return Transaction.fromJsonD(new JsonReader(result));
	}
	
	public Block getBlock(UInt256 hash) throws RpcException, IOException {
		JObject result = rpc.call("getblock", new JString(hash.toString()));
		System.out.println("rs:"+result);
		try {
			Block bb = JsonSerializable.from(result, Block.class);
			return bb;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public Block getBlock(int index) throws RpcException, IOException {
		JObject result = rpc.call("getblock", new JNumber(index));
		System.out.println("rs:"+result);
		try {
			Block bb = JsonSerializable.from(result, Block.class);
			return bb;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getBlockCount() throws RpcException, IOException {
		JObject result = rpc.call("getblockcount");
		return (int)result.asNumber();
	}
	@Override
	public Block getBlock(String hash) throws ConnectorException, IOException {
		JObject result = rpc.call("getblock", new JString(hash.toString()),new JNumber(1));
		System.out.println("rs:"+result);
		try {
			Block bb = JsonSerializable.from(result, Block.class);
			return bb;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}

