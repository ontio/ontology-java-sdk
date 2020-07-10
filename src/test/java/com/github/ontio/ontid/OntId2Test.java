package com.github.ontio.ontid;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.Digest;
import com.github.ontio.ontid.jwt.JWTCredential;
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

    public void testPresentationFromOldCred() throws Exception {
        String oldCred = "";
        String challenge = "d1b23d3...3d23d32d2";
        String[] domain = new String[]{"https://example.com"};
        String presentation = owner.createPresentationFromOldCred(new String[]{oldCred}, null,
                null, ownerIdentity.ontid, challenge, domain, ProofPurpose.assertionMethod);
        assertNotNull(presentation);
        JWTCredential jwtCred = JWTCredential.deserializeToJWTCred(presentation);
        // check jws
//        byte[] needSignData = jwtCred.genNeedSignData();
//        byte[] signature = jwtCred.parseSignature();
//        boolean presentationSigValid = verifier.verifyPubKeyIdSignature(jwtCred.header.kid, needSignData, signature);
//        assertTrue(presentationSigValid);
        String[] credibleOntIds = new String[]{"did:ont:AHzUfrqpNwHBfXA72D9HciNAKuCr83SzDG"};
        for (String vc : jwtCred.payload.vp.verifiableCredential) {
//            assertTrue(ontSdk.nativevm().ontId().verifyCredNotExpired(vc));
//            assertTrue(ontSdk.nativevm().ontId().verifyCredIssuanceDate(vc));
//            assertTrue(ontSdk.nativevm().ontId().verifyCredOntIdCredible(vc, credibleOntIds));
//            assertTrue(ontSdk.nativevm().ontId().verifyCredSignature(vc));
            // use corresponding credential record contract to verify credential status
//            ontSdk.neovm().credentialRecord().setContractAddress("36bb5c053b6b839c8f6b923fe852f91239b9fccc");
//            assertTrue(ontSdk.nativevm().ontId().verifyCredNotRevoked(vc));
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

    public void testVerifyStringJWTCred() throws Exception {
        String[] credibleOntIds = new String[]{"did:ont:TD5ixYQnxmUy7qTmyfKpSmysSnjwTB1Sdi",
                "did:ont:TYNEYu7t8tp439vmcgpJj7CR4kUqJe6ab2"};
        String cred = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6VFlORVl1N3Q4dHA0Mzl2bWNncEpqN0NSNGtVcUplNmFiMiNrZXlzLTEiLCJ0eXAiOiJKV1QifQ==.eyJpc3MiOiJkaWQ6b250OlRZTkVZdTd0OHRwNDM5dm1jZ3BKajdDUjRrVXFKZTZhYjIiLCJleHAiOjE1OTM1MDgxNTksIm5iZiI6MTU5MzQyMTc1OSwiaWF0IjoxNTkzNDIxNzU5LCJqdGkiOiJ1cm46dXVpZDo0YTkyYWRiZC1iOWUyLTQ3ZTQtODk4ZC1hNTQ2NDcyNmRlOTUiLCJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vb250aWQub250LmlvL2NyZWRlbnRpYWxzL3YxIiwiY29udGV4dDEiLCJjb250ZXh0MiJdLCJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiUmVsYXRpb25zaGlwQ3JlZGVudGlhbCJdLCJjcmVkZW50aWFsU3ViamVjdCI6W3siaWQiOiJkaWQ6ZXhhbXBsZTplYmZlYjFmNzEyZWJjNmYxYzI3NmUxMmVjMjEiLCJuYW1lIjoiSmF5ZGVuIERvZSIsInNwb3VzZSI6ImRpZDpleGFtcGxlOmMyNzZlMTJlYzIxZWJmZWIxZjcxMmViYzZmMSJ9LHsiaWQiOiJkaWQ6ZXhhbXBsZTpjMjc2ZTEyZWMyMWViZmViMWY3MTJlYmM2ZjEiLCJuYW1lIjoiTW9yZ2FuIERvZSIsInNwb3VzZSI6ImRpZDpleGFtcGxlOmViZmViMWY3MTJlYmM2ZjFjMjc2ZTEyZWMyMSJ9XSwiY3JlZGVudGlhbFN0YXR1cyI6eyJpZCI6IjUyZGYzNzA2ODBkZTE3YmM1ZDQyNjJjNDQ2ZjEwMmEwZWUwZDYzMTIiLCJ0eXBlIjoiQXR0ZXN0Q29udHJhY3QifSwicHJvb2YiOnsiY3JlYXRlZCI6IjIwMjAtMDYtMjlUMDk6MDk6MTlaIiwiZG9tYWluIjoiIiwicHJvb2ZQdXJwb3NlIjoiYXNzZXJ0aW9uTWV0aG9kIn19fQ==.d9aAj9Ip0OadOXnAtlCBRv11KYx6rYF7ViHQIqiLKiOilcoav+huZMSUhZls5r/JfuXOhRGkjXH6CdxljfI3xA==";
        boolean verified = verifier.verifyJWTCred(credibleOntIds, cred);
        assertTrue(verified);
        String presentation = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6VEQ1aXhZUW54bVV5N3FUbXlmS3BTbXlzU25qd1RCMVNkaSNrZXlzLTEiLCJ0eXAiOiJKV1QifQ==.eyJpc3MiOiJkaWQ6b250OlRENWl4WVFueG1VeTdxVG15ZktwU215c1NuandUQjFTZGkiLCJhdWQiOiIiLCJqdGkiOiJ1cm46dXVpZDplMGNlZDM4YS0yNzRiLTQ1ZTctYWZhNS1iYmUyY2U5ZTcxZWIiLCJ2cCI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vb250aWQub250LmlvL2NyZWRlbnRpYWxzL3YxIiwiY29udGV4dDEiLCJjb250ZXh0MiJdLCJ0eXBlIjpbIlZlcmlmaWFibGVDcmVkZW50aWFsIiwiUmVsYXRpb25zaGlwQ3JlZGVudGlhbCJdLCJ2ZXJpZmlhYmxlQ3JlZGVudGlhbCI6WyJleUpoYkdjaU9pSkZVekkxTmlJc0ltdHBaQ0k2SW1ScFpEcHZiblE2VkZsT1JWbDFOM1E0ZEhBME16bDJiV05uY0VwcU4wTlNOR3RWY1VwbE5tRmlNaU5yWlhsekxURWlMQ0owZVhBaU9pSktWMVFpZlE9PS5leUpwYzNNaU9pSmthV1E2YjI1ME9sUlpUa1ZaZFRkME9IUndORE01ZG0xalozQkthamREVWpSclZYRktaVFpoWWpJaUxDSmxlSEFpT2pFMU9UTTFNRGd4TlRrc0ltNWlaaUk2TVRVNU16UXlNVGMxT1N3aWFXRjBJam94TlRrek5ESXhOelU1TENKcWRHa2lPaUoxY200NmRYVnBaRG8wWVRreVlXUmlaQzFpT1dVeUxUUTNaVFF0T0RrNFpDMWhOVFEyTkRjeU5tUmxPVFVpTENKMll5STZleUpBWTI5dWRHVjRkQ0k2V3lKb2RIUndjem92TDNkM2R5NTNNeTV2Y21jdk1qQXhPQzlqY21Wa1pXNTBhV0ZzY3k5Mk1TSXNJbWgwZEhCek9pOHZiMjUwYVdRdWIyNTBMbWx2TDJOeVpXUmxiblJwWVd4ekwzWXhJaXdpWTI5dWRHVjRkREVpTENKamIyNTBaWGgwTWlKZExDSjBlWEJsSWpwYklsWmxjbWxtYVdGaWJHVkRjbVZrWlc1MGFXRnNJaXdpVW1Wc1lYUnBiMjV6YUdsd1EzSmxaR1Z1ZEdsaGJDSmRMQ0pqY21Wa1pXNTBhV0ZzVTNWaWFtVmpkQ0k2VzNzaWFXUWlPaUprYVdRNlpYaGhiWEJzWlRwbFltWmxZakZtTnpFeVpXSmpObVl4WXpJM05tVXhNbVZqTWpFaUxDSnVZVzFsSWpvaVNtRjVaR1Z1SUVSdlpTSXNJbk53YjNWelpTSTZJbVJwWkRwbGVHRnRjR3hsT21NeU56WmxNVEpsWXpJeFpXSm1aV0l4WmpjeE1tVmlZelptTVNKOUxIc2lhV1FpT2lKa2FXUTZaWGhoYlhCc1pUcGpNamMyWlRFeVpXTXlNV1ZpWm1WaU1XWTNNVEpsWW1NMlpqRWlMQ0p1WVcxbElqb2lUVzl5WjJGdUlFUnZaU0lzSW5Od2IzVnpaU0k2SW1ScFpEcGxlR0Z0Y0d4bE9tVmlabVZpTVdZM01USmxZbU0yWmpGak1qYzJaVEV5WldNeU1TSjlYU3dpWTNKbFpHVnVkR2xoYkZOMFlYUjFjeUk2ZXlKcFpDSTZJalV5WkdZek56QTJPREJrWlRFM1ltTTFaRFF5TmpKak5EUTJaakV3TW1Fd1pXVXdaRFl6TVRJaUxDSjBlWEJsSWpvaVFYUjBaWE4wUTI5dWRISmhZM1FpZlN3aWNISnZiMllpT25zaVkzSmxZWFJsWkNJNklqSXdNakF0TURZdE1qbFVNRGs2TURrNk1UbGFJaXdpWkc5dFlXbHVJam9pSWl3aWNISnZiMlpRZFhKd2IzTmxJam9pWVhOelpYSjBhVzl1VFdWMGFHOWtJbjE5ZlE9PS5kOWFBajlJcDBPYWRPWG5BdGxDQlJ2MTFLWXg2cllGN1ZpSFFJcWlMS2lPaWxjb2F2K2h1Wk1TVWhabHM1ci9KZnVYT2hSR2tqWEg2Q2R4bGpmSTN4QT09Il0sInByb29mIjp7ImNyZWF0ZWQiOiIyMDIwLTA2LTI5VDA5OjEwOjEzWiIsInByb29mUHVycG9zZSI6ImFzc2VydGlvbk1ldGhvZCJ9fX0=.K+kvWWkMqOpYXPdaEeeJFyJ5U0P6wY9QcSjMiCNBrEASc0X9zOLdyzrg/+YVYmBVBd9EYPkxPp7ypdY72BT+1g==";
        verified = verifier.verifyJWTPresentation(credibleOntIds, presentation);
        assertTrue(verified);
    }

    public void testVerifyStringCred() throws Exception {
        String[] credibleOntIds = new String[]{"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F",
                "did:ont:THJjPtj9yLy64CteyPAiUCJmbEqekCXkt1"};
        String cred = "{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\",\"context1\",\"context2\"],\"id\":\"urn:uuid:43ae4ecd-34ae-43f2-96b1-fe5e8a9859b5\",\"type\":[\"VerifiableCredential\",\"RelationshipCredential\"],\"issuer\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F\",\"issuanceDate\":\"2020-06-29T09:30:39Z\",\"expirationDate\":\"2020-06-30T09:30:39Z\",\"credentialSubject\":[{\"id\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\",\"name\":\"Jayden Doe\",\"spouse\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\"},{\"id\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\",\"name\":\"Morgan Doe\",\"spouse\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\"}],\"credentialStatus\":{\"id\":\"52df370680de17bc5d4262c446f102a0ee0d6312\",\"type\":\"AttestContract\"},\"proof\":{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-29T09:30:39Z\",\"domain\":\"\",\"proofPurpose\":\"assertionMethod\",\"verificationMethod\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F#keys-1\",\"hex\":\"07790e7dc69942fc1265d510fb5e7f0194b661a6cb2c802ad55968b4bd5fd7fb23fdf71255356e75eb376415158c25a4dddd81a42f492ce391f34a08b696850d\"}}";
        VerifiableCredential credential = JSON.parseObject(cred, VerifiableCredential.class);
        assertTrue(verifier.verifyCred(credibleOntIds, credential));
        String presentation = "{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\",\"context1\",\"context2\"],\"id\":\"urn:uuid:f1bc8840-ebed-4bdf-b9f4-883dc8ff7b3c\",\"type\":[\"VerifiablePresentation\",\"RelationshipCredential\"],\"verifiableCredential\":[{\"@context\":[\"https://www.w3.org/2018/credentials/v1\",\"https://ontid.ont.io/credentials/v1\",\"context1\",\"context2\"],\"id\":\"urn:uuid:43ae4ecd-34ae-43f2-96b1-fe5e8a9859b5\",\"type\":[\"VerifiableCredential\",\"RelationshipCredential\"],\"issuer\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F\",\"issuanceDate\":\"2020-06-29T09:30:39Z\",\"expirationDate\":\"2020-06-30T09:30:39Z\",\"credentialSubject\":[{\"id\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\",\"name\":\"Jayden Doe\",\"spouse\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\"},{\"id\":\"did:example:c276e12ec21ebfeb1f712ebc6f1\",\"name\":\"Morgan Doe\",\"spouse\":\"did:example:ebfeb1f712ebc6f1c276e12ec21\"}],\"credentialStatus\":{\"id\":\"52df370680de17bc5d4262c446f102a0ee0d6312\",\"type\":\"AttestContract\"},\"proof\":{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-29T09:30:39Z\",\"domain\":\"\",\"proofPurpose\":\"assertionMethod\",\"verificationMethod\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F#keys-1\",\"hex\":\"07790e7dc69942fc1265d510fb5e7f0194b661a6cb2c802ad55968b4bd5fd7fb23fdf71255356e75eb376415158c25a4dddd81a42f492ce391f34a08b696850d\"}}],\"holder\":\"did:ont:THJjPtj9yLy64CteyPAiUCJmbEqekCXkt1\",\"proof\":[{\"type\":\"EcdsaSecp256r1VerificationKey2019\",\"created\":\"2020-06-29T09:30:43Z\",\"domain\":\"\",\"proofPurpose\":\"assertionMethod\",\"verificationMethod\":\"did:ont:TQgTxAAcqjxRRvmQ9f7G7tAr4bRAMj9n2F#keys-1\",\"hex\":\"81a05a13e81855ea97173722f9cda697923727e5459886d4e8b582a932983793f7d410cbd5822ee4cea575bc8386eaac9bdccf22a157382751f8271c8171815b\"}]}";
        VerifiablePresentation verifiablePresentation = JSON.parseObject(presentation, VerifiablePresentation.class);
        assertTrue(verifier.verifyPresentationProof(verifiablePresentation, 0));
    }

    public void testGolangSignature() throws Exception {
        byte[] pubKeyData = Helper.hexToBytes("02fe02f80bce49f59f7d34f39fd2a81e8dff91e04d9892a35425a81e0ff12acee7");
        Account account = new Account(false, pubKeyData);
        String original = "asdkhfakfhakfj";
        byte[] needSignData = Digest.sha256(original.getBytes());
        byte[] sig = Helper.hexToBytes("2e663b13811a24a9d65b746a72b788da111bd23460caefa419c7997d46247dc04edeafb399c3d94c33a32df479326a145c9d0cde2c61c8a4d2d3f565671e1757");
        assertTrue(account.verifySignature(needSignData, sig));
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