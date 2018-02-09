package ontology.network.rpc;

import ontology.network.connect.ConnectorException;

/**
 * Created by zx on 2018/2/1.
 */
class RpcException extends ConnectorException {
	private static final long serialVersionUID = -8558006777817318117L;

	public final int code;

	public RpcException(int code, String message) {
		super(message);
		this.code = code;
	}
}
