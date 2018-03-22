package ontology.sdk.manager;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import ontology.common.Helper;
import ontology.core.Block;
import ontology.core.Blockchain;
import ontology.core.Transaction;
import ontology.network.connect.ConnectorException;
import ontology.network.connect.IConnector;
import ontology.network.rest.RestBlockchain;
import ontology.network.rest.RestClient;
import ontology.network.rest.Result;

/**
 * 连接管理器
 * 
 * @author 12146
 *
 */
public class ConnectMgr {
	private IConnector connector;
	
	
	public ConnectMgr(String url) {
		setConnector(new RestClient(url));
		Blockchain.register(new RestBlockchain(new RestClient(url)));
	}
	public ConnectMgr(String url, String token) {
		setConnector(new RestClient(url, token));
		Blockchain.register(new RestBlockchain(new RestClient(url, token)));
	}
	
	public ConnectMgr(IConnector connector) {
		setConnector(connector);
	}
	public void setConnector(IConnector connector) {
		this.connector = connector;
	}
	
	public String getUrl() {
		return connector.getUrl();
	}
	
	public void updateToken(String token) {
		connector.updateToken(token);
	}


	public boolean sendRawTransaction(Transaction tx) throws ConnectorException, IOException {
		String rs = connector.sendRawTransaction(Helper.toHexString(tx.toArray()));
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0){
			return true;
		}
		return false;
	}
	
	public boolean sendRawTransaction(String hexData) throws ConnectorException, IOException {
		String rs = connector.sendRawTransaction(hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0){
			return true;
		}
		return false;
	}
	public boolean sendRawTransaction(String uuid, String hexData) throws ConnectorException, IOException {
		String rs = connector.sendRawTransaction(false,uuid,hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0){
			return true;
		}
		return false;
	}
	public Object sendRawTransactionPreExec(String hexData) throws ConnectorException, IOException {
		String rs = connector.sendRawTransaction(true,null,hexData);
		Result rr = JSON.parseObject(rs, Result.class);
		if(rr.Error == 0){
			return rr.Result;
		}
		return null;
	}
	public Transaction getRawTransaction(String txid) throws ConnectorException, IOException {
		return connector.getRawTransaction(txid);
	}
	public String getTransaction(String txid) throws ConnectorException, IOException {
		return connector.getRawTransactionJson(txid);
	}
	public int getGenerateBlockTime() throws ConnectorException, IOException {
		return connector.getGenerateBlockTime();
	}
	public int getNodeCount() throws ConnectorException, IOException {
		return connector.getNodeCount();
	}
	public int getBlockHeight() throws ConnectorException, IOException {
		return connector.getBlockHeight();
	}
	public Block getBlock(int height) throws ConnectorException, IOException {
		return connector.getBlock(height);
	}
		
	public Block getBlock(String hash) throws ConnectorException, IOException {
		return connector.getBlock(hash);
		
	}
	
	public long getBalance(String address,String assetId) throws ConnectorException, IOException {
		return connector.getBalance(address,assetId);
	}
//	public long getBalances(String address) throws ConnectorException, IOException {
//		return connector.getBalances(address);
//	}

	public String getDDO(String codehash, String ontid) throws ConnectorException, IOException {
		return connector.getDDO(codehash, ontid);
	}

	public String getRawTransactionJson(String txid) throws ConnectorException, IOException {
		return connector.getRawTransactionJson(txid);
	}
	
	public String getBlockJson(int height) throws ConnectorException, IOException {
		return connector.getBlockJson(height);
	}
		
	public String getBlockJson(String hash) throws ConnectorException, IOException {
		return connector.getBlockJson(hash);
	}
}


