package ontology.core;

/**
 * 记账模式
 */
public enum RecordType {
	UTXO(0x00),		// utxo模式
	Balance(0x01),	// 余额模式
	;
	
	private byte v;
	RecordType(int v) {
		this.v = (byte)v;
	}
	public byte value() {
		return v;
	}
	public static RecordType valueOf(byte b) {
		for(RecordType tt: RecordType.values()) {
			if(tt.value() == b) {
				return tt;
			}
		}
		throw new IllegalArgumentException();
	}
}