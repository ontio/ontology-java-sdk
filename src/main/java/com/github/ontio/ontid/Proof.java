package com.github.ontio.ontid;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;

@JSONType(orders = {"type", "created", "proofPurpose", "verificationMethod", "hex", "jws"})
public class Proof {
    public PubKeyType type;
    public String created; // time stamp
    public ProofPurpose proofPurpose;
    public String verificationMethod; // pubkey uri
    public String hex;
    public String jws;

    public Proof(String publicKeyURI, String created, PubKeyType type, ProofPurpose proofPurpose) {
        this.type = type;
        this.created = created;
        if (proofPurpose == null) {
            proofPurpose = ProofPurpose.assertionMethod;
        }
        this.proofPurpose = proofPurpose;
        this.verificationMethod = publicKeyURI;
    }

    public void fillSignature(Account account, byte[] needSignData) throws Exception {
        byte[] sig = account.generateSignature(needSignData, account.getSignatureScheme(), null);
        hex = Helper.toHexString(sig);
    }

    public byte[] parseSignature() {
        return Helper.hexToBytes(hex);
    }
}
