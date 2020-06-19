package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.*;
import com.github.ontio.ontid.jwt.JWTClaim;
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
            testDeserialize();
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
        SignRequest req = owner.genSignReq(claim, ProofPurpose.assertionMethod, true);
        // issuer verify sign request
        if (!issuer.verifySignReq(req)) {
            System.out.println("sign request not verified");
            return;
        }
        // create claim after verified
        // the parameters that used to create claim should be unmarshal from signRequest.claim
        // for convenient, using those field at here
        VerifiableCredential verifiableCredential = issuer.createClaim(credential.context, credential.type,
                issuerIdentity.ontid,
                credential.credentialSubject, expiration,
                CredentialStatusType.AttestContract,
                ProofPurpose.assertionMethod);
        String jwt1 = issuer.createJWTClaim(credential.context, credential.type, issuerIdentity.ontid,
                credentialSubject, expiration, CredentialStatusType.AttestContract,
                PubKeyType.EcdsaSecp256r1Signature2019);
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
        ExampleIssuer exampleIssuer = new ExampleIssuer(issuerIdentity.ontid, "issuer");
        ExampleCredentialSubject otherCredentialSubject = new ExampleCredentialSubject("did:ont:111111",
                "he", "she");
        VerifiableCredential otherVerifiableCredential = issuer.createClaim(credential.context, credential.type,
                new ExampleCredentialSubject[]{otherCredentialSubject}, exampleIssuer, expiration,
                CredentialStatusType.AttestContract,
                ProofPurpose.assertionMethod);
        String jwt2 = issuer.createJWTClaim(credential.context, credential.type,
                new ExampleCredentialSubject[]{otherCredentialSubject}, exampleIssuer, expiration,
                CredentialStatusType.AttestContract,
                PubKeyType.EcdsaSecp256r1Signature2019);
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
        VerifiablePresentation presentation = owner.createPresentation(
                new VerifiableCredential[]{verifiableCredential, otherVerifiableCredential},
                presentationContext, presentationType, ownerIdentity.ontid, new OntIdSigner[]{},
                ProofPurpose.assertionMethod);
        System.out.println("presentation: " + JSON.toJSONString(presentation));
        // the 2 credential has no jws
//        String jwtPresentation1 = owner.createJWTPresentation(
//                new VerifiableCredential[]{verifiableCredential, otherVerifiableCredential},
//                presentationContext, presentationType, ownerIdentity.ontid,
//                ProofType.EcdsaSecp256r1Signature2019);
//        System.out.println("jwtPresentation1: " + jwtPresentation1);
        String jwtPresentation2 = owner.createJWTPresentation(new String[]{jwt1, jwt2},
                presentationContext, presentationType, ownerIdentity.ontid,
                PubKeyType.EcdsaSecp256r1Signature2019, ProofPurpose.assertionMethod);
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

    public static void testDeserialize() throws Exception {
        String jwtCredential = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcXpNQ3FKR" +
                "FJVZCNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiJkaWQ6b250OjExMTExMSIsImp0aSI6IjUyNzM1NTgxLTM" +
                "wMGItNDc5OS1hNDAwLTUxNmJhYjJkOWRmMSIsImlzcyI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcX" +
                "pNQ3FKRFJVZCIsIm5iZiI6IjE1OTI0NzM2MTEiLCJpYXQiOiIxNTkyNDczNjExIiwiZXhwIjoiMTU5MjU2MDAxMSIsI" +
                "nZjIjp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIiwiaHR0cHM6Ly9v" +
                "bnRpZC5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCIsIlJlbGF0aW9" +
                "uc2hpcENyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsibmFtZSI6IkJvYiIsInNwb3VzZSI6IkFsaWNlIn" +
                "0sImNyZWRlbnRpYWxTdGF0dXMiOnsiaWQiOiI1MmRmMzcwNjgwZGUxN2JjNWQ0MjYyYzQ0NmYxMDJhMGVlMGQ2MzEyI" +
                "iwidHlwZSI6IkNsYWltQ29udHJhY3QifX19.AcxAzQqY5ZnSCzNuPJuYyTdStw4Otjffm0eqUfAQdRUKbqRV243VVNK" +
                "kPhoDdFSpOUneL3qto5nqsz2Zqq9ta/Q=";
        String jwtPresentation = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QVZlNHpWWnp0ZW82SG9McGRCd3BLTnREWE" +
                "xqSkJ6QjlmdiNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiJkaWQ6b250OjExMTExMSIsImp0aSI6ImUzYjZl" +
                "NjY1LTA4M2QtNDM1Ni1hMTMyLTdhZGZiYjE3MDA1YSIsImlzcyI6ImRpZDpvbnQ6QVZlNHpWWnp0ZW82SG9McGRCd3B" +
                "LTnREWExqSkJ6QjlmdiIsIm5iZiI6IjE1OTI0NzM2MTEiLCJpYXQiOiIxNTkyNDczNjExIiwiZXhwIjoiMTU5MjU2MD" +
                "AxMSIsInZwIjp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIiwiaHR0c" +
                "HM6Ly9vbnRpZC5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlUHJlc2VudGF0aW9uIiwi" +
                "Q3JlZGVudGlhbE1hbmFnZXJQcmVzZW50YXRpb24iXSwidmVyaWZpYWJsZUNyZWRlbnRpYWwiOlsiZXlKaGJHY2lPaUp" +
                "GVXpJMU5pSXNJbXRwWkNJNkltUnBaRHB2Ym5RNlFVbzBRemxoVkZsNFZFZFZhRVZ3WVZwa1VHcEdVM0ZEY1hwTlEzRk" +
                "tSRkpWWkNOclpYbHpMVElpTENKMGVYQWlPaUpLVjFRaWZRPT0uZXlKemRXSWlPaUprYVdRNmIyNTBPakV4TVRFeE1TS" +
                "XNJbXAwYVNJNklqVXlOek0xTlRneExUTXdNR0l0TkRjNU9TMWhOREF3TFRVeE5tSmhZakprT1dSbU1TSXNJbWx6Y3lJ" +
                "NkltUnBaRHB2Ym5RNlFVbzBRemxoVkZsNFZFZFZhRVZ3WVZwa1VHcEdVM0ZEY1hwTlEzRktSRkpWWkNJc0ltNWlaaUk" +
                "2SWpFMU9USTBOek0yTVRFaUxDSnBZWFFpT2lJeE5Ua3lORGN6TmpFeElpd2laWGh3SWpvaU1UVTVNalUyTURBeE1TSX" +
                "NJblpqSWpwN0lrQmpiMjUwWlhoMElqcGJJbWgwZEhCek9pOHZkM2QzTG5jekxtOXlaeTh5TURFNEwyTnlaV1JsYm5Sc" +
                "FlXeHpMM1l4SWl3aWFIUjBjSE02THk5dmJuUnBaQzV2Ym5RdWFXOHZZM0psWkdWdWRHbGhiSE12ZGpFaVhTd2lkSGx3" +
                "WlNJNld5SldaWEpwWm1saFlteGxRM0psWkdWdWRHbGhiQ0lzSWxKbGJHRjBhVzl1YzJocGNFTnlaV1JsYm5ScFlXd2l" +
                "YU3dpWTNKbFpHVnVkR2xoYkZOMVltcGxZM1FpT25zaWJtRnRaU0k2SWtKdllpSXNJbk53YjNWelpTSTZJa0ZzYVdObE" +
                "luMHNJbU55WldSbGJuUnBZV3hUZEdGMGRYTWlPbnNpYVdRaU9pSTFNbVJtTXpjd05qZ3daR1V4TjJKak5XUTBNall5W" +
                "XpRME5tWXhNREpoTUdWbE1HUTJNekV5SWl3aWRIbHdaU0k2SWtOc1lXbHRRMjl1ZEhKaFkzUWlmWDE5LkFjeEF6UXFZ" +
                "NVpuU0N6TnVQSnVZeVRkU3R3NE90amZmbTBlcVVmQVFkUlVLYnFSVjI0M1ZWTktrUGhvRGRGU3BPVW5lTDNxdG81bnF" +
                "zejJacXE5dGEvUT0iLCJleUpoYkdjaU9pSkZVekkxTmlJc0ltdHBaQ0k2SW1ScFpEcHZiblE2UVVvMFF6bGhWRmw0Vk" +
                "VkVmFFVndZVnBrVUdwR1UzRkRjWHBOUTNGS1JGSlZaQ05yWlhsekxUSWlMQ0owZVhBaU9pSktWMVFpZlE9PS5leUp6Z" +
                "FdJaU9pSmthV1E2YjI1ME9qRXhNVEV4TVNJc0ltcDBhU0k2SW1VM1ltTmtaVGRqTFdRMU56SXROR0UxTlMwNE5HWmxM" +
                "V1JoTkdJNVkyVmhOREF4T1NJc0ltbHpjeUk2SW1ScFpEcHZiblE2UVVvMFF6bGhWRmw0VkVkVmFFVndZVnBrVUdwR1U" +
                "zRkRjWHBOUTNGS1JGSlZaQ0lzSW01aVppSTZJakUxT1RJME56TTJNekFpTENKcFlYUWlPaUl4TlRreU5EY3pOak13SW" +
                "l3aVpYaHdJam9pTVRVNU1qVTJNREF4TVNJc0luWmpJanA3SWtCamIyNTBaWGgwSWpwYkltaDBkSEJ6T2k4dmQzZDNMb" +
                "mN6TG05eVp5OHlNREU0TDJOeVpXUmxiblJwWVd4ekwzWXhJaXdpYUhSMGNITTZMeTl2Ym5ScFpDNXZiblF1YVc4dlkz" +
                "SmxaR1Z1ZEdsaGJITXZkakVpWFN3aWRIbHdaU0k2V3lKV1pYSnBabWxoWW14bFEzSmxaR1Z1ZEdsaGJDSXNJbEpsYkd" +
                "GMGFXOXVjMmhwY0VOeVpXUmxiblJwWVd3aVhTd2lZM0psWkdWdWRHbGhiRk4xWW1wbFkzUWlPbHQ3SW01aGJXVWlPaU" +
                "pvWlNJc0luTndiM1Z6WlNJNkluTm9aU0o5WFN3aVkzSmxaR1Z1ZEdsaGJGTjBZWFIxY3lJNmV5SnBaQ0k2SWpVeVpHW" +
                "XpOekEyT0RCa1pURTNZbU0xWkRReU5qSmpORFEyWmpFd01tRXdaV1V3WkRZek1USWlMQ0owZVhCbElqb2lRMnhoYVcx" +
                "RGIyNTBjbUZqZENKOWZYMD0uQVhJVzR2dThvYnNzQjVxdjZMaEo5dDN2OUcwZXZ5TTVCM3d3SW9TaTFocU85NENmc28" +
                "1MG44WkNvcnVmd3A4MUVFMVR6MTUybkorQTZzN1Mzc2NtMTMwPSJdfX0=.AaXTFqjgIof01n+fft33yM+Th391vMIAY" +
                "rW+z9exSnOlXwlVFw6JO0drwFrON6iJ1PqqjFW1hXduZUb6U3L4iDc=";
        JWTClaim jwtClaim1 = JWTClaim.deserializeToJWTClaim(jwtCredential);
        System.out.println(JSON.toJSONString(jwtClaim1));
        VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(jwtClaim1, null);
        System.out.println(JSON.toJSONString(credential));
        JWTClaim jwtClaim2 = JWTClaim.deserializeToJWTClaim(jwtPresentation);
        System.out.println(JSON.toJSONString(jwtClaim2));
        VerifiablePresentation presentation = VerifiablePresentation.deserializeFromJWT(jwtClaim2, null);
        System.out.println(JSON.toJSONString(presentation));
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

class ExampleIssuer {
    public String id;
    public String name;

    public ExampleIssuer(String id, String name) {
        this.id = id;
        this.name = name;
    }
}