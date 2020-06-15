package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.neovm.ClaimRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OntId2 {
    public static final String CLAIM_COMMITTED = "01";
    public static final String CLAIM_REVOKED = "00";
    public static final String CLAIM_NOT_EXIST = "02";

    private String ontId;
    private Account signer;
    private OntIdPubKey signerPubKey;
    private ClaimRecord claimRecord;
    private OntId ontIdContract;

    public String getOntId() {
        return ontId;
    }

    public Account getSigner() {
        return signer;
    }


    // which index that signer public key corresponding in ontId keys set
    private int signerPubKeyIndex;

    public ClaimRecord getClaimRecord() {
        return claimRecord;
    }

    public void setClaimRecord(ClaimRecord claimRecord) {
        this.claimRecord = claimRecord;
    }

    public OntId getOntIdContract() {
        return ontIdContract;
    }

    public void setOntIdContract(OntId ontIdContract) {
        this.ontIdContract = ontIdContract;
    }

    public OntId2(String ontId, Account signer, ClaimRecord claimRecord, OntId ontIdContract) throws Exception {
        this.ontId = ontId;
        this.signer = signer;
        this.claimRecord = claimRecord;
        this.ontIdContract = ontIdContract;
        this.signerPubKey = querySignerPubKey();
    }

    public void updateOntIdAndSigner(String ontId, Account signer) throws Exception {
        this.signer = signer;
        this.ontId = ontId;
        this.signerPubKey = querySignerPubKey();
    }

    private OntIdPubKey querySignerPubKey() throws Exception {
        String allPubKeysJson = ontIdContract.sendGetPublicKeys(ontId);
        ArrayList<OntIdPubKey> allPubKeys = new ArrayList<>(JSON.parseArray(allPubKeysJson, OntIdPubKey.class));
        String signerPubKey = Helper.toHexString(signer.serializePublicKey()).toLowerCase();
        int index = 0;
        for (OntIdPubKey pubKey :
                allPubKeys) {
            index++;
            if (signerPubKey.equals(pubKey.publicKeyHex.toLowerCase())) {
                return pubKey;
            }
        }
        throw new SDKException("signer not found in ontId");
    }

    public SignRequest genSignReq(String claim) throws Exception {
        byte[] signature = signer.generateSignature(Digest.hash256(claim.getBytes()), signer.getSignatureScheme(),
                null);
        return new SignRequest(claim, ontId, Helper.getbyteStr(signature));
    }

    public VerifiableCredential createClaim(String[] context, String[] type, JSONObject credentialSubject,
                                            Date expiration) throws Exception {
        VerifiableCredential credential = new VerifiableCredential();
        credential.context = context;
        credential.type = type;
        credential.credentialSubject = credentialSubject;
        // check expiration
        Date current = new Date();
        if (expiration.before(current)) {
            throw new SDKException("claim expired");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
        credential.expirationDate = formatter.format(expiration);
        credential.issuanceDate = formatter.format(current);
        // generate proof
        Proof proof = new Proof(signerPubKey.id);
        String serializedStr = JSON.toJSONString(credential);
        proof.fillSignature(signer, serializedStr);
        credential.proof = proof;
        // generate id
        String wholeStr = JSON.toJSONString(credential);
        credential.id = Helper.toHexString(Digest.hash256(wholeStr.getBytes()));
        return credential;
    }

    public boolean verifyClaim(String[] credibleOntIds, VerifiableCredential credential) throws Exception {
        boolean ontIdCredible = verifyClaimOntIdCredible(credibleOntIds, credential);
        boolean claimNotExpired = verifyClaimNotExpired(credential);
        boolean verifiedSig = verifyClaimSignature(credential);
        boolean notRevoked = verifyClaimNotRevoked(credential);
        return ontIdCredible && claimNotExpired && verifiedSig && notRevoked;
    }

    public boolean verifyClaimOntIdCredible(String[] credibleOntIds, VerifiableCredential credential)
            throws Exception {
        String[] keyInfo = credential.proof.verificationMethod.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid proof verificationMethod %s",
                    credential.proof.verificationMethod));
        }
        int index = Integer.parseInt(keyInfo[1]);
        for (String ontId :
                credibleOntIds) {
            // check ontID equals
            if (credential.proof.verificationMethod.startsWith(ontId)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyClaimNotExpired(VerifiableCredential credential) throws Exception {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ssZ");
        Date expiration = formatter.parse(credential.expirationDate);
        return expiration.after(new Date());
    }

    public boolean verifyClaimSignature(VerifiableCredential credential) throws Exception {
        String[] keyInfo = credential.proof.verificationMethod.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid proof verificationMethod %s",
                    credential.proof.verificationMethod));
        }
        int index = Integer.parseInt(keyInfo[1]);
        String ontId = keyInfo[0];
        String allPubKeysJson = ontIdContract.sendGetPublicKeys(ontId);
        ArrayList<OntIdPubKey> allPubKeys = new ArrayList<>(JSON.parseArray(allPubKeysJson, OntIdPubKey.class));
        for (OntIdPubKey pubKey :
                allPubKeys) {
            if (pubKey.id.equals(credential.proof.verificationMethod)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyClaimNotRevoked(VerifiableCredential credential) throws Exception {
        return CLAIM_COMMITTED.equals(this.claimRecord.sendGetStatus2(credential.id));
    }
}
