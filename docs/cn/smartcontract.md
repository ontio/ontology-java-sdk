## 智能合约基本说明

* AbiInfo结构是怎样的？

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

* codehash是什么

codehash是对智能合约byte code做两次sha160，智能合约的唯一标识。


* 调用智能合约invokeTransaction的过程，sdk中具体做了什么

```
//step1：构造交易
//需先将智能合约参数转换成vm可识别的opcode
Transaction tx = sdk.getSmartcodeTx().makeInvokeCodeTransaction(opcodes,codeHash,info.address,info.pubkey);

//step2：对交易签名
String txHex = sdk.getWalletMgr().signatureData(password,tx);

//step3：发送交易
sdk.getConnectMgr().sendRawTransaction(txHex);
```

* invoke时为什么要传入账号和密码

调用智能合约时需要用户签名，钱包中保存的是加密后的用户私钥，需要密码才能解密获取私钥。


* 查询资产操作时，智能合约预执行是怎么回事，如何使用？

如智能合约get相关操作，从智能合约存储空间里读取数据，无需走节点共识，只在该节点执行即可返回结果。发送交易时调用预执行接口。
```
String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(txHex);
```



## 智能合约部署

#### **部署智能合约Demo例子**：
```
InputStream is = new FileInputStream("/Users/sss/dev/ontologytest/IdContract/IdContract.avm");
byte[] bys = new byte[is.available()];
is.read(bys);
is.close();
code = Helper.toHexString(bys);
ontSdk.setCodeHash(Helper.getCodeHash(code,VmType.NEOVM.value()));

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
|        | needStorage    | Boolean | 是否需要存储   | 必选 |
|        | name    | String  | 名字       | 必选|
|        | codeVersion   | String | 版本       |  必选 |
|        | author   | String | 作者     | 必选 |
|        | email   | String | emal     | 必选 |
|        | desp   | String | 描述信息     | 必选 |
|        | returnType   | ContractParameterType | 合约返回的数据类型     | 必选 |
| 输出参数 | txid   | String  | 交易编号  | 交易编号是64位字符串 |

## 智能合约调用

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

```
> 如果需要监控推送结果，可以了解下面章节。

## 智能合约执行过程推送

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

