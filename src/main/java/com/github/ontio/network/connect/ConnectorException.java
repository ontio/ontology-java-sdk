package com.github.ontio.network.connect;

import com.github.ontio.sdk.exception.SDKException;

public class ConnectorException extends SDKException {
	private static final long serialVersionUID = 1110342144692879043L;
	
	public ConnectorException(String message) {
		super(message);
	}
	
	public ConnectorException(String message, Throwable ex) {
		super(message, ex);
	}
}
