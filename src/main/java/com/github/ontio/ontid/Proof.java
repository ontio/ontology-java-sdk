package com.github.ontio.ontid;

import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.sdk.exception.SDKException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Proof {
    public static final String PROOF_TYPE_ECDSA = "ECDSASignature2019";

    public String type;
    public String created; // time stamp
    public String proofPurpose; // fixed as "assertionMethod"
    public String verificationMethod; // pubkey uri
    public String signature;

    public Proof(String publicKeyURI) {
        this.type = PROOF_TYPE_ECDSA;
        Date currentTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        this.created = format.format(currentTime);
        this.proofPurpose = "assertionMethod";
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
