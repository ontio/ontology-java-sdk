package ontology.network.rest;


public class RestRuntimeException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public RestRuntimeException(String message) {
		super(message);
	}
	
	public RestRuntimeException(String message, Throwable thr) {
		super(message, thr);
	}
}