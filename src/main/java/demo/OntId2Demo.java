package demo;

import com.alibaba.fastjson.JSON;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.ontid.*;
import com.github.ontio.sdk.wallet.Identity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OntId2Demo {
    public static void main(String[] args) {
        try {
            long gasLimit = 2000000;
            long gasPrice = 500;
            OntSdk ontSdk = ClaimRecordTxDemo.getOntSdk();
            // set claim contract address
            ontSdk.neovm().claimRecord().setContractAddress("52df370680de17bc5d4262c446f102a0ee0d6312");
            String password = "passwordtest";
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
            credential.context = new String[]{"https://www.w3.org/2018/credentials/v1",
                    "https://www.w3.org/2018/credentials/examples/v1"};
            credential.type = new String[]{"VerifiableCredential", "RelationshipCredential"};
            ExampleCredentialSubject credentialSubject = new ExampleCredentialSubject("did:ont:111111",
                    "Bob", "Alice");
            credential.credentialSubject = new ExampleCredentialSubject[]{credentialSubject};
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            credential.expirationDate = formatter.format(expiration);
            String claim = JSON.toJSONString(credential);
            // generate a sign request
            SignRequest req = owner.genSignReq(claim);
            // issuer verify sign request
            if (!issuer.verifySignReq(req)) {
                System.out.println("sign request not verified");
                return;
            }
            // create claim after verified
            // the parameters that used to create claim should be unmarshal from signRequest.claim
            // for convenient, using those field at here
            VerifiableCredential verifiableCredential = issuer.createClaim(credential.context, credential.type,
                    credential.credentialSubject, expiration);
            // for debug, print verifiableCredential
            System.out.println("verifiableCredential: " + JSON.toJSONString(verifiableCredential));
            // commit claim to blcokchain
            String commitClaimHash = issuer.commitClaim(verifiableCredential, ownerIdentity.ontid, payer, gasLimit,
                    gasPrice, ontSdk);
            System.out.println("commit claim: " + verifiableCredential.id + ", txHash: " + commitClaimHash);
            Thread.sleep(6000);
            // verify claim
            // user should own self credible ontIds, not only use issuerIdentity.ontid and ownerIdentity.ontid
            String[] credibleOntIds = new String[]{issuerIdentity.ontid, ownerIdentity.ontid};
            boolean claimVerified = verifier.verifyClaim(credibleOntIds, verifiableCredential);
            if (!claimVerified) {
                System.out.println("claim not verified");
                return;
            }
            // create other VerifiableCredential to create presentation
            ExampleCredentialSubject otherCredentialSubject = new ExampleCredentialSubject("did:ont:222222",
                    "he", "she");
            VerifiableCredential otherVerifiableCredential = issuer.createClaim(credential.context, credential.type,
                    new ExampleCredentialSubject[]{otherCredentialSubject}, expiration);
            System.out.println("otherVerifiableCredential: " + JSON.toJSONString(otherVerifiableCredential));
            String otherCommitClaimHash = issuer.commitClaim(otherVerifiableCredential, ownerIdentity.ontid, payer,
                    gasLimit, gasPrice, ontSdk);
            System.out.println("commit claim: " + otherVerifiableCredential.id + ", txHash: " + otherCommitClaimHash);
            Thread.sleep(6000);
            // create presentation
            String[] presentationContext = new String[]{"https://www.w3.org/2018/credentials/v1",
                    "https://www.w3.org/2018/credentials/examples/v1"};
            String[] presentationType = new String[]{"VerifiablePresentation", "CredentialManagerPresentation"};
            // you can use any ontId as otherSigner if you want
            OntIdSigner otherSigner = new OntIdSigner(issuerIdentity.ontid,
                    issuerIdentity.ontid + "#keys-2", issuerSigner);
            VerifiablePresentation presentation = owner.createPresentation(
                    new VerifiableCredential[]{verifiableCredential, otherVerifiableCredential},
                    presentationContext, presentationType, new OntIdSigner[]{otherSigner}, ownerIdentity.ontid);
            System.out.println("presentation: " + JSON.toJSONString(presentation));
            // verify presentation
            boolean presentationVerified = verifier.verifyPresentation(presentation, credibleOntIds);
            System.out.println("presentation verify: " + presentationVerified);
        } catch (Exception e) {
            e.printStackTrace();
        }
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