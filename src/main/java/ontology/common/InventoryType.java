package ontology.common;

/**
 *  定义清单中的对象类型
 */
public enum InventoryType {
    /**
     *  交易
     */
    TX(0x01),
    /**
     *  区块
     */
    Block(0x02),
    /**
     *  共识数据
     */
    Consensus(0xe0),
    
    ;
    private byte value;
    private InventoryType(int v) {
        value = (byte)v;
    }
    public int value() {
        return value;
    }
    
    public static InventoryType from(byte b) {
    	for(InventoryType t: InventoryType.values()) {
    		if(t.value() == b) {
    			return t;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}

