package ontology.core;

public enum PayloadEncryptType {
	ECDH_AES256(0x01),
	;
	
	private byte value;
	public int value() {
		return value;
	}
	
	PayloadEncryptType(int value) {
		this.value = (byte) value;
	}
	
	public static PayloadEncryptType from(byte i) {
		for(PayloadEncryptType type: PayloadEncryptType.values()) {
			if(type.value == i) {
				return type;
			}
		}
		throw new IllegalArgumentException();
	}
	
}
