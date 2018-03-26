## **Overivew**

Relevant descriptions of digital id can be referred to [ONT ID Protocol and Trust Framework](https://git.ont.network/Ontology_Open_Platform/ontid).

## **Wallet file specification**

A wallet file is a JSON data storage file that stores multiple digital identities and digital asset accounts. 
You may refer to [Wallet File Specification](Wallet_File_Specification.md) for detailed information.

You need to create/open a wallet file to create a digital identity.

```
//If the wallet file does not exist, a wallet file will be auto-generated.
wm.openWalletFile("Demo3.json");
```
> Note: only wallet file in the operating file format is currently supported, with extended support of database or other storage methods.



## **Digital id account management**

### 1 **Data structure**

`ontid` a user’s only identity
`label` the name of a user id。
`lock` indicates whether the user’s id is locked, whose default value is false. Locked id info cannot get updated in the client.
`controls` the array of identity ControlData
`extra` the field that client developer stores extra information, whose value may be null
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

`algorithm` encryption algorithm
`parameters` the parameters used in the encryption algorithm
`curve` elliptic curve
`id` the single identifier of control
`key` NEP-2 private key
```
public class Control {
    public String algorithm = "ECDSA";
    public Map parameters = new HashMap() ;
    public String id = "";
    public String key = "";
}
```

### 2 **Create a digital identity**

Digital identity creation refers to generation of a digital identity with identity data structure and writing it to wallet file. 

```
Identity identity = ontSdk.getWalletMgr().createIdentity("password");
//The account or identity created is stored in the memory only and a write api is required to write it to the wallet file.
ontSdk.getWalletMgr().writeWallet();
```

### 3 **Register blockchain-based identity**

The identity cannot be put to use until being successfully registered on the blockchain.

```
ontSdk.getOntIdTx().register(identity,"passwordtest");
或
ontSdk.getOntIdTx().register("passwordtest");
```

Upon successful registration, the corresponding DDO of the ONT ID will be stored in Ontology blockchain. Detailed information about DDO can be found in [ONT ID identity protocol and smart contract implementation](https://git.ont.network/Ontology_Open_Platform/ontid/src/master/docs/en/ONTID_protocol_spec.md).


### 4 **Import account or identity**

Users who have already created a digital identity or account may import it into wallet file from SDK.

> **Note：** It is advised to check if an identity already exists on the blockchain before you import one. If DDO does not exist, it means that no such identity has been registered on the blockchain. Then you may need to use ontSdk.getOntIdTx().register(identity) for registration.

```
Identity identity = ontSdk.getWalletMgr().importIdentity("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
//write to wallet     
ontSdk.getWalletMgr().writeWallet();
```

### 5 **Query blockchain-based identity**

DDO of blockchain-based identity can be queried by entering ONT ID.
```
//get DDO by entering ONT ID
String ddo = ontSdk.getOntIdTx().getDDO(ontid,"passwordtest",ontid);

//return in DDO format
{
	"OntId": "did:ont:AMs5NFdXPgCgC7Dci1FdFttvD42HELoLxG",
	"Attributes": {
		"attri0": {
			"Type": "String",
			"Value": "\"value0\""
		}
	},
	"Owners": [
		{
			"Type": "ECDSA",
			"Value": "0392a4dbb2a44da81e0942cee1a62ff4298e04ed463b88911b97de19a1597fa83d"
		}
	]
}

```

### 6 **remove identity**
```
ontSdk.getWalletMgr().getWallet().removeIdentity(ontid);
//wrote to wallet
ontSdk.getWalletMgr().writeWallet();
```

### 7 **set default account or identity**
```
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(index);setDefaultAccount
ontSdk.getWalletMgr().getWallet().setDefaultIdentity("ontid");
```

### 8 **update blockchain-based DDO attribute**

```
//update an attribute
String updateAttribute(String ontid,String password,byte[] key,byte[] type,byte[] value)
```
| param   | field   | type  | descriptions |      remarks |
| ----- | ------- | ------ | ------------- | ----------- |
| input  | password| String | publisher's address | required, password to decrypt private key|
| param.  | ontid    | String | name of asset | required,ID |
|        | key    | byte[]  | key       | required,key |
|        | type    | byte[] | type     |  required,type |
|        | value   | byte[] | value     | required, value |
| output | txid   | String  | transaction  | 64-bit string |
  param.                      code


## **Verifiable claim**

### 1 **Data structure specification**

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

`unsignedData`  a JSON string of unsigned claim objects, including Context, Id, Claim and Metadata
`signedData` a JSON string of signed claim objects, including claim object and digitally signed object
`Context` identification of claim template
`Id` identification of claim object
`Claim` claim content
`Metadata` metadata of claim object

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
`createtime` the time the claim is created
`issuer` claim issuer
`subject` claim subject
`expires` expiry date of the claim
`revocation` revocation method of the claim
`crl` the link of claim revocation list


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
String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
System.out.println(claim);
```

### 3 Verify verifiable claim

```
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(dids.get(0).ontid,"passwordtest",claim);

```


### 4 Use cases


```
//register ontid
Identity ident = ontSdk.getOntIdTx().register("passwordtest");
String ontid = ident.ontid;
//update attribute
String hash = ontSdk.getOntIdTx().updateAttribute(ontid,"passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
```

Claim issuance and verification:
```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);

//Password is confidentially held by the issuer, who must be contained in wallet file ontid.
String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
System.out.println(claim);
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(ontid,"passwordtest",claim);
```

