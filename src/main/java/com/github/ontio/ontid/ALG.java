package com.github.ontio.ontid;

import com.github.ontio.crypto.Digest;
import com.github.ontio.sdk.exception.SDKException;

// EcdsaSecp224r1VerificationKey2019      ES224    ECDSA P-224 SHA-224
// EcdsaSecp256r1VerificationKey2019      ES256    ECDSA P-256 SHA-256
// EcdsaSecp384r1VerificationKey2019      ES384    ECDSA P-384 SHA-384
// EcdsaSecp521r1VerificationKey2019      ES512    ECDSA P-521 SHA-512
// EcdsaSecp256k1VerificationKey2019      ES256K   ECDSA secp256k1 SHA-256
// Ed25519VerificationKey2018             EdDSA    EDDSA Curve25519 SHA-256
// SM2VerificationKey2019                 SM       SM2   SM2P256V1  SM3
public enum ALG {
    ES224, ES256, ES384, ES512, ES256K, EdDSA, SM;

    public static final String TYPE_ECDSA = "ECDSA";
    public static final String TYPE_EDDSA = "EDDSA";
    public static final String TYPE_SM2 = "SM2";

    public static final String CURVE_P224 = "P-224";
    public static final String CURVE_P256 = "P-256";
    public static final String CURVE_P384 = "P-384";
    public static final String CURVE_P521 = "P-521";
    public static final String CURVE_secp256k1 = "secp256k1";
    public static final String CURVE_Curve25519 = "Curve25519";
    public static final String CURVE_SM2P256V1 = "SM2P256V1";

    public static final String HASH_224 = "SHA-224";
    public static final String HASH_256 = "SHA-256";
    public static final String HASH_384 = "SHA-384";
    public static final String HASH_512 = "SHA-512";
    public static final String HASH_SM3 = "SM3";

    private PubKeyType pubKeyType;

    ALG() {
    }

    public void setProofPubKeyType(PubKeyType pubKeyType) {
        this.pubKeyType = pubKeyType;
    }

    public PubKeyType proofPubKeyType() {
        return pubKeyType;
    }

    public byte[] hash(byte[] msg) throws Exception {
        switch (this) {
            case ES224:
                return Digest.sha224(msg);
            case ES256:
            case ES256K:
            case EdDSA:
                return Digest.sha256(msg);
            case ES384:
                return Digest.sha384(msg);
            case ES512:
                return Digest.sha512(msg);
            case SM:
                return Digest.sm3(msg);
            default:
                throw new SDKException("unsupport hash method");
        }
    }
}
