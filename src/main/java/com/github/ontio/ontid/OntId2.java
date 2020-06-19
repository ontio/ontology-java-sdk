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
import java.util.Arrays;
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
            this.signer = new OntIdSigner(ontId, pubKey, signer);
        }
    }

    public void updateOntIdAndSigner(String ontId, Account signer) throws Exception {
        OntIdPubKey pubKey = querySignerPubKey(ontId, Helper.toHexString(signer.serializePublicKey()));
        this.signer = new OntIdSigner(ontId, pubKey, signer);
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

    public SignRequest genSignReq(String claim, ProofPurpose proofPurpose,
                                  boolean hasSignature) throws Exception {
        if (hasSignature) {
            Date current = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Proof proof = new Proof(signer.pubKey.id, formatter.format(current), signer.pubKey.type, proofPurpose);
            proof.fillSignature(signer.signer, Digest.sha256(claim.getBytes()));
            return new SignRequest(claim, signer.ontId, proof);
        } else {
            return new SignRequest(claim, signer.ontId, null);
        }
    }

    public boolean verifySignReq(SignRequest req) throws Exception {
        return verifyOntIdSignature(req.ontId, Digest.sha256(req.claim.getBytes()), req.signature.parseSignature());
    }

    public VerifiableCredential createClaim(String[] context, String[] type, Object issuer,
                                            Object credentialSubject, Date expiration,
                                            CredentialStatusType credentialStatusType,
                                            ProofPurpose proofPurpose) throws Exception {
        VerifiableCredential credential = genCredentialWithoutProof(context, type, issuer, credentialSubject,
                expiration, credentialStatusType);
        // generate proof
        Proof proof = new Proof(signer.pubKey.id, credential.issuanceDate, signer.pubKey.type, proofPurpose);
        byte[] needSignData = credential.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        credential.proof = proof;
        return credential;
    }

    public String createJWTClaim(String[] context, String[] type, Object issuer, Object credentialSubject,
                                 Date expiration, CredentialStatusType statusType,
                                 PubKeyType pubKeyType) throws Exception {
        JWTHeader header = new JWTHeader(pubKeyType.getAlg(), this.signer.pubKey.id);
        JWTPayload payload = new JWTPayload(genCredentialWithoutProof(context, type, issuer, credentialSubject,
                expiration, statusType));
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

//    public String createJWTClaim(String[] context, String[] type, Object credentialSubject,
//                                 Date expiration, String audience,
//                                 CredentialStatusType statusType,
//                                 ProofType proofType) throws Exception {
//        JWTHeader header = new JWTHeader(proofType.getAlg(), this.signer.pubKeyId);
//        JWTPayload payload = new JWTPayload(genCredentialWithoutProof(context, type, credentialSubject,
//                expiration, statusType), audience);
//        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
//        return claim.toString();
//    }

    private VerifiableCredential genCredentialWithoutProof(String[] context, String[] type, Object issuer,
                                                           Object credentialSubject, Date expiration,
                                                           CredentialStatusType statusType)
            throws Exception {
        VerifiableCredential credential = new VerifiableCredential();
        ArrayList<String> wholeContext = new ArrayList<>();
        wholeContext.add(CLAIM_DEFAULT_CONTEXT1);
        wholeContext.add(CLAIM_DEFAULT_CONTEXT2);
        if (context != null) {
            wholeContext.addAll(Arrays.asList(context));
        }
        credential.context = new String[]{};
        credential.context = wholeContext.toArray(credential.context);
        ArrayList<String> wholeType = new ArrayList<>();
        wholeType.add(CLAIM_DEFAULT_TYPE);
        if (type != null) {
            wholeType.addAll(Arrays.asList(type));
        }
        credential.type = new String[]{};
        credential.type = wholeType.toArray(credential.type);
        credential.credentialSubject = credentialSubject;
        String issuerId = Util.fetchId(issuer);
        if (!signer.ontId.equals(issuerId)) {
            throw new SDKException(String.format("param issuer %s vs self signer %s", issuerId, signer.ontId));
        }
        credential.issuer = issuer;
        credential.credentialStatus = new CredentialStatus(claimRecord.getContractAddress(), statusType);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date current = new Date();
        credential.issuanceDate = formatter.format(current);
        if (expiration != null) {
            // check expiration
            if (expiration.before(current)) {
                throw new SDKException("claim expired");
            }
            credential.expirationDate = formatter.format(expiration);
        }
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

    // @param claim: jwt format claim
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
        if (claim.proof == null) {
            return false;
        }
        if (claim.proof.jws != null) {
            JWTClaim jwtClaim = new JWTClaim(claim);
            if (!verifyJWTClaimSignature(jwtClaim.toString())) {
                return false;
            }
        } else {
            if (!verifyClaimSignature(claim)) {
                return false;
            }
        }
        return verifyClaimNotRevoked(claim);
    }

    public boolean verifyJWTClaim(String[] credibleOntIds, String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        if (jwtClaim.payload.vc == null) {
            throw new SDKException("claim vc doesn't exist");
        }
        if (!verifyJWTClaimOntIdCredible(credibleOntIds, jwtClaim)) {
            return false;
        }
        if (!verifyJWTClaimNotExpired(jwtClaim)) {
            return false;
        }
        if (!verifyJWTClaimSignature(jwtClaim)) {
            return false;
        }
        return verifyJWTClaimNotRevoked(jwtClaim);
    }

    public boolean verifyClaimOntIdCredible(String[] credibleOntIds, VerifiableCredential claim) {
        // insure proof is generated by issuer
        if (!claim.proof.verificationMethod.startsWith(claim.fetchIssuerOntId())) {
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
        return verifyJWTClaimOntIdCredible(credibleOntIds, jwtClaim);
    }

    private boolean verifyJWTClaimOntIdCredible(String[] credibleOntIds, JWTClaim jwtClaim) {
        for (String ontId :
                credibleOntIds) {
            // check ontID equals
            if (jwtClaim.payload.iss.startsWith(ontId)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyClaimNotExpired(VerifiableCredential claim) throws Exception {
        if (claim.expirationDate == null || claim.expirationDate.isEmpty()) {
            return true;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date expiration = formatter.parse(claim.expirationDate);
        return expiration.after(new Date());
    }

    public boolean verifyJWTClaimNotExpired(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        if (jwtClaim.payload.exp == null || jwtClaim.payload.exp.isEmpty()) {
            return true;
        }
        return verifyJWTClaimNotExpired(jwtClaim);
    }

    private boolean verifyJWTClaimNotExpired(JWTClaim jwtClaim) {
        long current = System.currentTimeMillis() / 1000;
        long exp = Long.parseLong(jwtClaim.payload.exp);
        return current < exp;
    }

    public boolean verifyClaimSignature(VerifiableCredential claim) throws Exception {
        return verifyPubKeyIdSignature(claim.fetchIssuerOntId(), claim.proof.verificationMethod,
                claim.genNeedSignData(), claim.proof.parseSignature());
    }

    public boolean verifyJWTClaimSignature(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        return verifyJWTClaimSignature(jwtClaim);
    }

    private boolean verifyJWTClaimSignature(JWTClaim jwtClaim) throws Exception {
        byte[] needSignData = jwtClaim.genNeedSignData();
        byte[] signature = jwtClaim.parseSignature();
        return verifyPubKeyIdSignature(jwtClaim.payload.iss, jwtClaim.header.kid, needSignData, signature);
    }

    public boolean verifyClaimNotRevoked(VerifiableCredential claim) throws Exception {
        switch (claim.credentialStatus.type) {
            case AttestContract:
                String defaultContractAddr = this.claimRecord.getContractAddress();
                this.claimRecord.setContractAddress(claim.credentialStatus.id);
                boolean notRevoked = CLAIM_COMMITTED.equals(this.claimRecord.sendGetStatus2(claim.id));
                this.claimRecord.setContractAddress(defaultContractAddr);
                return notRevoked;
            case RevocationList:
                return false;
            default:
                return false;
        }
    }

    public boolean verifyJWTClaimNotRevoked(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        if (jwtClaim.payload.vc == null) {
            throw new SDKException("claim vc doesn't exist");
        }
        return verifyJWTClaimNotRevoked(jwtClaim);
    }

    private boolean verifyJWTClaimNotRevoked(JWTClaim jwtClaim) throws Exception {
        switch (jwtClaim.payload.vc.credentialStatus.type) {
            case AttestContract:
                String defaultContractAddr = this.claimRecord.getContractAddress();
                this.claimRecord.setContractAddress(jwtClaim.payload.vc.credentialStatus.id);
                boolean notRevoked = CLAIM_COMMITTED.equals(this.claimRecord.sendGetStatus2(jwtClaim.payload.jti));
                this.claimRecord.setContractAddress(defaultContractAddr);
                return notRevoked;
            case RevocationList:
                return false;
            default:
                return false;
        }
    }

    public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                                     Object holderOntId, OntIdSigner[] otherSigners,
                                                     ProofPurpose proofPurpose) throws Exception {
        VerifiablePresentation presentation = genPresentationWithoutProof(claims, context, type, holderOntId);
        Proof[] proofs = new Proof[otherSigners.length + 1];
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTimeStamp = formatter.format(current);
        Proof proof = new Proof(signer.pubKey.id, currentTimeStamp, signer.pubKey.type, proofPurpose);
        byte[] needSignData = presentation.genNeedSignData();
        proof.fillSignature(signer.signer, needSignData);
        proofs[0] = proof;
        int index = 0;
        for (OntIdSigner signer :
                otherSigners) {
            Proof p = new Proof(signer.pubKey.id, currentTimeStamp, signer.pubKey.type, proofPurpose);
            p.fillSignature(signer.signer, needSignData);
            index++;
            proofs[index] = p;
        }
        presentation.proof = proofs;
        return presentation;
    }

    public String createJWTPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                        Object holder, PubKeyType pubKeyType) throws Exception {
        JWTHeader header = new JWTHeader(pubKeyType.getAlg(), this.signer.pubKey.id);
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(claims, context, type, holder));
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    // claims: jwt claim array
    public String createJWTPresentation(String[] claims, String[] context, String[] type, Object holder,
                                        PubKeyType pubKeyType, ProofPurpose purpose) throws Exception {
        JWTHeader header = new JWTHeader(pubKeyType.getAlg(), this.signer.pubKey.id);
        VerifiableCredential[] credentials = new VerifiableCredential[claims.length];
        for (int i = 0; i < claims.length; i++) {
            JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claims[i]);
            credentials[i] = VerifiableCredential.deserializeFromJWT(jwtClaim, purpose);
        }
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(credentials, context, type, holder));
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    public VerifiablePresentation genPresentationWithoutProof(VerifiableCredential[] claims, String[] context,
                                                              String[] type, Object holder) {
        VerifiablePresentation presentation = new VerifiablePresentation();
        ArrayList<String> wholeContext = new ArrayList<>();
        wholeContext.add(CLAIM_DEFAULT_CONTEXT1);
        wholeContext.add(CLAIM_DEFAULT_CONTEXT2);
        if (context != null) {
            wholeContext.addAll(Arrays.asList(context));
        }
        presentation.context = new String[]{};
        presentation.context = wholeContext.toArray(presentation.context);
        ArrayList<String> wholeType = new ArrayList<>();
        wholeType.add(PRESENTATION_DEFAULT_TYPE);
        if (type != null) {
            wholeType.addAll(Arrays.asList(type));
        }
        presentation.type = new String[]{};
        presentation.type = wholeType.toArray(presentation.type);
        presentation.verifiableCredential = claims;
        presentation.holder = holder;
        return presentation;
    }

    public boolean verifyPresentationProof(VerifiablePresentation presentation, int proofIndex) throws Exception {
        if (proofIndex >= presentation.proof.length) {
            throw new SDKException(String.format("proof index %d out of bound %d",
                    proofIndex, presentation.proof.length));
        }
        Proof proof = presentation.proof[proofIndex];
        if (proof.jws != null) {
            JWTClaim jwtPresentation = new JWTClaim(presentation);
            byte[] needSignData = jwtPresentation.genNeedSignData();
            byte[] signature = jwtPresentation.parseSignature();
            return verifyPubKeyIdSignature(jwtPresentation.header.kid, needSignData, signature);
        } else {
            // verify presentation proof
            byte[] needSignData = presentation.genNeedSignData();
            return verifyPubKeyIdSignature(proof.verificationMethod, needSignData, proof.parseSignature());
        }
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
        if (claim.credentialStatus.type != CredentialStatusType.AttestContract) {
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
                Util.getIndexFromPubKeyURI(signer.pubKey.id), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
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
                Util.getIndexFromPubKeyURI(signer.pubKey.id), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
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
