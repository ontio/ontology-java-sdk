package ontology.sdk.exception;

public class CoinException extends SDKException {

	private static final long serialVersionUID = 8048654854552608312L;

	public CoinException(String msg) {
		super(msg);
	}
	
	public CoinException(String msg, Exception ex) {
		super(msg, ex);
	}
}
