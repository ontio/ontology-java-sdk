package com.github.ontio.network.rest;

import com.github.ontio.network.connect.ConnectorException;

public class RestException extends ConnectorException {
	private static final long serialVersionUID = -8558006777817318117L;
	
	public RestException(String message) {
		super(message);
	}
	
	public RestException(String message,Throwable ex) {
		super(message, ex);
	}
}
