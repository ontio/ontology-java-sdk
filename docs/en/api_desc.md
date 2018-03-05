
# 接口说明



## 钱包管理

钱包Wallet是一个Json格式的数据存储文件。在本体Ontology中， 一个Wallet可同时存储多个数字身份和多个数字资产。

### Wallet 数据存储规范

为了便于数字身份在不同客户端和去中心应用中可以通用，需要制定一套数据存储规范。Wallet 按照此规范组织数据格式，根据需要可以存储到文件系统，也可以存储到数据库系统。
```
{
    name: String;
    createTime: String;
    version: String;
    scrypt: {
        "n": int;
        "r": int;
        "p": int;
    };
    identities: Array<Identity>;
    accounts: Array<Account>;
    extra: null;
}
```

`name` 是用户为钱包所取的名称。

```createTime``` 是ISO格式表示的钱包的创建时间，如 : "2018-02-06T03:05:12.360Z"

`version` 目前为固定值1.0，留待未来功能升级使用。

`scrypt` 是加密算法所需的参数，该算法是在钱包加密和解密私钥时使用。

`identities` 是**钱包中所有数字身份对象的数组**

```accounts``` 是**钱包中所有数字资产账户对象的数组**

```extra``` 是客户端由开发者用来存储额外数据字段，可以为null。

希望了解更多钱包数据规范请参考[Wallet_File_Specification](https://github.com/ontio/opendoc/blob/master/resources/specifications/Wallet_File_Specification.md).


### 创建新的数字身份和数字资产账户

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
ontSdk.getOntIdTx().register("password",identity);

```

### 查询链上身份

链上身份将以DDO的形式存放，可以通过ONT ID进行查询。
```
//通过ONT ID获取DDO
String ddo = ontSdk.getOntIdTx().getDDO("passwordtest",ontid,ontid);

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

### 导入数字身份和数字账户 

当用户已经拥有了一个数字身份或者数字账户，SDK支持将其导入到Wallet中。

**Note：** 建议导入一个数字身份之前，建议查询链上身份，如果链上身份DDO不存在，表示此数字身份未在链上注册，请使用ontSdk.getOntIdTx().register(identity)把身份注册到链上。

```
//导入数字身份和数字资产账户到Wallet
Account account = ontSdk.getWalletMgr().importAccount("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
Identity identity = ontSdk.getWalletMgr().importIdentity("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
```


### 设置为钱包默认数字身份

```
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(3);
//必须写入wallet，否则设置默认失败
ontSdk.getWalletMgr().writeWallet(); 
```


## 链上身份管理




更新一个属性
```
//更新一个属性
String updateAttribute(String password,String ontid,byte[] key,byte[] type,byte[] value)
```
| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | password| String | 发行者地址 | 必选，私钥解密的密码 |
|        | ontid    | String | 资产名称   | 必选，身份Id |
|        | key    | byte[]  | key       | 必选，key |
|        | type    | byte[] | 类型       |  必选，类型 |
|        | value   | byte[] | value     | 必选，值 |
| 输出参数 | txid   | String  | 交易编号  | 交易编号是64位字符串 |

Demo例子：
```
//注册ontid
Identity ident = ontSdk.getOntIdTx().register("passwordtest");
String ontid = ident.ontid;
//更新属性
String hash = ontSdk.getOntIdTx().updateAttribute("passwordtest", ontid, attri.getBytes(), "Json".getBytes(), JSON.toJSONString(recordMap).getBytes());
```
Claim签发和验证：
```
Map<String, Object> map = new HashMap<String, Object>();
map.put("Issuer", dids.get(0).ontid);
map.put("Subject", dids.get(1).ontid);

//密码是签发人的秘密，钱包文件ontid中必须要有该签发人。
String claim = ontSdk.getOntIdTx().createOntIdClaim("passwordtest","claim:context",map,map);
System.out.println(claim);
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim("passwordtest",ontid,claim);
```


## 存证管理

存证和查询存证。

Demo例子：
```
Map recordData = constructRecord(JSON.toJSONString(recordMap));
String hash = ontSdk.getRecordTx().recordTransaction(JSON.toJSONString(recordData));
//等待出块
Thread.sleep(6000);

String result = ontSdk.getRecordTx().queryRecord(hash);
```

## 智能合约


部署智能合约Demo例子：
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

调用智能合约Demo例子：
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
AccountInfo info = ontSdk.getWalletMgr().getAccountInfo("passwordtest",did.ontid);

//构造智能合约函数
AbiFunction func = abiinfo.getFunction("AddAttribute");
System.out.println(func.getParameters());
func.setParamsValue(did.ontid.getBytes(),"key".getBytes(),"bytes".getBytes(),"values02".getBytes(),Helper.hexToBytes(info.pubkey));
System.out.println(func);

//调用智能合约
String hash = ontSdk.getSmartcodeTx().invokeTransaction("passwordtest",did.ontid,func);

//如果需要等待推送结果，需要启动websocket线程
```

## 智能合约websocket推送event

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

## 资产管理

通过传递资产的基本信息来产生一笔区块链上合法的资产，返回资产编号。


查询账户信息

```
AccountInfo info = ontSdk.getOntAccount().createAccount("password");
AccountInfo info2 = ontSdk.getOntAccount().getAccountInfo("password", info.address);

public class AccountInfo {
	public String address;	// 合约地址
	public String pubkey;	// 公钥
	public String prikey;	// 私钥
	public String priwif;	// 私钥 wif
	public String encryptedprikey;//加密后的私钥
	public String pkhash;	// 公钥hash
}
```


Demo例子：
```
//获取账号
 AccountInfo acct0 = ontSdk.getWalletMgr().getAccountInfo("passwordtest",ontSdk.getWalletMgr().getAccounts().get(0).address);
AccountInfo acct1 = ontSdk.getWalletMgr().getAccountInfo("passwordtest",ontSdk.getWalletMgr().getAccounts().get(1).address);
System.out.println(acct0.address);
//注册资产
String hash = ontSdk.getAssetTx().registerTransaction("passwordtest",acct0.address, "JF005", 1000000L, new Date().toString(), acct0.address);
System.out.println(hash);

Thread.sleep(6000);
System.out.println(acct0.encryptedprikey);
String assetid = hash;
//签发
String hashIssue = ontSdk.getAssetTx().issueTransaction("passwordtest",acct0.address,assetid,100,acct0.address,"no");
System.out.println(hashIssue);

Thread.sleep(6000);
//转账
String hashTransfer = ontSdk.getAssetTx().transferTransaction("passwordtest",acct1.address, assetid, 20L, acct0.address, "no");

```

## 查询链上信息

查询类操作。传递交易编号，返回交易具体信息。

Demo例子：
```
//获取交易
InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getRawTransaction(hash);
String info = ontSdk.getConnectMgr().getTransaction(hash);
System.out.println(info);
//获取块
Block block = ontSdk.getConnectMgr().getBlock(9757);
//获取当前高度
int height = ontSdk.getConnectMgr().blockHeight();
//获取节点数
System.out.println(ontSdk.getConnectMgr().getNodeCount());
//获取出块时间
System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());

```


## 其它接口

```
ontSdk.getOntIdTx().verifySign(String password, String reqOntid, String ontid, byte[] data, byte[] signature);
password和reqOntid 是发起查询的人的ontid和密码。
```


## 错误代码

| 返回代码 | 描述信息 | 说明 |
| :---- | ----------------------------- | ----------------- |
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