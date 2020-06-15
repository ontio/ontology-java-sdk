package com.github.ontio.ontid;

import com.github.ontio.account.Account;

public class OntIdSigner {
    String ontId;
    String pubKeyId;
    Account signer;

    public OntIdSigner(String ontId, String pubKeyId, Account signer) {
        this.ontId = ontId;
        this.pubKeyId = pubKeyId;
        this.signer = signer;
    }
}
