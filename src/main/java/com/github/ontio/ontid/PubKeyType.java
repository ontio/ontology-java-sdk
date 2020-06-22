package com.github.ontio.ontid;

public enum PubKeyType {
    EcdsaSecp224r1VerificationKey2019(ALG.ES224, ALG.TYPE_ECDSA, ALG.CURVE_P224, ALG.HASH_224),
    EcdsaSecp256r1VerificationKey2019(ALG.ES256, ALG.TYPE_ECDSA, ALG.CURVE_P256, ALG.HASH_256),
    EcdsaSecp384r1VerificationKey2019(ALG.ES384, ALG.TYPE_ECDSA, ALG.CURVE_P384, ALG.HASH_384),
    EcdsaSecp521r1VerificationKey2019(ALG.ES512, ALG.TYPE_ECDSA, ALG.CURVE_P521, ALG.HASH_512),
    EcdsaSecp256k1VerificationKey2019(ALG.ES256K, ALG.TYPE_ECDSA, ALG.CURVE_secp256k1, ALG.HASH_256),
    Ed25519VerificationKey2018(ALG.EdDSA, ALG.TYPE_EDDSA, ALG.CURVE_Curve25519, ALG.HASH_256),
    SM2VerificationKey2019(ALG.SM, ALG.TYPE_SM2, ALG.CURVE_SM2P256V1, ALG.HASH_SM3);

    private ALG alg;
    private String algType;
    private String curve;
    private String hashMethod;

    PubKeyType(ALG alg, String algType, String curve, String hashMethod) {
        this.alg = alg;
        this.algType = algType;
        this.curve = curve;
        this.hashMethod = hashMethod;

        // inject self to alg pub key type
        alg.setProofPubKeyType(this);
    }

    public ALG getAlg() {
        return alg;
    }

    public String getAlgType() {
        return algType;
    }

    public String getCurve() {
        return curve;
    }

    public String getHashMethod() {
        return hashMethod;
    }
}
