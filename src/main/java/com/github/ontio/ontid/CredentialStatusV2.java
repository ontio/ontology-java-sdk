package com.github.ontio.ontid;


public class CredentialStatusV2 {
    // should be CredentialRecord contract address
    public String id;
    public String type = "BlockchainSmartContractStatus";
    public String statusPurpose = "revocation";
    public String statusReference;

    public CredentialStatusV2() {
    }

    public CredentialStatusV2(String scriptHash, String statusReference) {
        if (scriptHash.startsWith("did:ont:contract:")) {
            this.id = scriptHash;
        } else {
            this.id = "did:ont:contract:" + scriptHash;
        }

        this.statusReference = statusReference;
    }
}