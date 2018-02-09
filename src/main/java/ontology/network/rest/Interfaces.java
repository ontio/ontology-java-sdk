package ontology.network.rest;

import ontology.sdk.exception.Error;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zx on 2018/2/1.
 */
class Interfaces {
	private String url;
	public Interfaces(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public String delete(String url, Map<String, String> params, Map<String, Object> body) throws RestException {
		try {
			return RestHttp.delete(url, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String post(String url, Map<String, String> params, Map<String, String> body) throws RestException {
		try {
			return RestHttp.post(url, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String postObject(String url, Map<String, String> params, Map<String, Object> body) throws RestException {
		try {
			return RestHttp.postObject(url, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String get(String url, Map<String, String> params) throws RestException{
		try {
			return RestHttp.get(url, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String sendTransaction(boolean preExec,String userid,String authType, String accessToken, String action, String version, String type, String data) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		if(userid != null) {
			params.put("userid", userid);
		}
		if(preExec) {
			params.put("preExec", "1");
		}
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		Map<String, String> body = new HashMap<String, String>();
		body.put("Action", action);
		body.put("Version", version);
		body.put("Type", type);
		body.put("Data", data);
		try {
			//System.out.println(params);
			return RestHttp.post(url + Consts.Url_send_transaction, params, body);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getTransaction(String authType, String accessToken, String txid,boolean raw) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		if(raw) {
			params.put("raw", "1");
		}
		try {
			return RestHttp.get(url + Consts.Url_get_transaction + txid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getGenerateBlockTime(String authType, String accessToken) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_GenerateBlockTime, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getNodeCount(String authType, String accessToken) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_node_count, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String getBlockHeight(String authType, String accessToken) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_block_height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getBlock(String authType, String accessToken, int height) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		params.put("raw", "1");
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getBlock(String authType, String accessToken, String hash) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		params.put("raw", "1");
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getAsset(String authType, String accessToken, String assetid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_asset + assetid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getUTXO(String authType, String accessToken, String address, String assetid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_UTXO_By_address_assetid + address + "/" + assetid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String getUTXOs(String authType, String accessToken, String address) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_UTXO_By_address + address, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getBalance(String authType, String accessToken, String address,String assetid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_account_balance + address+"/"+assetid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getStateUpdate(String authType, String accessToken, String namespace, String key) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_StateUpdate + namespace + "/" + key, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getIdentityUpdate(String authType, String accessToken, String method, String key) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_IdentityUpdate + method + "/" + key, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getDDO(String authType, String accessToken, String codehash, String ontid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_DDO+ codehash + "/" + ontid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String sendToIssService(String data) throws RestException {
		try {
			return RestHttp.post(url + Consts.Url_send_to_issService, data);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String sendToTrfService(String data) throws RestException {
		try {
			return RestHttp.post(url + Consts.Url_send_to_trfService, data);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	// ****************************************************************************************************8
	public String getTransactionJson(String authType, String accessToken, String txid) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_transaction + txid, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
	public String getBlockJson(String authType, String accessToken, int height) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Height + height, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}

	public String getBlockJson(String authType, String accessToken, String hash) throws RestException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("auth_type", authType);
		params.put("access_token", accessToken);
		try {
			return RestHttp.get(url + Consts.Url_get_block_By_Hash + hash, params);
		} catch (KeyManagementException | NoSuchAlgorithmException
				| NoSuchProviderException | IOException e) {
			throw new RestException(Error.getDescNetworkError("Invalid url:"+url + ",errMsg:"+e.getMessage()), e);
		}
	}
}
