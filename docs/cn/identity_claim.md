<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 总体介绍

数字身份相关介绍可参考[ONT ID 身份标识协议及信任框架](https://github.com/ontio/ontology-DID)。

## 钱包文件及规范

钱包文件是一个Json格式的数据存储文件，可同时存储多个数字身份和多个数字资产账户。具体参考[钱包文件规范](Wallet_File_Specification.md)。

为了创建数字身份，您首先需要创建/打开一个钱包文件。


```
//如果不存在钱包文件，会自动创建钱包文件。
wm.openWalletFile("Demo3.json");
```
> 注：wm表示OntSdk实例，目前仅支持操作文件形式钱包文件，也可以扩展支持数据库或其他存储方式。



## 数字身份账户管理
   
```
* 1 数据结构说明

`ontid` 是代表身份的唯一的id
`label` 是用户给身份所取的名称。
`lock` 表明身份是否被用户锁定了。客户端不能更新被锁定的身份信息。默认值为false。
`controls` 是身份的所有控制对象ControlData的数组。
`extra` 是客户端开发者存储额外信息的字段。可以为null。
```
//Identity数据结构
public class Identity {
	public String label = "";
	public String ontid = "";
	public boolean isDefault = false;
	public boolean lock = false;
	public List<Control> controls = new ArrayList<Control>();
	public  Object extra = null;
}
```

`algorithm`是用来加密的算法名称。
`parameters` 是加密算法所需参数。
`curve` 是椭圆曲线的名称。
`id` 是control的唯一标识。
`key` 是NEP-2格式的私钥。
```
public class Control {
    public String algorithm = "ECDSA";
    public Map parameters = new HashMap() ;
    public String id = "";
    public String key = "";
}
```

* 2 创建数字身份

ontsdk实例初始化

```
String url = "http://127.0.0.1:20384";
OntSdk wm = OntSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setDefaultConnect(wm.getRestful());
wm.openWalletFile("InvokeSmartCodeDemo.json");
```

创建数字身份指的是产生一个Identity数据结构的身份信息，并写入到到钱包文件中。

```
Identity identity = ontSdk.getWalletMgr().createIdentity("passwordtest");
//创建的账号或身份只在内存中，如果要写入钱包文件，需调用写入接口
ontSdk.getWalletMgr().writeWallet();
```

* 3 向链上注册身份

只有向区块链链成功注册身份之后，该身份才可以真正使用。

有两种方法实现向链上注册身份

方法一

注册者指定支付交易费用的账户地址
```
Identity identity = ontSdk.getWalletMgr().createIdentity(password);
ontSdk.nativevm().ontId().sendRegister(identity,password,payer,payerpassword,gas);
```

方法二

将构造好的交易发送给服务器，让服务器进行交易费用账号的签名操作。

```
Identity identity = ontSdk.getWalletMgr().createIdentity(password);
Transaction tx = ontSdk.nativevm().ontId().makeRegister(identity.ontid,password,serverAddress,0);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);

```

链上注册成功后，对应此ONT ID的身份描述对象DDO将被存储在本体区块链上。关于DDO的信息可以从[ONT ID 身份标识协议及智能合约实现说明](https://git.ont.network/Ontology_Open_Platform/ontid/src/master/docs/en/ONTID_protocol_spec.md)详细了解。


* 4 导入账号或身份

当用户已经拥有了一个数字身份或者数字账户，SDK支持将其导入到钱包文件中。

> Note: 建议导入一个数字身份之前，建议查询链上身份，如果链上身份DDO不存在，表示此数字身份未在链上注册，请使用上面注册数字身份的方法把身份注册到链上。

```
Identity identity = ontSdk.getWalletMgr().importIdentity(encriptPrivateKey,password);
//写入钱包      
ontSdk.getWalletMgr().writeWallet();
```

参数说明：
encriptPrivateKey: 加密后的私钥
password： 加密私钥使用的密码

* 5 查询链上身份

链上身份DDO信息，可以通过ONT ID进行查询。

```
//通过ONT ID获取DDO
String ddo = ontSdk.nativevm().ontId().sendGetDDO(ontid);

//返回DDO格式
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

* 6 移除身份
```
ontSdk.getWalletMgr().getWallet().removeIdentity(ontid);
//写入钱包
ontSdk.getWalletMgr().writeWallet();
```

* 7 设置默认账号或身份
```
//根据账户地址设置默认账户
ontSdk.getWalletMgr().getWallet().setDefaultAccount(address);
//根据identity索引设置默认identity
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(index);
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(ontid);
```

* 8 更新链上DDO属性

方法一

指定支付交易费用的账户
```
//添加或者更新属性
String sendAddAttributes(String ontid, String password, Map<String, Object> attrsMap,String payer,String payerpassword,long gas)
```

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 数字身份密码 | 必选，私钥解密的密码 |
|        | ontid    | String | 数字身份id  | 必选，身份Id |
|        | attrsMap | Map  | 属性map集合       | 必选 |
|        | payer    | String | 交易费用支付者账户地址       |  必选， |
|        | payerpassword   | String | payer密码     | 必选 |
|        | gas   | long | 支付的交易费用     | 必选 |
| 输出参数 | txhash   | String  | 交易hash  | 交易hash是64位字符串 |


方法二

将构造好的交易发送给服务器，让服务器进行交易费用账号的签名操作。

```
Transaction tx = ontSdk.nativevm().ontId().makeAddAttributes(ontid,password,attrsMap,payer,0);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 9 移除链上DDO属性

方法一
```
String hash = ontSdk.getOntIdTx().sendRemoveAttribute(String ontid,String password,String path,String payer,String payerpassword,long gas);
```

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 数字身份密码 | 必选 |
|        | ontid    | String | 数字身份ID   | 必选，身份Id |
|        | path    | byte[]  | path       | 必选，path |
|        | payer    | String  | payer       | 必选，payer |
|        | payerpassword | String  | 支付交易费用的账户地址  | 必选 |
|        | gas   | long | 支付的交易费用     | 必选 |
| 输出参数 | txhash   | String  | 交易hash  | 交易hash是64位字符串 |


方法二

将构造好的交易发送给服务器，让服务器进行交易费用账号的签名操作。
```
Transaction tx = ontSdk.nativevm().ontId().makeRemoveAttribute(ontid,password,attrsMap,payer,0);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 添加公钥

方法一

```
String sendAddPubKey(String ontid, String password, String newpubkey,String payer,String payerpassword,long gas)
```

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 数字身份密码 | 必选 |
|        | ontid    | String | 数字身份ID   | 必选，身份Id |
|        | newpubkey| String  |公钥       | 必选， newpubkey|
|        | payer    | String  | payer       | 必选，payer |
|        | payerpassword | String  | 支付交易费用的账户地址  | 必选 |
|        | gas   | long | 支付的交易费用     | 必选 |
| 输出参数 | txhash   | String  | 交易hash  | 交易hash是64位字符串 |


方法二

将构造好的交易发送给服务器，让服务器进行交易费用账号的签名操作。
```
Transaction tx = ontSdk.nativevm().ontId().makeAddPubKey(ontid,password,newpubkey,payer,gas);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 删除公钥

方法一

```
String sendRemovePubKey(String ontid, String password, String removePubkey,String payer,String payerpassword,long gas)
```

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 数字身份密码 | 必选 |
|        | ontid    | String | 数字身份ID   | 必选，身份Id |
|        | removePubkey| String  |公钥       | 必选， removePubkey|
|        | payer    | String  | payer       | 必选，payer |
|        | payerpassword | String  | 支付交易费用的账户地址  | 必选 |
|        | gas   | long | 支付的交易费用     | 必选 |
| 输出参数 | txhash   | String  | 交易hash  | 交易hash是64位字符串 |


方法二

将构造好的交易发送给服务器，让服务器进行交易费用账号的签名操作。
```
Transaction tx = ontSdk.nativevm().ontId().makeRemovePubKey(ontid,password,removePubkey,payer,gas);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 添加recovery

方法一

```
String sendAddRecovery(String ontid, String password, String recovery,String payer,String payerpassword,long gas)
```

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 数字身份密码 | 必选 |
|        | ontid    | String | 数字身份ID   | 必选，身份Id |
|        | recovery| String  |recovery账户地址 | 必选，recovery|
|        | payer    | String  | payer       | 必选，payer |
|        | payerpassword | String  | 支付交易费用的账户地址  | 必选 |
|        | gas   | long | 支付的交易费用     | 必选 |
| 输出参数 | txhash   | String  | 交易hash  | 交易hash是64位字符串 |


方法二

将构造好的交易发送给服务器，让服务器进行交易费用账号的签名操作。
```
Transaction tx = ontSdk.nativevm().ontId().makeAddRecovery(ontid,password,recovery,payer,gas);
ontSdk.signTx(tx,identity.ontid.replace(Common.didont,""),password);
ontSdk.getConnectMgr().sendRawTransaction(tx);
```

* 修改recovery

```
String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gas)
```

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 数字身份密码 | 必选 |
|        | ontid    | String | 数字身份ID   | 必选，身份Id |
|        | newRecovery| String  |newRecovery账户地址 | 必选，newRecovery|
|        | oldRecovery| String  |oldRecovery账户地址 | 必选，oldRecovery|
|        | oldRecovery password | String  | oldRecovery password  | 必选 |
|        | gas   | long | 支付的交易费用     | 必选 |
| 输出参数 | txhash   | String  | 交易hash  | 交易hash是64位字符串 |



## 可信申明

### 1 数据结构和规范

具体标准请参考https://github.com/kunxian-xia/ontology-DID/blob/master/docs/en/claim_spec.md

java-sdk采用JSON Web Token的格式表示claim以便于在申明发行者和申请者之间进行传递，jwt格式包含三部分header,payload,signature.

* Claim 具有以下数据结构

```
class Claim{
  header : Header
  payload : Payload
  signature : byte[]
}
```

```
class Header {
    public String Alg = "ONT-ES256";
    public String Typ = "JWT-X";
    public String Kid;
    }
```

字段说明
`alg` 使用的签名框架
`typ` 可以是下面两个值中的一个
     JWT: 表示区块链证明不包含在claim中
     JWT-X: 表示区块链证明是claim中的一部分
`kid` 用于签名的公钥

```
class Payload {
    public String Ver;
    public String Iss;
    public String Sub;
    public long Iat;
    public long Exp;
    public String Jti;
    @JSONField(name = "@context")
    public String Context;
    public Map<String, Object> ClmMap = new HashMap<String, Object>();
    public Map<String, Object> ClmRevMap = new HashMap<String, Object>();
    }
```

`ver` Claim版本号
`iss` 发行方的ontid
`sub` 申请方的ontid
`iat` 创建时间
`exp` 超期时间
`jti` claim的唯一标志
`@context` 指定申明内容定义文档URI，其定义了每个字段的含义和值得类型
`clm` 包含claim内容的对象
`clm-rev` 定义个claim 的撤销机制，

### 2 可信申明接口列表

1. createOntIdClaim(String signerOntid, String password, String context, Map<String, Object> claimMap, Map metaData,Map clmRevMap,long expire)

    功能说明： 创建可信申明

    参数说明：

    signerOntid：签名者ontid

    password： 签名者密码

    context ： 指定申明内容定义文档URI，其定义了每个字段的含义和值得类型

    claimMap ： 申明内容

    metaData ： 申明发行者和申请者ontid

    expire ： 申明过期时间

    返回值：可信申明

2. boolean verifyOntIdClaim(String claim)

    功能说明： 验证可信申明

    参数说明：

    claim：可信申明

    返回值：true或false


### 3 签发可信申明
根据用户输入内容构造声明对象，该声明对象里包含了签名后的数据。
创建claim：
* 1.查询链上是否存在Issuer的DDO
* 2.签名者的公钥必须在DDO的Owners中存在
* 3.Id 是对claim中删除Signature、Id、Proof的数据转byte数组，做一次sha256，再转hexstring 
* 4.对要签名的json数据转成Map对key做排序。
* 5.Signature中Value值：claim 删除Signature、Proof后转byte数组, 做两次sha256得到的byte数组。
```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);

Map clmRevMap = new HashMap();
clmRevMap.put("typ","AttestContract");
clmRevMap.put("addr",dids.get(1).ontid.replace(Common.didont,""));
String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,password, "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
```

> Note: Issuer可能有多把公钥，createOntIdClaim的参数ontid指定使用哪一把公钥。

### 4 验证可信申明
验证cliam：
* 1.查询链上是否存在Metadata中Issuer的DDO
* 2.Owner是否存在SIgnature中的PublicKeyId
* 3.对要验签的json数据转成Map对key做排序。
* 4.删除Signature做验签（根据PublicKeyId的id值查找到公钥,签名是Signature中Value做base64解码）

```
boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
```

### 5 实例说明


```
//注册ontid
Identity identity = ontSdk.getWalletMgr().createIdentity(password);
ontSdk.nativevm().ontId().sendRegister(identity,password,payeraddress,payerpassword,0);
String ontid = ident.ontid;
//更新属性
Map recordMap = new HashMap();
recordMap.put("key0", "world0");
recordMap.put("keyNum", 1234589);
recordMap.put("key2", false);
String hash = ontSdk.nativevm().ontId().sendAddAttributes(ontid,password, recordMap,payer,payerpassword,gas);
```

> Note: 当不存在该属性时，调用sendAddAttributes方法，会增加相应的属性，当属性存在时，会更新相应属性。

Claim签发和验证：

```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);

Map clmRevMap = new HashMap();
clmRevMap.put("typ","AttestContract");
clmRevMap.put("addr",dids.get(1).ontid.replace(Common.didont,""));

//密码是签发人的秘密，钱包文件ontid中必须要有该签发人。
String claim = ontSdk.nativevm().ontId().createOntIdClaim(dids.get(0).ontid,password, "claim:context", map, map,clmRevMap,System.currentTimeMillis()/1000 +100000);
boolean b = ontSdk.nativevm().ontId().verifyOntIdClaim(claim);
```
