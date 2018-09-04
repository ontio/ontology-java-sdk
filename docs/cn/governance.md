<h1 align="center"> 治理合约 </h1>

<p align="center" class="version">Version 1.0.0 </p>

[English](../en/governance.md) / 中文

## 接口列表

* 注册成为候选人

```
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

* 取消注册

```
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

* 提取ong

```
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

* 查询节点信息

```
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


* 提取

```
String withdraw(Account account,String peerPubkey[],long[] withdrawList,Account payerAcct,long gaslimit,long gasprice)
```

参数说明
```
peerPubkey 节点公钥
```

返回值
```
节点信息
```