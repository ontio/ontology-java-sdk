package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.*;
import com.github.ontio.ontid.jwt.JWTClaim;
import com.github.ontio.sdk.wallet.Identity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
            testVerifyClaimSignature(ontSdk);
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
                credentialSubject, expiration, CredentialStatusType.AttestContract, null);
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
        VerifiableCredential verifiableCredential2 = issuer.createClaim(credential.context, credential.type,
                exampleIssuer, new ExampleCredentialSubject[]{otherCredentialSubject}, expiration,
                CredentialStatusType.AttestContract,
                ProofPurpose.assertionMethod);
        String jwt2 = issuer.createJWTClaim(credential.context, credential.type,
                exampleIssuer, new ExampleCredentialSubject[]{otherCredentialSubject}, expiration,
                CredentialStatusType.AttestContract, null);
        System.out.println("verifiableCredential2: " + JSON.toJSONString(verifiableCredential2));
        System.out.println("jwt2: " + jwt2);
        String otherCommitClaimHash = issuer.commitClaim(verifiableCredential2, ownerIdentity.ontid, payer,
                gasLimit, gasPrice, ontSdk);
        System.out.println("commit claim: " + verifiableCredential2.id + ", txHash: " + otherCommitClaimHash);
        String jwt2CommitClaimHash = issuer.commitClaim(jwt2, ownerIdentity.ontid, payer,
                gasLimit, gasPrice, ontSdk);
        System.out.println("commit other jwt claim, txHash: " + jwt2CommitClaimHash);
        Thread.sleep(6000);
        ClaimRecordTxDemo.showEvent(ontSdk, "Commit", otherCommitClaimHash);
        ClaimRecordTxDemo.showEvent(ontSdk, "Commit", jwt2CommitClaimHash);
        boolean verifiableCredential2Verified = verifier.verifyClaim(credibleOntIds, verifiableCredential2);
        if (!verifiableCredential2Verified) {
            System.out.println("verifiableCredential2Verified: " + verifiableCredential2Verified);
            return;
        }
        boolean jwt2Verified = verifier.verifyJWTClaim(credibleOntIds, jwt2);
        if (!jwt2Verified) {
            System.out.println("jwt2Verified: " + jwt2Verified);
            return;
        }
        // create presentation
        String[] presentationContext = new String[]{};
        String[] presentationType = new String[]{"CredentialManagerPresentation"};
        ArrayList<String> challenge = new ArrayList<>();
        challenge.add("d1b23d3...3d23d32d2");
        ArrayList<Object> domain = new ArrayList<>();
        domain.add(new String[]{"https://example.com"});
        // you can use any ontId as otherSigner if you want
        VerifiablePresentation presentation = owner.createPresentation(
                new VerifiableCredential[]{verifiableCredential, verifiableCredential2},
                presentationContext, presentationType, challenge, Collections.singletonList(domain), ownerIdentity.ontid,
                new OntIdSigner[]{}, ProofPurpose.assertionMethod);
        System.out.println("presentation: " + JSON.toJSONString(presentation));
        // the 2 credential has no jws
//        String jwtPresentation1 = owner.createJWTPresentation(
//                new VerifiableCredential[]{verifiableCredential, verifiableCredential2},
//                presentationContext, presentationType, ownerIdentity.ontid,
//                ProofType.EcdsaSecp256r1Signature2019);
//        System.out.println("jwtPresentation1: " + jwtPresentation1);
        String jwtPresentation2 = owner.createJWTPresentation(new String[]{jwt1, jwt2},
                presentationContext, presentationType, ownerIdentity.ontid, challenge.get(0), domain.get(0), "",
                ProofPurpose.assertionMethod);
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
        String ownerRevokeHash = owner.revokeClaimById(verifiableCredential2.id, payer, gasLimit, gasPrice, ontSdk);
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
        String jwtCredential = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcXpNQ3" +
                "FKRFJVZCNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiJkaWQ6b250OjExMTExMSIsImp0aSI6InVybjp1dWlkOm" +
                "NhNmM1ZmY1LTlkODMtNGM0Mi05OGVjLTQwYzYxOTFmMWZiNyIsImlzcyI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVp" +
                "kUGpGU3FDcXpNQ3FKRFJVZCIsIm5iZiI6MTU5MjgxNTUwMCwiaWF0IjoxNTkyODE1NTAwLCJleHAiOjE1OTI5MDE5MDAs" +
                "InZjIjp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIiwiaHR0cHM6Ly9vb" +
                "nRpZC5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCIsIlJlbGF0aW9uc2" +
                "hpcENyZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsibmFtZSI6IkJvYiIsInNwb3VzZSI6IkFsaWNlIn0sImN" +
                "yZWRlbnRpYWxTdGF0dXMiOnsiaWQiOiI1MmRmMzcwNjgwZGUxN2JjNWQ0MjYyYzQ0NmYxMDJhMGVlMGQ2MzEyIiwidHlw" +
                "ZSI6IkF0dGVzdENvbnRyYWN0In0sInByb29mIjp7InR5cGUiOiJFY2RzYVNlY3AyNTZyMVZlcmlmaWNhdGlvbktleTIwM" +
                "TkiLCJjcmVhdGVkIjoiMjAyMC0wNi0yMlQxNjo0NTowMFoiLCJwcm9vZlB1cnBvc2UiOiJhc3NlcnRpb25NZXRob2QifX" +
                "19.AY4NRJdnb1GUpDdqrbXLupB9cctLwop/YwE9PA7hen7DyJsMh+AOt8x3CrIEss6MXhgsQcuW46sKiZiAIUP8538=";
        String jwtPresentation = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QVZlNHpWWnp0ZW82SG9McGRCd3BLTnREWExq" +
                "SkJ6QjlmdiNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiIiLCJqdGkiOiJ1cm46dXVpZDo4MDg3NTE5YS1iYThk" +
                "LTQ5ODUtYjc3Yi0wYTMxNGM3YmVjMDEiLCJpc3MiOiJkaWQ6b250OkFWZTR6Vlp6dGVvNkhvTHBkQndwS050RFhMakpCe" +
                "kI5ZnYiLCJuYmYiOjE1OTI4MTU1MzgsImlhdCI6MTU5MjgxNTUzOCwiZXhwIjowLCJhdWQiOlsiaHR0cHM6Ly9leGFtcG" +
                "xlLmNvbSJdLCJub25jZSI6IiIsInZwIjp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnR" +
                "pYWxzL3YxIiwiaHR0cHM6Ly9vbnRpZC5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlUHJl" +
                "c2VudGF0aW9uIiwiQ3JlZGVudGlhbE1hbmFnZXJQcmVzZW50YXRpb24iXSwidmVyaWZpYWJsZUNyZWRlbnRpYWwiOlsiZ" +
                "XlKaGJHY2lPaUpGVXpJMU5pSXNJbXRwWkNJNkltUnBaRHB2Ym5RNlFVbzBRemxoVkZsNFZFZFZhRVZ3WVZwa1VHcEdVM0" +
                "ZEY1hwTlEzRktSRkpWWkNOclpYbHpMVElpTENKMGVYQWlPaUpLVjFRaWZRPT0uZXlKemRXSWlPaUprYVdRNmIyNTBPakV" +
                "4TVRFeE1TSXNJbXAwYVNJNkluVnlianAxZFdsa09tTmhObU0xWm1ZMUxUbGtPRE10TkdNME1pMDVPR1ZqTFRRd1l6WXhP" +
                "VEZtTVdaaU55SXNJbWx6Y3lJNkltUnBaRHB2Ym5RNlFVbzBRemxoVkZsNFZFZFZhRVZ3WVZwa1VHcEdVM0ZEY1hwTlEzR" +
                "ktSRkpWWkNJc0ltNWlaaUk2TVRVNU1qZ3hOVFV3TUN3aWFXRjBJam94TlRreU9ERTFOVEF3TENKbGVIQWlPakUxT1RJNU" +
                "1ERTVNREFzSW5aaklqcDdJa0JqYjI1MFpYaDBJanBiSW1oMGRIQnpPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1ERTRMMk55Wld" +
                "SbGJuUnBZV3h6TDNZeElpd2lhSFIwY0hNNkx5OXZiblJwWkM1dmJuUXVhVzh2WTNKbFpHVnVkR2xoYkhNdmRqRWlYU3dp" +
                "ZEhsd1pTSTZXeUpXWlhKcFptbGhZbXhsUTNKbFpHVnVkR2xoYkNJc0lsSmxiR0YwYVc5dWMyaHBjRU55WldSbGJuUnBZV" +
                "3dpWFN3aVkzSmxaR1Z1ZEdsaGJGTjFZbXBsWTNRaU9uc2libUZ0WlNJNklrSnZZaUlzSW5Od2IzVnpaU0k2SWtGc2FXTm" +
                "xJbjBzSW1OeVpXUmxiblJwWVd4VGRHRjBkWE1pT25zaWFXUWlPaUkxTW1SbU16Y3dOamd3WkdVeE4ySmpOV1EwTWpZeVl" +
                "6UTBObVl4TURKaE1HVmxNR1EyTXpFeUlpd2lkSGx3WlNJNklrRjBkR1Z6ZEVOdmJuUnlZV04wSW4wc0luQnliMjltSWpw" +
                "N0luUjVjR1VpT2lKRlkyUnpZVk5sWTNBeU5UWnlNVlpsY21sbWFXTmhkR2x2Ymt0bGVUSXdNVGtpTENKamNtVmhkR1ZrS" +
                "WpvaU1qQXlNQzB3TmkweU1sUXhOam8wTlRvd01Gb2lMQ0p3Y205dlpsQjFjbkJ2YzJVaU9pSmhjM05sY25ScGIyNU5aWF" +
                "JvYjJRaWZYMTkuQVk0TlJKZG5iMUdVcERkcXJiWEx1cEI5Y2N0THdvcC9Zd0U5UEE3aGVuN0R5SnNNaCtBT3Q4eDNDckl" +
                "Fc3M2TVhoZ3NRY3VXNDZzS2laaUFJVVA4NTM4PSIsImV5SmhiR2NpT2lKRlV6STFOaUlzSW10cFpDSTZJbVJwWkRwdmJu" +
                "UTZRVW8wUXpsaFZGbDRWRWRWYUVWd1lWcGtVR3BHVTNGRGNYcE5RM0ZLUkZKVlpDTnJaWGx6TFRJaUxDSjBlWEFpT2lKS" +
                "1YxUWlmUT09LmV5SnFkR2tpT2lKMWNtNDZkWFZwWkRwbE1qRmpNREpoWVMxa1lUZzNMVFJrWldFdFlUZzVPUzA0WmpCaV" +
                "pERmlNREkxWVRNaUxDSnBjM01pT2lKa2FXUTZiMjUwT2tGS05FTTVZVlJaZUZSSFZXaEZjR0ZhWkZCcVJsTnhRM0Y2VFV" +
                "OeFNrUlNWV1FpTENKdVltWWlPakUxT1RJNE1UVTFNVGtzSW1saGRDSTZNVFU1TWpneE5UVXhPU3dpWlhod0lqb3hOVGt5" +
                "T1RBeE9UQXdMQ0oyWXlJNmV5SkFZMjl1ZEdWNGRDSTZXeUpvZEhSd2N6b3ZMM2QzZHk1M015NXZjbWN2TWpBeE9DOWpjb" +
                "VZrWlc1MGFXRnNjeTkyTVNJc0ltaDBkSEJ6T2k4dmIyNTBhV1F1YjI1MExtbHZMMk55WldSbGJuUnBZV3h6TDNZeElsMH" +
                "NJblI1Y0dVaU9sc2lWbVZ5YVdacFlXSnNaVU55WldSbGJuUnBZV3dpTENKU1pXeGhkR2x2Ym5Ob2FYQkRjbVZrWlc1MGF" +
                "XRnNJbDBzSW1semMzVmxjaUk2ZXlKdVlXMWxJam9pYVhOemRXVnlJbjBzSW1OeVpXUmxiblJwWVd4VGRXSnFaV04wSWpw" +
                "YmV5SnBaQ0k2SW1ScFpEcHZiblE2TVRFeE1URXhJaXdpYm1GdFpTSTZJbWhsSWl3aWMzQnZkWE5sSWpvaWMyaGxJbjFkT" +
                "ENKamNtVmtaVzUwYVdGc1UzUmhkSFZ6SWpwN0ltbGtJam9pTlRKa1pqTTNNRFk0TUdSbE1UZGlZelZrTkRJMk1tTTBORF" +
                "ptTVRBeVlUQmxaVEJrTmpNeE1pSXNJblI1Y0dVaU9pSkJkSFJsYzNSRGIyNTBjbUZqZENKOUxDSndjbTl2WmlJNmV5SjB" +
                "lWEJsSWpvaVJXTmtjMkZUWldOd01qVTJjakZXWlhKcFptbGpZWFJwYjI1TFpYa3lNREU1SWl3aVkzSmxZWFJsWkNJNklq" +
                "SXdNakF0TURZdE1qSlVNVFk2TkRVNk1UbGFJaXdpY0hKdmIyWlFkWEp3YjNObElqb2lZWE56WlhKMGFXOXVUV1YwYUc5a" +
                "0luMTlmUT09LkFjY2Q0dkJIZHR0R0R1aVFUa3BCVmtLak5SYzh6RWxCOUlvVmZXVjJCbkNjS3pHMGtOT1pjSW9YNldpVW" +
                "9acG0xWGFvL1lrTi9NUmdSTTloREJFR1B2ND0iXSwicHJvb2YiOnsidHlwZSI6IkVjZHNhU2VjcDI1NnIxVmVyaWZpY2F" +
                "0aW9uS2V5MjAxOSIsImNyZWF0ZWQiOiIyMDIwLTA2LTIyVDE2OjQ1OjM4WiIsImNoYWxsZW5nZSI6ImQxYjIzZDMuLi4z" +
                "ZDIzZDMyZDIiLCJkb21haW4iOlsiaHR0cHM6Ly9leGFtcGxlLmNvbSJdLCJwcm9vZlB1cnBvc2UiOiJhc3NlcnRpb25NZ" +
                "XRob2QifX19.AbQD8FTwRpNeOmzjsUbgeDVKthLHVykxsgCejA8TsHVrx1DhTvOt+K/MY05OsYPLY5iI5DcAoq5zsAzKY" +
                "eeSoWA=";
        JWTClaim jwtClaim1 = JWTClaim.deserializeToJWTClaim(jwtCredential);
        System.out.println(JSON.toJSONString(jwtClaim1));
        VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(jwtClaim1);
        System.out.println(JSON.toJSONString(credential));
        JWTClaim jwtClaim2 = JWTClaim.deserializeToJWTClaim(jwtPresentation);
        System.out.println(JSON.toJSONString(jwtClaim2));
        VerifiablePresentation presentation = VerifiablePresentation.deserializeFromJWT(jwtClaim2);
        System.out.println(JSON.toJSONString(presentation));
    }

    public static void testVerifyClaimSignature(OntSdk ontSdk) throws Exception {
        String jwtClaim = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcXpNQ3FKRFJ" +
                "VZCNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiJkaWQ6b250OjExMTExMSIsImp0aSI6InVybjp1dWlkOmNhNmM" +
                "1ZmY1LTlkODMtNGM0Mi05OGVjLTQwYzYxOTFmMWZiNyIsImlzcyI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpG" +
                "U3FDcXpNQ3FKRFJVZCIsIm5iZiI6MTU5MjgxNTUwMCwiaWF0IjoxNTkyODE1NTAwLCJleHAiOjE1OTI5MDE5MDAsInZjI" +
                "jp7IkBjb250ZXh0IjpbImh0dHBzOi8vd3d3LnczLm9yZy8yMDE4L2NyZWRlbnRpYWxzL3YxIiwiaHR0cHM6Ly9vbnRpZC" +
                "5vbnQuaW8vY3JlZGVudGlhbHMvdjEiXSwidHlwZSI6WyJWZXJpZmlhYmxlQ3JlZGVudGlhbCIsIlJlbGF0aW9uc2hpcEN" +
                "yZWRlbnRpYWwiXSwiY3JlZGVudGlhbFN1YmplY3QiOnsibmFtZSI6IkJvYiIsInNwb3VzZSI6IkFsaWNlIn0sImNyZWRl" +
                "bnRpYWxTdGF0dXMiOnsiaWQiOiI1MmRmMzcwNjgwZGUxN2JjNWQ0MjYyYzQ0NmYxMDJhMGVlMGQ2MzEyIiwidHlwZSI6I" +
                "kF0dGVzdENvbnRyYWN0In0sInByb29mIjp7InR5cGUiOiJFY2RzYVNlY3AyNTZyMVZlcmlmaWNhdGlvbktleTIwMTkiLC" +
                "JjcmVhdGVkIjoiMjAyMC0wNi0yMlQxNjo0NTowMFoiLCJwcm9vZlB1cnBvc2UiOiJhc3NlcnRpb25NZXRob2QifX19.AY" +
                "4NRJdnb1GUpDdqrbXLupB9cctLwop/YwE9PA7hen7DyJsMh+AOt8x3CrIEss6MXhgsQcuW46sKiZiAIUP8538=";
        OntId2 verifier = new OntId2("", null, ontSdk.neovm().claimRecord(), ontSdk.nativevm().ontId());
        System.out.println(verifier.verifyJWTClaimSignature(jwtClaim));
    }
}

@JSONType(orders = {"id", "name", "spouse"})
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