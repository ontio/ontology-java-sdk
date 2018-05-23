<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# Overivew

Relevant descriptions of digital ID can be found in [ONT ID Protocol and Trust Framework](https://github.com/ontio/ontology-DID).

## Wallet file specification

A wallet file is a JSON data storage file that stores multiple digital identities and digital asset accounts. 
You may refer to [Wallet File Specification](Wallet_File_Specification.md) for detailed information.

You need to create/open a wallet file to create a digital identity.

```
//If the wallet file does not exist, a wallet file will be auto-generated.
wm.openWalletFile("Demo3.json");
```

> Note: Only wallet file in the operating file format is currently supported, with extended support of database or other storage methods.



## Digital ID account management

* 1 Data structure

`ontid` A user’s only identity  
`label` The name of a user ID  
`lock` Indicates whether the user’s ID is locked, whose default value is false. Locked id info cannot get updated in the client  
`controls` The array of identity ControlData  
`extra` The field that client developer stores extra information, whose value may be null

```
//Identity data structure
public class Identity {
	public String label = "";
	public String ontid = "";
	public boolean isDefault = false;
	public boolean lock = false;
	public List<Control> controls = new ArrayList<Control>();
}

```
`algorithm` Encryption algorithm  
`parameters` The parameters used in the encryption algorithm  
`curve` Elliptic curve  
`id` The single identifier of control  
`key` NEP-2 private key
```

public class Control {
    public String algorithm = "ECDSA";
    public Map parameters = new HashMap() ;
    public String id = "";
    public String key = "";
}
```

* 2 Create a digital identity


ontsdk instance init

```
String url = "http://127.0.0.1:20384";
OntSdk wm = OntSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setDefaultConnect(wm.getRestful());
wm.openWalletFile("InvokeSmartCodeDemo.json");
```

Digital identity creation refers to generation of a digital identity with identity data structure and writing it to wallet file. 

```
Identity identity = ontSdk.getWalletMgr().createIdentity("password");
//The account or identity created is stored in the memory only and a write api is required to write it to the wallet file.
ontSdk.getWalletMgr().writeWallet();
```

* 3 Register blockchain-based identity

Only after successfully registering an identity with the block chain can the identity be truly used.

There are two ways to register your identity with the chain

method one

Registrant specifies the account address for payment of transaction fees
```
Identity identity = ontSdk.getWalletMgr().createIdentity(password);
ontSdk.nativevm().ontId().sendRegister(identity,password,payer,payerpassword,gas);
```

method two

Send the constructed transaction to the server and let the server sign the transaction fee account.

```
Identity identity = ontSdk.getWalletMgr().createIdentity(password);
Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,serverAddress,0);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);

```

Upon successful registration, the corresponding DDO of the ONT ID will be stored in Ontology blockchain. Detailed information about DDO can be found in [ONT ID identity protocol and smart contract implementation](https://git.ont.network/Ontology_Open_Platform/ontid/src/master/docs/en/ONTID_protocol_spec.md).


* 4 Import account or identity

Users who have already created a digital identity or account may import it into a wallet file from SDK.

> **Note:** It is advised to check if an identity already exists on the blockchain before you import one. If DDO does not exist, it means that no such identity has been registered on the blockchain. Then you may need to use ontSdk.getOntIdTx().sendRegister(identity,"passwordtest") for registration.

```
Identity identity = ontSdk.getWalletMgr().importIdentity(encriptPrivateKey,password);
//write to wallet     
ontSdk.getWalletMgr().writeWallet();
```

Parameter Description：
encriptPrivateKey: Encrypted private key
password： Password used to encrypt the private key

* 5 Query blockchain-based identity

DDO of blockchain-based identity can be queried by entering ONT ID.

```
//get DDO by entering ONT ID
String ddo = ontSdk.nativevm().ontId().sendGetDDO(ontid);

//return in DDO format
{
	"Attributes": [{
		"Type": "String",
		"Value": "value1",
		"Key": "key1"
	}],
	"OntId": "did:ont:TA5UqF8iPqecMdBzTdzzANVeY8HW1krrgy",
	"Recovery": "TA6AhqudP1dcLknEXmFinHPugDdudDnMJZ",
	"Owners": [{
		"Type": "ECDSA",
		"Curve": "P256",
		"Value": "12020346f8c238c9e4deaf6110e8f5967cf973f53b778ed183f4a6e7571acd51ddf80e",
		"PubKeyId": "did:ont:TA5UqF8iPqecMdBzTdzzANVeY8HW1krrgy#keys-1"
	}, {
		"Type": "ECDSA",
		"Curve": "P256",
		"Value": "1202022fabd733d7d7d7009125bfde3cb0afe274769c78fd653079ecd5954ae9f52644",
		"PubKeyId": "did:ont:TA5UqF8iPqecMdBzTdzzANVeY8HW1krrgy#keys-2"
	}]
}
```

* 6 Remove identity

```
ontSdk.getWalletMgr().getWallet().removeIdentity(ontid);
//wrote to wallet
ontSdk.getWalletMgr().writeWallet();
```

* 7 Set default account or identity

```
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(index);
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(ontid);
```

* 8 Update blockchain-based DDO attribute

method one

specifies the account address for payment of transaction fees

```
//update an attribute
String sendAddAttributes(String ontid, String password, Map<String, Object> attrsMap,String payer,String payerpassword,long gas)
```

| Param   | Field   | Type  | Descriptions |      Remarks |
| ----- | ------- | ------ | ------------- | ----------- |
| input param | password| String | publisher's address | required, password to decrypt private key|
|   | ontid    | String | name of asset | required, ID |
|        | attrsMap    | Map  | attribute collection  | required |
|        | payer    | String | type     |  required |
|        | payerpassword   | String | payer password     | required|
|        | gas   | long | transaction fee | required |
| output param | txhash   | String  | transaction hash | 64-bit string |


method two

Send the constructed transaction to the server and let the server sign the transaction fee account.

```
Transaction tx = ontSdk.nativevm().ontId().makeAddAttributes(ontid,password,attrsMap,payer,0);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 9 Remove blockchain-based DDO attribute

method one

```
String hash = ontSdk.getOntIdTx().sendRemoveAttribute(String ontid,String password,String path,String payer,String payerpassword,long gas);
```

| Param        | Field   | Type   | Descriptions  |       Remarks       |
| -----        | ------- | ------ | ------------- | ------------------- |
| input param  | password| String | publisher's address | required, password to decrypt private key |
|   | ontid    | String | name of asset | required, ID |
|   | path    | String | path       | required |
|   | payer    | String  | payer       | required，payer |
|   | payerpassword | String  | account address used to pay transaction fee | required |
|   | gas   | long | transaction fee     | required |
| output param | txhash   | String  | transaction hash | 64-bit string |

method two

Send the constructed transaction to the server and let the server sign the transaction fee account.
```
Transaction tx = ontSdk.nativevm().ontId().makeRemoveAttribute(ontid,password,attrsMap,payer,0);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```


* add publicKey

method one

```
String sendAddPubKey(String ontid, String password, String newpubkey,String payer,String payerpassword,long gas)
```

| Param      | Field   | Type  | Descriptions |             Remarks |
| ----- | ------- | ------ | ------------- | ----------- |
| input param| password| String | identity password | required |
|        | ontid    | String | identity ID   | required，identity Id |
|        | newpubkey| String  |public key       | required， newpubkey|
|        | payer    | String  | payer       | required，payer |
|        | payerpassword | String  | account address used to pay transaction fee  | required |
|        | gas   | long |   transaction fee    | required |
| output param | txhash   | String  | transaction hash  | transaction hash |


method two

Send the constructed transaction to the server and let the server sign the transaction fee account.
```
Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(ontid,password,newpubkey,payer,gas);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 删除公钥

method one

```
String sendRemovePubKey(String ontid, String password, String removePubkey,String payer,String payerpassword,long gas)
```

| Param      | Field   | Type  | Descriptions |             Remarks |
| ----- | ------- | ------ | ------------- | ----------- |
| input param | password| String | identity password | required |
|        | ontid    | String | identity ID   | required，identity Id |
|        | removePubkey| String  |public key       | required， removePubkey|
|        | payer    | String  | payer       | required，payer |
|        | payerpassword | String  | account address used to pay transaction fee  | required |
|        | gas   | long | transaction fee | required |
| output param | txhash   | String  | transaction hash  | Transaction hash |


method two

Send the constructed transaction to the server and let the server sign the transaction fee account.
```
Transaction tx = ontSdk.nativevm().ontId().makeRemovePubKey(ontid,password,removePubkey,payer,gas);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* add recovery

method one

```
String sendAddRecovery(String ontid, String password, String recovery,String payer,String payerpassword,long gas)
```

| Param      | Field   | Type  | Descriptions |             Remarks |
| ----- | ------- | ------ | ------------- | ----------- |
| input param | password| String | identity password | required |
|        | ontid    | String | identity ID   | required，identity Id |
|        | recovery| String  |recovery address | required，recovery|
|        | payer    | String  | payer       | required，payer |
|        | payerpassword | String  | account address used to pay transaction fee  | required |
|        | gas   | long |transaction fee  | required |
| output param | txhash   | String  | transaction hash  | Transaction hash |


method two

Send the constructed transaction to the server and let the server sign the transaction fee account.

```
Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(ontid,password,recovery,payer,gas);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 修改recovery


```
String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gas)
```

| Param      | Field   | Type  | Descriptions |             Remarks |
| ----- | ------- | ------ | ------------- | ----------- |
| input param | password| String | identity password | required |
|        | ontid    | String | identity ID   | required，identity Id |
|        | newRecovery| String  |newRecovery address | required，newRecovery|
|        | oldRecovery| String  |oldRecovery address | required，oldRecovery|
|        | oldRecovery password | String  | oldRecovery password  | required |
|        | gas   | long |transaction fee  | required |
| output param | txhash   | String  | transaction hash  | transaction hash |




## Verifiable claim

### 1 Data structure specification

* Claim has the following data structure:

```
{
  unsignedData : string,  
  signedData : string,  
  context : string,  
  id : string,  
  claim : {},  
  metadata : Metadata,  
  signature : Signature
}

```

`unsignedData`  A JSON string of unsigned claim objects, including Context, Id, Claim, and Metadata  
`signedData` A JSON string of signed claim objects, including claim object and digitally signed object  
`Context` Identification of claim template  
`Id` Identification of claim object  
`Claim` Claim content  
`Metadata` Metadata of claim object  

* Metadata has the following data structure

```
{
  createTime : datetime string
  issuer : string,
  subject : string,
  expires : datetime string
  revocation : string,
  crl : string
}

```
`createtime` The time the claim is created  
`issuer` Claim issuer  
`subject` Claim subject  
`expires` Expiry date of the claim  
`revocation` Revocation method of the claim  
`crl` The link of claim revocation list


* Signature has the following data structure

```
{
    format : string,
    algorithm : string,
    value : string
}
format refers to signature format
algorithm represnets signature algorithm.
value refers to computed signature value
```

### 2 Sign and issue verifiable claim
Verifiable claim is constructed based on user input, which contains signed data.

```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);
String claim = ontSdk.getOntIdTx().createOntIdClaim(ontid,"passwordtest","claim:context",map,map);
System.out.println(claim);
```
> Note: The Issuer may have multiple public keys. The parameter ontid of createOntIdClaim specifies which public key to use.

### 3 Verify verifiable claim

```
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(dids.get(0).ontid,"passwordtest",claim);

```


### 4 Use cases


```
//register ontid
Identity ident = ontSdk.getOntIdTx().sendRegister("passwordtest");
String ontid = ident.ontid;
//update attribute
String hash = ontSdk.getOntIdTx().sendUpdateAttribute(ontid,"passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
```
> Note: When the attribute does not exist, calling the sendUpdateAttribute method will increase the corresponding attribute. When the attribute exists, the corresponding attribute will be updated. Attri represents the attribute name, "Json" is the attribute value data type, and recordMap represents the attribute value.

Claim issuance and verification:
```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);

//Password is confidentially held by the issuer, who must be contained in wallet file ontid.
String claim = ontSdk.getOntIdTx().createOntIdClaim(ontid,"passwordtest","claim:context",map,map);
System.out.println(claim);
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(claim);
```

