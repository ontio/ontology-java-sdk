package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.*;
import com.github.ontio.sdk.wallet.Identity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OntId2Demo {
    static long gasLimit = 2000000;
    static long gasPrice = 500;
    static String password = "passwordtest";

    public static void main(String[] args) {
        try {
            OntSdk ontSdk = ClaimRecordTxDemo.getOntSdk();
            // set claim contract address
            ontSdk.neovm().claimRecord().setContractAddress("52df370680de17bc5d4262c446f102a0ee0d6312");
            testClaim(ontSdk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testClaim(OntSdk ontSdk) throws Exception {
        Account payer = ontSdk.getWalletMgr().getAccount("AUNB7xQuBVg8hnRfVz9pyAuZQUqPBiDxDF", password);
        Identity issuerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity(
                "did:ont:AJ4C9aTYxTGUhEpaZdPjFSqCqzMCqJDRUd");
        Account issuerSigner = ontSdk.getWalletMgr().getAccount(issuerIdentity.ontid, password,
                issuerIdentity.controls.get(0).getSalt());
        Identity ownerIdentity = ontSdk.getWalletMgr().getWallet().getIdentity(
                "did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv");
//            String issuerRegTx = ontSdk.nativevm().ontId().sendAddPubKey(issuerIdentity.ontid, payer,
//                    issuerSigner.serializePublicKey(), "", payer, gasLimit, gasPrice);
//            System.out.println("issuerRegTx: " + issuerRegTx);
        Account ownerSigner = ontSdk.getWalletMgr().getAccount(ownerIdentity.ontid, password,
                ownerIdentity.controls.get(0).getSalt());
//            String ownerRegTx = ontSdk.nativevm().ontId().sendAddPubKey(ownerIdentity.ontid, payer,
//                    ownerSigner.serializePublicKey(), "", payer, gasLimit, gasPrice);
//            System.out.println("ownerRegTx: " + ownerRegTx);
//            Thread.sleep(6000);
        OntId2 issuer = new OntId2(issuerIdentity.ontid, issuerSigner,
                ontSdk.neovm().claimRecord(), ontSdk.nativevm().ontId());
        OntId2 owner = new OntId2(ownerIdentity.ontid, ownerSigner,
                ontSdk.neovm().claimRecord(), ontSdk.nativevm().ontId());
        // verifier may not own ontId and signer
        OntId2 verifier = new OntId2("", null,
                ontSdk.neovm().claimRecord(), ontSdk.nativevm().ontId());
        // generate a example claim
        VerifiableCredential credential = new VerifiableCredential();
        credential.context = new String[]{};
        credential.type = new String[]{"RelationshipCredential"};
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        credential.expirationDate = formatter.format(expiration);
        String noSubjectClaim = JSON.toJSONString(credential);
        System.out.println("noSubjectClaim: " + noSubjectClaim);
        ExampleCredentialSubject credentialSubject = new ExampleCredentialSubject("did:ont:111111",
                "Bob", "Alice");
        credential.credentialSubject = new ExampleCredentialSubject[]{credentialSubject};
        String claim = JSON.toJSONString(credential);
        // generate a sign request
        SignRequest req = owner.genSignReq(claim, Proof.ProofType.EcdsaSecp256r1Signature2019,
                Proof.ProofPurpose.assertionMethod, true);
        // issuer verify sign request
        if (!issuer.verifySignReq(req)) {
            System.out.println("sign request not verified");
            return;
        }
        // create claim after verified
        // the parameters that used to create claim should be unmarshal from signRequest.claim
        // for convenient, using those field at here
        VerifiableCredential verifiableCredential = issuer.createClaim(credential.context, credential.type,
                credential.credentialSubject, expiration,
                CredentialStatus.CredentialStatusType.ClaimContract,
                Proof.ProofType.EcdsaSecp256r1Signature2019,
                Proof.ProofPurpose.assertionMethod);
        String jwt1 = issuer.createJWTClaim(credential.context, credential.type, credentialSubject, expiration,
                CredentialStatus.CredentialStatusType.ClaimContract, Proof.ProofType.EcdsaSecp256r1Signature2019);
        // for debug, print verifiableCredential
        System.out.println("verifiableCredential: " + JSON.toJSONString(verifiableCredential));
        System.out.println("jwt1: " + jwt1);
        // commit claim to blcokchain
        String commitClaimHash = issuer.commitClaim(verifiableCredential, ownerIdentity.ontid, payer, gasLimit,
                gasPrice, ontSdk);
        String commitJWTClaimHash = issuer.commitClaim(jwt1, ownerIdentity.ontid, payer, gasLimit,
                gasPrice, ontSdk);
        System.out.println("commit claim: " + verifiableCredential.id + ", txHash: " + commitClaimHash);
        System.out.println("commit jwt claim, txHash: " + commitJWTClaimHash);
        Thread.sleep(6000);
        ClaimRecordTxDemo.showEvent(ontSdk, "Commit", commitClaimHash);
        ClaimRecordTxDemo.showEvent(ontSdk, "Commit", commitJWTClaimHash);
        // verify claim
        // user should own self credible ontIds, not only use issuerIdentity.ontid and ownerIdentity.ontid
        String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
        boolean claimVerified = verifier.verifyClaim(credibleOntIds, verifiableCredential);
        if (!claimVerified) {
            System.out.println("claim not verified");
            return;
        }
        boolean jwt1Verified = verifier.verifyJWTClaim(credibleOntIds, jwt1);
        if (!jwt1Verified) {
            System.out.println("jwt1 not verified");
            return;
        }
        // create other VerifiableCredential to create presentation
        ExampleCredentialSubject otherCredentialSubject = new ExampleCredentialSubject("did:ont:111111",
                "he", "she");
        VerifiableCredential otherVerifiableCredential = issuer.createClaim(credential.context, credential.type,
                new ExampleCredentialSubject[]{otherCredentialSubject}, expiration,
                CredentialStatus.CredentialStatusType.ClaimContract,
                Proof.ProofType.EcdsaSecp256r1Signature2019,
                Proof.ProofPurpose.assertionMethod);
        String jwt2 = issuer.createJWTClaim(credential.context, credential.type,
                new ExampleCredentialSubject[]{otherCredentialSubject}, expiration,
                CredentialStatus.CredentialStatusType.ClaimContract,
                Proof.ProofType.EcdsaSecp256r1Signature2019);
        System.out.println("otherVerifiableCredential: " + JSON.toJSONString(otherVerifiableCredential));
        System.out.println("jwt2: " + jwt2);
        String otherCommitClaimHash = issuer.commitClaim(otherVerifiableCredential, ownerIdentity.ontid, payer,
                gasLimit, gasPrice, ontSdk);
        System.out.println("commit claim: " + otherVerifiableCredential.id + ", txHash: " + otherCommitClaimHash);
        Thread.sleep(6000);
        String otherJWTCommitClaimHash = issuer.commitClaim(jwt2, ownerIdentity.ontid, payer,
                gasLimit, gasPrice, ontSdk);
        System.out.println("commit other jwt claim, txHash: " + otherJWTCommitClaimHash);
        Thread.sleep(6000);
        ClaimRecordTxDemo.showEvent(ontSdk, "Commit", otherCommitClaimHash);
        ClaimRecordTxDemo.showEvent(ontSdk, "Commit", otherJWTCommitClaimHash);
        // create presentation
        String[] presentationContext = new String[]{};
        String[] presentationType = new String[]{"CredentialManagerPresentation"};
        // you can use any ontId as otherSigner if you want
        OntIdSigner otherSigner = new OntIdSigner(issuerIdentity.ontid,
                issuerIdentity.ontid + "#keys-2", issuerSigner);
        VerifiablePresentation presentation = owner.createPresentation(
                new VerifiableCredential[]{verifiableCredential, otherVerifiableCredential},
                presentationContext, presentationType, ownerIdentity.ontid, new OntIdSigner[]{otherSigner},
                Proof.ProofType.EcdsaSecp256r1Signature2019,
                Proof.ProofPurpose.assertionMethod);
        System.out.println("presentation: " + JSON.toJSONString(presentation));
        // the 2 credential has no jws
//        String jwtPresentation1 = owner.createJWTPresentation(
//                new VerifiableCredential[]{verifiableCredential, otherVerifiableCredential},
//                presentationContext, presentationType, ownerIdentity.ontid,
//                Proof.ProofType.EcdsaSecp256r1Signature2019);
//        System.out.println("jwtPresentation1: " + jwtPresentation1);
        String jwtPresentation2 = owner.createJWTPresentation(new String[]{jwt1, jwt2},
                presentationContext, presentationType, ownerIdentity.ontid,
                Proof.ProofType.EcdsaSecp256r1Signature2019, Proof.ProofPurpose.assertionMethod);
        System.out.println("jwtPresentation2: " + jwtPresentation2);
        // verify presentation
        // verify each claim firstly
        for (VerifiableCredential credential1 : presentation.verifiableCredential) {
            boolean v = issuer.verifyClaim(credibleOntIds, credential1);
            System.out.println("presentation verify: " + v);
        }
        // verify each proof
        for (int i = 0; i < presentation.proof.length; i++) {
            boolean proofVerified = verifier.verifyPresentationProof(presentation, i);
            System.out.println(String.format("%d proof verify: %s", i, proofVerified));
        }
//        boolean jwtPresentation1Verified = verifier.verifyJWTPresentation(credibleOntIds, jwtPresentation1);
        boolean jwtPresentation2Verified = verifier.verifyJWTPresentation(credibleOntIds, jwtPresentation2);
//        System.out.println("jwtPresentation1Verified: " + jwtPresentation1Verified);
        System.out.println("jwtPresentation2Verified: " + jwtPresentation2Verified);
        // issuer revoke claim
        String issuerRevokeHash = issuer.revokeClaim(verifiableCredential, payer, gasLimit, gasPrice, ontSdk);
        System.out.println("issuerRevokeHash: " + issuerRevokeHash);
        Thread.sleep(6000);
        ClaimRecordTxDemo.showEvent(ontSdk, "Revoke", issuerRevokeHash);
        // owner revoke claim
        String ownerRevokeHash = owner.revokeClaimById(otherVerifiableCredential.id, payer, gasLimit, gasPrice, ontSdk);
        System.out.println("ownerRevokeHash: " + ownerRevokeHash);
        Thread.sleep(6000);
        ClaimRecordTxDemo.showEvent(ontSdk, "Revoke", ownerRevokeHash);

        String revokeJWTClaimHash1 = owner.revokeJWTClaim(jwt1, payer, gasLimit, gasPrice, ontSdk);
        String revokeJWTClaimHash2 = owner.revokeJWTClaim(jwt2, payer, gasLimit, gasPrice, ontSdk);
        System.out.println("revokeJWTClaimHash1: " + revokeJWTClaimHash1);
        System.out.println("revokeJWTClaimHash2: " + revokeJWTClaimHash2);
        Thread.sleep(6000);
        ClaimRecordTxDemo.showEvent(ontSdk, "Revoke", revokeJWTClaimHash1);
        ClaimRecordTxDemo.showEvent(ontSdk, "Revoke", revokeJWTClaimHash2);
    }
}

class ExampleCredentialSubject {
    public String id;
    public String name;
    public String spouse;

    public ExampleCredentialSubject(String id, String name, String spouse) {
        this.id = id;
        this.name = name;
        this.spouse = spouse;
    }
}