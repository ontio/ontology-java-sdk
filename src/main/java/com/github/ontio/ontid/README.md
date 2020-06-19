# ONTID 2.0 Java SDK

## Role

owner: claim owner, who hold some claims and create presentation;

issuer: claim issuer, who issue claim;

verifier: claim verifier, who verify claim and presentation.

## Class Specification

There are two formats of verifiable claim, `JSON-LD` and `JWT`.

Refer: https://www.w3.org/TR/vc-data-model/#basic-concepts
Refer: https://www.w3.org/TR/vc-data-model/#json-web-token

### JSON-LD Format

#### CredentialStatus

[code](./VerifiableCredential.java#L30)

Refer: https://www.w3.org/TR/vc-data-model/#status

CredentialStatus.id: claim contract address;

CredentialStatus.type: constant value, "Claim Contract".

#### VerifiableCredential

[code](./VerifiableCredential.java)

Refer: https://www.w3.org/TR/vc-data-model/#basic-concepts

VerifiableCredential.issuer: issuer ontId.

#### Proof

[code](./Proof.java)

Refer: https://www.w3.org/TR/vc-data-model/#proofs-signatures

Proof.type: only use "EcdsaSecp256r1Signature2019" at currently;

ProofPurpose: only use "assertionMethod" at currently;

Proof.verificationMethod: pubkey uri, like:"did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv#keys-2";

Proof.hex: hex-encoded ontology signature;

Proof.jws: jws-encoded ontology signature;

#### VerifiablePresentation

[code](./VerifiablePresentation.java)

Refer: https://www.w3.org/TR/vc-data-model/#presentations-0

VerifiablePresentation.holder: holder ontId.

### JWT Format

#### JWTClaim

[code](./jwt/JWTClaim.java)

The verifiable claim with JWT format.

#### JWTHeader

[code](./jwt/JWTHeader.java)

Refer: https://www.w3.org/TR/vc-data-model/#example-30-jwt-header-of-a-jwt-based-verifiable-presentation-non-normative

The JWT header.

#### JWTPayload

[code](./jwt/JWTPayload.java)

Refer: https://www.w3.org/TR/vc-data-model/#example-31-jwt-payload-of-a-jwt-based-verifiable-presentation-non-normative

The JWT payload.

#### JWTVC

[code](./jwt/JWTVC.java)

Refer: https://www.w3.org/TR/vc-data-model/#json-web-token-extensions

The all attributes of `VerifiableCredential` that not contained in JWT payload should be defined in `JWTVC`.

#### JWTVP

[code](./jwt/JWTVP.java)

Refer: https://www.w3.org/TR/vc-data-model/#json-web-token-extensions

The all attributes of `VerifiablePresentation` that not contained in JWT payload should be defined in `JWTVP`.

### Util Class

There are some utility class.

#### SignRequest

[code](./SignRequest.java)

Claim owner send `SignRequest` to issuer to create claim.

#### OntIdPubKey

[code](./OntIdPubKey.java)

It's a utility class, represent one public key of ontId.

#### OntIdSigner

[code](./OntIdSigner.java)

It's a utility class, represent ontId signer.

#### ALG

[code](./jwt/ALG.java)

Defined `jwt.header.alg`.

#### Util

[code](./Util.java)

Providing some static method.

### SDK

The OntId2 class is ONTID 2.0 protocol SDK class.

#### OntId2

[code](./OntId2.java)

It's ONTID2 SDK class, all ontId 2.0 function should entry from here.

OntId2.ClaimRecord: claim contract;

OntId2.OntId: ontId contract.

## Interface

There are many interfaces to use ONTID 2.0 protocol.

### Constructor

1. **public OntId2(String ontId, Account signer, ClaimRecord claimRecord, OntId ontIdContract)**

    Generate OntId2 object.

    If `ontId` not empty and `signer` not null, there will query `OntIdPubKey` from ontology chain and use it as self signer.
    
    So owner and issuer should set `ontId` and `signer` while create OntId2 object, and verifier may not set those.

2. **public void updateOntIdAndSigner(String ontId, Account signer)**

    Update `ontId` and `signer` account.

### GenSignRequest and VerifySignRequest

1. **public SignRequest genSignReq(String claim, boolean hasSignature)**

    * calculate claim sha256 bytes;
    * if hasSignature, use self `signer` to sign hash;
    * return SignRequest object.

2. **public boolean verifySignReq(SignRequest req)**

    * use `req.ontId` to verify `req.signature` of `req.claim`.

### Create Claim

Issuer create claim.

1. **public VerifiableCredential createClaim(String[] context, String[] type,
                                             Object credentialSubject, Date expiration,
                                             CredentialStatusType credentialStatusType,
                                             PubKeyType pubKeyType,
                                             ProofPurpose proofPurpose)**

    comment:

    * create VerifiableCredential object;
    * check `expiration`;
    * generate `Proof`;
    * calculate `VerifiableCredential.id`.

    param:
    
    * context: refer w3c definition;
    * type: refer w3c definition;
    * credentialSubject: refer w3c definition;
    * expiration: refer w3c definition;
    * credentialStatusType: only use `ClaimContract` at current;
    * pubKeyType: only use `EcdsaSecp256r1Signature2019` at current;
    * proofPurpose: only use `assertionMethod` at current;
    
    return: `VerifiableCredential`

2. **public String createJWTClaim(String[] context, String[] type, Object credentialSubject,
                                    Date expiration, CredentialStatusType statusType,
                                    PubKeyType pubKeyType)**
   
   comment:
   
   * parse params to JWT header and payload;
   * generate jws.
   
   param: no need `proofPurpose` param, the others are same with the former.
   
   return: JWT claim.

### Commit Claim

Issuer commit claim hash to ontology chain.

1. **public String commitClaim(VerifiableCredential claim, String ownerOntId, Account payer, long gasLimit,
                               long gasPrice, OntSdk sdk)**

    comment: issuer invoke this method to commit a verifiable claim(JSON-LD) to ontology chain, issuer should use self
     ontId to sign this transaction.
    
    param:
    
    * claim: a instance of verifiable credential;
    * ownerOntId: ontId of claim owner;
    * payer: transaction payer;
    * gasLimit & gasPrice: transaction gas limit and price;
    * sdk: a instance of OntSdk.
    
    return: transaction hash.

2. **public String commitClaim(String claim, String ownerOntId, Account payer,
                                   long gasLimit, long gasPrice, OntSdk sdk)**

    comment: issuer invoke this method to commit a verifiable claim(JWT) to ontology chain, issuer should use self
                  ontId to sign this transaction.

    param: `claim` is JWT verifiable claim, the others params are same with the former.

### Verify Claim

Verifier could verify claim.

The process consists of 4 aspects: `verifyClaimOntIdCredible`, `verifyClaimNotExpired`, `verifyClaimSignature`, `verifyClaimNotRevoked`.

1. **public boolean verifyClaim(String[] credibleOntIds, VerifiableCredential claim)**

    comment: Verify claim from four aspects. If signature of claim.proof is `jws`, this function will parse claim to `JWT` and then verify JWT signature.
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * claim: instance of `VerifiableCredential`.
    
    return: boolean value of whether claim valid or invalid.

2. **public boolean verifyJWTClaim(String[] credibleOntIds, String claim)**

    comment: verify JWT claim;
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * claim: instance of `VerifiableCredential`.
    
    return: boolean value of whether claim valid or invalid.

#### Verify Claim OntId Is Credible

1. **public boolean verifyClaimOntIdCredible(String[] credibleOntIds, VerifiableCredential claim)**

    comment:
    
    * check `claim.proof.verificationMethod` is `claim.issuer`;
    * check `claim.proof.verificationMethod` is existed in `credibleOntIds`.
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * claim: a instance of `VerifiableCredential`.
    
    return: boolean.

2. **public boolean verifyJWTClaimOntIdCredible(String[] credibleOntIds, String claim)**

    comment: verify `claim.payload.iss` existed in credibleOntIds.
    
    param: `claim` is JWT format of verifiable claim.
    
    return: boolean.
    
#### Verify Claim Not Expired

1. **public boolean verifyClaimNotExpired(VerifiableCredential claim)**

    comment: check claim not expired.
    
    param: `claim` is an instance of `VerifiableCredential`.
        
    return: boolean.

2. **public boolean verifyJWTClaimNotExpired(String claim)**

    comment: check `claim.payload.exp` not expired.
        
    param: `claim` is JWT format of verifiable claim.
            
    return: boolean.

#### Verify Claim Signature Is Valid
3. **public boolean verifyClaimSignature(VerifiableCredential claim)**

    comment:
    
    * check `claim.proof.verificationMethod` is `claim.issuer`;
    * use `claim.proof.verificationMethod` as public key to verify `claim.proof`.
    
    param: `claim` is JWT format of verifiable claim.
        
    return: boolean.

4. **public boolean verifyClaimNotRevoked(VerifiableCredential claim)**

    comment:
    
    * query status of claim;
    * check status equals `01`.
    
    param:
    
    * claim: a instance of `VerifiableCredential`.
    
    return: boolean.

### Create Presentation

Refer: https://www.w3.org/TR/vc-data-model/#presentations-0.

Owner could create presentation by using one or multi `VerifiableCredential`.

1. **public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context, String[] type, OntIdSigner[] otherSigners, String holderOntId)**

    comment:
    
    * create `VerifiablePresentation` object;
    * use self signer to generate first proof;
    * use `otherSigners` to generate other proofs;
    * calculate id.
    
    param:
    
    * claims: array instance of verifiable claim;
    * context: refer w3c definition;
    * type: refer w3c definition;
    * otherSigners: presentation could own multi proof, so there would be other signer to sign presentation;
    * holderOntId: ontId of presentation holder.
    
    return: instance of `VerifiablePresentation`.

### Verify Presentation

Verifier could verify presentation.

**public boolean verifyPresentationProof(VerifiablePresentation presentation, int proofIndex)**

* check proof of `presentation`.


## Claim Record Contract

[code](../smartcontract/neovm/ClaimRecord.java)

OntId 2.0 use a new version ClaimRecord contract. There are 4 new interface.

### Commit

**public String sendCommit2(String issuerOntid, String password, byte[] salt, String subjectOntid, String claimId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice)**

Used to commit a claim id to ontology chain.

### Revoke

**public String sendRevoke2(String ownerId, String password, byte[] salt, String claimId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice)**

Used to revoke a recorded claim id.

### Remove

**public String sendRemove2(String ownerId, String password, byte[] salt, String claimId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice)**

Used to remove a recorded claim id.

### GetStatus

**public String sendGetStatus2(String claimId)**

Used to query claim status by claim id, there are 3 return value:

* "00": revoked.
* "01": committed;
* "02": removed;

## Compatibility

Considering that there are a lot of old version of claim, the protocol upgrade must be compatible with old data. The main
difference between the new and old protocol is in the form of claim.

The old ONTID protocol use [OntId](../smartcontract/nativevm/OntId.java) to generate and verify claim. However, the new
use [OntId2](./OntId2.java) to generate and verify claim. 

In the other hands, we also provide the methods that verify old claim by new ClaimRecord contract:

* **public boolean verifyClaimOntIdCredible(String claim, String[] credibleIds)**
* **public boolean verifyClaimNotExpired(String claim)**
* **public boolean verifyClaimSignature(String claim)**
* **public boolean verifyClaimNotRevoked(String claim)**

See these code at [here](../smartcontract/nativevm/OntId.java#L2186-L2258).

## Demo

There are some code to illustrate how to use ONTID 2.0 sdk.

[code](../../../../demo/OntId2Demo.java)