package com.github.ontio.ontid;

import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.ontid.jwt.ALG;

@JSONType(orders = {"type", "created", "proofPurpose", "verificationMethod", "hex", "jws"})
public class Proof {

    public enum ProofType {
        EcdsaSecp256r1Signature2019(ALG.ALG_ES256);

        private String alg;

        private ProofType(String alg) {
            this.alg = alg;
        }

        public String getAlg() {
            return alg;
        }
    }

    public enum ProofPurpose {assertionMethod}

    public ProofType type;
    public String created; // time stamp
    public ProofPurpose proofPurpose;
    public String verificationMethod; // pubkey uri
    public String hex;
    public String jws;

    public Proof(String publicKeyURI, String created, ProofType type, ProofPurpose proofPurpose) {
        this.type = type;
        this.created = created;
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
