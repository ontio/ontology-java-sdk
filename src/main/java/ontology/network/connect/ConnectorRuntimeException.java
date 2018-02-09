package ontology.network.connect;

public class ConnectorRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -6975983378836515414L;

	public ConnectorRuntimeException(String message) {
		super(message);
	}
	
	public ConnectorRuntimeException(String message, Throwable ex) {
		super(message, ex);
	}
}