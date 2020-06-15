package com.github.ontio.ontid;

import com.alibaba.fastjson.JSONObject;

// Verifiable credential also called claim
public class VerifiableCredential {
    public String[] context;
    public String id; // hash
    public String[] type;
    public String issuer;
    public String issuanceDate; // TODO: need to check format
    public String expirationDate;
    public JSONObject credentialSubject;
    public Proof proof;
}

class CredentialStatus {
    public String id; // should be claim contract address
    public String type = "Claim Contract";

    public CredentialStatus(String scriptHash) {
        id = scriptHash;
    }
}
