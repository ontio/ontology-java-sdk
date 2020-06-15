## GenSignReq

comment: use ontId to sign claim

params: String claim, String ontId, Account signer
return: Request

## CreateClaim

comment: issuer create claim and commit claimId to blockchain

params: contexts []string, types []string, credentialSubject interface{}, issuerId string, ownerOntId string,
 expirationDateTimestamp int64, signer *Account
 
return: VerifiableCredential
 
## VerifyClaim

comment: must verify 4 aspects

### verify credible ontId;

params: String[] credibleOntIds, VerifiableCredential claim
return: boolean

### verify not expired

params: VerifiableCredential claim
return: boolean

### verify issuer signature

params: VerifiableCredential claim
return: boolean

### verify status

params: VerifiableCredential claim
return: boolean

## CreatePresentation

comment: use ontId to sign presentation, should query signer public key index of signer.

params: VerifiableCredential[] claim, string[] context, string[] type, String signerOntId, Account[] signers
return: VerifiablePresentation

## Verify Presentation

params: VerifiablePresentation presentation, String[] credibleOntIds
return: boolean

1. verify each claim;
2. verify presentation proof;