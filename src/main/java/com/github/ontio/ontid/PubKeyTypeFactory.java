package com.github.ontio.ontid;

import com.github.ontio.sdk.exception.SDKException;

public class PubKeyTypeFactory {
    public static PubKeyType genPubKeyType(String pubKeyType) throws Exception {
        if ("EcdsaSecp224r1VerificationKey2019".equals(pubKeyType)) {
            return PubKeyType.EcdsaSecp224r1VerificationKey2019;
        } else if ("EcdsaSecp256r1Signature2019".equals(pubKeyType)) {
            return PubKeyType.EcdsaSecp256r1Signature2019;
        } else if ("EcdsaSecp384r1VerificationKey2019".equals(pubKeyType)) {
            return PubKeyType.EcdsaSecp384r1VerificationKey2019;
        } else if ("EcdsaSecp521r1VerificationKey2019".equals(pubKeyType)) {
            return PubKeyType.EcdsaSecp521r1VerificationKey2019;
        } else if ("EcdsaSecp256k1VerificationKey2019".equals(pubKeyType)) {
            return PubKeyType.EcdsaSecp256k1VerificationKey2019;
        } else if ("Ed25519VerificationKey2018".equals(pubKeyType)) {
            return PubKeyType.Ed25519VerificationKey2018;
        } else if ("SM2VerificationKey2019".equals(pubKeyType)) {
            return PubKeyType.SM2VerificationKey2019;
        } else {
            throw new SDKException("un support pub key type");
        }
    }
}
