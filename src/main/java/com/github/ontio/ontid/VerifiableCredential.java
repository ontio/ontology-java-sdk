package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.github.ontio.crypto.Digest;

// Verifiable credential also called claim
public class VerifiableCredential {
    public String[] context;
    public String id; // hash
    public String[] type;
    public String issuer; // issuer ontId
    public String issuanceDate;
    public String expirationDate;
    public Object credentialSubject;
    public CredentialStatus credentialStatus;
    public Proof proof;

    public byte[] genNeedSignData() {
        String id = this.id;
        Proof proof = this.proof;
        this.id = "";
        this.proof = null;
        String jsonStr = JSON.toJSONString(this);
        this.id = id;
        this.proof = proof;
        return Digest.hash256(jsonStr.getBytes());
    }
}

class CredentialStatus {
    public String id; // should be claim contract address
    public String type = "ClaimContract";

    public CredentialStatus(String scriptHash) {
        id = scriptHash;
    }
}
