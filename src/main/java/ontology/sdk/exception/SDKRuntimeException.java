package ontology.sdk.exception;

import com.alibaba.fastjson.JSON;

public class SDKRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2005335065357755315L;
	public SDKRuntimeException(String message) {
		super(message);
		initExMsg(message);
	}
	
	public SDKRuntimeException(String message, Throwable ex) {
		super(message, ex);
		initExMsg(message);
	}
	
	private void initExMsg(String message) {
		exMsg = JSON.parseObject(message, ExMsg.class);
	}
	private ExMsg exMsg;
	public long getErrorCode() {
		return exMsg.Error;
	}
	public String getErrorMessage() {
		return exMsg.Desc;
	}
}