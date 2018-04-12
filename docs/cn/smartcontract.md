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

codehash是智能合约的唯一标识。


* 调用智能合约invokeTransaction的过程，sdk中具体做了什么

```
//step1：构造交易
//需先将智能合约参数转换成vm可识别的opcode
Transaction tx = ontSdk.getSmartcodeTx().makeInvokeCodeTransaction(params, vmtype, fees);

//step2：对交易签名
ontSdk.signTx(tx, info1.address, password);

//step3：发送交易
ontSdk.getConnectMgr().sendRawTransaction(tx.toHexString());
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
String txhash = ontSdk.getSmartcodeTx().makeDeployCodeTransaction(code, true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value());
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
String hash = ontSdk.getSmartcodeTx().sendInvokeSmartCodeWithSign(did.ontid, "passwordtest", func, (byte) VmType.NEOVM.value()););

```
> 如果需要监控推送结果，可以了解下面章节。

## 智能合约执行过程推送

创建websocket线程，解析推送结果。

1. 设置websocket链接

```
//lock 全局变量,同步锁
public static Object lock = new Object();

//获得ont实例
String ip = "http://127.0.0.1";
String wsUrl = ip + ":" + "20335";
OntSdk wm = OntSdk.getInstance();
wm.setWesocket(wsUrl, lock);
wm.setDefaultConnect(wm.getWebSocket());
wm.openWalletFile("OntAssetDemo.json");

```

2. 启动websocket线程

```
//false 表示不打印回调函数信息
ontSdk.getWebSocket().startWebsocketThread(false);

```

3. 启动结果处理线程
```
Thread thread = new Thread(
                    new Runnable() {
                        @Override
                        public void run() {
                            waitResult(lock);
                        }
                    });
            thread.start();
            //将MsgQueue中的数据取出打印
            public static void waitResult(Object lock) {
                    try {
                        synchronized (lock) {
                            while (true) {
                                lock.wait();
                                for (String e : MsgQueue.getResultSet()) {
                                    System.out.println("RECV: " + e);
                                    Result rt = JSON.parseObject(e, Result.class);
                                    //TODO
                                    MsgQueue.removeResult(e);
                                    if (rt.Action.equals("getblockbyheight")) {
                                        Block bb = Serializable.from(Helper.hexToBytes((String) rt.Result), Block.class);
                                        //System.out.println(bb.json());
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
```
4. 每6秒发送一次心跳程序，维持socket链接

```
for (;;){
                    Map map = new HashMap();
                    if(i >0) {
                        map.put("SubscribeEvent", true);
                        map.put("SubscribeRawBlock", false);
                    }else{
                        map.put("SubscribeJsonBlock", false);
                        map.put("SubscribeRawBlock", true);
                    }
                    //System.out.println(map);
                    ontSdk.getWebSocket().setReqId(i);
                    ontSdk.getWebSocket().sendHeartBeat(map);     
                Thread.sleep(6000);
            }
```
