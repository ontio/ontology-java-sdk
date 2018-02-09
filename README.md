
# 准备工作

*** 下载DNA SKD(java版本)， 配置JAVA8运行环境
>注意： 配置java运行环境后运行程序时如出现如下错误：
>java.security.InvalidKeyException: Illegal key size
>则这是秘钥长度大于128，安全策略文件受限的原因。可以去官网下载local_policy.jar和US_export_policy.jar，替换jre目录中${java_home}/jre/lib/security原有的与安全策略这两个jar即可。下载地址：
>http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html

# 接入步骤
账户信息和OntId身份信息存储在json文件中。

## json文件说明


账户信息和OntId身份信息存储在json文件中，格式如下：

```
{
	"accounts": [
		{
			"address": "AMs5NFdXPgCgC7Dci1FdFttvD42HELoLxG",
			"algorithm": "ECDSA",
			"contract": {
				"deployed": false,
				"parameters": ["Signature"],
				"script": "210392a4dbb2a44da81e0942cee1a62ff4298e04ed463b88911b97de19a1597fa83dac"
			},
			"isDefault": false,
			"key": "6PYT85poeK8XpuQhnroArEov64NfRsEeB4KiGD1YCoq5xU7sJrnXC92Vey", //加密后的私钥
			"label": "",
			"lock": false,
			"parameters": {
				 "curve": "secp256r1"
			}
		}
	],
	"identitys": [
		{
			"controls": [{
				"algorithm": "ECDSA",
				"id": "",
				"key": "6PYT85poeK8XpuQhnroArEov64NfRsEeB4KiGD1YCoq5xU7sJrnXC92Vey", //加密后的私钥
				"parameters": {
				    "curve": "secp256r1"
				}
			}],
			"isDefault": false,
			"label": "",
			"lock": false,
			"ontid": "did:ont:AMs5NFdXPgCgC7Dci1FdFttvD42HELoLxG"  //ontid,身份id
		}
	],
	"name": "ontology",
	"scrypt": {
		"n": 16384,
		"p": 8,
		"r": 8
	},
	"version": "v1.0.0"
}
```



# 接口说明

调用每一个接口方法之前必须实例化ontSdk，后续的接口都是基于ontSdk调用。实例化账户管理器所需参数包括：节点连接地址url，账户存储位置路径path，访问令牌accessToken。

* 钱包管理
* 身份管理
* 存证管理
* 智能合约
* 智能合约websocket推送event
* 资产管理
* 与链通信
* 错误码

```
//钱包管理实例
ontSdk.getWalletMgr()
//ontid实例
ontSdk.getOntIdTx()
//存证实例
ontSdk.getRecordTx()
//智能合约实例
ontSdk.getSmartcodeTx()
//启动智能合约websocket推送event
WsProcess.startWebsocketThread(lock,wsUrl);
//资产管理实例
ontSdk.getAssetTx()
//与区块链连接实例
ontSdk.getConnectMgr()

```

## 钱包管理

```
//初始化
OntSdk ontSdk = OntSdk.getInstance();
ontSdk.setBlockChainConfig(url, "");
//打开ont文件
ontSdk.openWalletFile("Demo.json");

//创建账号和身份
Account info = ontSdk.getWalletMgr().createAccount("123456");
Identity info = ontSdk.getWalletMgr().createIdentity("123456");

//导入账号和身份
Account account = ontSdk.getWalletMgr().importAccount("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
Identity identity = ontSdk.getWalletMgr().importIdentity("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
//设置为默认账号和身份
ontSdk.getWalletMgr().getWallet().setDefaultAccount(3);
ontSdk.getWalletMgr().getWallet().setDefaultIdentity(3);
ontSdk.getWalletMgr().writeWallet(); //必须写入wallet，否则设置默认失败

```


## 身份管理
注册ontid

```

//注册ontid
Identity register(String password)


public class Identity {
	public String label = "";
	public String ontid = "";
	public boolean isDefault = false;
	public boolean lock = false;
	public List<Control> controls = new ArrayList<Control>();
}
```

获取ontid的DDO
```
//获取DDO
String ddo = ontSdk.getOntIdTx().getDDO(ontid);


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
boolean b = ontSdk.getOntIdTx().verifyOntIdClaim(claim);
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