package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
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
import java.util.List;

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

    public SignRequest genSignReq(Object credentialSubject, ProofPurpose proofPurpose,
                                  boolean hasSignature) throws Exception {
        if (hasSignature) {
            Date current = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Proof proof = new Proof(signer.pubKey.id, formatter.format(current), signer.pubKey.type, proofPurpose);
            SignRequest signReq = new SignRequest(credentialSubject, signer.ontId, proof);
            proof.fillHexSignature(signer.signer, signer.hash(signReq.genNeedSignData()));
            return signReq;
        } else {
            return new SignRequest(credentialSubject, signer.ontId, null);
        }
    }

    public boolean verifySignReq(SignRequest req) throws Exception {
        return verifyOntIdSignature(req.ontId, signer.hash(req.genNeedSignData()), req.proof.parseHexSignature());
    }

    public VerifiableCredential createClaim(String[] context, String[] type, Object issuer,
                                            Object credentialSubject, Date expiration,
                                            CredentialStatusType credentialStatusType,
                                            ProofPurpose proofPurpose) throws Exception {
        VerifiableCredential credential = genCredentialWithoutSig(context, type, issuer, credentialSubject,
                expiration, credentialStatusType, proofPurpose);
        // generate proof
        byte[] needSignData = credential.genNeedSignData();
        credential.proof.fillHexSignature(signer.signer, needSignData);
        return credential;
    }

    public String createJWTClaim(String[] context, String[] type, Object issuer, Object credentialSubject,
                                 Date expiration, CredentialStatusType statusType, ProofPurpose purpose)
            throws Exception {
        JWTHeader header = new JWTHeader(signer.pubKey.type.getAlg(), this.signer.pubKey.id);
        JWTPayload payload = new JWTPayload(genCredentialWithoutSig(context, type, issuer, credentialSubject,
                expiration, statusType, purpose)); // use default proof purpose
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    private VerifiableCredential genCredentialWithoutSig(String[] context, String[] type, Object issuer,
                                                         Object credentialSubject, Date expiration,
                                                         CredentialStatusType statusType, ProofPurpose purpose)
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
        credential.proof = new Proof(signer.pubKey.id, credential.issuanceDate, signer.pubKey.type, purpose);
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
        if (!verifyClaimDate(claim)) {
            return false;
        }
        if (claim.proof == null) {
            return false;
        }
        if (!verifyClaimSignature(claim)) {
            return false;
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
        if (!verifyJWTClaimDate(jwtClaim)) {
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

    public boolean verifyClaimDate(VerifiableCredential claim) throws Exception {
        if (claim.expirationDate == null || claim.expirationDate.isEmpty()) {
            return true;
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date expiration = formatter.parse(claim.expirationDate);
        Date issuanceDate = formatter.parse(claim.issuanceDate);
        Date current = new Date();
        return expiration.after(current) && issuanceDate.before(current);
    }

    public boolean verifyJWTClaimDate(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        if (jwtClaim.payload.exp == 0) {
            return true;
        }
        return verifyJWTClaimDate(jwtClaim);
    }

    private boolean verifyJWTClaimDate(JWTClaim jwtClaim) {
        long current = System.currentTimeMillis() / 1000;
        if (current > jwtClaim.payload.exp) {
            return false;
        }
        if (current < jwtClaim.payload.nbf) {
            return false;
        }
        return current >= jwtClaim.payload.iat;
    }

    public boolean verifyClaimSignature(VerifiableCredential claim) throws Exception {
        return verifyPubKeyIdSignature(claim.fetchIssuerOntId(), claim.proof.verificationMethod,
                claim.genNeedSignData(), claim.proof.parseHexSignature());
    }

    public boolean verifyJWTClaimSignature(String claim) throws Exception {
        JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claim);
        return verifyJWTClaimSignature(jwtClaim);
    }

    private boolean verifyJWTClaimSignature(JWTClaim jwtClaim) throws Exception {
        System.out.println(jwtClaim.toString());
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

    public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context,
                                                     String[] type, List<String> challenge,
                                                     List<Object> domain, Object holder,
                                                     OntIdSigner[] otherSigners, ProofPurpose proofPurpose)
            throws Exception {
        VerifiablePresentation presentation = genPresentationWithoutProof(claims, context, type, holder);
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTimeStamp = formatter.format(current);
        ArrayList<Proof> proofs = new ArrayList<>();
        ArrayList<OntIdSigner> signers = new ArrayList<>(Arrays.asList(otherSigners));
        signers.add(0, signer);
        for (int i = 0; i < signers.size(); i++) {
            OntIdSigner signer = signers.get(i);
            Proof p = new Proof(signer.pubKey.id, currentTimeStamp, signer.pubKey.type, proofPurpose,
                    challenge.get(i), domain.get(i));
            proofs.add(p);
        }
        presentation.proof = new Proof[]{};
        presentation.proof = proofs.toArray(presentation.proof);
        byte[] needSignData = presentation.genNeedSignData();
        for (int i = 0; i < proofs.size(); i++) {
            Proof p = proofs.get(i);
            p.fillHexSignature(signers.get(i).signer, needSignData);
        }
        presentation.proof = proofs.toArray(presentation.proof);
        return presentation;
    }

    public String createJWTPresentation(VerifiableCredential[] claims, String[] context, String[] type,
                                        String challenge, Object domain, Object holder, String nonce,
                                        ProofPurpose proofPurpose) throws Exception {
        JWTHeader header = new JWTHeader(signer.pubKey.type.getAlg(), this.signer.pubKey.id);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = formatter.format(new Date());
        Proof proof = new Proof(signer.pubKey.id, created, signer.pubKey.type, proofPurpose, challenge, domain);
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(claims, context, type, holder),
                proof, nonce);
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    // claims: jwt claim array
    public String createJWTPresentation(String[] claims, String[] context, String[] type, Object holder,
                                        String challenge, Object domain, String nonce, ProofPurpose purpose)
            throws Exception {
        JWTHeader header = new JWTHeader(signer.pubKey.type.getAlg(), this.signer.pubKey.id);
        VerifiableCredential[] credentials = new VerifiableCredential[claims.length];
        // check claims
        for (int i = 0; i < claims.length; i++) {
            JWTClaim jwtClaim = JWTClaim.deserializeToJWTClaim(claims[i]);
            credentials[i] = VerifiableCredential.deserializeFromJWT(jwtClaim);
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = formatter.format(new Date());
        Proof proof = new Proof(signer.pubKey.id, created, signer.pubKey.type, purpose, challenge, domain);
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(credentials, context, type, holder),
                proof, nonce);
        JWTClaim claim = new JWTClaim(header, payload, signer.signer);
        return claim.toString();
    }

    private VerifiablePresentation genPresentationWithoutProof(VerifiableCredential[] claims, String[] context,
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
        // verify presentation proof
        byte[] needSignData = presentation.genNeedSignData();
        return verifyPubKeyIdSignature(proof.verificationMethod, needSignData, proof.parseHexSignature());
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
