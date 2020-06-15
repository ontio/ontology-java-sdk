package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

// Verifiable credential also called claim
public class VerifiableCredential {
    public String[] context;
    public String id; // hash
    public String[] type;
    public String issuer;
    public String issuanceDate;
    public String expirationDate;
    public JSONObject credentialSubject;
    public Proof proof;
    public CredentialStatus credentialStatus;

    public String genNeedSignData() {
        String id = this.id;
        Proof proof = this.proof;
        this.id = "";
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.id = id;
        this.proof = proof;
        return jsonStr;
    }
}

class CredentialStatus {
    public String id; // should be claim contract address
    public String type = "Claim Contract";

    public CredentialStatus(String scriptHash) {
        id = scriptHash;
    }
}
