package ontology.network.connect;

import java.io.IOException;
import java.util.List;

import ontology.core.Block;
import ontology.core.Transaction;

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

	public long getBalance(String address,String assetid) throws ConnectorException, IOException;

	public String getDDO(String codehash, String ontid) throws ConnectorException, IOException;
	
	public String getRawTransactionJson(String txid) throws ConnectorException, IOException;
	public String getBlockJson(int height) throws ConnectorException, IOException;
	public String getBlockJson(String hash) throws ConnectorException, IOException;
}