package ontology.network.connect;

import java.io.IOException;
import java.util.List;

import ontology.core.Block;
import ontology.core.Transaction;
import ontology.sdk.info.asset.UTXOsInfo;
import ontology.sdk.info.asset.UTXOInfo;

public interface IConnector {

	public String getUrl();
	public String sendRawTransaction(Transaction tx) throws ConnectorException, IOException;
	public String sendRawTransaction(boolean preExec,String userid,Transaction tx) throws ConnectorException, IOException;
	public String sendRawTransaction(boolean preExec,String userid,String hexData) throws ConnectorException, IOException;
	public String sendRawTransaction(String hexData) throws ConnectorException, IOException;
	public Transaction getRawTransaction(String txid) throws ConnectorException, IOException;
	public int getGenerateBlockTime() throws ConnectorException, IOException;
	public int getNodeCount() throws ConnectorException, IOException;
	public int getBlockHeight() throws ConnectorException, IOException;
	public Block getBlock(int height) throws ConnectorException, IOException;
	public Block getBlock(String hash) throws ConnectorException, IOException ;

	public void updateToken(String token);
	public String getAsset(String assetid) throws ConnectorException, IOException ;
	public List<UTXOInfo> getUTXO(String address, String assetid) throws ConnectorException, IOException;
	public List<UTXOsInfo> getUTXOs(String address) throws ConnectorException, IOException;
	public long getBalance(String address,String assetid) throws ConnectorException, IOException;
	public String getStateUpdate(String namespace, String key) throws ConnectorException, IOException;
	public String getIdentityUpdate(String method, String key) throws ConnectorException, IOException;
	public String getDDO(String codehash, String ontid) throws ConnectorException, IOException;

	public boolean sendToIssService(String data) throws ConnectorException, IOException;
	public boolean sendToTrfService(String data) throws ConnectorException, IOException;
	
	public String getRawTransactionJson(String txid) throws ConnectorException, IOException;
	public String getBlockJson(int height) throws ConnectorException, IOException;
	public String getBlockJson(String hash) throws ConnectorException, IOException;
}