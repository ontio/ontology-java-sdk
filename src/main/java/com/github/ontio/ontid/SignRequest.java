package com.github.ontio.ontid;

public class SignRequest {
    String claim;
    String ontId;
    Proof signature;

    public SignRequest(String claim, String ontId, Proof signature) {
        this.claim = claim;
        this.ontId = ontId;
        this.signature = signature;
    }
}
