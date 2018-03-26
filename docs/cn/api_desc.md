[中文版](./api_desc_cn.md)


<h1 align="center">Ontology JAVA SDK </h1>
<h4 align="center">版本 V0.6.0 </h4>

## 索引

- [总体介绍](##总体介绍)
- [快速上手](##快速上手)
- [区块链节点基本操作](##区块链节点基本操作)
- [钱包文件及规范](##钱包文件及规范)
- [数字身份](##数字身份)
- [可信申明](##可信申明)
- [数字资产](##数字资产)
- [错误码](##错误码)

## 总体介绍

SDK主要功能是封装账号、交易、与节点通信，构造交易向链上ontsdk类创建了功能管理实例，在Demo程序中举例如何使用ontsdk类。

### demo：

* 不同类型的交易的使用demo

### ontology：

* acount：账号相关操作，如生成公私钥
* common：通用基础接口
* core：核心层，包括合约、交易、签名等
* crypto：加密相关，如ECC SM
* io：io操作
* network：与链上restful或rpc接口通信接口
* sdk：对底层做封装、Info信息、通信管理、UTXO管理、钱包文件管理、异常类。
* ontsdk类：提供管理器和交易实例

管理器：walletMgr、coinManager、connManager。walletMgr钱包管理器主要管理账户和身份，用户向链上发送交易需要钱包私钥做签名。coinManager主要是对UTXO做管理，比如用户未花费utxo。 connManager与链上通信管理。任何发送交易和查询都需要通过连接管理器。

交易：OntIdTx（身份）、DataTx\(数据交易\)、AssetTx\(UTXO资产\)、RecordTx\(存证\)、SmartcodeTx（智能合约）。与链交互中可以构造不同类型的交易，这里将交易类型做分类，如果交易都是通过智能合约实现，那交易都会基于SmartcodeTx进行构造。

## 区块链节点基本操作

查询类操作。传递交易编号，返回交易具体信息。

例子：
```
//选择连接到链方式restful
wm.setRestfulConnection(url);
或rpc
wm.setRpcConnection(url)

//获取InvokeCodeTransaction
InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getRawTransaction(hash);
//获取一般的交易
String info = ontSdk.getConnectMgr().getTransaction(hash);
System.out.println(info);
//获取块
Block block = ontSdk.getConnectMgr().getBlock(9757);
//获取当前高度
int height = ontSdk.getConnectMgr().getBlockHeight();
//获取节点数
System.out.println(ontSdk.getConnectMgr().getNodeCount());
//获取出块时间
System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
//获取smartcode event
System.out.println(ontSdk.getConnectMgr().getSmartCodeEvent(9757));
System.out.println(ontSdk.getConnectMgr().getSmartCodeEvent(txhash));
```

## 钱包文件及规范

钱包Wallet是一个Json格式的数据存储文件。在本体Ontology中， Wallet可同时存储多个数字身份和多个数字资产账户。

为了便于数字身份在不同客户端和去中心应用中可以通用，需要制定一套钱包文件规范。Wallet 按照此规范组织数据格式，根据需要可以存储到文件系统，也可以存储到数据库系统。

希望了解更多钱包数据规范请参考[Wallet_File_Specification](https://github.com/ONTIO-Community/ONTO/blob/master/Wallet_File_Specification.md).


使用以下方式创建或打开钱包，如果不存在钱包文件，会自动创建钱包文件。
```
wm.openWalletFile("Demo3.json");
```

## 数字身份


### 身份数据结构说明

`ontid` 是代表身份的唯一的id
`label` 是用户给身份所取的名称。
`isDefault` 表明身份是用户默认的身份。默认值为false。
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
}

```
`algorithm`是用来加密的算法名称。
`parameters` 是加密算法所需参数。
`curve` 是椭圆曲线的名称。
`id` 是control的唯一标识。
`key` 是NEP-2格式的私钥。该字段可以为null（对于只读地址或非标准地址时）。
```
public class Control {
    public String algorithm = "ECDSA";
    public Map parameters = new HashMap() ;
    public String id = "";
    public String key = "";
}
```

### 数字身份管理

**创建数字身份**
```
Identity identity = ontSdk.getWalletMgr().createIdentity("password");
//创建的账号或身份只在内存中，如果要写入钱包文件，需调用写入接口
ontSdk.getWalletMgr().writeWallet();
```

**导入账号或身份**
当用户已经拥有了一个数字身份或者数字账户，SDK支持将其导入到Wallet中。

> **Note：** 建议导入一个数字身份之前，建议查询链上身份，如果链上身份DDO不存在，表示此数字身份未在链上注册，请使用ontSdk.getOntIdTx().register(identity)把身份注册到链上。

```
Identity identity = ontSdk.getWalletMgr().importIdentity("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
//写入钱包      
ontSdk.getWalletMgr().writeWallet();
```
**移除身份**
```
ontSdk.getWalletMgr().getWallet().removeIdentity(ontid);
//写入钱包 
ontSdk.getWalletMgr().writeWallet();
```
**设置默认账号或身份**
```
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(index);setDefaultAccount
ontSdk.getWalletMgr().getWallet().setDefaultIdentity("ontid");
```

**向链上注册身份**
```
ontSdk.getOntIdTx().register(identity,"passwordtest");
或
ontSdk.getOntIdTx().register("passwordtest");
```

**更新DDO属性**

```
//更新一个属性
String updateAttribute(String ontid,String password,byte[] key,byte[] type,byte[] value)
```
| 参数      | 字段   | 类型  | 描述 |             说明 |
| :----- | :------- | :------ | :------------- | :----------- |
| 输入参数 | password| String | 发行者地址 | 必选，私钥解密的密码 |
|        | ontid    | String | 资产名称   | 必选，身份Id |
|        | key    | byte[]  | key       | 必选，key |
|        | type    | byte[] | 类型       |  必选，类型 |
|        | value   | byte[] | value     | 必选，值 |
| 输出参数 | txid   | String  | 交易编号  | 交易编号是64位字符串 |

**查询链上身份**，链上身份将以DDO的形式存放，可以通过ONT ID进行查询。
```
//通过ONT ID获取DDO
String ddo = ontSdk.getOntIdTx().getDDO(ontid,"passwordtest",ontid);

//返回DDO格式
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

**验证用户签名**

```
ontSdk.getOntIdTx().verifySign(String reqOntid, String password, String ontid, byte[] data, byte[] signature);
reqOntid和password 是发起查询的人的ontid和密码。
```


## 可信申明

### 数据结构和规范

* Claim 具有以下数据结构

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

`unsignedData` 是未被签名的声明对象的json格式字符串，声明对象包含Context, Id, Claim, Metadata这些字段。
`signedData` 是声明对象被签名后的json格式字符串，该json包含声明对象和签名对象。
`Context` 是声明模板的标识。
`Id` 是声明对象的标识。
`Claim` 是声明的内容。
`Metadata` 是声明对象的元数据。

* Metadata 具有以下数据结构

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
`createtime` 是声明的创建时间。
`issuer` 是声明的发布者。
`subject` 是声明的主语。
`expires` 是声明的过期时间。
`revocation` 是声明撤销方法。
`crl` 是声明撤销列表的链接。


* Signature 具有以下数据结构

```
{
    format : string,
    algorithm : string,
    value : string
}
format 是签名的格式。
algorithm 是签名的算法。
value 是计算后的签名值。
```

###  签发可信申明
根据用户输入内容构造声明对象，该声明对象里包含了签名后的数据。

```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);
String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
System.out.println(claim);
```

###  验证可信申明

```
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(dids.get(0).ontid,"passwordtest",claim);

```

## 数字资产

### **数据结构说明**
`address` 是base58编码的账户地址。
`label` 是账户的名称。
`isDefault`表明账户是否是默认的账户。默认值为false。
`lock` 表明账户是否是被用户锁住的。客户端不能消费掉被锁的账户中的资金。
`algorithm` 是加密算法名称。
`parameters` 是加密算法所需参数。
`curve` 是椭圆曲线的名称。
`key` 是NEP-2格式的私钥。该字段可以为null（对于只读地址或非标准地址）。
`contract` 是智能合约对象。该字段可以为null（对于只读的账户地址）。
`extra` 是客户端存储额外信息的字段。该字段可以为null。

```
public class Account {
    public String label = "";
    public String address = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public String algorithm = "";
    public Map parameters = new HashMap() ;
    public String key = "";
    public Contract contract = new Contract();
}
```

### **数字资产账户管理**

**创建数字资产账号**

```
Account acct = ontSdk.getWalletMgr().createAccount("password");
//创建的账号或身份只在内存中，如果要写入钱包文件，需调用写入接口
ontSdk.getWalletMgr().writeWallet();
```
**导入数字资产账号**
当用户已经拥有了一个数字身份或者数字账户，SDK支持将其导入到Wallet中。

> **Note：** 建议导入一个数字身份之前，建议查询链上身份，如果链上身份DDO不存在，表示此数字身份未在链上注册，请使用ontSdk.getOntIdTx().register(identity)把身份注册到链上。

```
Account acct = ontSdk.getWalletMgr().importAccount("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
//写入钱包      
ontSdk.getWalletMgr().writeWallet();
```

**移除数字资产账号**

```
ontSdk.getWalletMgr().getWallet().removeAccount(address);
//写入钱包 
ontSdk.getWalletMgr().writeWallet();
```

**设置默认数字资产账号**

```
ontSdk.getWalletMgr().getWallet().setDefaultAccount(index);
ontSdk.getWalletMgr().getWallet().setDefaultAccount("address");
```

### 数字资产使用
通过sdk接口转账
```
//由转出方签名，钱包中必须要存在该转出方。
String hash = ontSdk.getOntAssetTx().transfer(info1.address,"passwordtest",1,info2.address,"no");
//转给多个地址
String hash = ontSdk.getOntAssetTx().transferToMany(info1.address,"passwordtest",new long[]{100L,200L},new String[]{info2.address,info3.address});
//多个地址转给一个地址
String hash = ontSdk.getOntAssetTx().transferFromMany(new String[]{info1.address,info2.address},new String[]{"passwordtest","passwordtest"},new long[]{1L,2L},info3.address);
```

ontology资产智能合约abi文件，abi文件是对智能合约函数接口的描述，通过abi文件可以清楚如何传参：

```
{
    "hash":"0xceab719b8baa2310f232ee0d277c061704541cfb",
    "entrypoint":"Main",
    "functions":
    [
        {
            "name":"Main",
            "parameters":
            [
                {
                    "name":"operation",
                    "type":"String"
                },
                {
                    "name":"args",
                    "type":"Array"
                }
            ],
            "returntype":"Any"
        },
        {
            "name":"Transfer",
            "parameters":
            [
                {
                    "name":"from",
                    "type":"ByteArray"
                },
                {
                    "name":"to",
                    "type":"ByteArray"
                },
                {
                    "name":"value",
                    "type":"Integer"
                }
            ],
            "returntype":"Boolean"
        },
        {
            "name":"BalanceOf",
            "parameters":
            [
                {
                    "name":"address",
                    "type":"ByteArray"
                }
            ],
            "returntype":"Integer"
        }
    ],
    "events":
    [
    ]
}
```

如何通过调用ontology资产智能合约进行转账操作？

```
//step1:读取智能合约abi文件
InputStream is = new FileInputStream("C:\\NeoContract1.abi.json");
byte[] bys = new byte[is.available()];
is.read(bys);
is.close();
String abi = new String(bys);

//step2：解析abi文件
AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);

//step3：设置智能合约codehash
ontSdk.setCodeHash(abiinfo.getHash());

//step4：选择函数，设置函数参数
AbiFunction func = abiinfo.getFunction("Transfer");
System.out.println(func.getParameters());
func.setParamsValue(param0.getBytes(),param1.getBytes(),param2.getBytes());

//setp5：调用合约
String hash = ontSdk.getSmartcodeTx().invokeTransaction("passwordtest",addr,func);
```

AbiInfo结构是怎样的？

```
public class AbiInfo {
    public String hash;
    public String entrypoint;
    public List<AbiFunction> functions;
    public List<AbiEvent> events;
}
public class AbiFunction {
    public String name;
    public String returntype;
    public List<Parameter> parameters;
}
public class Parameter {
    public String name;
    public String type;
    public String value;
}
```

codehash是什么？

```
是对智能合约byte code做两次sha160，智能合约的唯一标识。
```

调用智能合约invokeTransaction的过程，sdk中具体做了什么？

```
//step1：构造交易
//需先将智能合约参数转换成vm可识别的opcode
Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(opcodes,codeHash,info.address,info.pubkey);

//step2：对交易签名
String txHex = sdk.getWalletMgr().signatureData(password,tx);

//step3：发送交易
sdk.getConnectMgr().sendRawTransaction(txHex);
```

invoke时为什么要传入账号和密码？

```
调用智能合约时需要用户签名，钱包中保存的是加密后的用户私钥，需要密码才能解密获取私钥。
```

查询资产操作时，智能合约预是怎么回事，如何使用？

```
如智能合约get相关操作，从智能合约存储空间里读取数据，无需走节点共识，只在该节点执行即可返回结果。
发送交易时调用预执行接口
String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(txHex);
```


## 快速上手

列举sdk的几种功能demo
### 1）创建新的数字身份和数字资产账户

```
//Step1 初始化
OntSdk ontSdk = OntSdk.getInstance();
ontSdk.setBlockChainConfig(url, "");

//Step2 打开ont文件,如果文件为空，则创建一个新的文件
ontSdk.openWalletFile("/usr/local/demowallet.json");

//Step3 创建一个空的数字身份,需要设置密码
Identity identity = ontSdk.getWalletMgr().createIdentity("123456");

//Identity数据结构
public class Identity {
	public String label = "";
	public String ontid = "";
	public boolean isDefault = false;
	public boolean lock = false;
	public List<Control> controls = new ArrayList<Control>();
}
//创建数字资产账户，需要设置密码（目前支持Ontology链原生资产和智能合约资产）
Account account = ontSdk.getWalletMgr().createAccount("123456");

//Step4 将身份注册到链上
ontSdk.getOntIdTx().register(identity,"password");

```


### 2）链上身份管理


Demo例子：
```
//注册ontid
Identity ident = ontSdk.getOntIdTx().register("passwordtest");
String ontid = ident.ontid;
//更新属性
String hash = ontSdk.getOntIdTx().updateAttribute(ontid,"passwordtest", attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
```

Claim签发和验证：
```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);

//密码是签发人的秘密，钱包文件ontid中必须要有该签发人。
String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
System.out.println(claim);
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(ontid,"passwordtest",claim);
```


### 3)存证管理

存证和查询存证。

Demo例子：
```
Map recordData = constructRecord(JSON.toJSONString(recordMap));
String hash = ontSdk.getRecordTx().recordTransaction(JSON.toJSONString(recordData));
//等待出块
Thread.sleep(6000);

String result = ontSdk.getRecordTx().queryRecord(hash);
```

### 4)智能合约
资产智能合约和ontid身份智能合约均可采用本例子调用。

#### **部署智能合约Demo例子**：
```
ontSdk.setCodeHash(Helper.getCodeHash(code));

//部署合约
String txhash = ontSdk.getSmartcodeTx().DeployCodeTransaction(code, true, "name", "1.0", "author", "email", "desp", ContractParameterType.Boolean.name());
System.out.println("txhash:" + txhash);
//等待出块
Thread.sleep(6000);
DeployCodeTransaction t = (DeployCodeTransaction) ontSdk.getConnectMgr().getRawTransaction(txhash);
```
| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | codeHexStr| String | 合约code | 必选 |
|        | needStorage    | String | 是否需要存储   | 必选 |
|        | name    | String  | 名字       | 必选|
|        | codeVersion   | String | 版本       |  必选 |
|        | author   | String | 作者     | 必选 |
|        | email   | String | emal     | 必选 |
|        | desp   | String | 描述信息     | 必选 |
|        | returnType   | ContractParameterType | 合约返回的数据类型     | 必选 |
| 输出参数 | txid   | String  | 交易编号  | 交易编号是64位字符串 |

#### **调用智能合约Demo例子**：
读取智能合约的abi文件，构造调用智能合约函数，发送交易。
```
//读取智能合约的abi文件
InputStream is = new FileInputStream("C:\\ZX\\NeoContract1.abi.json");
byte[] bys = new byte[is.available()];
is.read(bys);
is.close();
String abi = new String(bys);
            
//解析abi文件
AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);
System.out.println("codeHash:"+abiinfo.getHash());
System.out.println("Entrypoint:"+abiinfo.getEntrypoint());
System.out.println("Functions:"+abiinfo.getFunctions());
System.out.println("Events"+abiinfo.getEvents());

//设置智能合约codehash
ontSdk.setCodeHash(abiinfo.getHash());

//获取账号信息
Identity did = ontSdk.getWalletMgr().getIdentitys().get(0);
AccountInfo info = ontSdk.getWalletMgr().getAccountInfo(did.ontid,"passwordtest");

//构造智能合约函数
AbiFunction func = abiinfo.getFunction("AddAttribute");
System.out.println(func.getParameters());
func.setParamsValue(did.ontid.getBytes(),"key".getBytes(),"bytes".getBytes(),"values02".getBytes(),Helper.hexToBytes(info.pubkey));
System.out.println(func);

//调用智能合约
String hash = ontSdk.getSmartcodeTx().invokeTransaction(did.ontid,"passwordtest",func);

//如果需要等待推送结果，需要启动websocket线程
```

#### 5) 智能合约websocket推送event

创建websocket线程，解析推送结果。

Demo例子：
```
String wsUrl = "ws://101.132.193.149:21335";

OntSdk ontSdk = getOntSdk();
Object lock = new Object();
WsProcess.startWebsocketThread(lock,wsUrl);
WsProcess.setBroadcast(true);
waitResult(ontSdk,lock);

public static void waitResult(OntSdk ontSdk, Object lock){
        try {
            synchronized (lock) {
                boolean flag = false;
                while(true) {
                    //等待新的推送
                    lock.wait();
                    //心跳
                    if(MsgQueue.getChangeFlag()){
                        System.out.println(MsgQueue.getHeartBeat());
                    }
                    //获取推送结果
                    for (String e : MsgQueue.getResultSet()) {
                        System.out.println("####"+e);
                        Result rt = JSON.parseObject(e, Result.class);
                        //TODO
                        MsgQueue.removeResult(e);
                        if(rt.Action.equals("Notify")) {
                            flag = true;
                            List<Map<String,Object>> list = (List<Map<String,Object>>)((Map)rt.Result).get("State");
                            for(Map m:(List<Map<String,Object>>)(list.get(0).get("Value"))){
                                String value = (String)m.get("Value");
                                String val = new String(Helper.hexToBytes(value));
                                System.out.print(val+" ");
                            }
                            System.out.println();
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 }

```







## 错误码

| 返回代码 | 描述信息 | 说明 |
| :---- | :----------------------------- | :----------------- |
| 0 | SUCCESS | 成功 |
| 41001 | SESSION_EXPIRED | 会话无效或已过期（ 需要重新登录） |
| 41002 | SERVICE_CEILING | 达到服务上限 |
| 41003 | ILLEGAL_DATAFORMAT | 不合法数据格式 |
| 42001 | INVALID_METHOD | 无效的方法 |
| 42002 | INVALID_PARAMS | 无效的参数 |
| 42003 | INVALID_TOKEN | 无效的令牌 |
| 43001 | INVALID_TRANSACTION | 无效的交易 |
| 43002 | INVALID_ASSET | 无效的资产 |
| 43003 | INVALID_BLOCK | 无效的块 |
| 44001 | UNKNOWN_TRANSACTION | 找不到交易 |
| 44002 | UNKNOWN_ASSET | 找不到资产 |
| 44003 | UNKNOWN_BLOCK | 找不到块 |
| 45001 | INVALID_VERSION | 协议版本错误 |
| 45002 | INTERNAL_ERROR | 内部错误 |
| 60000 | NETWORK_ERROR | 网络错误 |
| 60001 | DB_OP_ERROR | 数据库操作错误 |
| 60002 | NO_BALANCE | 余额不足 |
| 60003 | Decrypto_ERROR | 解密错误 |
| 60004 | Encrypto_ERROR | 加密错误 |
| 60005 | Deserialize_BLOCK_ERROR | 反序列化Block错误 |
| 60006 | Deserialize_TRANSACTION_ERROR | 反序列化Transaction错误 |
| 60007 | ComposeIssTransaction_ERROR | 组合交易Iss错误 |
| 60008 | ComposeTrfTransaction_ERROR | 组合交易Trf错误 |
| 60009 | Signature_INCOMPLETE | 签名未完成 |
| 60011 | IllegalArgument | 不合法参数 |
| 60012 | IllegalAddress | 不合法地址 |
| 60013 | IllegalAssetId | 不合法资产编号 |
| 60014 | IllegalAmount | 不合法数值 |
| 60015 | IllegalTxid | 不合法交易编号 |