## Overview

This Java SDK released by Ontology official currently supports multiple functions, including native wallet management, digital identity management, digital asset management, smart contract deployment and invocation, node communication,with more to come in the future. 

Version 0.6.0

## Main functions


- [Basic operation of blockchain nodes](basic.md)
- [Wallet file specification](Wallet_File_Specification.md)
- [Digital identity and vefieable claim management](identity_claim.md)
- [Digital assets](assert.md)
- [Smart contract deployment and invocation](smartcontract.md)
- [Error code](errorcode.md)


## Code structure：

* acount: account operations, e.g. public/private key generation
* common: common base interface
* core: core layer, including contract, transaction and signature, etc.
* crypto: relevant to crytography, e.g. ECC/SM
* io: io operation
* network: 与链上restful或rpc接口通信接口
* sdk: underlying encapsulation, info management, communications management, UTXO management, wallet file management, exception class.
* ontsdk class: provide manager and transaction use cases.There are two types of managers：walletMgr and connManager. walletMgr manages digital identities and digital asset accounts. Transactions sent to the blockchain need to be digitally signed with the user's private key.connManager manages blockchain communications system. Any send transaction and query need to be processed by connManager.