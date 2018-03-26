## Basic blockchain interop

The following describes basic blockchain interop function of SDK and defines relevant data structure.

Please use the following methods to initialize OntSDK use case before luanching JAVA SDK.

```
OntSdk ontSdk = OntSdk.getInstance();
ontSdk.setRpcConnection(url);
//ontSdk.setRestfulConnection（url）
```

> Note: setRestfulConnection indicates that the connection is established using the restful interface, and setRpcConnection indicates that the connection is established using the rpc interface.

### get the current block height

```
int height = ontSdk.getConnectMgr().getBlockHeight();
```

### get block

```
Block block = ontSdk.getConnectMgr().getBlock(9757);
```



### get blockchain node count

```
System.out.println(ontSdk.getConnectMgr().getNodeCount());
```

### get block time

```
System.out.println(ontSdk.getConnectMgr().getGenerateBlockTime());
```

### get blockchain-based transaction

```
String info = ontSdk.getConnectMgr().getTransaction(hash);
System.out.println(info);
```
### get InvokeTransaction 

```
InvokeCodeTransaction t = (InvokeCodeTransaction) ontSdk.getConnectMgr().getRawTransaction(hash);
System.out.println(t);
```

## Data structure

### Block

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    version|   int|  version  |
|    prevBlockHash|   UInt256|  scripthash of the previous block|
|    transactionsRoot|   UInt256|  merkel root of all the transactions in the block|
|    blockRoot|   UInt256| block root|
|    timestamp|   int| block time stamp, unix time stamp|
|    height|   int|  block height |
|    consensusData|   long |  consensus data |
|    nextBookKeeper|   UInt160 |  bookkeeping contract scripthash of the next block |
|    sigData|   array|  signature |
|    bookKeepers|   array|  bookkeepers |
|    hash|   UInt256 |  hash value of the block |
|    transactions|   Transaction[] |  transaction list in the block |


### Transaction

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    version|   int|  version  |
|    txType|   TransactionType|transaction type|
|    nonce|   int |  random number|
|    attributes|   TransactionAttribute[]|  transaction attribute list |
|    fee|   Fee[] |  transaction fee list |
|    networkFee|   long| network fee  |
|    sigs|   Sign[]|   signature array  |
|    payload| Payload |  payload  |




### TransactionType

| Value     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    208|   int |  smart contract deployment |
|    209|   int | smart contract invocation |


### 签名字段

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    pubKeys|   array |  public key array|
|    M|   int | M |
|    sigData|   array | signature value array |


### Fee

| Field     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    amount|   long|  amount|
|    payer|   UInt160 | payer |

### TransactionAttribute

| Field    |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    usage |   TransactionAttributeUsage |  usage|
|    data|   byte[] | attribute value |


### TransactionAttributeUsage

| Value     |     Type |   Description   | 
| :--------------: | :--------:| :------: |
|    0|   int|  Nonce|
|    32|   int | Script |
|    129|   int | DescriptionUrl |
|    144|   int | Description |
