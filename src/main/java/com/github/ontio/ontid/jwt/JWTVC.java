package com.github.ontio.ontid.jwt;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.ontid.CredentialStatus;
import com.github.ontio.ontid.VerifiableCredential;

@JSONType(orders = {"@context", "type", "credentialSubject", "credentialStatus"})
public class JWTVC {
    @JSONField(name = "@context")
    public String[] context;
    public String[] type;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;
    public Object issuer;

    public JWTVC() {
    }

    public JWTVC(VerifiableCredential credential) {
        this.context = credential.context;
        this.type = credential.type;
        this.credentialStatus = credential.credentialStatus;
        if (!(credential.issuer instanceof String)
                && !credential.issuer.getClass().isPrimitive()
                && !credential.issuer.getClass().isArray()) {
            JSONObject issuer = (JSONObject) JSONObject.toJSON(credential.issuer);
            issuer.remove("id");
            if (issuer.size() > 0) {
                this.issuer = issuer;
            }
        }
        // remove id attribute
        if (credential.credentialSubject != null && !credential.credentialSubject.getClass().isArray()) {
            JSONObject credentialSubject = (JSONObject) JSONObject.toJSON(credential.credentialSubject);
            credentialSubject.remove("id");
            this.credentialSubject = credentialSubject;
        } else {
            this.credentialSubject = credential.credentialSubject;
        }
    }
}