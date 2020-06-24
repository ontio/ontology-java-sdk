package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.jwt.JWTCredential;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@JSONType(orders = {"@context", "id", "type", "issuer", "issuanceDate", "expirationDate",
        "credentialSubject", "credentialStatus", "proof"})
public class VerifiableCredential {
    @JSONField(name = "@context")
    public String[] context;
    public String id; // uuid
    public String[] type;
    public Object issuer; // issuer ontId, or an object contains ONTID
    public String issuanceDate;
    public String expirationDate;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;
    public Proof proof; // TODO: support multi

    public VerifiableCredential() {
        this.id = "urn:uuid:" + UUID.randomUUID().toString();
    }

    public String fetchIssuerOntId() {
        return Util.fetchId(this.issuer);
    }

    public byte[] genNeedSignData() throws Exception {
        Proof proof = this.proof;
        this.proof = this.proof.genNeedSignProof();
        String jsonStr = JSON.toJSONString(this);
        this.proof = proof;
        return this.proof.type.getAlg().hash(jsonStr.getBytes());
    }

    public String findSubjectId() {
        return Util.fetchId(credentialSubject);
    }

    public static VerifiableCredential deserializeFromJWT(JWTCredential jwtCred) {
        VerifiableCredential credential = new VerifiableCredential();
        credential.context = jwtCred.payload.vc.context;
        credential.id = jwtCred.payload.jti;
        credential.type = jwtCred.payload.vc.type;
        // set date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        if (jwtCred.payload.iat > 0) {
            credential.issuanceDate = formatter.format(new Date(jwtCred.payload.iat * 1000));
        } else if (jwtCred.payload.nbf > 0) {
            credential.issuanceDate = formatter.format(new Date(jwtCred.payload.nbf * 1000));
        }
        if (jwtCred.payload.exp > 0) {
            credential.expirationDate = formatter.format(new Date(jwtCred.payload.exp * 1000));
        }
        credential.credentialStatus = jwtCred.payload.vc.credentialStatus;
        // assign issuer
        if (jwtCred.payload.vc.issuer == null) {
            credential.issuer = jwtCred.payload.iss;
        } else {
            JSONObject jsonIssuer = (JSONObject) JSONObject.toJSON(jwtCred.payload.vc.issuer);
            jsonIssuer.put("id", jwtCred.payload.iss);
            credential.issuer = jsonIssuer;
        }
        // generate proof
        credential.proof = jwtCred.parseProof();
        // generate credential subject
        if (jwtCred.payload.sub != null && !jwtCred.payload.sub.isEmpty()) { // inject id to credential subject
            JSONObject subject = (JSONObject) JSONObject.toJSON(jwtCred.payload.vc.credentialSubject);
            subject.put("id", jwtCred.payload.sub);
            credential.credentialSubject = subject;
        } else {
            credential.credentialSubject = jwtCred.payload.vc.credentialSubject;
        }
        return credential;
    }
}
