package com.github.ontio.ontid;

public class SignRequest {
    String claim;
    String ontId;
    String signature;

    public SignRequest(String claim, String ontId, String signature) {
        this.claim = claim;
        this.ontId = ontId;
        this.signature = signature;
    }
}
