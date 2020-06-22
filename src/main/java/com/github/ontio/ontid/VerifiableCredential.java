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
        this.proof = this.proof.genNeedSignProof();
        String jsonStr = JSON.toJSONString(this);
        this.proof = proof;
        return Digest.sha256(jsonStr.getBytes());
    }

    public String findSubjectId() {
        return Util.fetchId(credentialSubject);
    }

    public static VerifiableCredential deserializeFromJWT(JWTClaim claim) {
        VerifiableCredential credential = new VerifiableCredential();
        credential.context = claim.payload.vc.context;
        credential.id = claim.payload.jti;
        credential.type = claim.payload.vc.type;
        // set date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (claim.payload.iat > 0) {
            credential.issuanceDate = formatter.format(new Date(claim.payload.iat * 1000));
        } else if (claim.payload.nbf > 0) {
            credential.issuanceDate = formatter.format(new Date(claim.payload.nbf * 1000));
        }
        if (claim.payload.exp > 0) {
            credential.expirationDate = formatter.format(new Date(claim.payload.exp * 1000));
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
        credential.proof = claim.payload.vc.proof;
        credential.proof.jws = claim.jws;
        credential.proof.verificationMethod = claim.header.kid;
        // generate credential subject
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
