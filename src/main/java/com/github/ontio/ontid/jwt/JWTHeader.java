package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.*;

@JSONType(orders = {"alg", "kid", "typ"})
public class JWTHeader {
    public ALG alg;
    public String kid; // VerifiableCredential issuer
    public String typ = "JWT";

    public JWTHeader() {
    }

    public JWTHeader(ALG alg, String kid) {
        this.alg = alg;
        this.kid = kid;
    }

    public JWTHeader(PubKeyType pubKeyType, String kid) {
        this.alg = pubKeyType.getAlg();
        this.kid = kid;
    }

    public JWTHeader(VerifiableCredential credential) {
        this.alg = credential.proof.type.getAlg();
        this.kid = credential.proof.verificationMethod;
    }

    public JWTHeader(Proof proof) throws Exception {
        this.alg = proof.type.getAlg();
        this.kid = proof.verificationMethod;
    }
}
