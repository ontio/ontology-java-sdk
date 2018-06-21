package com.github.neo.core;

/**
 *
 */
public enum TransactionAttributeUsage {

	Nonce(0x00),
    /**
     *
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
