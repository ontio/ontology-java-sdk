
<h1 align="center">Ontology Java SDK Developer's Guide</h1>
<h4 align="center">Version V0.6.0 </h4>

## Overview

This is a comprehensive Java library for the Ontology blockchain,which is released by Ontology currently supports multiple functions, including native wallet management, digital identity management, digital asset management, smart contract deployment and invocation, node communication, with more to come in the future. 

## Main functions


- [Basic operation of blockchain nodes](basic.md)
- [Wallet file specification](Wallet_File_Specification.md)
- [Digital identity and verifable claim management](identity_claim.md)
- [Digital assets](asset.md)
- [Smart contract deployment and invocation](smartcontract.md)
- [Error code](errorcode.md)


## Code structure:

* account: Account operations, e.g. public/private key generation
* common: Common interface
* core: Core layer, including smart contract, transaction and signature, etc.
* crypto: Relevant to crytography, e.g. ECC/SM
* io: IO operation
* network: Interfacing with the restful or rpc interface of the chain.
* sdk: Underlying encapsulation, info management, communications management, UTXO management, wallet file management, exception class.
* ontsdk class: Provide manager and transaction use cases. There are two types of managers: walletMgr and connManager. walletMgr manages digital identities and digital asset accounts. Transactions sent to the blockchain need to be digitally signed with the user's private key. connManager manages blockchain communications system. Any send transaction and query needs to be processed by connManager.