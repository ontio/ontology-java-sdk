package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.JWTClaim;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

// Verifiable credential also called claim
@JSONType(orders = {"@context", "id", "type", "issuer", "issuanceDate", "expirationDate",
        "credentialSubject", "credentialStatus", "proof"})
public class VerifiableCredential {
    @JSONField(name = "@context")
    public String[] context;
    public String id; // hash
    public String[] type;
    public Object issuer; // issuer ontId, or an object contains ONTID
    public String issuanceDate;
    public String expirationDate;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;
    public Proof proof;

    public VerifiableCredential() {
        this.id = "urn:uuid:" + UUID.randomUUID().toString();
    }

    public String fetchIssuerOntId() {
        return Util.fetchId(this.issuer);
    }

    public byte[] genNeedSignData() {
        Proof proof = this.proof;
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.proof = proof;
        return Digest.sha256(jsonStr.getBytes());
    }

    public String findSubjectId() {
        return Util.fetchId(credentialSubject);
    }

    public static VerifiableCredential deserializeFromJWT(JWTClaim claim, ProofPurpose proofPurpose)
            throws Exception {
        VerifiableCredential credential = new VerifiableCredential();
        credential.context = claim.payload.vc.context;
        credential.id = claim.payload.jti;
        credential.type = claim.payload.vc.type;
        credential.issuer = claim.payload.iss;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (claim.payload.iat != null && !claim.payload.iat.isEmpty()) {
            credential.issuanceDate = formatter.format(new Date(Long.parseLong(claim.payload.iat) * 1000));
        } else {
            credential.issuanceDate = formatter.format(new Date(Long.parseLong(claim.payload.nbf) * 1000));
        }
        if (claim.payload.exp != null && !claim.payload.exp.isEmpty()) {
            credential.expirationDate = formatter.format(new Date(Long.parseLong(claim.payload.exp) * 1000));
        }
        credential.credentialStatus = claim.payload.vc.credentialStatus;
        // assign issuer
        if (claim.payload.vc.issuer == null) {
            credential.issuer = claim.payload.iss;
        } else {
            JSONObject jsonIssuer = (JSONObject) JSONObject.toJSON(claim.payload.vc.issuer);
            jsonIssuer.put("id", claim.payload.iss);
            credential.issuer = jsonIssuer;
        }
        // generate proof
        Proof proof = new Proof(claim.header.kid, credential.issuanceDate, claim.header.alg.proofPubKeyType(),
                proofPurpose);
        proof.jws = claim.jws;
        credential.proof = proof;
        if (claim.payload.vc.credentialSubject == null) {
            return credential;
        }
        if (claim.payload.sub != null && !claim.payload.sub.isEmpty()) { // inject id to credential subject
            JSONObject subject = (JSONObject) JSONObject.toJSON(claim.payload.vc.credentialSubject);
            subject.put("id", claim.payload.sub);
            credential.credentialSubject = subject;
        } else {
            credential.credentialSubject = claim.payload.vc.credentialSubject;
        }
        return credential;
    }
}
