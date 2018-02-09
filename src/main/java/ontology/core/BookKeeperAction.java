package ontology.core;

public enum BookKeeperAction {
	BookKeeperAction_ADD(0x00),
	BookKeeperAction_SUB(0x01),
	;
	private byte value;
	BookKeeperAction(int v) {
        value = (byte)v;
    }
    public byte value() {
        return value;
    }

    public static BookKeeperAction valueOf(byte v) {
    	for (BookKeeperAction e : BookKeeperAction.values()) {
    		if (e.value == v) {
    			return e;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}