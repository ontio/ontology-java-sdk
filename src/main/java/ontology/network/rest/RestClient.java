package ontology.network.rest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import ontology.common.Helper;
import ontology.core.Block;
import ontology.core.Transaction;
import ontology.io.Serializable;
import ontology.network.connect.AbstractConnector;
import ontology.sdk.exception.Error;
import ontology.sdk.info.asset.UTXOsInfo;
import ontology.sdk.info.asset.UTXOInfo;

import com.alibaba.fastjson.JSON;

public class RestClient extends AbstractConnector {
	private Interfaces rest;
	private String version = "v1.0", type = "t1.0", action = "sendrawtransaction";
	private String accessToken="token", authType="OAuth2.0";
	
	public RestClient(String restUrl) {
		rest = new Interfaces(restUrl);
		setAccessToken(accessToken);
	}
	
	public RestClient(String restUrl, String accessToken) {
		rest = new Interfaces(restUrl);
		setAccessToken(accessToken);
	}
	
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	@Override
	public String getUrl() {
		return rest.getUrl();
	}
	public String delete(String url, Map<String, String> params, Map<String, Object> body) throws RestException{
		return rest.delete(url,params,body);
	}
	public String post(String url, Map<String, String> params, Map<String, String> body) throws RestException{
		return rest.post(url,params,body);
	}

	public String postObject(String url, Map<String, String> params, Map<String, Object> body) throws RestException {
		return rest.postObject(url, params, body);
	}
	public String get(String url, Map<String, String> params) throws RestException{
		return rest.get(url,params);
	}
	@Override
	public void updateToken(String token) {
		setAccessToken(token);
	}

	@Override
	public String sendRawTransaction(Transaction tx) throws RestException {
		return sendRawTransaction(false,null,Helper.toHexString(tx.toArray()));
	}
	@Override
	public String sendRawTransaction(boolean preExec,String userid,Transaction tx) throws RestException {
		return sendRawTransaction(preExec,userid,Helper.toHexString(tx.toArray()));
	}
	@Override
	public String sendRawTransaction(String hexData) throws RestException {
		String rs = rest.sendTransaction(false,null,authType, accessToken, action, version, type, hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rs;
		}
		throw new RestException(to(rr));
	}
	@Override
	public String sendRawTransaction(boolean preExec,String userid,String hexData) throws RestException {
		String rs = rest.sendTransaction(preExec,userid,authType, accessToken, action, version, type, hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rs;
		}
		throw new RestException(to(rr));
	}
	@Override
	public Transaction getRawTransaction(String txid) throws RestException {
		String rs = rest.getTransaction(authType, accessToken, txid,true);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Transaction.deserializeFrom(Helper.hexToBytes(rr.Result));
			} catch (IOException e) {
				throw new RestException(Error.getDescDeserializeTx("Transaction.deserializeFrom(txid) failed"), e);
			}
		}
		throw new RestException(to(rr));
	}
	@Override
	public int getGenerateBlockTime() throws RestException {
		String rs = rest.getGenerateBlockTime(authType, accessToken);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Integer.valueOf(rr.Result).intValue();
		}
		throw new RestException(to(rr));

	}
	@Override
	public int getNodeCount() throws RestException {
		String rs = rest.getNodeCount(authType, accessToken);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Integer.valueOf(rr.Result).intValue();
		}
		throw new RestException(to(rr));

	}

	@Override
	public int getBlockHeight() throws RestException {
		String rs = rest.getBlockHeight(authType, accessToken);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Integer.valueOf(rr.Result).intValue();
		}
		throw new RestException(to(rr));
		
	}
	@Override
	public Block getBlock(int height) throws RestException {
		String rs = rest.getBlock(authType, accessToken, height);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Serializable.from(Helper.hexToBytes(rr.Result), Block.class);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RestException(Error.getDescDeserializeBlock("Block.deserializeFrom(height) failed"), e);
			}
		}
		throw new RestException(to(rr));
	}
	@Override
	public Block getBlock(String hash) throws RestException {
		String rs = rest.getBlock(authType, accessToken, hash);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			try {
				return Serializable.from(Helper.hexToBytes(rr.Result), Block.class);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RestException(Error.getDescDeserializeBlock("Block.deserializeFrom(hash) failed"), e);
			}
		}
		throw new RestException(to(rr));
		
	}

	@Override
	public String getAsset(String assetid) throws RestException {
		String rs = rest.getAsset(authType, accessToken, assetid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
	}
	@Override
	public List<UTXOInfo> getUTXO(String address, String assetid) throws RestException {
		String rs = rest.getUTXO(authType, accessToken, address, assetid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return JSON.parseArray(rr.Result, UTXOInfo.class);
		}
		throw new RestException(to(rr));
	}
	@Override
	public List<UTXOsInfo> getUTXOs(String address) throws RestException {
		String rs = rest.getUTXOs(authType, accessToken, address);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return JSON.parseArray(rr.Result, UTXOsInfo.class);
		}
		throw new RestException(to(rr));
	}
	@Override
	public long getBalance(String address,String assetid) throws RestException {
		String rs = rest.getBalance(address, address, address,assetid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return Long.parseLong(rr.Result);
		}
		throw new RestException(to(rr));
	}
	@Override
	public String getStateUpdate(String namespace, String key) throws RestException {
		String rs = rest.getStateUpdate(authType, accessToken, namespace, key);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
	}
	@Override
	public String getIdentityUpdate(String method, String key) throws RestException {
		String rs = rest.getIdentityUpdate(authType, accessToken, method, key);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
	}
	@Override
	public String getDDO(String codehash, String ontid) throws RestException {
		String rs = rest.getDDO(authType, accessToken, codehash, ontid);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
	}

	@Override
	public boolean sendToIssService(String data) throws RestException {
		String rs = rest.sendToIssService(data);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return true;
		}
		throw new RestException(to(rr));
	}
	@Override
	public boolean sendToTrfService(String data) throws RestException {
		String rs = rest.sendToTrfService(data);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return true;
		}
		throw new RestException(to(rr));
	}

	@Override
	public String getRawTransactionJson(String txid) throws RestException {
		String rs = rest.getTransaction(authType, accessToken, txid,false);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
	}
	@Override
	public String getBlockJson(int height) throws RestException {
		String rs = rest.getBlock(authType, accessToken, height);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
	}
	@Override
	public String getBlockJson(String hash) throws RestException {
		String rs = rest.getBlock(authType, accessToken, hash);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error != 0) {
			return rr.Result;
		}
		throw new RestException(to(rr));
		
	}
	private String to(Result rr) {
		return JSON.toJSONString(rr);
	}
}




