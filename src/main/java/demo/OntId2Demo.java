package demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
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
//            testVerifyClaimSignature(ontSdk);
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
        VerifiableCredential verifiableCredential2 = issuer.createClaim(credential.context, credential.type,
                exampleIssuer, new ExampleCredentialSubject[]{otherCredentialSubject}, expiration,
                CredentialStatusType.AttestContract,
                ProofPurpose.assertionMethod);
        String jwt2 = issuer.createJWTClaim(credential.context, credential.type,
                exampleIssuer, new ExampleCredentialSubject[]{otherCredentialSubject}, expiration,
                CredentialStatusType.AttestContract,
                PubKeyType.EcdsaSecp256r1Signature2019);
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
        // you can use any ontId as otherSigner if you want
        VerifiablePresentation presentation = owner.createPresentation(
                new VerifiableCredential[]{verifiableCredential, verifiableCredential2},
                presentationContext, presentationType, ownerIdentity.ontid, new OntIdSigner[]{},
                ProofPurpose.assertionMethod);
        System.out.println("presentation: " + JSON.toJSONString(presentation));
        // the 2 credential has no jws
//        String jwtPresentation1 = owner.createJWTPresentation(
//                new VerifiableCredential[]{verifiableCredential, verifiableCredential2},
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
        String jwtCredential = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcXpNQ3F" +
                "KRFJVZCNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJqdGkiOiJ1cm46dXVpZDpiMGFhM2NiYS0zODU5LTQ2MDMtOGY1Yy0z" +
                "ZTE1MmM2MmE3NmUiLCJpc3MiOiJkaWQ6b250OkFKNEM5YVRZeFRHVWhFcGFaZFBqRlNxQ3F6TUNxSkRSVWQiLCJuYmYiOi" +
                "IxNTkyNzkzMzQwIiwiaWF0IjoiMTU5Mjc5MzM0MCIsImV4cCI6IjE1OTI4Nzk3MjEiLCJ2YyI6eyJAY29udGV4dCI6WyJo" +
                "dHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vb250aWQub250LmlvL2NyZWRlbnRpYW" +
                "xzL3YxIl0sInR5cGUiOlsiVmVyaWZpYWJsZUNyZWRlbnRpYWwiLCJSZWxhdGlvbnNoaXBDcmVkZW50aWFsIl0sImNyZWRl" +
                "bnRpYWxTdWJqZWN0IjpbeyJpZCI6ImRpZDpvbnQ6MTExMTExIiwibmFtZSI6ImhlIiwic3BvdXNlIjoic2hlIn1dLCJjcm" +
                "VkZW50aWFsU3RhdHVzIjp7ImlkIjoiNTJkZjM3MDY4MGRlMTdiYzVkNDI2MmM0NDZmMTAyYTBlZTBkNjMxMiIsInR5cGUi" +
                "OiJBdHRlc3RDb250cmFjdCJ9LCJpc3N1ZXIiOnsibmFtZSI6Imlzc3VlciJ9fX0=.ASGQOoD3qpWPBQFb81izUx2UxqjMC" +
                "zrESYmA/b62fCcjT0++OC1AnwwnoIetQmNGW80sSSaj42KEnUtB1Gy+5WY=";
        String jwtPresentation = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QVZlNHpWWnp0ZW82SG9McGRCd3BLTnREWExqS" +
                "kJ6QjlmdiNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJzdWIiOiIiLCJqdGkiOiJ1cm46dXVpZDo2MzZlNzdkOS01ZGY4LT" +
                "Q3NTQtYTM1My02ZDUyMWFmYjUzNzkiLCJpc3MiOiJkaWQ6b250OkFWZTR6Vlp6dGVvNkhvTHBkQndwS050RFhMakpCekI5" +
                "ZnYiLCJ2cCI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi" +
                "8vb250aWQub250LmlvL2NyZWRlbnRpYWxzL3YxIl0sInR5cGUiOlsiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsIkNyZWRl" +
                "bnRpYWxNYW5hZ2VyUHJlc2VudGF0aW9uIl0sInZlcmlmaWFibGVDcmVkZW50aWFsIjpbImV5SmhiR2NpT2lKRlV6STFOaU" +
                "lzSW10cFpDSTZJbVJwWkRwdmJuUTZRVW8wUXpsaFZGbDRWRWRWYUVWd1lWcGtVR3BHVTNGRGNYcE5RM0ZLUkZKVlpDTnJa" +
                "WGx6TFRJaUxDSjBlWEFpT2lKS1YxUWlmUT09LmV5SnpkV0lpT2lKa2FXUTZiMjUwT2pFeE1URXhNU0lzSW1wMGFTSTZJbl" +
                "Z5YmpwMWRXbGtPbU13T1dOaU56VTRMV1UwTVdJdE5HSTVNUzA1T0dFd0xXWXhPRGd4WkRVME5EZGxPU0lzSW1semN5STZJ" +
                "bVJwWkRwdmJuUTZRVW8wUXpsaFZGbDRWRWRWYUVWd1lWcGtVR3BHVTNGRGNYcE5RM0ZLUkZKVlpDSXNJbTVpWmlJNklqRT" +
                "FPVEkzT1RNek1qRWlMQ0pwWVhRaU9pSXhOVGt5Tnprek16SXhJaXdpWlhod0lqb2lNVFU1TWpnM09UY3lNU0lzSW5aaklq" +
                "cDdJa0JqYjI1MFpYaDBJanBiSW1oMGRIQnpPaTh2ZDNkM0xuY3pMbTl5Wnk4eU1ERTRMMk55WldSbGJuUnBZV3h6TDNZeE" +
                "lpd2lhSFIwY0hNNkx5OXZiblJwWkM1dmJuUXVhVzh2WTNKbFpHVnVkR2xoYkhNdmRqRWlYU3dpZEhsd1pTSTZXeUpXWlhK" +
                "cFptbGhZbXhsUTNKbFpHVnVkR2xoYkNJc0lsSmxiR0YwYVc5dWMyaHBjRU55WldSbGJuUnBZV3dpWFN3aVkzSmxaR1Z1ZE" +
                "dsaGJGTjFZbXBsWTNRaU9uc2libUZ0WlNJNklrSnZZaUlzSW5Od2IzVnpaU0k2SWtGc2FXTmxJbjBzSW1OeVpXUmxiblJw" +
                "WVd4VGRHRjBkWE1pT25zaWFXUWlPaUkxTW1SbU16Y3dOamd3WkdVeE4ySmpOV1EwTWpZeVl6UTBObVl4TURKaE1HVmxNR1" +
                "EyTXpFeUlpd2lkSGx3WlNJNklrRjBkR1Z6ZEVOdmJuUnlZV04wSW4xOWZRPT0uQWZHODRTcUtuOFFFZUNKWWc3bzRabkpt" +
                "NXBma0RWeVVGa1RSTmlxM2krS2ZkTW94dGdDaFh5aHVDbUlXd05UY0NHblVJT3dHTzNPVGZ0NUZVOFBRWUpJPSIsImV5Sm" +
                "hiR2NpT2lKRlV6STFOaUlzSW10cFpDSTZJbVJwWkRwdmJuUTZRVW8wUXpsaFZGbDRWRWRWYUVWd1lWcGtVR3BHVTNGRGNY" +
                "cE5RM0ZLUkZKVlpDTnJaWGx6TFRJaUxDSjBlWEFpT2lKS1YxUWlmUT09LmV5SnFkR2tpT2lKMWNtNDZkWFZwWkRwaU1HRm" +
                "hNMk5pWVMwek9EVTVMVFEyTURNdE9HWTFZeTB6WlRFMU1tTTJNbUUzTm1VaUxDSnBjM01pT2lKa2FXUTZiMjUwT2tGS05F" +
                "TTVZVlJaZUZSSFZXaEZjR0ZhWkZCcVJsTnhRM0Y2VFVOeFNrUlNWV1FpTENKdVltWWlPaUl4TlRreU56a3pNelF3SWl3aW" +
                "FXRjBJam9pTVRVNU1qYzVNek0wTUNJc0ltVjRjQ0k2SWpFMU9USTROemszTWpFaUxDSjJZeUk2ZXlKQVkyOXVkR1Y0ZENJ" +
                "Nld5Sm9kSFJ3Y3pvdkwzZDNkeTUzTXk1dmNtY3ZNakF4T0M5amNtVmtaVzUwYVdGc2N5OTJNU0lzSW1oMGRIQnpPaTh2Yj" +
                "I1MGFXUXViMjUwTG1sdkwyTnlaV1JsYm5ScFlXeHpMM1l4SWwwc0luUjVjR1VpT2xzaVZtVnlhV1pwWVdKc1pVTnlaV1Js" +
                "Ym5ScFlXd2lMQ0pTWld4aGRHbHZibk5vYVhCRGNtVmtaVzUwYVdGc0lsMHNJbU55WldSbGJuUnBZV3hUZFdKcVpXTjBJan" +
                "BiZXlKdVlXMWxJam9pYUdVaUxDSnBaQ0k2SW1ScFpEcHZiblE2TVRFeE1URXhJaXdpYzNCdmRYTmxJam9pYzJobEluMWRM" +
                "Q0pqY21Wa1pXNTBhV0ZzVTNSaGRIVnpJanA3SW1sa0lqb2lOVEprWmpNM01EWTRNR1JsTVRkaVl6VmtOREkyTW1NME5EWm" +
                "1NVEF5WVRCbFpUQmtOak14TWlJc0luUjVjR1VpT2lKQmRIUmxjM1JEYjI1MGNtRmpkQ0o5TENKcGMzTjFaWElpT25zaWJt" +
                "RnRaU0k2SW1semMzVmxjaUo5ZlgwPS5BU0dRT29EM3FwV1BCUUZiODFpelV4MlV4cWpNQ3pyRVNZbUEvYjYyZkNjalQwKy" +
                "tPQzFBbnd3bm9JZXRRbU5HVzgwc1NTYWo0MktFblV0QjFHeSs1V1k9Il19fQ==.AZfjHNlf1CoBwCfUoegDYgYNjfm3dJO" +
                "g/8zWXGT1/HJzi70FzYNHPT5FESwqBcMYKpZKOZusVR3kJ54/ORYuKxI=";
        JWTClaim jwtClaim1 = JWTClaim.deserializeToJWTClaim(jwtCredential);
        System.out.println(JSON.toJSONString(jwtClaim1));
        VerifiableCredential credential = VerifiableCredential.deserializeFromJWT(jwtClaim1, null);
        System.out.println(JSON.toJSONString(credential));
        JWTClaim jwtClaim2 = JWTClaim.deserializeToJWTClaim(jwtPresentation);
        System.out.println(JSON.toJSONString(jwtClaim2));
        VerifiablePresentation presentation = VerifiablePresentation.deserializeFromJWT(jwtClaim2, null);
        System.out.println(JSON.toJSONString(presentation));
    }

    public static void testVerifyClaimSignature(OntSdk ontSdk) throws Exception {
        String jwtClaim = "eyJhbGciOiJFUzI1NiIsImtpZCI6ImRpZDpvbnQ6QUo0QzlhVFl4VEdVaEVwYVpkUGpGU3FDcXpNQ3FKRFJVZCNrZXlzLTIiLCJ0eXAiOiJKV1QifQ==.eyJqdGkiOiJ1cm46dXVpZDozZWI0M2JkYy1jNWNjLTQ3NmYtOGRmYi03YzFhNzNlOGQ3NmEiLCJpc3MiOiJkaWQ6b250OkFKNEM5YVRZeFRHVWhFcGFaZFBqRlNxQ3F6TUNxSkRSVWQiLCJuYmYiOiIxNTkyNzk1NjEwIiwiaWF0IjoiMTU5Mjc5NTYxMCIsImV4cCI6IjE1OTI4ODE5OTEiLCJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vb250aWQub250LmlvL2NyZWRlbnRpYWxzL3YxIl0sInR5cGUiOlsiVmVyaWZpYWJsZUNyZWRlbnRpYWwiLCJSZWxhdGlvbnNoaXBDcmVkZW50aWFsIl0sImlzc3VlciI6eyJuYW1lIjoiaXNzdWVyIn0sImNyZWRlbnRpYWxTdWJqZWN0IjpbeyJpZCI6ImRpZDpvbnQ6MTExMTExIiwibmFtZSI6ImhlIiwic3BvdXNlIjoic2hlIn1dLCJjcmVkZW50aWFsU3RhdHVzIjp7ImlkIjoiNTJkZjM3MDY4MGRlMTdiYzVkNDI2MmM0NDZmMTAyYTBlZTBkNjMxMiIsInR5cGUiOiJBdHRlc3RDb250cmFjdCJ9fX0=.AUHdTiMxaS1voZDB1B3Hthh4DPi7qXBzfvccbOuVY2CsSVo8vonn2dUwVwqAQlFaeoxyektAfXL3Zb5Bw/5Xvko=";
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