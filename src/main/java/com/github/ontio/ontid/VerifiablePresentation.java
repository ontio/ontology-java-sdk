package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.JWTClaim;
import com.github.ontio.sdk.exception.SDKException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

@JSONType(orders = {"@context", "id", "type", "verifiableCredential", "holder", "proof"})
public class VerifiablePresentation {
    @JSONField(name = "@context")
    public String[] context;
    public String id;
    public String[] type;
    public VerifiableCredential[] verifiableCredential;
    public Object holder; // holder may not use
    public Proof[] proof;

    public VerifiablePresentation() {
        this.id = "urn:uuid:" + UUID.randomUUID().toString();
    }

    public byte[] genNeedSignData() {
        Proof[] proofs = this.proof;
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.proof = proofs;
        return Digest.sha256(jsonStr.getBytes());
    }

    public String fetchHolderOntId() {
        return Util.fetchId(holder);
    }

    public String findSubjectId() {
        if (this.verifiableCredential.length != 1) {
            return this.verifiableCredential[0].findSubjectId();
        }
        return "";
    }

    public PubKeyType fetchProofType() throws Exception {
        if (this.proof == null || this.proof.length != 1) {
            int len = this.proof == null ? 0 : this.proof.length;
            throw new SDKException(String.format("the num of presentation proofs: %d mismatch", len));
        }
        return this.proof[0].type;
    }

    public String findVerificationMethod() throws Exception {
        if (this.proof == null || this.proof.length != 1) {
            int len = this.proof == null ? 0 : this.proof.length;
            throw new SDKException(String.format("the num of presentation proofs: %d mismatch", len));
        }
        return this.proof[0].verificationMethod;
    }

    public String findJWS() throws Exception {
        if (this.proof == null || this.proof.length != 1) {
            int len = this.proof == null ? 0 : this.proof.length;
            throw new SDKException(String.format("the num of presentation proofs: %d mismatch", len));
        }
        return this.proof[0].jws;
    }

    public static VerifiablePresentation deserializeFromJWT(JWTClaim claim, ProofPurpose purpose)
            throws Exception {
        VerifiablePresentation presentation = new VerifiablePresentation();
        presentation.context = claim.payload.vp.context;
        presentation.id = claim.payload.jti;
        presentation.type = claim.payload.vp.type;
        presentation.holder = claim.payload.iss;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = "";
        if (claim.payload.iat == null || claim.payload.iat.isEmpty()) {
            created = formatter.format(new Date(Long.parseLong(claim.payload.nbf) * 1000));
        } else {
            created = formatter.format(new Date(Long.parseLong(claim.payload.iat) * 1000));
        }
        Proof proof = new Proof(claim.header.kid, created, claim.header.alg.proofType(), purpose);
        proof.jws = claim.jws;
        presentation.proof = new Proof[]{proof};
        int vcLength = claim.payload.vp.verifiableCredential.length;
        VerifiableCredential[] credentials = new VerifiableCredential[vcLength];
        for (int i = 0; i < vcLength; i++) {
            String vc = claim.payload.vp.verifiableCredential[i];
            JWTClaim vcJWT = JWTClaim.deserializeToJWTClaim(vc);
            VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(vcJWT, purpose);
            credentials[i] = credential;
        }
        presentation.verifiableCredential = credentials;
        return presentation;
    }
}
