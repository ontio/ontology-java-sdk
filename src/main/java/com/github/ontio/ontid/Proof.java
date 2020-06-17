package com.github.ontio.ontid;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.exception.SDKException;

@JSONType(orders = {"type", "created", "proofPurpose", "verificationMethod", "signature"})
public class Proof {
    public enum ProofType {EcdsaSecp256r1Signature2019}

    public enum ProofPurpose {assertionMethod}

    public ProofType type;
    public String created; // time stamp
    public ProofPurpose proofPurpose;
    public String verificationMethod; // pubkey uri
    public String signature;

    public Proof(String publicKeyURI, String created, ProofType type, ProofPurpose proofPurpose) {
        this.type = type;
        this.created = created;
        this.proofPurpose = proofPurpose;
        this.verificationMethod = publicKeyURI;
    }

    public void fillSignature(Account account, byte[] needSignData) throws Exception {
        byte[] sig = account.generateSignature(needSignData, account.getSignatureScheme(), null);
        signature = Helper.toHexString(sig);
    }

    public int parsePubKeyIndex() throws Exception {
        if (this.verificationMethod == null || "".equals(this.verificationMethod)) {
            return 0;
        }
        String[] keyInfo = this.verificationMethod.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid proof verificationMethod %s", this.verificationMethod));
        }
        return Integer.parseInt(keyInfo[1]);
    }

    public byte[] parseSignature() {
        return Helper.hexToBytes(signature);
    }
}
