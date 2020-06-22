package com.github.ontio.ontid;

import com.github.ontio.account.Account;

public class OntIdSigner {
    String ontId;
    OntIdPubKey pubKey; // pubkey URI
    Account signer;

    public OntIdSigner(String ontId, OntIdPubKey pubKey, Account signer) {
        this.ontId = ontId;
        this.pubKey = pubKey;
        this.signer = signer;
    }

    public byte[] hash(byte[] msg) throws Exception {
        return pubKey.type.getAlg().hash(msg);
    }
}
