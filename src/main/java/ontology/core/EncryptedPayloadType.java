package ontology.core;

public enum EncryptedPayloadType {
	RawPayload(0x01),
	;
	
	private byte value;
	public int value() {
		return value;
	}
	
	EncryptedPayloadType(int value) {
		this.value = (byte) value;
	}
	
	public static EncryptedPayloadType from(byte i) {
		for(EncryptedPayloadType type: EncryptedPayloadType.values()) {
			if(type.value == i) {
				return type;
			}
		}
		throw new IllegalArgumentException();
	}
	
}
