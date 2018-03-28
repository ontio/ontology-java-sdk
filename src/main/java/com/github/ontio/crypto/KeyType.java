package com.github.ontio.crypto;

public enum KeyType {
    ECDSA(0x12),
    SM2(0x14);

    private int label;

    private KeyType(int b) {
        this.label = b;
    }

    public int getLabel() {
        return label;
    }

    // get the crypto.KeyType according to the input label
    public static KeyType fromLabel(byte label) throws Exception {
        for (KeyType k : KeyType.values()) {
            if (k.label == label) {
                return k;
            }
        }
        throw new Exception("unknown asymmetric key type");
    }
}
