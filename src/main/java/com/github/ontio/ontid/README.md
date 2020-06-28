# ONTID 2.0 Java SDK

## Role

owner: credential owner, who hold some credentials and create presentation;

issuer: credential issuer, who issue credential;

verifier: credential verifier, who verify credential and presentation.

## Class Specification

There are two formats of verifiable credential, `JSON-LD` and `JWT`.

Refer: https://www.w3.org/TR/vc-data-model/#basic-concepts
Refer: https://www.w3.org/TR/vc-data-model/#json-web-token

### JSON-LD Format

#### CredentialStatus

[code](./VerifiableCredential.java#L30)

Refer: https://www.w3.org/TR/vc-data-model/#status

There are two type credential status now, `AttestContract` and `RevocationList`.

If `CredentialStatus.type` equals `AttestContract`, `CredentialStatus.id` should be `CredentialRecord` contract address.

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

#### JWTCredential

[code](./jwt/JWTCredential.java)

The verifiable credential with JWT format.

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

Credential owner send `SignRequest` to issuer to create credential.

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

OntId2.CredentialRecord: credential record contract;

OntId2.OntId: ontId contract.

## API

There are many interfaces to use ONTID 2.0 protocol.

### Constructor and Updater

1. **public OntId2(String ontId, Account signer, CredentialRecord credRecord, OntId ontIdContract)**

    Generate OntId2 object.

    If `ontId` not empty and `signer` not null, there will query `OntIdPubKey` from ontology chain and use it as self signer.
    
    So owner and issuer should set `ontId` and `signer` while create OntId2 object, and verifier may not set those.

2. **public void updateOntIdAndSigner(String ontId, Account signer)**

    Update `ontId` and `signer` account.

### GenSignRequest and VerifySignRequest

1. **public SignRequest genSignReq(Object credentialSubject, ProofPurpose proofPurpose, boolean hasSignature)**

    * generate `SignRequest` object, calculate its' hash;
    * if hasSignature, use self `signer` to sign hash;
    * return SignRequest object.

2. **public boolean verifySignReq(SignRequest req)**

    * use `req.ontId` to verify `req.signature` of `req`.

### Create Credential

Issuer create credential.

1. **public VerifiableCredential createCred(String[] context, String[] type, Object issuer,
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
    * credentialStatusType: only use `AttestContract` and `RevocationList` at current;
    * proofPurpose: only use `assertionMethod` at current;
    
    return: `VerifiableCredential`

2. **public String createJWTCred(String[] context, String[] type, Object issuer, Object credentialSubject,
                                      Date expiration, CredentialStatusType statusType, ProofPurpose purpose)**
   
   comment:
   
   * parse params to JWT header and payload;
   * generate jws.
   
   param: same with above.
   
   return: JWT credential.

### Commit Credential

Issuer commit credential hash to ontology chain.

1. **public String commitCred(VerifiableCredential cred, String ownerOntId, Account payer, long gasLimit,
                               long gasPrice, OntSdk sdk)**

    comment: issuer invoke this method to commit a verifiable credential(JSON-LD) to ontology chain, issuer should use self
     ontId to sign this transaction.
    
    param:
    
    * cred: a instance of verifiable credential;
    * ownerOntId: ontId of credential owner;
    * payer: transaction payer;
    * gasLimit & gasPrice: transaction gas limit and price;
    * sdk: a instance of OntSdk.
    
    return: transaction hash.

2. **public String commitCred(String cred, String ownerOntId, Account payer,
                                   long gasLimit, long gasPrice, OntSdk sdk)**

    comment: issuer invoke this method to commit a verifiable credential(JWT) to ontology chain, issuer should use self
                  ontId to sign this transaction.

    param: `cred` is JWT verifiable credential, the others params are same with the former.
    
3. **public String commitCredById(String credId, String ownerOntId, Account payer,
                                       long gasLimit, long gasPrice, OntSdk sdk)**
                                       
    comment: commit a credential to ontology chain.
    
    param: `credId` is identification of credential, others param are same with above.

### Verify Credential

Verifier could verify credential.

The process consists of 4 aspects: `verifyCredOntIdCredible`, `verifyCredNotExpired`, `verifyCredSignature`, `verifyCredNotRevoked`.

1. **public boolean verifyCred(String[] credibleOntIds, VerifiableCredential cred)**

    comment: Verify credential from four aspects.
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * cred: instance of `VerifiableCredential`.
    
    return: boolean value of whether credential valid or invalid.

2. **public boolean verifyJWTCred(String[] credibleOntIds, String cred)**

    comment: verify JWT credential;
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * cred: `cred` is JWT format of verifiable credential.
    
    return: boolean value of whether credential valid or invalid.

#### Verify Credential OntId Is Credible

1. **public boolean verifyCredOntIdCredible(String[] credibleOntIds, VerifiableCredential cred)**

    comment:
    
    * check `cred.proof.verificationMethod` is `cred.issuer`;
    * check `cred.proof.verificationMethod` is existed in `credibleOntIds`.
    
    param:
    
    * credibleOntIds: credible ONTID list, similar to trust certificate;
    * cred: a instance of `VerifiableCredential`.
    
    return: boolean.

2. **public boolean verifyJWTCredOntIdCredible(String[] credibleOntIds, String cred)**

    comment: verify `cred.payload.iss` existed in credibleOntIds.
    
    param: `cred` is JWT format of verifiable credential.
    
    return: boolean.
    
#### Verify Credential Not Expired

1. **public boolean verifyCredDate(VerifiableCredential cred)**

    comment: check credential not expired, check credential issuance date before current.
    
    param: `cred` is an instance of `VerifiableCredential`.
        
    return: boolean.

2. **public boolean verifyJWTCredDate(String cred)**

    comment: check `cred.payload.exp` not expired, check `cred.payload.iat` and `cred.payload.nbf` before current.
        
    param: `cred` is JWT format of verifiable credential.
            
    return: boolean.

#### Verify Credential Signature Is Valid

1. **public boolean verifyCredSignature(VerifiableCredential cred)**

    comment:
    
    * check `cred.proof.verificationMethod` is `cred.issuer`;
    * use `cred.proof.verificationMethod` as public key to verify `cred.proof`.
    
    param: `cred` is an instance of `VerifiableCredential`.
        
    return: boolean.
    
2. **public boolean verifyJWTCredSignature(String cred)**

    comment: verify JWT credential signature valid.
    
    param: `cred` is JWT format of verifiable credential.
    
    return: value.

#### Verify Credential Has Not Been Revoked

1. **public boolean verifyCredNotRevoked(VerifiableCredential cred)**

    comment:
    
    * query status of credential from `CredentialRecord` contract that identified by `cred.credentialStatus.id` if 
    `cred.credentialStatus.type == AttestContract`;
    * check status equals `01`.
    
    param:
    
    * cred: a instance of `VerifiableCredential`.
    
    return: boolean.

2. **public boolean verifyJWTCredNotRevoked(String cred)**

    comment:
    
    * deserialize `cred` to `JWTCredential` instance;
    * query status of credential from `CredentialRecord` contract that identified by `cred.credentialStatus.id` if 
    `cred.credentialStatus.type == AttestContract`;
    * check status equals `01`.
    
    param: `cred` is JWT format of verifiable credential.
    
    return: boolean value.

### Create Presentation

Refer: https://www.w3.org/TR/vc-data-model/#presentations-0.

Owner could create presentation by using one or multi `VerifiableCredential`.

1. **public VerifiablePresentation createPresentation(VerifiableCredential[] creds, String[] context,
                                                          String[] type, List`<`String`>` challenge,
                                                          List`<`Object`>` domain, Object holder,
                                                          OntIdSigner[] otherSigners, ProofPurpose proofPurpose)**

    comment:
    
    * create `VerifiablePresentation` object;
    * use self signer to generate first proof;
    * use `otherSigners` to generate other proofs.
    
    param:
    
    * creds: array instance of verifiable credential;
    * context: refer w3c definition;
    * type: refer w3c definition;
    * challenge: each proof should contain a challenge to prevent replay attack;
    * domain: like challenge;
    * holder: maybe an ontId of String type or an object that has "id" attribute and "id" must be ontId;
    * otherSigners: presentation could own multi proof, so there would be other signer to sign presentation;
    * proofPurpose: only use `assertionMethod` at current;
    
    return: instance of `VerifiablePresentation`.
    
2. **public String createJWTPresentation(String[] creds, String[] context, String[] type, Object holder,
                                                String challenge, Object domain, String nonce, ProofPurpose purpose)**
                                             
    comment: use multi `VerifiableCredential` instance to create a JWT credential.
    
    param:
    
    * creds: `creds` is array of JWT `VerifiableCredential`. Others params are same with above;
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
    
    * parse `presentation` to `JWTCredential`;
    * verify each `JWTCredential.vc`;
    * verify `JWTCredential.jws`.
    
    param:
    
    * credibleOntIds: array of credible ontId;
    * presentation: a JWT string of `VerifiablePresentation`.
    
    return: boolean value.

### Revoke Credential

Because the SDK used by different roles, so we encapsulate a credential revoking interface. User would use these interface at different circumstance.

1. **public String revokeCred(VerifiableCredential cred, Account payer,
                                   long gasLimit, long gasPrice, OntSdk sdk)**

    comment: revoke a credential by an instance of `VerifiableCredential`.
    
    param:
    
    * cred: an instance of `VerifiableCredential`;
    * payer: transaction payer;
    * gasLimit & gasPrice: transaction param;
    * sdk: an instance of `OntSdk`;
    
    return: transaction hash of revoking credential.
    
2. **public String revokeCredById(String credId, Account payer, long gasLimit, long gasPrice,
                                       OntSdk sdk)**

    comment: revoke credential by credential id.
    
    param: `credId`  is identification of credential, others param are same with above;
    
    return: transaction hash of revoking credential.
    
3. **public String revokeJWTCred(String cred, Account payer, long gasLimit, long gasPrice,
                                      OntSdk sdk)**
                                      
    comment: revoke credential by JWT format credential.
    
    * parse credential to `JWTCredential` instance;
    * use `JWTCredential.payload.jti` to revoke this credential;
    
    param: `cred` is a JWT format of `VerifiableCredential` or `VerifiablePresentation`. Others param are same with above.
    
    return: transaction hash of revoking credential.

### Remove Credential

1. **public String removeCredById(String credId, Account payer, long gasLimit, long gasPrice, OntSdk sdk)**

    comment: remove credential by credential id.
        
    param: `credId`  is identification of credential, other params are ontology transaction param;
        
    return: transaction hash of remove credential.

2. **public String removeJWTCred(String cred, Account payer, long gasLimit, long gasPrice, OntSdk sdk)**

    comment: remove a JWT credential.
        
    param: `cred`  is JWT format of credential, other params are ontology transaction param;
        
    return: transaction hash of remove credential.

### Transition between JSON-LD and JWT

We also provide some interface to parse verifiable credential between the format of JSON-LD and JWT.

#### Parse JSON-LD to JWT

1. **public JWTCredential(VerifiableCredential credential)**

    comment:
    
    * check `credential.proof.jws` is empty;
    * parse `jwt.header`;
    * parse `jwt.payload`;
    * retrieve `credential.proof.jws`.
    
    param: `credential` is an instance of `VerifiableCredential`.

    return: an instance of `JWTCredential`.
    
2. **public JWTCredential(VerifiablePresentation presentation, Proof proof, String nonce)**

    comment: Because there maybe many proof in presentation, you must specify the proof instance when you want to parse 
    `VerifiablePresentation` instance to JWT.
    
    param:
    
    * presentation: an instance of `VerifiablePresntation`;
    * proof: an instance of `Proof`;
    * nonce: refer w3c specification;
    deserializeToJWTCred
    return: an instance of `JWTCredential` that represent `VerifiablePresntation`.

3. **public static JWTCredential deserializeToJWTCred(String jwt)**

    comment: deserialize a jwt string to `JWTCredential` object.
    
    param: a JWT format string of `VerifiableCredential` or `VerifiablePresentation`.
    
    return: an instance of `JWTCredential`.

#### Parse JWT to JSON-LD

1. **public static VerifiableCredential deserializeFromJWT(JWTCredential cred)**

    comment: parse `JWTCredential` to `VerifiableCredential`;
    
    param: `cred` is an instance of `JWTCredential`;
    
    return: an instance of `VerifiableCredential`.
    
2. **public static VerifiablePresentation deserializeFromJWT(JWTCredential cred)**

    comment: parse `JWTCredential` to `VerifiablePresentation`;
    
    param: `cred` is an instance of `JWTCredential`;
    
    return: an instance of `VerifiableCredential`.

## Credential Record Contract

[code](../smartcontract/neovm/CredentialRecord.java)

OntId 2.0 use a new version CredentialRecord contract. There are 4 new interface.

### Commit

**public String sendCommit2(String issuerOntid, String password, byte[] salt, String subjectOntid, String credId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice)**

Used to commit a credential id to ontology chain.

### Revoke

**public String sendRevoke2(String ownerId, String password, byte[] salt, String credId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice)**

Used to revoke a recorded credential id.

### Remove

**public String sendRemove2(String ownerId, String password, byte[] salt, String credId, int pubkeyIndex, Account payerAcct, long gaslimit, long gasprice)**

Used to remove a recorded credential id.

### GetStatus

**public String sendGetStatus2(String credId)**

Used to query credential status by cred id, there are 3 return value:

* "00": revoked.
* "01": committed;
* "02": removed;

## Compatibility

Considering that there are a lot of old version of credential, the protocol upgrade must be compatible with old data. The main
difference between the new and old protocol is in the form of credential.

The old ONTID protocol use [OntId](../smartcontract/nativevm/OntId.java) to generate and verify credential. However, the new
use [OntId2](./OntId2.java) to generate and verify credential. 

In the other hands, we also provide the methods that verify old credential by new CredentialRecord contract:

* **public boolean verifyCredOntIdCredible(String cred, String[] credibleIds)**
* **public boolean verifyCredNotExpired(String cred)**
* **public boolean verifyCredSignature(String cred)**
* **public boolean verifyCredNotRevoked(String cred)**

See these code at [here](../smartcontract/nativevm/OntId.java#L2186-L2258).

## How to Use

The [document](./how%20to%20use.md) is a simple introduction about how to use ONT ID 2.0.

There are some [code](../../../../demo/OntId2Demo.java) to illustrate how to use ONTID 2.0 sdk.