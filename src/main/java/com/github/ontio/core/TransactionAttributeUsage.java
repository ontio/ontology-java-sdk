package com.github.ontio.core;

/**
 *  表示交易特性的用途
 */
public enum TransactionAttributeUsage {

	Nonce(0x00),
    /**
     *  用于对交易进行额外的验证
     */
    Script(0x20),

    DescriptionUrl(0x81),
    Description(0x90),

    ;
    private byte value;

    TransactionAttributeUsage(int v) {
        value = (byte)v;
    }

    public byte value() {
        return value;
    }
    
    public static TransactionAttributeUsage valueOf(byte v) {
    	for (TransactionAttributeUsage e : TransactionAttributeUsage.values()) {
    		if (e.value == v) {
    			return e;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}
