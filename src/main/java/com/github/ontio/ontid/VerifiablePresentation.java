package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.ALG;
import com.github.ontio.ontid.jwt.JWTClaim;
import com.github.ontio.sdk.exception.SDKException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@JSONType(orders = {"@context", "id", "type", "verifiableCredential", "holder", "proof"})
public class VerifiablePresentation {
    @JSONField(name = "@context")
    public String[] context;
    public String id;
    public String[] type;
    public VerifiableCredential[] verifiableCredential;
    public String holder; // holder may not use
    public Proof[] proof;

    public byte[] genNeedSignData() {
        String id = this.id;
        Proof[] proofs = this.proof;
        this.id = "";
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.id = id;
        this.proof = proofs;
        return Digest.hash256(jsonStr.getBytes());
    }

    public Date findExpiration() throws Exception {
        Date minExpiration = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (VerifiableCredential credential : this.verifiableCredential) {
            Date exp = formatter.parse(credential.expirationDate);
            if (minExpiration == null || minExpiration.after(exp)) {
                minExpiration = exp;
            }
        }
        if (minExpiration == null) {
            throw new SDKException("presentation has no credential");
        }
        return minExpiration;
    }

    public Date findIssuanceDate() throws Exception {
        Date minIssuanceDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        for (VerifiableCredential credential : this.verifiableCredential) {
            Date issuance = formatter.parse(credential.issuanceDate);
            if (minIssuanceDate == null || minIssuanceDate.after(issuance)) {
                minIssuanceDate = issuance;
            }
        }
        if (minIssuanceDate == null) {
            throw new SDKException("presentation has no credential");
        }
        return minIssuanceDate;
    }

    public String findSubjectId() throws Exception {
        Set<String> subjectIds = new HashSet<>();
        for (VerifiableCredential credential : this.verifiableCredential) {
            subjectIds.add(credential.findSubjectId());
        }
        if (subjectIds.size() == 0) {
            return "";
        }
        if (subjectIds.size() == 1) {
            return (String) subjectIds.toArray()[0];
        }
        throw new SDKException("presentation cannot unify credential subject id");
    }

    public Proof.ProofType fetchProofType() throws Exception {
        if (this.proof == null || this.proof.length != 1) {
            int len = this.proof == null ? 0 : this.proof.length;
            throw new SDKException(String.format("the num of presentation: %d mismatch", len));
        }
        return this.proof[0].type;
    }

    public String findVerificationMethod() throws Exception {
        if (this.proof == null || this.proof.length != 1) {
            int len = this.proof == null ? 0 : this.proof.length;
            throw new SDKException(String.format("the num of presentation: %d mismatch", len));
        }
        return this.proof[0].verificationMethod;
    }

    public String findJWS() throws Exception {
        if (this.proof == null || this.proof.length != 1) {
            int len = this.proof == null ? 0 : this.proof.length;
            throw new SDKException(String.format("the num of presentation: %d mismatch", len));
        }
        return this.proof[0].jws;
    }

    public static VerifiablePresentation deserializeFromJWT(String jwtPresentation, Proof.ProofPurpose purpose)
            throws Exception {
        JWTClaim claim = JWTClaim.deserializeToJWTClaim(jwtPresentation);
        if (claim.payload.vp == null) {
            throw new SDKException("illegal jwt");
        }
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
        Proof proof = new Proof(claim.header.kid, created, ALG.getProofTypeFromAlg(claim.header.alg), purpose);
        proof.jws = claim.jws;
        int vcLength = claim.payload.vp.verifiableCredential.length;
        VerifiableCredential[] credentials = new VerifiableCredential[vcLength];
        for (int i = 0; i < vcLength; i++) {
            String vc = claim.payload.vp.verifiableCredential[i];
            VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(vc, purpose);
            credentials[i] = credential;
        }
        presentation.verifiableCredential = credentials;
        return presentation;
    }
}
