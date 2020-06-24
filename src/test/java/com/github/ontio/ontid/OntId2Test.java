package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.sdk.wallet.Identity;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;

public class OntId2Test extends TestCase {
    static long gasLimit = 2000000;
    static long gasPrice = 500;
    static String password = "passwordtest";
    static Account payer, issuerSigner, ownerSigner;
    static Identity issuerIdentity, ownerIdentity;
    static OntId2 issuer, owner, verifier;
    static OntSdk ontSdk;

    static {
        try {
            String ip = "http://polaris1.ont.io";
            String restUrl = ip + ":" + "20334";
            String rpcUrl = ip + ":" + "20336";

            ontSdk = OntSdk.getInstance();
            ontSdk.setRpc(rpcUrl);
            ontSdk.setRestful(restUrl);
            ontSdk.setDefaultConnect(ontSdk.getRestful());
            ontSdk.openWalletFile("wallet.json");
            // set credential contract address
            ontSdk.neovm().credentialRecord().setContractAddress("52df370680de17bc5d4262c446f102a0ee0d6312");
            payer = ontSdk.getWalletMgr().getAccount("AUNB7xQuBVg8hnRfVz9pyAuZQUqPBiDxDF", password);
            issuerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity(
                    "did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd");
            issuerSigner = ontSdk.getWalletMgr().getAccount(issuerIdentity.ontid, password,
                    issuerIdentity.controls.get(0).getSalt());
            ownerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity(
                    "did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv");
            ownerSigner = ontSdk.getWalletMgr().getAccount(ownerIdentity.ontid, password,
                    ownerIdentity.controls.get(0).getSalt());
            issuer = new OntId2(issuerIdentity.ontid, issuerSigner,
                    ontSdk.neovm().credentialRecord(), ontSdk.nativevm().ontId());
            owner = new OntId2(ownerIdentity.ontid, ownerSigner,
                    ontSdk.neovm().credentialRecord(), ontSdk.nativevm().ontId());
            // verifier may not own ontId and signer
            verifier = new OntId2("", null,
                    ontSdk.neovm().credentialRecord(), ontSdk.nativevm().ontId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testGenSignReq() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            SignRequest signReq = issuer.genSignReq(subject, ProofPurpose.assertionMethod, true);
            assertNotNull(signReq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifySignReq() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            SignRequest signReq = owner.genSignReq(subject, ProofPurpose.assertionMethod, true);
            assertTrue(issuer.verifySignReq(signReq));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCreateCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            assertNotNull(credential);
            assertNotNull(credential.proof);
            assertNotNull(credential.proof.hex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCreateJWTCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            assertNotNull(jwtCred);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCommitCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String txHash = issuer.commitCred(credential, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            assertNotNull(txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCommitJWTCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String txHash = owner.commitCred(jwtCred, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            assertNotNull(txHash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            issuer.commitCred(credential, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            boolean verified = verifier.verifyCred(credibleOntIds, credential);
            assertTrue(verified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyJWTCred() {
        try {
            CredentialSubject subject = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            issuer.commitCred(jwtCred, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            boolean verified = verifier.verifyJWTCred(credibleOntIds, jwtCred);
            assertTrue(verified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testCreatePresentation() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(issuerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential1 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            VerifiableCredential credential2 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            ArrayList<String> challenge = new ArrayList<>();
            challenge.add("d1b23d3...3d23d32d2");
            ArrayList<Object> domain = new ArrayList<>();
            domain.add(new String[]{"https://example.com"});
            VerifiablePresentation presentation = owner.createPresentation(new VerifiableCredential[]{credential1,
                            credential2}, null, null, challenge, domain, ownerIdentity.ontid,
                    null, ProofPurpose.assertionMethod);
            assertNotNull(presentation);
            assertNotNull(presentation.proof);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testJWTPresentation() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(ownerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred1 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String jwtCred2 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            String challenge = "d1b23d3...3d23d32d2";
            String[] domain = new String[]{"https://example.com"};
            String jwtPresentation = issuer.createJWTPresentation(new String[]{jwtCred1, jwtCred2}, null,
                    null, ownerIdentity.ontid, challenge, domain, ProofPurpose.assertionMethod);
            assertNotNull(jwtPresentation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyPresentationProof() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(issuerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            VerifiableCredential credential1 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(credential1, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            VerifiableCredential credential2 = issuer.createCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(credential2, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            ArrayList<String> challenge = new ArrayList<>();
            challenge.add("d1b23d3...3d23d32d2");
            ArrayList<Object> domain = new ArrayList<>();
            domain.add(new String[]{"https://example.com"});
            VerifiablePresentation presentation = owner.createPresentation(new VerifiableCredential[]{credential1,
                            credential2}, null, null, challenge, domain, ownerIdentity.ontid,
                    null, ProofPurpose.assertionMethod);
            boolean presentationVerified = verifier.verifyPresentationProof(presentation, 0);
            assertTrue(presentationVerified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testVerifyJWTPresentation() {
        try {
            CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
            CredentialSubject subject2 = new CredentialSubject(ownerIdentity.ontid, "iii", "ddd");
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            String jwtCred1 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(jwtCred1, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            String jwtCred2 = issuer.createJWTCred(null, null, issuerIdentity.ontid,
                    subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
            issuer.commitCred(jwtCred2, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
            Thread.sleep(6000);
            String challenge = "d1b23d3...3d23d32d2";
            String[] domain = new String[]{"https://example.com"};
            String jwtPresentation = issuer.createJWTPresentation(new String[]{jwtCred1, jwtCred2}, null,
                    null, ownerIdentity.ontid, challenge, domain, ProofPurpose.assertionMethod);
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            boolean presentationVerified = verifier.verifyJWTPresentation(credibleOntIds, jwtPresentation);
            assertTrue(presentationVerified);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testJSONLDPresentation() throws Exception {
        CredentialSubject subject1 = new CredentialSubject(ownerIdentity.ontid, "nnn", "sss");
        CredentialSubject subject2 = new CredentialSubject(issuerIdentity.ontid, "iii", "ddd");
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        VerifiableCredential credential1 = issuer.createCred(null, null, issuerIdentity.ontid,
                subject1, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
        issuer.commitCred(credential1, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
        VerifiableCredential credential2 = issuer.createCred(null, null, issuerIdentity.ontid,
                subject2, expiration, CredentialStatusType.AttestContract, ProofPurpose.assertionMethod);
        issuer.commitCred(credential2, ownerIdentity.ontid, payer, gasLimit, gasPrice, ontSdk);
        Thread.sleep(6000);
        ArrayList<String> challenge = new ArrayList<>();
        challenge.add("d1b23d3...3d23d32d2");
        ArrayList<Object> domain = new ArrayList<>();
        domain.add(new String[]{"https://example.com"});
        VerifiablePresentation presentation = owner.createPresentation(new VerifiableCredential[]{credential1,
                        credential2}, null, null, challenge, domain, ownerIdentity.ontid,
                null, ProofPurpose.assertionMethod);
        String jsonPresentation = JSON.toJSONString(presentation);
        presentation = JSON.parseObject(jsonPresentation, VerifiablePresentation.class);
        String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
        for (VerifiableCredential c : presentation.verifiableCredential) {
            boolean v = verifier.verifyCred(credibleOntIds, c);
            assertTrue(v);
        }
        // verify each proof
        for (int i = 0; i < presentation.proof.length; i++) {
            boolean proofVerified = verifier.verifyPresentationProof(presentation, i);
            assertTrue(proofVerified);
        }
    }
}

class CredentialSubject {
    String id;
    String name;
    String spouse;

    public CredentialSubject(String id, String name, String spouse) {
        this.id = id;
        this.name = name;
        this.spouse = spouse;
    }
}