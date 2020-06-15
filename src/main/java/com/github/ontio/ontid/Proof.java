package com.github.ontio.ontid;

import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Proof {
    public static final String PROOF_TYPE_ECDSA = "ECDSASignature2019";

    public String type;
    public String created;
    public String proofPurpose;
    public String verificationMethod;
    public String signature;

    public Proof(String publicKeyURI) {
        this.type = PROOF_TYPE_ECDSA;
        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
        this.created = format.format(currentTime);
        this.proofPurpose = "assertionMethod";
        this.verificationMethod = publicKeyURI;
    }

    public void genJWS(Account account, String needSignData) throws Exception {
        byte[] sig = account.generateSignature(Digest.hash256(needSignData.getBytes()), account.getSignatureScheme(),
                null);
        signature = Helper.toHexString(sig);
    }
}
