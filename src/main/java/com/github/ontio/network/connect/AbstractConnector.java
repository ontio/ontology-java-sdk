package com.github.ontio.network.connect;


public abstract class AbstractConnector implements IConnector {

	@Override
	public void updateToken(String token) {}

	@Override
	public Object getBalance(String address) throws ConnectorException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDDO(String codehash, String ontid)
			throws ConnectorException {
		// TODO Auto-generated method stub
		return null;
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
