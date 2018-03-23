package com.github.ontio.core;

/**
 * list transaction types supported by DNA 
 */
public enum VmType {

    NativeVM(0xff),
    NEOVM(0x80),
    WASMVM(0x90);

    private byte value;
    VmType(int v) {
        value = (byte)v;
    }
    public byte value() {
        return value;
    }

    public static VmType valueOf(byte v) {
    	for (VmType e : VmType.values()) {
    		if (e.value == v) {
    			return e;
    		}
    	}
    	throw new IllegalArgumentException();
    }
}
