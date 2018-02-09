package ontology.core;

/**
 * 资产类别
 */
public enum AssetType {

	/**
     * 法币
     */
    Currency(0x00),

    /**
     * 股权
     */
    Share(0x01),

    /**
     * 电子发票
     */
    Invoice(0x10),

    /**
     * 代币
     */
    Token(0x11),
    ;

    private byte value;
    AssetType(int v) {
        value = (byte)v;
    }
    
    public byte value() {
        return value;
    }
    
    public static AssetType valueOf(byte v) {
    	for (AssetType tt : AssetType.values()) {
    		if(tt.value() == v) {
    			return tt;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}
