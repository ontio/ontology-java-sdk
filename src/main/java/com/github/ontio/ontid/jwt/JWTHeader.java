package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.ontid.VerifiablePresentation;

@JSONType(orders = {"alg", "kid", "typ"})
public class JWTHeader {
    public String alg;
    public String kid; // VerifiableCredential issuer
    public String typ = "JWT";

    public JWTHeader(String alg, String kid) {
        this.alg = alg;
        this.kid = kid;
    }

    public JWTHeader(Proof.ProofType proofType, String kid) {
        this.alg = proofType.getAlg();
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
