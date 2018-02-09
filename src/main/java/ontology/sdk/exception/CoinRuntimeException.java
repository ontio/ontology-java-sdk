package ontology.sdk.exception;

public class CoinRuntimeException extends RuntimeException {
	private static final long serialVersionUID = 8048654854552608312L;

	public CoinRuntimeException(String msg) {
		super(msg);
	}
	
	public CoinRuntimeException(String msg, Exception ex) {
		super(msg, ex);
	}
}