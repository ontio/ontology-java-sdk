package ontology.sdk.exception;

public class AccountException extends SDKRuntimeException {
	private static final long serialVersionUID = 6003938627169039108L;

	public AccountException(String message) {
		super(message);
	}
	
	public AccountException(String message, Throwable ex) {
		super(message, ex);
	}

}
