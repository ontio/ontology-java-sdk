package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.ontid.jwt.JWTCredential;
import com.github.ontio.ontid.jwt.JWTHeader;
import com.github.ontio.ontid.jwt.JWTPayload;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.smartcontract.nativevm.OntId;
import com.github.ontio.smartcontract.neovm.CredentialRecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class OntId2 {
    public static final String CRED_DEFAULT_CONTEXT1 = "https://www.w3.org/2018/credentials/v1";
    public static final String CRED_DEFAULT_CONTEXT2 = "https://ontid.ont.io/credentials/v1";

    public static final String CRED_DEFAULT_TYPE = "VerifiableCredential";

    public static final String PRESENTATION_DEFAULT_TYPE = "VerifiablePresentation";

    public static final String CRED_COMMITTED = "01";
    public static final String CRED_REVOKED = "00";
    public static final String CRED_NOT_EXIST = "02";

    private OntIdSigner signer;
    private CredentialRecord credRecord;
    private OntId ontIdContract;

    public OntIdSigner getSigner() {
        return signer;
    }

    public void setOntIdAndSigner(OntIdSigner signer) {
        this.signer = signer;
    }


    // which index that signer public key corresponding in ontId keys set
    private int signerPubKeyIndex;

    public CredentialRecord getCredRecord() {
        return credRecord;
    }

    public void setCredRecord(CredentialRecord credRecord) {
        this.credRecord = credRecord;
    }

    public OntId getOntIdContract() {
        return ontIdContract;
    }

    public void setOntIdContract(OntId ontIdContract) {
        this.ontIdContract = ontIdContract;
    }

    public OntId2(String ontId, Account signer, CredentialRecord credRecord, OntId ontIdContract) throws Exception {
        this.credRecord = credRecord;
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

    public VerifiableCredential createCred(String[] context, String[] type, Object issuer,
                                           Object credentialSubject, Date expiration,
                                           CredentialStatusType credentialStatusType,
                                           ProofPurpose proofPurpose) throws Exception {
        if (credentialStatusType == CredentialStatusType.RevocationList) {
            throw new SDKException("unsupported CredentialStatusType");
        }
        VerifiableCredential credential = genCredentialWithoutSig(context, type, issuer, credentialSubject,
                expiration, credentialStatusType, proofPurpose);
        // generate proof
        byte[] needSignData = credential.genNeedSignData();
        credential.proof.fillHexSignature(signer.signer, needSignData);
        return credential;
    }

    public String createJWTCred(String[] context, String[] type, Object issuer, Object credentialSubject,
                                Date expiration, CredentialStatusType statusType, ProofPurpose purpose)
            throws Exception {
        if (statusType == CredentialStatusType.RevocationList) {
            throw new SDKException("unsupported CredentialStatusType");
        }
        JWTHeader header = new JWTHeader(signer.pubKey.type.getAlg(), this.signer.pubKey.id);
        JWTPayload payload = new JWTPayload(genCredentialWithoutSig(context, type, issuer, credentialSubject,
                expiration, statusType, purpose)); // use default proof purpose
        JWTCredential jwtCred = new JWTCredential(header, payload, signer.signer);
        return jwtCred.toString();
    }

    private VerifiableCredential genCredentialWithoutSig(String[] context, String[] type, Object issuer,
                                                         Object credentialSubject, Date expiration,
                                                         CredentialStatusType statusType, ProofPurpose purpose)
            throws Exception {
        VerifiableCredential credential = new VerifiableCredential();
        ArrayList<String> wholeContext = new ArrayList<>();
        wholeContext.add(CRED_DEFAULT_CONTEXT1);
        wholeContext.add(CRED_DEFAULT_CONTEXT2);
        if (context != null) {
            wholeContext.addAll(Arrays.asList(context));
        }
        credential.context = new String[]{};
        credential.context = wholeContext.toArray(credential.context);
        ArrayList<String> wholeType = new ArrayList<>();
        wholeType.add(CRED_DEFAULT_TYPE);
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
        credential.credentialStatus = new CredentialStatus(credRecord.getContractAddress(), statusType);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date current = new Date();
        credential.issuanceDate = formatter.format(current);
        if (expiration != null) {
            // check expiration
            if (expiration.before(current)) {
                throw new SDKException("cred expired");
            }
            credential.expirationDate = formatter.format(expiration);
        }
        credential.proof = new Proof(signer.pubKey.id, credential.issuanceDate, signer.pubKey.type, purpose);
        return credential;
    }

    // self ontId is issuer
    // fetch index from cred.proof
    // use self signer and payer to sign tx
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String commitCred(VerifiableCredential cred, String ownerOntId, Account payer,
                             long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        Transaction tx = credRecord.makeCommit2(signer.ontId, ownerOntId, cred.id,
                Util.getIndexFromPubKeyURI(cred.proof.verificationMethod), payer.getAddressU160().toBase58(),
                gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }


    public String commitCredById(String credId, String ownerOntId, Account payer,
                                 long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        Transaction tx = credRecord.makeCommit2(signer.ontId, ownerOntId, credId,
                Util.getIndexFromPubKeyURI(signer.pubKey.id), payer.getAddressU160().toBase58(),
                gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }

    // @param cred: jwt format cred
    public String commitCred(String cred, String ownerOntId, Account payer,
                             long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        Transaction tx = credRecord.makeCommit2(signer.ontId, ownerOntId, jwtCred.payload.jti,
                Util.getIndexFromPubKeyURI(jwtCred.header.kid), payer.getAddressU160().toBase58(),
                gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        boolean b = sdk.getConnect().sendRawTransaction(tx.toHexString());
        if (b) {
            return tx.hash().toString();
        }
        return "";
    }

    public boolean verifyCred(String[] credibleOntIds, VerifiableCredential cred) throws Exception {
        if (!verifyCredOntIdCredible(credibleOntIds, cred)) {
            return false;
        }
        if (!verifyCredDate(cred)) {
            return false;
        }
        if (cred.proof == null) {
            return false;
        }
        if (!verifyCredSignature(cred)) {
            return false;
        }
        return verifyCredNotRevoked(cred);
    }

    public boolean verifyJWTCred(String[] credibleOntIds, String cred) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        if (jwtCred.payload.vc == null) {
            throw new SDKException("cred vc doesn't exist");
        }
        if (!verifyJWTCredOntIdCredible(credibleOntIds, jwtCred)) {
            return false;
        }
        if (!verifyJWTCredDate(jwtCred)) {
            return false;
        }
        if (!verifyJWTCredSignature(jwtCred)) {
            return false;
        }
        return verifyJWTCredNotRevoked(jwtCred);
    }

    public boolean verifyCredOntIdCredible(String[] credibleOntIds, VerifiableCredential cred) {
        // insure proof is generated by issuer
        if (!cred.proof.verificationMethod.startsWith(cred.fetchIssuerOntId())) {
            return false;
        }
        for (String ontId :
                credibleOntIds) {
            // check ontID equals
            if (cred.proof.verificationMethod.startsWith(ontId)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyJWTCredOntIdCredible(String[] credibleOntIds, String cred) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        return verifyJWTCredOntIdCredible(credibleOntIds, jwtCred);
    }

    private boolean verifyJWTCredOntIdCredible(String[] credibleOntIds, JWTCredential jwtCred) {
        for (String ontId :
                credibleOntIds) {
            // check ontID equals
            if (jwtCred.payload.iss.startsWith(ontId)) {
                return true;
            }
        }
        return false;
    }

    public boolean verifyCredDate(VerifiableCredential cred) throws Exception {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date current = new Date();
        if (cred.expirationDate != null && !cred.expirationDate.isEmpty()) {
            Date expiration = formatter.parse(cred.expirationDate);
            if (expiration.before(current)) {
                return false;
            }
        }
        if (cred.issuanceDate != null && !cred.issuanceDate.isEmpty()) {
            Date issuanceDate = formatter.parse(cred.issuanceDate);
            return !issuanceDate.after(current);
        }
        return true;
    }

    public boolean verifyJWTCredDate(String cred) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        if (jwtCred.payload.exp == 0) {
            return true;
        }
        return verifyJWTCredDate(jwtCred);
    }

    private boolean verifyJWTCredDate(JWTCredential jwtCred) {
        long current = System.currentTimeMillis() / 1000;
        if (jwtCred.payload.exp > 0 && current > jwtCred.payload.exp) {
            return false;
        }
        if (jwtCred.payload.nbf > 0 && current < jwtCred.payload.nbf) {
            return false;
        }
        if (jwtCred.payload.iat <= 0) {
            return true;
        }
        return current >= jwtCred.payload.iat;
    }

    public boolean verifyCredSignature(VerifiableCredential cred) throws Exception {
        return verifyPubKeyIdSignature(cred.fetchIssuerOntId(), cred.proof.verificationMethod,
                cred.genNeedSignData(), cred.proof.parseHexSignature());
    }

    public boolean verifyJWTCredSignature(String cred) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        return verifyJWTCredSignature(jwtCred);
    }

    private boolean verifyJWTCredSignature(JWTCredential jwtCred) throws Exception {
        byte[] needSignData = jwtCred.genNeedSignData();
        byte[] signature = jwtCred.parseSignature();
        return verifyPubKeyIdSignature(jwtCred.payload.iss, jwtCred.header.kid, needSignData, signature);
    }

    public boolean verifyCredNotRevoked(VerifiableCredential cred) throws Exception {
        switch (cred.credentialStatus.type) {
            case AttestContract:
                String defaultContractAddr = this.credRecord.getContractAddress();
                this.credRecord.setContractAddress(cred.credentialStatus.id);
                boolean notRevoked = CRED_COMMITTED.equals(this.credRecord.sendGetStatus2(cred.id));
                this.credRecord.setContractAddress(defaultContractAddr);
                return notRevoked;
            case RevocationList: // TODO: verify this
                return false;
            default:
                return false;
        }
    }

    public boolean verifyJWTCredNotRevoked(String cred) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        if (jwtCred.payload.vc == null) {
            throw new SDKException("cred vc doesn't exist");
        }
        return verifyJWTCredNotRevoked(jwtCred);
    }

    private boolean verifyJWTCredNotRevoked(JWTCredential jwtCred) throws Exception {
        switch (jwtCred.payload.vc.credentialStatus.type) {
            case AttestContract:
                String defaultContractAddr = this.credRecord.getContractAddress();
                this.credRecord.setContractAddress(jwtCred.payload.vc.credentialStatus.id);
                boolean notRevoked = CRED_COMMITTED.equals(this.credRecord.sendGetStatus2(jwtCred.payload.jti));
                this.credRecord.setContractAddress(defaultContractAddr);
                return notRevoked;
            case RevocationList:
                return false;
            default:
                return false;
        }
    }

    public VerifiablePresentation createPresentation(VerifiableCredential[] creds, String[] context,
                                                     String[] type, List<String> challenge,
                                                     List<Object> domain, Object holder,
                                                     OntIdSigner[] otherSigners, ProofPurpose proofPurpose)
            throws Exception {
        VerifiablePresentation presentation = genPresentationWithoutProof(creds, context, type, holder);
        Date current = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String currentTimeStamp = formatter.format(current);
        ArrayList<Proof> proofs = new ArrayList<>();
        ArrayList<OntIdSigner> signers = new ArrayList<>();
        signers.add(signer);
        if (otherSigners != null) {
            signers.addAll(Arrays.asList(otherSigners));
        }
        for (int i = 0; i < signers.size(); i++) {
            OntIdSigner signer = signers.get(i);
            Proof p = new Proof(signer.pubKey.id, currentTimeStamp, signer.pubKey.type, proofPurpose,
                    challenge.get(i), domain.get(i));
            proofs.add(p);
        }
        presentation.proof = new Proof[]{};
        presentation.proof = proofs.toArray(presentation.proof);
        for (int i = 0; i < proofs.size(); i++) {
            Proof p = proofs.get(i);
            byte[] needSignData = presentation.genNeedSignData(p);
            p.fillHexSignature(signers.get(i).signer, needSignData);
        }
        presentation.proof = proofs.toArray(presentation.proof);
        return presentation;
    }

    // creds: jwt cred array
    public String createJWTPresentation(String[] creds, String[] context, String[] type, Object holder,
                                        String challenge, Object domain, ProofPurpose purpose)
            throws Exception {
        JWTHeader header = new JWTHeader(signer.pubKey.type.getAlg(), this.signer.pubKey.id);
        VerifiableCredential[] credentials = new VerifiableCredential[creds.length];
        // check creds
        for (int i = 0; i < creds.length; i++) {
            JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(creds[i]);
            credentials[i] = VerifiableCredential.deserializeFromJWT(jwtCred);
        }
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = formatter.format(new Date());
        Proof proof = new Proof(signer.pubKey.id, created, signer.pubKey.type, purpose, challenge, domain);
        JWTPayload payload = new JWTPayload(genPresentationWithoutProof(credentials, context, type, holder), proof);
        JWTCredential jwtCred = new JWTCredential(header, payload, signer.signer);
        return jwtCred.toString();
    }

    private VerifiablePresentation genPresentationWithoutProof(VerifiableCredential[] creds, String[] context,
                                                               String[] type, Object holder) {
        VerifiablePresentation presentation = new VerifiablePresentation();
        ArrayList<String> wholeContext = new ArrayList<>();
        wholeContext.add(CRED_DEFAULT_CONTEXT1);
        wholeContext.add(CRED_DEFAULT_CONTEXT2);
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
        presentation.verifiableCredential = creds;
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
        byte[] needSignData = presentation.genNeedSignData(proof);
        return verifyPubKeyIdSignature(proof.verificationMethod, needSignData, proof.parseHexSignature());
    }

    public boolean verifyJWTPresentation(String[] credibleOntIds, String presentation) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(presentation);
        if (jwtCred.payload.vp == null) {
            throw new SDKException("invalid presentation");
        }
        // check each payload.vc( VerifiableCredential )
        for (String vc : jwtCred.payload.vp.verifiableCredential) {
            if (!verifyJWTCred(credibleOntIds, vc)) {
                return false;
            }
        }
        // check jws
        byte[] needSignData = jwtCred.genNeedSignData();
        byte[] signature = jwtCred.parseSignature();
        return verifyPubKeyIdSignature(jwtCred.header.kid, needSignData, signature);
    }

    // issuer revoke cred
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeCred(VerifiableCredential cred, Account payer,
                             long gasLimit, long gasPrice, OntSdk sdk) throws Exception {
        if (cred.credentialStatus.type != CredentialStatusType.AttestContract) {
            throw new SDKException(String.format("not support cred type %s", cred.credentialStatus.type));
        }
        String defaultContractAddr = this.credRecord.getContractAddress();
        this.credRecord.setContractAddress(cred.credentialStatus.id);
        Transaction tx = this.credRecord.makeRevoke2(signer.ontId, cred.id,
                Util.getIndexFromPubKeyURI(cred.proof.verificationMethod),
                payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        this.credRecord.setContractAddress(defaultContractAddr);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }


    // owner revoke cred by cred id
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeCredById(String credId, Account payer, long gasLimit, long gasPrice,
                                 OntSdk sdk) throws Exception {
        Transaction tx = this.credRecord.makeRevoke2(signer.ontId, credId,
                Util.getIndexFromPubKeyURI(signer.pubKey.id), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }

    // owner revoke JWT cred
    // When multi-threaded calls are made, they need to be locked externally.
    // The function itself does not provide asynchronous locks.
    public String revokeJWTCred(String cred, Account payer, long gasLimit, long gasPrice,
                                OntSdk sdk) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        Transaction tx = this.credRecord.makeRevoke2(signer.ontId, jwtCred.payload.jti,
                Util.getIndexFromPubKeyURI(signer.pubKey.id), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }

    public String removeCredById(String credId, Account payer, long gasLimit, long gasPrice, OntSdk sdk)
            throws Exception {
        Transaction tx = this.credRecord.makeRemove2(signer.ontId, credId,
                Util.getIndexFromPubKeyURI(signer.pubKey.id), payer.getAddressU160().toBase58(), gasLimit, gasPrice);
        sdk.addSign(tx, signer.signer);
        sdk.addSign(tx, payer);
        if (sdk.getConnect().sendRawTransaction(tx.toHexString())) {
            return tx.hash().toHexString();
        }
        return "";
    }

    public String removeJWTCred(String cred, Account payer, long gasLimit, long gasPrice,
                                OntSdk sdk) throws Exception {
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(cred);
        Transaction tx = this.credRecord.makeRemove2(signer.ontId, jwtCred.payload.jti,
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
