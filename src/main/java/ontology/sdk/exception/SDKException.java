package ontology.sdk.exception;


import com.alibaba.fastjson.JSON;

public class SDKException extends Exception {

	private static final long serialVersionUID = -3056715808373341597L;
	
	public SDKException(String message) {
		super(message);
		initExMsg(message);
	}
	public SDKException(String message, Throwable ex) {
		super(message, ex);
		initExMsg(message);
	}
	public SDKException(Throwable ex) {
		super(ex);
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

