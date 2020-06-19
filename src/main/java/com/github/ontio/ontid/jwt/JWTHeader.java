package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.ALG;
import com.github.ontio.ontid.PubKeyType;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;

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

    public JWTHeader(VerifiablePresentation presentation) throws Exception {
        this.alg = presentation.fetchProofType().getAlg();
        this.kid = presentation.findVerificationMethod();
    }
}
