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
Identity identity = ontSdk.getWalletMgr().importIdentity("6PYMpk8DjWzaEvneyaqxMBap9DuUPH72W6BsWWTtpWE4JJZkGq5ENtfYbT","passwordtest");
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


