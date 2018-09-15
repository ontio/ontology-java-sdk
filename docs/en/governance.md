<h1 align="center"> Governance </h1>

<p align="center" class="version">Version 1.0.0 </p>

[English](../cn/governance.md) / English

## Interface list

#### Mortgage a certain ONT, consume a certain amount of additional ONG, apply to become a candidate node

```
String registerCandidate(Account account, String peerPubkey, long initPos, String ontid,String ontidpwd,byte[] salt,  long keyNo, Account payerAcct, long gaslimit, long gasprice)
```
	Parameters:

			account: peer wallet account
			peerPubkey: peer publickey
			initPos: init pos
			ontid: authorized ontid
			ontidpwd: ontid password
			salt: salt
			keyNo: publickey index
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice
	
	return value: transaction hash


#### Cancel the application to become a candidate node, unfreeze the mortgaged ONT
```
String unRegisterCandidate(Account account, String peerPubkey,Account payerAcct, long gaslimit, long gasprice)
```

	Parameters:
			account: peer wallet account
			peerPubkey: peer publickey
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice

	return value: transaction hash


#### Extract unbound ONG
```
String withdrawOng(Account account,Account payerAcct,long gaslimit,long gasprice)
```

	Parameters:
			account: peer wallet account
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice

	return value: transaction hash


#### Query node information
```
String getPeerInfo(String peerPubkey)
```

	Parameters:
			peerPubkey: peer publickey

	return value: peer information


#### Query all node information
```
String getPeerInfoAll()
```

	return value: all nodes information



#### Query the authorization information of a certain address to a node
```
String getAuthorizeInfo(String peerPubkey,Address addr)
```

	Parameters:
			peerPubkey: peer publickey
			withdrawList: ont number

	return value: transaction hash
	

#### Take out the mortgage ONT in an unfrozen state
```
String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice)
```
	Parameters:
			account: peer wallet account
			peerPubkey: peer publickey
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice

	return value: transaction hash

#### addInitPos interface (can only be called by the node owner)
```
String addInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

	Parameters:
			account: peer wallet account
			peerPubkey: peer publickey
			pos: increase the number of ont mortgages
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice
	
	return value: transaction hash
	

#### reduceInitPos (can only be called by the node owner)
The initPos cannot be lower than the promised value, and cannot be lower than 1/10 of the accepted number of licenses.
```
String reduceInitPos(Account account,String peerPubkey,int pos,Account payerAcct,long gaslimit,long gasprice)
```

	Parameters:
			account: peer wallet account
			peerPubkey: peer publickey
			pos: reduce the number of ont mortgages
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice

	return value: transaction hash


#### setPeerCost
The node sets the proportion of its own exclusive incentives
```
String setPeerCost(Account account,String peerPubkey,int peerCost,Account payerAcct,long gaslimit,long gasprice)
```

	Parameters:
			account: peer wallet account
			peerPubkey: peer publickey
			peerCost: the proportion of its own exclusive incentives
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice

	return value: transaction hash
	
	

#### changeMaxAuthorization
The node modifies the maximum number of authorized ONT nodes it accepts
```
String changeMaxAuthorization(Account account,String peerPubkey,int maxAuthorize,Account payerAcct,long gaslimit,long gasprice)
```

	Parameters:
			account: peer wallet account
			peerPubkey: peer publickey
			maxAuthorize: The maximum number of authorized ONT nodes accepted by the node itself
			payerAcct: account for payment of transaction fees
			gaslimit
			gasprice

	return value: transaction hash


#### Query node attribute information
```
String getPeerAttributes(String peerPubkey)
```

	Parameters:
			peerPubkey  peer publickey

	return value: peer attributes information
	

#### Query the incentives for an address
```
String getSplitFeeAddress(String address)
```

	Parameters:
			address: Account bas58 encoded address

	return value: Incentive for an address
