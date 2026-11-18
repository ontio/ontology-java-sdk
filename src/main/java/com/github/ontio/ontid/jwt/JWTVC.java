package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.CredentialStatus;
import com.github.ontio.ontid.Proof;
import com.github.ontio.ontid.VerifiableCredential;
import com.github.ontio.sdk.exception.SDKException;

@JSONType(orders = {"@context", "id", "type", "issuer", "validFrom", "validUntil",
        "credentialSubject", "credentialStatus", "proof"})
public class JWTVC {
    @JSONField(name = "@context")
    public String[] context;
    public String id;
    public String[] type;
    public Object issuer;
    public String validFrom;
    public String validUntil;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;
    public Proof proof;

    public JWTVC() {
    }

    public JWTVC(VerifiableCredential credential) throws Exception {
        this.context = credential.context;
        this.id = credential.id;
        this.type = credential.type;
        this.credentialStatus = credential.credentialStatus;
        this.validFrom = credential.validFrom;
        this.validUntil = credential.validUntil;
        if (credential.proof != null) {
            this.proof = credential.proof.genJWTProof();
        }
        if (credential.issuer.getClass().isPrimitive() || credential.issuer.getClass().isArray() ||
                credential.issuer instanceof JSONArray) {
            throw new SDKException("illegal credential issuer");
        }
        if (!(credential.issuer instanceof String)) {
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(credential.issuer);
            jsonObject.remove("id");
            if (jsonObject.size() > 0) {
                this.issuer = jsonObject;
            }
        } else {
            this.issuer = credential.issuer;
        }
        // remove id attribute
        if (credential.credentialSubject != null && !credential.credentialSubject.getClass().isArray()
                && !(credential.credentialSubject instanceof JSONArray)) {
            JSONObject credentialSubject = (JSONObject) JSONObject.toJSON(credential.credentialSubject);
            credentialSubject.remove("id");
            if (credentialSubject.size() > 0) {
                this.credentialSubject = credentialSubject;
            }
        } else {
            this.credentialSubject = credential.credentialSubject;
        }
    }
}
