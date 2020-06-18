package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.JWTClaim;
import com.github.ontio.ontid.jwt.JWTHeader;
import com.github.ontio.ontid.jwt.JWTPayload;
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

    public SignRequest genSignReq(String claim, Proof.ProofType proofType, Proof.ProofPurpose proofPurpose,
                                  boolean hasSignature) throws Exception {
        if (hasSignature) {
            Date current = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Proof proof = new Proof(signer.pubKeyId, formatter.format(current), proofType, proofPurpose);
            proof.fillSignature(signer.signer, Digest.hash256(claim.getBytes()));
            return new SignRequest(claim, signer.ontId, proof);
        } else {
            return new SignRequest(claim, signer.ontId, null);
        }
    }

    public boolean verifySignReq(SignRequest req) throws Exception {
        return verifyOntIdSignature(req.ontId, Digest.hash256(req.claim.getBytes()), req.signature.parseSignature());
    }

    public VerifiableCredential createClaim(String[] context, String[] type,
                                            Object credentialSubject, Date expiration,
                                            CredentialStatus.CredentialStatusType credentialStatusType,
                                            Proof.ProofType proofType,
                                            Proof.ProofPurpose proofPurpose) throws Exception {
        VerifiableCredential credential = genCredentialWithoutProof(context, type, credentialSubject, expiration,
                credentialStatusType);
        // generate proof
        Proof proof = new Proof(signer.pubKeyId, credential.issuanceDate, proofType, proofPurpose);
        byte[] needSignData = credential.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        credential.proof = proof;
        return credential;
    }

    public String createJWTClaim(String[] context, String[] type, Object credentialSubject,
                                 Date expiration, CredentialStatus.CredentialStatusType statusType,
                                 Proof.ProofType proofType) throws Exception {
        JWTHeader header = new JWTHeader(proofType.getAlg(), this.signer.pubKeyId);
        JWTPayload payload = new JWTPayload(genCredentialWithoutProof(context, type, credentialSubject,
                expiration, statusType));
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    public String createJWTClaim(String[] context, String[] type, Object credentialSubject,
                                 Date expiration, String audience,
                                 CredentialStatus.CredentialStatusType statusType,
                                 Proof.ProofType proofType) throws Exception {
        JWTHeader header = new JWTHeader(proofType.getAlg(), this.signer.pubKeyId);
        JWTPayload payload = new JWTPayload(genCredentialWithoutProof(context, type, credentialSubject,
                expiration, statusType), audience);
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    private VerifiableCredential genCredentialWithoutProof(String[] context, String[] type,
                                                           Object credentialSubject, Date expiration,
                                                           CredentialStatus.CredentialStatusType statusType)
            throws Exception {
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
        credential.credentialStatus = new CredentialStatus(claimRecord.getContractAddress(), statusType);
        // check expiration
        Date current = new Date();
        if (expiration.before(current)) {
            throw new SDKException("claim expired");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        credential.expirationDate = formatter.format(expiration);
        credential.issuanceDate = formatter.format(current);
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
                Util.getIndexFromPubKeyURI(claim.proof.verificationMethod), payer.getAddressU160().toBase58(),
                gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }

    public String commitClaim(String claim, String ownerOntId, Account payer,
                              long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        Transaction tx = claimRecord.makeCommit2(signer.ontId, ownerOntId, jwtClaim.payload.jti,
                Util.getIndexFromPubKeyURI(jwtClaim.header.kid), payer.getAddressU160().toBase58(),
                gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }

    public boolean verifyClaim(String[] credibleOntIds, VerifiableCredential claim) throws Exception {
        if (!verifyClaimOntIdCredible(credibleOntIds, claim)) {
            return false;
        }
        if (!verifyClaimNotExpired(claim)) {
            return false;
        }
        if (!verifyClaimSignature(claim)) {
            return false;
        }
        return verifyClaimNotRevoked(claim);
    }

    public boolean verifyJWTClaim(String[] credibleOntIds, String claim) throws Exception {
        if (!verifyJWTClaimOntIdCredible(credibleOntIds, claim)) {
            return false;
        }
        if (!verifyJWTClaimNotExpired(claim)) {
            return false;
        }
        if (!verifyJWTClaimSignature(claim)) {
            return false;
        }
        return verifyJWTClaimNotRevoked(claim);
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

    public boolean verifyJWTClaimOntIdCredible(String[] credibleOntIds, String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        for (String ontId :
                credibleOntIds) {
            // check ontID equals
            if (jwtClaim.header.kid.startsWith(ontId)) {
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

    public boolean verifyJWTClaimNotExpired(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        long current = System.currentTimeMillis() / 1000;
        long exp = Long.parseLong(jwtClaim.payload.exp);
        return current < exp;
    }

    public boolean verifyClaimSignature(VerifiableCredential claim) throws Exception {
        return verifyPubKeyIdSignature(claim.issuer, claim.proof.verificationMethod, claim.genNeedSignData(),
                claim.proof.parseSignature());
    }

    public boolean verifyJWTClaimSignature(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        byte[] needSignData = jwtClaim.genNeedSignData();
        byte[] signature = jwtClaim.parseSignature();
        return verifyPubKeyIdSignature(jwtClaim.header.kid, needSignData, signature);
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

    public boolean verifyJWTClaimNotRevoked(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        String defaultContractAddr = this.claimRecord.getContractAddress();
        if (jwtClaim.payload.vc == null) {
            throw new SDKException("claim vc doesn't exist");
        }
        this.claimRecord.setContractAddress(jwtClaim.payload.vc.credentialStatus.id);
        boolean notRevoked = CLAIM_COMMITTED.equals(this.claimRecord.sendGetStatus2(jwtClaim.payload.jti));
        this.claimRecord.setContractAddress(defaultContractAddr);
        return notRevoked;
    }

    public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                                     String holderOntId, OntIdSigner[] otherSigners,
                                                     Proof.ProofType proofType,
                                                     Proof.ProofPurpose proofPurpose) throws Exception {
        VerifiablePresentation presentation = genPresentationWithoutProof(claims, context, type, holderOntId);
        Proof[] proofs = new Proof[otherSigners.length + 1];
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTimeStamp = formatter.format(current);
        Proof proof = new Proof(signer.pubKeyId, currentTimeStamp, proofType, proofPurpose);
        byte[] needSignData = presentation.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        proofs[0] = proof;
        int index = 0;
        for (OntIdSigner signer :
                otherSigners) {
            Proof p = new Proof(signer.pubKeyId, currentTimeStamp, proofType, proofPurpose);
            p.fillSignature(signer.signer, needSignData);
            index++;
            proofs[index] = p;
        }
        presentation.proof = proofs;
        return presentation;
    }

    public String createJWTPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                        String holderOntId, Proof.ProofType proofType) throws Exception {
        JWTHeader header = new JWTHeader(proofType.getAlg(), this.signer.pubKeyId);
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(claims, context, type, holderOntId));
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    public String createJWTPresentation(String[] claims, String[] context, String[] type, String holderOntId,
                                        Proof.ProofType proofType, Proof.ProofPurpose purpose) throws Exception {
        JWTHeader header = new JWTHeader(proofType.getAlg(), this.signer.pubKeyId);
        VerifiableCredential[] credentials = new VerifiableCredential[claims.length];
        for (int i = 0; i < claims.length; i++) {
            credentials[i] = VerifiableCredential.deserializeFromJWT(claims[i], purpose);
        }
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(credentials, context, type, holderOntId));
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    public VerifiablePresentation genPresentationWithoutProof(VerifiableCredential[] claims, String[] context,
                                                              String[] type, String holderOntId) {
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
        return verifyPubKeyIdSignature(proof.verificationMethod, needSignData, proof.parseSignature());
    }

    public boolean verifyJWTPresentation(String[] credibleOntIds, String presentation) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(presentation);
        if (jwtClaim.payload.vp == null) {
            throw new SDKException("invalid presentation");
        }
        // check each payload.vc( VerifiableCredential )
        for (String vc : jwtClaim.payload.vp.verifiableCredential) {
            if (!verifyJWTClaim(credibleOntIds, vc)) {
                return false;
            }
        }
        // check jws
        byte[] needSignData = jwtClaim.genNeedSignData();
        byte[] signature = jwtClaim.parseSignature();
        return verifyPubKeyIdSignature(jwtClaim.header.kid, needSignData, signature);
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
        Transaction tx = this.claimRecord.makeRevoke2(signer.ontId, claim.id,
                Util.getIndexFromPubKeyURI(claim.proof.verificationMethod),
                payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        this.claimRecord.setContractAddress(defaultContractAddr);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }


    // owner revoke claim by claim id
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeClaimById(String claimId, Account payer, long gasLimit, long gasPrice,
                                  OntSdk sdk) throws Exception {
        Transaction tx = this.claimRecord.makeRevoke2(signer.ontId, claimId,
                Util.getIndexFromPubKeyURI(signer.pubKeyId), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }

    // owner revoke JWT claim
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeJWTClaim(String claim, Account payer, long gasLimit, long gasPrice,
                                 OntSdk sdk) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        Transaction tx = this.claimRecord.makeRevoke2(signer.ontId, jwtClaim.payload.jti,
                Util.getIndexFromPubKeyURI(signer.pubKeyId), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }

    private boolean verifyPubKeyIdSignature(String ontId, String pubKeyId, byte[] needSignData,
                                            byte[] signature) throws Exception {
        if (!pubKeyId.startsWith(ontId)) {
            return false;
        }
        return verifyPubKeyIdSignature(pubKeyId, needSignData, signature);
    }

    private boolean verifyPubKeyIdSignature(String pubKeyId, byte[] needSignData,
                                            byte[] signature) throws Exception {
        String ontId = Util.getOntIdFromPubKeyURI(pubKeyId);
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
