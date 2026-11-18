package com.github.ontio.ontid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.ontio.account.Account;
import com.github.ontio.crypto.Base58;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProofV2 {
    public String type = "DataIntegrityProof";
    public String cryptosuite = "ecdsa-rdfc-2019";
    public String created;
    public String verificationMethod;
    public ProofPurpose proofPurpose;
    public String proofValue;

    public ProofV2() {
    }

    public ProofV2(String publicKeyURI, String created, ProofPurpose proofPurpose) {
        this.created = created;
        if (proofPurpose == null) {
            proofPurpose = ProofPurpose.assertionMethod;
        }
        this.proofPurpose = proofPurpose;
        this.verificationMethod = publicKeyURI;
    }

    public ProofV2 genNeedSignProof() {
        return new ProofV2(verificationMethod, created, proofPurpose);
    }

    public void fillMultibaseSignature(Account account, byte[] needSignData) throws Exception {
        byte[] sig = account.generateSignature(needSignData, account.getSignatureScheme(), null);
        String base58Encoded = Base58.encode(sig);
        proofValue = "z" + base58Encoded;
    }

    public byte[] parseMultibaseSignature() {
        return Base58.decode(proofValue.substring(1));
    }
}
