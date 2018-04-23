<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

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

## Installation Environment

Please configure JDK 8 and above.

> **Note:** As the length of key used in SDK is greater than 128, due to the restriction of JAVA security policy files, it is necessary to download local_policy.jar and US_export_policy.jar from the official website , to replace the two jar of ${java_home}/jre/lib/security in JRE directory.

Download URL：

>http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html


### Build

```
mvn clean install
```

### Preparations

* Make sure Ontology Blockchain has deployed well,  RPC port has been opened, and SDK will connect the RPC server to initialize.


