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

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
initPos 初始化权益
ontid  已授权的ontid
ontidpwd ontid密码
salt ontid对应的salt
keyNo 公钥索引
payerAcct 支付交易费用的账户
gaslimit 用于计算gas
gasprice 用于计算gas
```

返回值
```
交易hash
```
### 2. 取消申请成为候选节点

* 取消申请成为候选节点，解冻抵押的ONT

```java
String unRegisterCandidate(Account account, String peerPubkey,Account payerAcct, long gaslimit, long gasprice)
```

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```

### 3. 提取解绑ong

* 提取解绑ong

```java
String withdrawOng(Account account,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
account 节点钱包账户
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```

### 4. 查询节点信息

* 查询节点信息

```java
String getPeerInfo(String peerPubkey)
```

参数说明
```
peerPubkey 节点公钥
```

返回值
```
节点信息
```

### 5. 取出处于未冻结状态的抵押ONT

* 取出处于未冻结状态的抵押ONT

```java
String withdraw(Account account,String peerPubkey[],long[] withdrawList,Account payerAcct,long gaslimit,long gasprice)
```

参数说明

```
peerPubkey 节点公钥
withdrawList 取出的ont数量
```

返回值
```
交易hash
```
### 6. 退出节点

* 退出节点

```java
String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```
### 7. 节点增加initPos

* 节点增加initPos接口，只能由节点所有者调用

```java
String addInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
pos 增加抵押的ont数量
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```
### 8. 节点减少initPos
* 节点减少initPos接口，只能由节点所有者调用，initPos不能低于承诺值，不能低于已接受授权数量的1/10

```java
String reduceInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
pos 减少抵押的ont数量
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```
### 9. 节点设置自己独占激励的比例

* 节点设置自己独占激励的比例

```java
String setPeerCost(Account account,String peerPubkey,int peerCost,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
peerCost 节点自己独占的比例
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```
### 10. 节点修改自己接受的最大授权ONT数量
* 节点修改自己接受的最大授权ONT数量

```java
String changeMaxAuthorization(Account account,String peerPubkey,int maxAuthorize,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
account 节点钱包账户
peerPubkey 节点公钥
maxAuthorize 节点自己接受的最大授权ont数量
payerAcct 支付交易费用的账户
gaslimit
gasprice
```

返回值
```
交易hash
```
### 11. 查询节点属性信息

* 查询节点属性信息

```java
String getPeerAttributes(String peerPubkey)
```

参数说明
```
peerPubkey 节点公钥
```

返回值
```
节点属性信息
```

### 12. 查询某地址得到的激励

* 查询某地址得到的激励

```java
String getSplitFeeAddress(String address)
```

参数说明
```
address 账户bas58编码的地址
```

返回值
```
查询某地址得到的激励
```