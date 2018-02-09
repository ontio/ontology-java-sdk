package ontology.network.rpc;

import java.io.IOException;
import java.net.MalformedURLException;

import ontology.common.Helper;
import ontology.common.UInt256;
import ontology.core.Block;
import ontology.core.Transaction;
import ontology.io.JsonReader;
import ontology.io.JsonSerializable;
import ontology.io.json.JNumber;
import ontology.io.json.JObject;
import ontology.io.json.JString;
import ontology.network.connect.AbstractConnector;
import ontology.network.connect.ConnectorException;

public class RpcClient extends AbstractConnector {
	private final Interfaces rpc;
	
	public RpcClient(String url) throws MalformedURLException {
		this.rpc = new Interfaces(url);
	}
	@Override
	public String getUrl() {
		return rpc.getHost();
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
		System.out.println("result:"+result);
		return null;
	}
	@Override
	public int getGenerateBlockTime() throws RpcException, IOException {
		JObject result = rpc.call("getgenerateblocktime");
		return (int)result.asNumber();
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
	
	public String getBlockByHeight(int index) throws RpcException, IOException {
		JObject result = rpc.call("getblock", new JNumber(index));
		return result.toString();
	}
	
	public String getBlockByHash(String hash) throws RpcException, IOException {
		JObject result = rpc.call("getblockhash", new JString(hash));
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
		JObject result = rpc.call("getblock", new JString(hash.toString()));
		System.out.println("rs:"+result);
		try {
			Block bb = JsonSerializable.from(result, Block.class);
			return bb;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
}

