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

There are two type credential status now, `AttestContract` and `RevocationList`.

If `CredentialStatus.type` equals `AttestContract`, `CredentialStatus.id` should be `ClaimRecord` contract address.

#### VerifiableCredential

[code](./VerifiableCredential.java)

Refer: https://www.w3.org/TR/vc-data-model/#basic-concepts

VerifiableCredential.issuer: issuer ontId.

#### Proof

[code](./Proof.java)

Refer: https://www.w3.org/TR/vc-data-model/#proofs-signatures

Proof.type: an instance of `PubKeyType`;

ProofPurpose: only use `assertionMethod` at currently;

Proof.verificationMethod: pubkey uri, like:`did:ont:AVe4zVZzteo6HoLpdBwpKNtDXLjJBzB9fv#keys-2`;

Proof.hex: hex-encoded ontology signature;

Proof.jws: jws-encoded ontology signature;

#### VerifiablePresentation

[code](./VerifiablePresentation.java)

Refer: https://www.w3.org/TR/vc-data-model/#presentations-0

VerifiablePresentation.holder: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId.

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

#### PubKeyType

[code](./PubKeyType.java)

OntId controller pubkey type.

#### ALG

[code](./jwt/ALG.java)

Defined `jwt.header.alg`. Each `PubKeyType` has a corresponding `ALG`, each ALG has {algorithm type, curve type, hash method}.

The corresponding relationship is as follows:

| PubKeyType | ALG | Algorithm | Curve | Hash Method |
|------------|-----|-----------|-------|-------------|
| EcdsaSecp224r1VerificationKey2019 | ES224 | ECDSA | P-224 | SHA-224 |
| EcdsaSecp256r1VerificationKey2019 | ES256 | ECDSA | P-256 | SHA-256 |
| EcdsaSecp384r1VerificationKey2019 | ES384 | ECDSA | P-384 | SHA-384 |
| EcdsaSecp521r1VerificationKey2019 | ES512 | ECDSA | P-521 | SHA-512 |
| EcdsaSecp256k1VerificationKey2019 | ES256K | ECDSA | secp256k1 | SHA-256 |
| Ed25519VerificationKey2018 | EdDSA | EDDSA | Curve25519 | SHA-256 |
| SM2VerificationKey2019 | SM | SM2 | SM2P256V1 | SM3 |

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

## API

There are many interfaces to use ONTID 2.0 protocol.

### Constructor and Updater

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

1. **public VerifiableCredential createClaim(String[] context, String[] type, Object issuer,
                                                 Object credentialSubject, Date expiration,
                                                 CredentialStatusType credentialStatusType,
                                                 ProofPurpose proofPurpose)**

    comment:

    * create VerifiableCredential object;
    * check `expiration`;
    * check `issuer` equals self signer ontId;
    * generate `Proof`.

    param:
    
    * context: refer w3c definition;
    * type: refer w3c definition;
    * issuer: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId;
    * credentialSubject: refer w3c definition;
    * expiration: refer w3c definition;
    * credentialStatusType: only use `ClaimContract` and `RevocationList` at current;
    * proofPurpose: only use `assertionMethod` at current;
    
    return: `VerifiableCredential`

2. **public String createJWTClaim(String[] context, String[] type, Object issuer, Object credentialSubject,
                                      Date expiration, CredentialStatusType statusType, ProofPurpose purpose)**
   
   comment:
   
   * parse params to JWT header and payload;
   * generate jws.
   
   param: same with above.
   
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
    
3. **public String commitClaimById(String claimId, String ownerOntId, Account payer,
                                       long gasLimit, long gasPrice, OntSdk sdk)**
                                       
    comment: commit a claim to ontology chain.
    
    param: `claimId` is identification of claim, others param are same with above.

### Verify Claim

Verifier could verify claim.

The process consists of 4 aspects: `verifyClaimOntIdCredible`, `verifyClaimNotExpired`, `verifyClaimSignature`, `verifyClaimNotRevoked`.

1. **public boolean verifyClaim(String[] credibleOntIds, VerifiableCredential claim)**

    comment: Verify claim from four aspects.
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * claim: instance of `VerifiableCredential`.
    
    return: boolean value of whether claim valid or invalid.

2. **public boolean verifyJWTClaim(String[] credibleOntIds, String claim)**

    comment: verify JWT claim;
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * claim: `claim` is JWT format of verifiable claim.
    
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

1. **public boolean verifyClaimDate(VerifiableCredential claim)**

    comment: check claim not expired, check claim issuance date before current.
    
    param: `claim` is an instance of `VerifiableCredential`.
        
    return: boolean.

2. **public boolean verifyJWTClaimDate(String claim)**

    comment: check `claim.payload.exp` not expired, check `claim.payload.iat` and `claim.payload.nbf` before current.
        
    param: `claim` is JWT format of verifiable claim.
            
    return: boolean.

#### Verify Claim Signature Is Valid

1. **public boolean verifyClaimSignature(VerifiableCredential claim)**

    comment:
    
    * check `claim.proof.verificationMethod` is `claim.issuer`;
    * use `claim.proof.verificationMethod` as public key to verify `claim.proof`.
    
    param: `claim` is an instance of `VerifiableCredential`.
        
    return: boolean.
    
2. **public boolean verifyJWTClaimSignature(String claim)**

    comment: verify JWT claim signature valid.
    
    param: `claim` is JWT format of verifiable claim.
    
    return: value.

#### Verify Claim Has Not Been Revoked

1. **public boolean verifyClaimNotRevoked(VerifiableCredential claim)**

    comment:
    
    * query status of claim from `ClaimRecord` contract that identified by `claim.credentialStatus.id` if 
    `claim.credentialStatus.type == AttestContract`;
    * check status equals `01`.
    
    param:
    
    * claim: a instance of `VerifiableCredential`.
    
    return: boolean.

2. **public boolean verifyJWTClaimNotRevoked(String claim)**

    comment:
    
    * deserialize `claim` to `JWTClaim` instance;
    * query status of claim from `ClaimRecord` contract that identified by `claim.credentialStatus.id` if 
    `claim.credentialStatus.type == AttestContract`;
    * check status equals `01`.
    
    param: `claim` is JWT format of verifiable claim.
    
    return: boolean value.

### Create Presentation

Refer: https://www.w3.org/TR/vc-data-model/#presentations-0.

Owner could create presentation by using one or multi `VerifiableCredential`.

1. **public VerifiablePresentation createPresentation(VerifiableCredential[] claims, String[] context,
                                                          String[] type, List<String> challenge,
                                                          List<Object> domain, Object holder,
                                                          OntIdSigner[] otherSigners, ProofPurpose proofPurpose)**

    comment:
    
    * create `VerifiablePresentation` object;
    * use self signer to generate first proof;
    * use `otherSigners` to generate other proofs.
    
    param:
    
    * claims: array instance of verifiable claim;
    * context: refer w3c definition;
    * type: refer w3c definition;
    * challenge: each proof should contain a challenge to prevent replay attack;
    * domain: like challenge;
    * holder: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId;
    * otherSigners: presentation could own multi proof, so there would be other signer to sign presentation;
    * proofPurpose: only use `assertionMethod` at current;
    
    return: instance of `VerifiablePresentation`.
    
2. **public String createJWTPresentation(String[] claims, String[] context, String[] type, Object holder,
                                                String challenge, Object domain, String nonce, ProofPurpose purpose)**
                                             
    comment: use multi `VerifiableCredential` instance to create a JWT claim.
    
    param:
    
    * claims: `claims` is array of JWT `VerifiableCredential`. Others params are same with above;
    * context: same with above;
    * type: same with above;
    * challenge: JWT presentation only has one jws, so there only need one challenge;
    * domain: like challenge;
    * holder: same with above;
    * nonce: refer w3c specification;
    * proofPurpose: only use `assertionMethod` at current;
    
    return: a JWT format of `VerifiablePresentation`.

### Verify Presentation

Verifier could verify presentation.

1. **public boolean verifyPresentationProof(VerifiablePresentation presentation, int proofIndex)**

    comment: check proof at `presentation.proof[proofIndex]` is valid.

    param:
    
    * presentation: an instance of `VerifiablePresentation`;
    * proofIndex: the index of destination proof at `presentation.proof`.
    
    return: boolean value.
    
2. **public boolean verifyJWTPresentation(String[] credibleOntIds, String presentation)**

    comment:
    
    * parse `presentation` to `JWTClaim`;
    * verify each `JWTClaim.vc`;
    * verify `JWTClaim.jws`.
    
    param:
    
    * credibleOntIds: array of credible ontId;
    * presentation: a JWT string of `VerifiablePresentation`.
    
    return: boolean value.

### Revoke Claim

Because the SDK used by different roles, so we encapsulate a claim revoking interface. User would use these interface at different circumstance.

1. **public String revokeClaim(VerifiableCredential claim, Account payer,
                                   long gasLimit, long gasPrice, OntSdk sdk)**

    comment: revoke a claim by an instance of `VerifiableCredential`.
    
    param:
    
    * claim: an instance of `VerifiableCredential`;
    * payer: transaction payer;
    * gasLimit & gasPrice: transaction param;
    * sdk: an instance of `OntSdk`;
    
    return: transaction hash of revoking claim.
    
2. **public String revokeClaimById(String claimId, Account payer, long gasLimit, long gasPrice,
                                       OntSdk sdk)**

    comment: revoke claim by claim id.
    
    param: `claimId`  is identification of claim, others param are same with above;
    
    return: transaction hash of revoking claim.
    
3. **public String revokeJWTClaim(String claim, Account payer, long gasLimit, long gasPrice,
                                      OntSdk sdk)**
                                      
    comment: revoke claim by JWT format claim.
    
    * parse claim to `JWTClaim` instance;
    * use `JWTClaim.payload.jti` to revoke this claim;
    
    param: `claim` is a JWT format of `VerifiableCredential` or `VerifiablePresentation`. Others param are same with above.
    
    return: transaction hash of revoking claim.
    
### Transition between JSON-LD and JWT

We also provide some interface to parse verifiable credential between the format of JSON-LD and JWT.

#### Parse JSON-LD to JWT

1. **public JWTClaim(VerifiableCredential credential)**

    comment:
    
    * check `credential.proof.jws` is empty;
    * parse `jwt.header`;
    * parse `jwt.payload`;
    * retrieve `credential.proof.jws`.
    
    param: `credential` is an instance of `VerifiableCredential`.

    return: an instance of `JWTClaim`.
    
2. **public JWTClaim(VerifiablePresentation presentation, Proof proof, String nonce)**

    comment: Because there maybe many proof in presentation, you must specify the proof instance when you want to parse 
    `VerifiablePresentation` instance to JWT.
    
    param:
    
    * presentation: an instance of `VerifiablePresntation`;
    * proof: an instance of `Proof`;
    * nonce: refer w3c specification;
    
    return: an instance of `JWTClaim` that represent `VerifiablePresntation`.

3. **public static JWTClaim deserializeToJWTClaim(String jwt)**

    comment: deserialize a jwt string to `JWTClaim` object.
    
    param: a JWT format string of `VerifiableCredential` or `VerifiablePresentation`.
    
    return: an instance of `JWTClaim`.

#### Parse JWT to JSON-LD

1. **public static VerifiableCredential deserializeFromJWT(JWTClaim claim)**

    comment: parse `JWTClaim` to `VerifiableCredential`;
    
    param: `claim` is an instance of `JWTClaim`;
    
    return: an instance of `VerifiableCredential`.
    
2. **public static VerifiablePresentation deserializeFromJWT(JWTClaim claim)**

    comment: parse `JWTClaim` to `VerifiablePresentation`;
    
    param: `claim` is an instance of `JWTClaim`;
    
    return: an instance of `VerifiableCredential`.

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