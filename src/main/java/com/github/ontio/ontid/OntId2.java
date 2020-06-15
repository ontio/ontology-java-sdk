package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
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

    private OntIdSigner signer;
    private ClaimRecord claimRecord;
    private OntId ontIdContract;

    public OntIdSigner getSigner() {
        return signer;
    }

    public void setOntIdAndSigner(OntIdSigner signer) {
        this.signer = signer;
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
        this.claimRecord = claimRecord;
        this.ontIdContract = ontIdContract;
        if (ontId != null && !"".equals(ontId) && signer != null) {
            OntIdPubKey pubKey = querySignerPubKey(ontId, Helper.toHexString(signer.serializePublicKey()));
            this.signer = new OntIdSigner(ontId, pubKey.id, signer);
        }
    }

    public void updateOntIdAndSigner(String ontId, Account signer) throws Exception {
        OntIdPubKey pubKey = querySignerPubKey(ontId, Helper.toHexString(signer.serializePublicKey()));
        this.signer = new OntIdSigner(ontId, pubKey.id, signer);
    }

    private OntIdPubKey querySignerPubKey(String ontId, String hexPubKey) throws Exception {
        String allPubKeysJson = ontIdContract.sendGetPublicKeys(ontId);
        ArrayList<OntIdPubKey> allPubKeys = new ArrayList<>(JSON.parseArray(allPubKeysJson, OntIdPubKey.class));
        String signerPubKey = hexPubKey.toLowerCase();
        for (OntIdPubKey pubKey :
                allPubKeys) {
            if (signerPubKey.equals(pubKey.publicKeyHex.toLowerCase())) {
                return pubKey;
            }
        }
        throw new SDKException("signer not found in ontId");
    }

    public SignRequest genSignReq(String claim) throws Exception {
        byte[] signature = signer.signer.generateSignature(Digest.hash256(claim.getBytes()),
                signer.signer.getSignatureScheme(), null);
        return new SignRequest(claim, signer.ontId, Helper.getbyteStr(signature));
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
        Proof proof = new Proof(signer.pubKeyId);
        String needSignData = credential.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        credential.proof = proof;
        // generate id
        String wholeStr = JSON.toJSONString(credential);
        credential.id = Helper.toHexString(Digest.hash256(wholeStr.getBytes()));
        return credential;
    }

    // self ontId is issuer
    // fetch index from credential.proof
    // use self signer and payer to sign tx
    public String commitClaim(VerifiableCredential credential, String ownerOntId, Account payer,
                              long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        Transaction tx = claimRecord.makeCommit2(signer.ontId, ownerOntId, credential.id,
                credential.proof.getPubKeyIndex(), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }

    public boolean verifyClaim(String[] credibleOntIds, VerifiableCredential credential) throws Exception {
        boolean ontIdCredible = verifyClaimOntIdCredible(credibleOntIds, credential);
        boolean claimNotExpired = verifyClaimNotExpired(credential);
        boolean verifiedSig = verifyClaimSignature(credential);
        boolean notRevoked = verifyClaimNotRevoked(credential);
        return ontIdCredible && claimNotExpired && verifiedSig && notRevoked;
    }

    public boolean verifyClaimOntIdCredible(String[] credibleOntIds, VerifiableCredential credential) {
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

    public boolean verifyClaimSignature(VerifiableCredential claim) throws Exception {
        return verifyOntIdSignature(claim.proof.verificationMethod,
                claim.genNeedSignData().getBytes(), claim.proof.getSignature());
    }

    public boolean verifyClaimNotRevoked(VerifiableCredential credential) throws Exception {
        return CLAIM_COMMITTED.equals(this.claimRecord.sendGetStatus2(credential.id));
    }

    public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                                     OntIdSigner[] otherSigners) throws Exception {
        VerifiablePresentation presentation = new VerifiablePresentation();
        presentation.context = context;
        presentation.type = type;
        presentation.verifiableCredential = claims;
        Proof[] proofs = new Proof[otherSigners.length + 1];
        String needSignData = JSON.toJSONString(presentation);
        Proof proof = new Proof(signer.pubKeyId);
        proof.fillSignature(signer.signer, needSignData);
        proofs[0] = proof;
        int index = 0;
        for (OntIdSigner signer :
                otherSigners) {
            Proof p = new Proof(signer.pubKeyId);
            p.fillSignature(signer.signer, needSignData);
            index++;
            proofs[index] = p;
        }
        presentation.proof = proofs;
        String serializedStr = JSON.toJSONString(presentation);
        presentation.id = Helper.toHexString(serializedStr.getBytes());
        return presentation;
    }

    public boolean verifyPresentation(VerifiablePresentation presentation, String[] credibleOntIds) throws Exception {
        // verify each claim
        for (VerifiableCredential claim :
                presentation.verifiableCredential) {
            if (!verifyClaim(credibleOntIds, claim)) {
                return false;
            }
        }
        // verify presentation
        byte[] needSignData = presentation.genNeedSignData().getBytes();
        for (Proof proof : presentation.proof) {
            if (!verifyOntIdSignature(proof.verificationMethod, needSignData, proof.getSignature())) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyOntIdSignature(String pubKeyId, byte[] needSignData, byte[] signature) throws Exception {
        String[] keyInfo = pubKeyId.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid pubKeyId %s", pubKeyId));
        }
        String ontId = keyInfo[0];
        String allPubKeysJson = ontIdContract.sendGetPublicKeys(ontId);
        ArrayList<OntIdPubKey> allPubKeys = new ArrayList<>(JSON.parseArray(allPubKeysJson, OntIdPubKey.class));
        for (OntIdPubKey pubKey :
                allPubKeys) {
            if (pubKey.id.equals(pubKeyId)) {
                Account account = new Account(false, Helper.hexToBytes(pubKey.publicKeyHex));
                return account.verifySignature(needSignData, signature);
            }
        }
        return false;
    }
}
