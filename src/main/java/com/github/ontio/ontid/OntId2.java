package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
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
    public static final String CLAIM_DEFAULT_CONTEXT1 = "https://www.w3.org/2018/credentials/v1";
    public static final String CLAIM_DEFAULT_CONTEXT2 = "https://ontid.ont.io/credentials/v1";

    public static final String CLAIM_DEFAULT_TYPE = "VerifiableCredential";

    public static final String PRESENTATION_DEFAULT_TYPE = "VerifiablePresentation";

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
            String lowerCasePubKey = pubKey.publicKeyHex.toLowerCase();
            if (signerPubKey.equals(lowerCasePubKey)) {
                return pubKey;
            }
        }
        throw new SDKException("signer not found in ontId");
    }

    public SignRequest genSignReq(String claim, boolean hasSignature) throws Exception {
        if (hasSignature) {
            Date current = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Proof proof = new Proof(signer.pubKeyId, formatter.format(current),
                    Proof.ProofType.EcdsaSecp256r1Signature2019, Proof.ProofPurpose.assertionMethod);
            proof.fillSignature(signer.signer, Digest.hash256(claim.getBytes()));
            return new SignRequest(claim, signer.ontId, proof);
        } else {
            return new SignRequest(claim, signer.ontId, null);
        }
    }

    public boolean verifySignReq(SignRequest req) throws Exception {
        return verifyOntIdSignature(req.ontId, Digest.hash256(req.claim.getBytes()), req.signature.parseSignature());
    }

    public VerifiableCredential createClaim(String[] context, String[] type, Object credentialSubject,
                                            Date expiration) throws Exception {
        VerifiableCredential credential = new VerifiableCredential();
        String[] wholeContext = new String[2 + context.length];
        wholeContext[0] = CLAIM_DEFAULT_CONTEXT1;
        wholeContext[1] = CLAIM_DEFAULT_CONTEXT2;
        int index = 2;
        for (String c : context) {
            wholeContext[index] = c;
            index++;
        }
        credential.context = wholeContext;
        String[] wholeType = new String[type.length + 1];
        wholeType[0] = CLAIM_DEFAULT_TYPE;
        index = 1;
        for (String c : type) {
            wholeType[index] = c;
            index++;
        }
        credential.type = wholeType;
        credential.credentialSubject = credentialSubject;
        credential.issuer = signer.ontId;
        credential.credentialStatus = new CredentialStatus(claimRecord.getContractAddress(),
                CredentialStatus.CredentialStatusType.ClaimContract);
        // check expiration
        Date current = new Date();
        if (expiration.before(current)) {
            throw new SDKException("claim expired");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        credential.expirationDate = formatter.format(expiration);
        credential.issuanceDate = formatter.format(current);
        // generate proof
        Proof proof = new Proof(signer.pubKeyId, credential.issuanceDate,
                Proof.ProofType.EcdsaSecp256r1Signature2019, Proof.ProofPurpose.assertionMethod);
        byte[] needSignData = credential.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        credential.proof = proof;
        // generate id
        String wholeStr = JSON.toJSONString(credential);
        credential.id = Helper.toHexString(Digest.hash256(wholeStr.getBytes()));
        return credential;
    }

    // self ontId is issuer
    // fetch index from claim.proof
    // use self signer and payer to sign tx
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String commitClaim(VerifiableCredential claim, String ownerOntId, Account payer,
                              long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        Transaction tx = claimRecord.makeCommit2(signer.ontId, ownerOntId, claim.id,
                claim.proof.parsePubKeyIndex(), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }

    public boolean verifyClaim(String[] credibleOntIds, VerifiableCredential claim) throws Exception {
        boolean ontIdCredible = verifyClaimOntIdCredible(credibleOntIds, claim);
        boolean claimNotExpired = verifyClaimNotExpired(claim);
        boolean verifiedSig = verifyClaimSignature(claim);
        boolean notRevoked = verifyClaimNotRevoked(claim);
        return ontIdCredible && claimNotExpired && verifiedSig && notRevoked;
    }

    public boolean verifyClaimOntIdCredible(String[] credibleOntIds, VerifiableCredential claim) {
        // insure proof is generated by issuer
        if (!claim.proof.verificationMethod.startsWith(claim.issuer)) {
            return false;
        }
        for (String ontId :
                credibleOntIds) {
            // check ontID equals
            if (claim.proof.verificationMethod.startsWith(ontId)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyClaimNotExpired(VerifiableCredential claim) throws Exception {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date expiration = formatter.parse(claim.expirationDate);
        return expiration.after(new Date());
    }

    public boolean verifyClaimSignature(VerifiableCredential claim) throws Exception {
        return verifyPubKeyIdSignature(claim.issuer, claim.proof.verificationMethod, claim.genNeedSignData(),
                claim.proof.parseSignature());
    }

    public boolean verifyClaimNotRevoked(VerifiableCredential claim) throws Exception {
        if (claim.credentialStatus.type != CredentialStatus.CredentialStatusType.ClaimContract) {
            return false;
        }
        String defaultContractAddr = this.claimRecord.getContractAddress();
        this.claimRecord.setContractAddress(claim.credentialStatus.id);
        boolean notRevoked = CLAIM_COMMITTED.equals(this.claimRecord.sendGetStatus2(claim.id));
        this.claimRecord.setContractAddress(defaultContractAddr);
        return notRevoked;
    }

    public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                                     OntIdSigner[] otherSigners, String holderOntId) throws Exception {
        VerifiablePresentation presentation = new VerifiablePresentation();
        String[] wholeContext = new String[2 + context.length];
        wholeContext[0] = CLAIM_DEFAULT_CONTEXT1;
        wholeContext[1] = CLAIM_DEFAULT_CONTEXT2;
        int index = 2;
        for (String c : context) {
            wholeContext[index] = c;
            index++;
        }
        presentation.context = wholeContext;
        String[] wholeType = new String[type.length + 1];
        wholeType[0] = PRESENTATION_DEFAULT_TYPE;
        index = 1;
        for (String c : type) {
            wholeType[index] = c;
            index++;
        }
        presentation.type = wholeType;
        presentation.verifiableCredential = claims;
        presentation.holder = holderOntId;
        Proof[] proofs = new Proof[otherSigners.length + 1];
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTimeStamp = formatter.format(current);
        Proof proof = new Proof(signer.pubKeyId, currentTimeStamp,
                Proof.ProofType.EcdsaSecp256r1Signature2019, Proof.ProofPurpose.assertionMethod);
        byte[] needSignData = presentation.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        proofs[0] = proof;
        index = 0;
        for (OntIdSigner signer :
                otherSigners) {
            Proof p = new Proof(signer.pubKeyId, currentTimeStamp,
                    Proof.ProofType.EcdsaSecp256r1Signature2019, Proof.ProofPurpose.assertionMethod);
            p.fillSignature(signer.signer, needSignData);
            index++;
            proofs[index] = p;
        }
        presentation.proof = proofs;
        String serializedStr = JSON.toJSONString(presentation);
        presentation.id = Helper.toHexString(Digest.hash256(serializedStr.getBytes()));
        return presentation;
    }

    public boolean verifyPresentationProof(VerifiablePresentation presentation, int proofIndex) throws Exception {
        if (proofIndex >= presentation.proof.length) {
            throw new SDKException(String.format("proof index %d out of bound %d",
                    proofIndex, presentation.proof.length));
        }
        // verify presentation proof
        byte[] needSignData = presentation.genNeedSignData();
        Proof proof = presentation.proof[proofIndex];
        String[] keyInfo = proof.verificationMethod.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid pubKeyId %s", proof.verificationMethod));
        }
        String ontId = keyInfo[0];
        return verifyPubKeyIdSignature(ontId, proof.verificationMethod, needSignData, proof.parseSignature());
    }

    // issuer revoke claim
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeClaim(VerifiableCredential claim, Account payer,
                              long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        if (claim.credentialStatus.type != CredentialStatus.CredentialStatusType.ClaimContract) {
            throw new SDKException(String.format("not support claim type %s", claim.credentialStatus.type));
        }
        String defaultContractAddr = this.claimRecord.getContractAddress();
        this.claimRecord.setContractAddress(claim.credentialStatus.id);
        Transaction tx = this.claimRecord.makeRevoke2(signer.ontId, claim.id, claim.proof.parsePubKeyIndex(),
                payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        this.claimRecord.setContractAddress(defaultContractAddr);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }


    // owner revoke claim
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeClaim(String claimId, Account payer, long gasLimit, long gasPrice,
                              OntSdk sdk) throws Exception {
        String[] keyInfo = signer.pubKeyId.split("#keys-");
        if (keyInfo.length != 2) {
            throw new SDKException(String.format("invalid pubKeyId %s", signer.pubKeyId));
        }
        Transaction tx = this.claimRecord.makeRevoke2(signer.ontId, claimId, Integer.parseInt(keyInfo[1]),
                payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }

    private boolean verifyPubKeyIdSignature(String ontId, String pubKeyId, byte[] needSignData,
                                            byte[] signature) throws Exception {
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

    private boolean verifyOntIdSignature(String ontId, byte[] needSignData, byte[] signature) throws Exception {
        String allPubKeysJson = ontIdContract.sendGetPublicKeys(ontId);
        ArrayList<OntIdPubKey> allPubKeys = new ArrayList<>(JSON.parseArray(allPubKeysJson, OntIdPubKey.class));
        for (OntIdPubKey pubKey :
                allPubKeys) {
            Account account = new Account(false, Helper.hexToBytes(pubKey.publicKeyHex));
            if (account.verifySignature(needSignData, signature)) {
                return true;
            }
        }
        return false;
    }
}
