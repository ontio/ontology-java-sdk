package com.github.ontio.crypto;

public enum SignatureScheme {
    SHA224WITHECDSA("SHA224withECDSA"),
    SHA256WITHECDSA("SHA256withECDSA"),
    SHA384WITHECDSA("SHA384withECDSA"),
    SHA512WITHECDSA("SHA512withECDSA"),
    SHA3_224WITHECDSA("SHA3-224withECDSA"),
    SHA3_256WITHECDSA("SHA3-256withECDSA"),
    SHA3_384WITHECDSA("SHA3-384withECDSA"),
    SHA3_512WITHECDSA("SHA3-512withECDSA"),
    RIPEMD160WITHECDSA("RIPEMD160withECDSA"),

    SM3WITHSM2("SM3withSM2");

    private String name;

    private SignatureScheme(String v) {
        name = v;
    }
    @Override
    public String toString() {
        return name;
    }
}
