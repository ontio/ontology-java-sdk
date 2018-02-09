package ontology.network.connect;

import java.util.List;

import ontology.sdk.info.asset.UTXOsInfo;
import ontology.sdk.info.asset.UTXOInfo;

public abstract class AbstractConnector implements IConnector {

	@Override
	public void updateToken(String token) {}
	
	@Override
	public String getAsset(String assetid) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UTXOsInfo> getUTXOs(String address)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UTXOInfo> getUTXO(String address, String assetid) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getBalance(String address,String assetid) throws ConnectorException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStateUpdate(String namespace, String key)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getIdentityUpdate(String method, String key)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getDDO(String codehash, String ontid)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean sendToIssService(String data) throws ConnectorException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendToTrfService(String data) throws ConnectorException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRawTransactionJson(String txid) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBlockJson(int height) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBlockJson(String hash) throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

}
