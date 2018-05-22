<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 区块链交互基本操作

以下针对使用SDK和区块交互的基本操作，以及相关数据结构定义。

用Java SDK之前，请使用以下方式初始化OntSDK实例。

```
OntSdk ontSdk = OntSdk.getInstance();
ontSdk.setRpc(rpcUrl);
ontSdk.setRestful(restUrl);
ontSdk.setDefaultConnect(wm.getRestful());
ontSdk.openWalletFile("OntAssetDemo.json");
```
> Note: setRestful表示采用restful接口建立连接，setRpc表示采用rpc接口建立连接,setDefaultConnect表示设置默认的链接方式。

## 基本操作接口


* 获取当前区块高度
```
int height = ontSdk.getConnectMgr().getBlockHeight();
```

* 获取区块

根据高度获取区块
```
Block block = ontSdk.getConnectMgr().getBlock(9757);
```

根据区块hash获得区块

```
Block block = ontSdk.getConnectMgr().getBlock(blockhash);
```

* 获得block json数据

根据高度获取区块json
```
Object block = ontSdk.getConnectMgr().getBlockJson(9757);
```

根据区块hash获得区块json

```
Object block = ontSdk.getConnectMgr().getBlockJson(blockhash);
```
* 获得合约代码

根据合约hash获得合约代码

```
Object contract =  ontSdk.getConnectMgr().getContract(contractHash)
```

根据合约hash获得合约代码json数据

```
Object contractJson = ontSdk.getConnectMgr().getContractJson(hash)
```

* 查询余额

根据账户地址查询余额

```
Object  balance = ontSdk.getConnectMgr().getBalance(address)
```

* 获取区块链节点数

```
int count = ontSdk.getConnectMgr().getNodeCount();
```

* 获取出块时间

```
int time = ontSdk.getConnectMgr().getGenerateBlockTime();
```

* 获得区块高度

```
int blockheight = ontSdk.getConnectMgr().getBlockHeight()
```

* 获得智能合约事件

根据高度获得智能合约事件

```
Object  event = ontSdk.getConnectMgr().getSmartCodeEvent(height)
```

根据交易hash获得智能合约事件

```
Object  event = ontSdk.getConnectMgr().getSmartCodeEvent(hash)
```

* 根据交易hash获得区块高度

```
int blockheight = ontSdk.getConnectMgr().getBlockHeightByTxHash(txhash)
```

* 获得智能合约存储的数据

```
String value = ontSdk.getConnectMgr().getStorage(codehash,key)
```

* 获得merkle证明

根据交易hash获得merkle证明

```
Object proof =  ontSdk.getConnectMgr().getMerkleProof(String hash)
```

* 从区块链中获取交易

根据交易hash获得交易对象
```
Transaction info = ontSdk.getConnectMgr().getTransaction(txhash);
```

根据交易hash获得交易json数据

```
Object info = ontSdk.getConnectMgr().getTransactionJson(txhash);
```

* 从区块链中获取InvokeCodeTransaction

```
InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getTransaction(txhash);
```
## 数据结构说明

* Block区块

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    version|   int|  版本号  |
|    prevBlockHash|   UInt256|  前一个区块的散列值|
|    transactionsRoot|   UInt256|  该区块中所有交易的Merkle树的根|
|    blockRoot|   UInt256| 区块根|
|    timestamp|   int| 区块时间戳，unix时间戳  |
|    height|   int|  区块高度  |
|    consensusData|   long |  共识数据 |
|    consensusPayload|   byte[] |  共识payload |
|    nextBookKeeper|   UInt160 |  下一个区块的记账合约的散列值 |
|    sigData|   array|  签名 |
|    bookKeepers|   array|  验签者 |
|    hash|   UInt256 |  该区块的hash值 |
|    transactions|   Transaction[] |  该区块的交易列表 |


* Transaction交易

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    version|   int|  版本号  |
|    txType|   TransactionType|  交易类型|
|    nonce|   int |  随机数|
| gasPrice|  long |  gas价格|
| gasLimit|  long |  gas上限|
|    payer|   Address |  支付交易费用账户|
|    attributes|   Attribute[]|  交易属性列表 |
|    sigs|   Sign[]|   签名数组  |
|    payload| Payload |  payload  |


* TransactionType交易类型

| Value     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    208|   int |  部署智能合约交易|
|    209|   int | 调用智能合约交易 |
|      0|   int |     Bookkeeping   |
|      4|   int |     注册       |
|      5|   int |     投票 |


* 签名字段

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    pubKeys|   array |  公钥数组|
|    M|   int | M |
|    sigData|   array | 签名值数组 |



* Attribute交易属性

| Field    |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    usage |   AttributeUsage |  用途|
|    data|   byte[] | 属性值 |


* TransactionAttributeUsage属性用途

| Value     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    0|   int|  Nonce|
|    32|   int | Script |
|    129|   int | DescriptionUrl |
|    144|   int | Description |
