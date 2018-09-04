<h1 align="center"> governance </h1>

<p align="center" class="version">Version 1.0.0 </p>

[English](../cn/governance.md) / English

## interface list

* Mortgage a certain ONT, consume a certain amount of additional ONG, apply to become a candidate node

```
String registerCandidate(Account account, String peerPubkey, long initPos, String ontid,String ontidpwd,byte[] salt,  long keyNo, Account payerAcct, long gaslimit, long gasprice)
```

parameter instruction
```
account     peer wallet account
peerPubkey  peer publickey
initPos     init pos
ontid       authorized ontid
ontidpwd    ontid password
salt        salt
keyNo       publickey index
payerAcct   account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* Cancel the application to become a candidate node, unfreeze the mortgaged ONT

```
String unRegisterCandidate(Account account, String peerPubkey,Account payerAcct, long gaslimit, long gasprice)
```

parameter instruction
```
account     peer wallet account
peerPubkey  peer publickey
payerAcct   account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* Extract untied ong

```
String withdrawOng(Account account,Account payerAcct,long gaslimit,long gasprice)
```

parameter instruction
```
account   peer wallet account
payerAcct account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* Query node information

```
String getPeerInfo(String peerPubkey)
```

parameter instruction
```
peerPubkey   peer publickey
```

return value
```
peer information
```

* Query all node information

```
String getPeerInfoAll()
```

return value
```
all nodes information
```


* Query the authorization information of a certain address to a node


* Take out the mortgage ONT in an unfrozen state

```
String getAuthorizeInfo(String peerPubkey,Address addr)
```

parameter instruction

```
peerPubkey  peer publickey
withdrawList ont number
```

return value
```
transaction hash
```

* Take out the mortgage ONT in an unfrozen state

```
String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice)
```

parameter instruction
```
account    peer wallet account
peerPubkey peer publickey
payerAcct  account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* The node adds the initPos interface, which can only be called by the node owner

```
String addInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

parameter instruction
```
account    peer wallet account
peerPubkey peer publickey
pos        increase the number of ont mortgages
payerAcct  account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* The node reduces the initPos interface and can only be called by the node owner. The initPos cannot be lower than the promised value, and cannot be lower than 1/10 of the accepted number of licenses

```
String reduceInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

parameter instruction
```
account      peer wallet account
peerPubkey   peer publickey
pos          reduce the number of ont mortgages
payerAcct    account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* The node sets the proportion of its own exclusive incentives

```
String setPeerCost(Account account,String peerPubkey,int peerCost,Account payerAcct,long gaslimit,long gasprice)
```

parameter instruction
```
account    peer wallet account
peerPubkey peer publickey
peerCost   the proportion of its own exclusive incentives
payerAcct  account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* The node modifies the maximum number of authorized ONTs it accepts

```
String changeMaxAuthorization(Account account,String peerPubkey,int maxAuthorize,Account payerAcct,long gaslimit,long gasprice)
```

parameter instruction
```
account      peer wallet account
peerPubkey   peer publickey
maxAuthorize The maximum number of authorized ont nodes accepted by the node itself
payerAcct     account for payment of transaction fees
gaslimit
gasprice
```

return value
```
transaction hash
```

* Query node attribute information

```
String getPeerAttributes(String peerPubkey)
```

parameter instruction
```
peerPubkey  peer publickey
```

return value
```
peer attributes information
```

* Query the incentives for an address

```
String getSplitFeeAddress(String address)
```

parameter instruction
```
address    Account bas58 encoded address
```

return value
```
Incentive for an address
```