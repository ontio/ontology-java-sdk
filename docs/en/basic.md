<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# Basic blockchain interop


The following describes basic blockchain interop function of SDK and defines relevant data structure.

Please use the following methods to initialize OntSDK use case before launching JAVA SDK.


```
OntSdk wm = OntSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setDefaultConnect(wm.getRestful());
wm.openWalletFile("OntAssetDemo.json");
```


> Note: setRestful indicates that the connection is established using the restful interface, and setRpc indicates that the connection is established using the rpc interface,setDefaultConnect is used to set default connect method.


## Basic operation interfaces


* Get the current block height


```
int height = ontSdk.getConnectMgr().getBlockHeight();
```


* Get block


get block by block height


```
Block block = ontSdk.getConnectMgr().getBlock(9757);
```


get block by block hash


```
Block block = ontSdk.getConnectMgr().getBlock(blockhash);
```


* Get block json


get block by block height

```
Object block = ontSdk.getConnectMgr().getBlockJson(9757);
```


get block by block hash


```
Object block = ontSdk.getConnectMgr().getBlockJson(blockhash);
```

* Get contarct code

get contract code by contract hash


```
Object contract =  ontSdk.getConnectMgr().getContract(contractHash)
```


get contract json  by contract hash


```
Object contractJson = ontSdk.getConnectMgr().getContractJson(hash)
```

* Get balance


query balance by address


```
Object  balance = ontSdk.getConnectMgr().getBalance(address)
```

* Get blockchain node count

```
ontSdk.getConnectMgr().getNodeCount();
```

* Get block time

```
ontSdk.getConnectMgr().getGenerateBlockTime();
```

* get block height

```
int blockheight = ontSdk.getConnectMgr().getBlockHeight()
```

* get smartcontract event

get smartcontract event by block height

```
Object  event = ontSdk.getConnectMgr().getSmartCodeEvent(height)
```

get smartcontract event by transaction hash

```
Object  event = ontSdk.getConnectMgr().getSmartCodeEvent(hash)
```

* get block height by transaction hash

```
int blockheight = ontSdk.getConnectMgr().getBlockHeightByTxHash(txhash)
```

* get data stored in smart contract by key

```
String value = ontSdk.getConnectMgr().getStorage(codehash,key)
```

* get merkle proof

get merkle proof by transaction hash

```
Object proof =  ontSdk.getConnectMgr().getMerkleProof(String hash)
```


* Get blockchain-based transaction

```
Transaction info = ontSdk.getConnectMgr().getTransaction(hash);
```

get transaction json by transaction hash

```
Object info = ontSdk.getConnectMgr().getTransactionJson(txhash);
```

* Get InvokeTransaction

```
InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getTransaction(hash);
```

## Data structure

* Block

| Field     |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    version|   int|  version  |
|    prevBlockHash|   UInt256|  scripthash of the previous block|
|    transactionsRoot|   UInt256|  merkel root of all the transactions in the block|
|    blockRoot|   UInt256| block root|
|    timestamp|   int| block time stamp, unix time stamp|
|    height|   int|  block height |
|    consensusData|   long |  consensus data |
|    consensusPayload|   byte[] |  consensus payload |
|    nextBookKeeper|   UInt160 |  bookkeeping contract scripthash of the next block |
|    sigData|   array|  signature |
|    bookKeepers|   array|  bookkeepers |
|    hash|   UInt256 |  hash value of the block |
|    transactions|   Transaction[] |  transaction list in the block |


* Transaction

| Field     |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    version|   int|  version  |
|    txType|   TransactionType|transaction type|
|    nonce|   int |  random number|
|    attributes|   Attribute[]|  transaction attribute list |
| gasPrice|  long |  gas price|
| gasLimit|  long |  gas limit|
|    payer|   Address |  account used to pay fee|
|    sigs|   Sign[]|   signature array  |
|    payload| Payload |  payload  |


* TransactionType

| Value     |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    208|   int |  smart contract deployment |
|    209|   int | smart contract invocation |
|      0|   int |        Bookkeeping  |
|      4|   int |     Enrollment       |
|      5|   int |     Vote |

* Signature Area

| Field     |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    pubKeys|   array |  public key array|
|    M|   int | M |
|    sigData|   array | signature value array |


* Fee

| Field     |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    amount|   long|  amount|
|    payer|   UInt160 | payer |

* Attribute

| Field    |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    usage |   AttributeUsage |  usage|
|    data|   byte[] | attribute value |


* TransactionAttributeUsage

| Value     |     Type |   Description   |
| :--------------: | :--------:| :------: |
|    0  |   int|  Nonce|
|    32 |   int | Script |
|    129|   int | DescriptionUrl |
|    144|   int | Description |
