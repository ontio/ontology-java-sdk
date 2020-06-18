package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.ALG;
import com.github.ontio.ontid.jwt.JWTClaim;
import com.github.ontio.sdk.exception.SDKException;

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
    public String issuer; // issuer ontId
    public String issuanceDate;
    public String expirationDate;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;
    public Proof proof;

    public VerifiableCredential() {
        this.id = UUID.randomUUID().toString();
    }

    public byte[] genNeedSignData() {
        Proof proof = this.proof;
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.proof = proof;
        return Digest.hash256(jsonStr.getBytes());
    }

    public String findSubjectId() throws Exception {
        return Util.fetchId(credentialSubject);
    }

    public static VerifiableCredential deserializeFromJWT(String jwtClaim, Proof.ProofPurpose proofPurpose)
            throws Exception {
        JWTClaim claim = JWTClaim.deserializeToJWTClaim(jwtClaim);
        if (claim.payload.vc == null) {
            throw new SDKException("illegal jwt claim");
        }
        VerifiableCredential credential = new VerifiableCredential();
        credential.context = claim.payload.vc.context;
        credential.id = claim.payload.jti;
        credential.type = claim.payload.vc.type;
        credential.issuer = claim.payload.iss;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        credential.issuanceDate = formatter.format(new Date(Long.parseLong(claim.payload.nbf) * 1000));
        credential.expirationDate = formatter.format(new Date(Long.parseLong(claim.payload.exp) * 1000));
        credential.credentialStatus = claim.payload.vc.credentialStatus;
        // generate proof
        Proof proof = new Proof(claim.header.kid, credential.issuanceDate,
                ALG.getProofTypeFromAlg(claim.header.alg), proofPurpose);
        proof.jws = claim.jws;
        credential.proof = proof;
        if (claim.payload.vc.credentialSubject == null) {
            return credential;
        }
        if (claim.payload.sub != null && !claim.payload.sub.isEmpty()) { // inject id to credential subject
            String credentialSubjectJson = JSON.toJSONString(claim.payload.vc.credentialSubject);
            if (credentialSubjectJson.startsWith("[")) {
                JSONArray subject = JSON.parseArray(credentialSubjectJson);
                for (int i = 0; i < subject.size(); i++) {
                    JSONObject object = subject.getJSONObject(i);
                    object.put("id", claim.payload.sub);
                }
                credential.credentialSubject = subject;
            } else {
                JSONObject subject = JSON.parseObject(credentialSubjectJson);
                subject.put("id", claim.payload.sub);
                credential.credentialSubject = subject;
            }
        } else {
            credential.credentialSubject = claim.payload.vc.credentialSubject;
        }
        return credential;
    }
}

