<h1 align="center"> 治理合约 </h1>

<p align="center" class="version">Version 1.0.0 </p>

[English](../en/governance.md) / 中文

## 治理合约接口

治理合约用于管理节点。目前提供了以下接口。

### 1. 申请成为候选节点

* 抵押一定的ONT，消耗一定的额外ONG，申请成为候选节点

```java
String registerCandidate(Account account, String peerPubkey, long initPos, String ontid,String ontidpwd,byte[] salt,  long keyNo, Account payerAcct, long gaslimit, long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | initPos       | long    | 初始化权益             |必选|
|         | ontid         | String  |已授权的ontid           | 必选|
|         | ontidpwd      | String  | ontid密码              |必选|
|         | salt          | byte[]  | ontid对应的salt        |必选|
|         | keyNo         | long    | 公钥索引               |必选|
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit | 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |



### 2. 取消申请成为候选节点

* 取消申请成为候选节点，解冻抵押的ONT

```java
String unRegisterCandidate(Account account, String peerPubkey,Account payerAcct, long gaslimit, long gasprice)
```


参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit | 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |



### 3. 查询节点信息

* 查询节点信息

```java
String getPeerInfo(String peerPubkey)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | peerPubkey     | String | 节点公钥| 必选 |
| 输出参数 | 节点信息       | String  | 节点信息  |  |

### 4. 查询所有节点信息

* 查询节点信息

```java
String getPeerInfoAll()
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 |      |  | |  |
| 输出参数 | 节点信息       | String  | 节点信息  |  |


### 5. 查询某个地址对某个节点的授权信息

* 查询某个地址对某个节点的授权信息

```java
String getAuthorizeInfo(String peerPubkey,Address addr)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | peerPubkey     | String | 节点公钥|需要  |
| 输入参数 | addr     | String | 地址|需要  |
| 输出参数 | 节点授权信息       | String  | 节点授权信息  |  |


### 6. 取出处于未冻结状态的抵押ONT

* 取出处于未冻结状态的抵押ONT

```java
String withdraw(Account account,String peerPubkey[],long[] withdrawList,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String[]| 节点公钥               | 必选 |
|         | withdrawList  | long[]  | 初始化权益             |必选|
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit | 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 7. 提取解绑ong

* 提取解绑ong

```java
String withdrawOng(Account account,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:


| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit | 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 8. 赎回抵押权益获得收益

* 赎回抵押权益获得收益

```java
String withdrawFee(Account account,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit | 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 9. 退出节点

* 退出节点

```java
String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit| 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


返回值
```
交易hash
```
### 10. 节点增加initPos

* 节点增加initPos接口，只能由节点所有者调用

```java
String addInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | pos           | int     | 增加抵押的ont数量       | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit| 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 11. 节点减少initPos
* 节点减少initPos接口，只能由节点所有者调用，initPos不能低于承诺值，不能低于已接受授权数量的1/10

```java
String reduceInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | pos           | int     | 减少抵押的ont数量       | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit| 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 12. 节点设置自己独占激励的比例

* 节点设置自己独占激励的比例

```java
String setPeerCost(Account account,String peerPubkey,int peerCost,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | peerCost      | int     | 节点自己独占的比例       | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit| 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 13. 节点修改自己接受的最大授权ONT数量
* 节点修改自己接受的最大授权ONT数量

```java
String changeMaxAuthorization(Account account,String peerPubkey,int maxAuthorize,Account payerAcct,long gaslimit,long gasprice)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account       | Account | 节点钱包账户            | 必选 |
|         | peerPubkey    | String  | 节点公钥               | 必选 |
|         | maxAuthorize  | int     | 节点自己接受的最大授权ont数量 | 必选 |
|         | payerAcct     | Account | 支付交易费用的账户      |必选|
|         | gaslimit      | long    | gaslimit| 必选 |
|         | gasprice      | long    | gas价格               | 必选|
| 输出参数 | 交易hash       | String  | 交易hash  |  |


### 14. 查询节点属性信息

* 查询节点属性信息

```java
String getPeerAttributes(String peerPubkey)
```

参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | peerPubkey    | String | 节点公钥            | 必选 |
| 输出参数 |        | String  | 节点属性信息  |  |


### 15. 查询某地址得到的激励

* 查询某地址得到的激励

```java
String getSplitFeeAddress(String address)
```


参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | address    | String | 地址            | 必选 |
| 输出参数 |        | String  | 查询某地址得到的激励  |  |

### 16. 给节点投票

* 给候选节点或者共识节点投票

```java
String authorizeForPeer(Account account,String peerPubkey[],long[] posList,Account payerAcct,long gaslimit,long gasprice)
```


参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account    | Account | 投票的账户          | 必选 |
| 输入参数 | peerPubkey    | String[] | 接受投票的节点公钥数组          | 必选 |
| 输入参数 | posList    | long[] |    抵押的权益     | 必选 |
| 输入参数 | payerAcct    | Account |   支付交易费用的账户     | 必选 |
| 输入参数 | gaslimit    | long |    gaslimit     | 必选 |
| 输入参数 | gasprice    | long |    gasprice     | 必选 |
| 输出参数 |        | String  | 交易hash  |  |

### 17. 取消给节点投票

* 取消给候选节点或者共识节点投票

```java
String unAuthorizeForPeer(Account account,String peerPubkey[],long[] posList,Account payerAcct,long gaslimit,long gasprice)
```


参数说明:

| 参数      | 字段   | 类型  | 描述 |             说明 |
| ----- | ------- | ------ | ------------- | ----------- |
| 输入参数 | account    | Account | 投票的账户          | 必选 |
| 输入参数 | peerPubkey    | String[] | 接受投票的节点公钥数组          | 必选 |
| 输入参数 | posList    | long[] |    抵押的权益     | 必选 |
| 输入参数 | payerAcct    | Account |   支付交易费用的账户     | 必选 |
| 输入参数 | gaslimit    | long |    gaslimit     | 必选 |
| 输入参数 | gasprice    | long |    gasprice     | 必选 |
| 输出参数 |        | String  | 交易hash  |  |


