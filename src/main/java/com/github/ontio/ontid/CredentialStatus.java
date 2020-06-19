package com.github.ontio.ontid;


import com.alibaba.fastjson.annotation.JSONType;

@JSONType(orders = {"id", "type"})
public class CredentialStatus {

    public String id; // should be claim contract address
    public CredentialStatusType type;

    public CredentialStatus() {
    }

    public CredentialStatus(String scriptHash, CredentialStatusType type) {
        this.id = scriptHash;
        this.type = type;
    }
}