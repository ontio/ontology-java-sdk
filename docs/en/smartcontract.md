<h1 align="center"> Ontology Java SDK Smart Contract </h1>

<p align="center" class="version">Version 1.0.0 </p>

English / [中文](../cn/smartcontract.md)

## Introduction
This chapter outlines the usage of smart contracts in the Java SDK <br>


## Deployment and invocation of smart contract

Note: At present, the Java SDK supports both NEO and WASM smart contract deployment and invocation. Deployment operations of NEO and WASM contract are the same, but the invocation is slightly different. See below for details.

### Example smart contract deployment 

| Parameters    | Field       | Type                  | Description                       | Explaination                           |
| -----         | -------     | ------                | -------------                     | -----------                            |
| Input params  | codeHexStr  | String                | Contract code hexadecimal string  | Required                               |
|               | needStorage | Boolean               | Need storage or not               | Required                               |
|               | name        | String                | Contract name                     | Required                               |
|               | codeVersion | String                | Contract version                  | Required                               |
|               | author      | String                | Contract author                   | Required                               |
|               | email       | String                | Author email                      | Required                               |
|               | desp        | String                | Description                       | Required                               |
|               | VmType      | byte                  | Virtual machine type              | Required                               |
|               | payer       | String                | Account address used to pay transaction fee| Required |
|               | gaslimit    | long                  | Gas limit                          | Required |
|               | gasprice    | long                  | Gas price                          | Required |
| Output params | tx          | Transaction           | Transaction instance              | |

```
InputStream is = new FileInputStream("/Users/sss/dev/ontologytest/IdContract/IdContract.avm");
byte[] bys = new byte[is.available()];
is.read(bys);
is.close();
code = Helper.toHexString(bys);


//Deploy the contract
Transaction tx = ontSdk.vm().makeDeployCodeTransaction(codeHexStr, true, "name", "1.0", "1", "1", "1", VmType.NEOVM.value(),payer,gaslimit,gasprice);
ontSdk.vm().setCodeAddress(Address.AddressFromVmCode(code).toHexString());
Account account = new Account(Helper.hexToBytes("75de8489fcb2dcaf2ef3cd607feffde18789de7da129b5e97c81e001793cb7cf"),SignatureScheme.SHA256WITHECDSA);
Transaction tx = ontSdk.vm().makeDeployCodeTransaction(code, true, "name",
                    "v1.0", "author", "email", "desp", account.getAddressU160().toBase58(),ontSdk.DEFAULT_DEPLOY_GAS_LIMIT,0);
String txHex = Helper.toHexString(tx.toArray());
ontSdk.getConnect().sendRawTransaction(txHex);
//Waiting for block generation
Thread.sleep(6000);
DeployCodeTransaction t = (DeployCodeTransaction) ontSdk.getConnect().getTransaction(txhash);
```



## Invocation of a smart contract

### Invocation of a NEO smart contract

Process overview
   1. Construct the function parameters that calls the smart contract
   2. Construct transaction
   3. Signature transaction
   4. Send transaction

##### Example

```
List paramList = new ArrayList<>();
paramList.add("testHello".getBytes());

List args = new ArrayList();
args.add(true);
args.add(100);
args.add("test".getBytes());
args.add("test");
args.add(account.getAddressU160().toArray());

paramList.add(args);
byte[] params = BuildParams.createCodeParamsScript(paramList);

String result = invokeContract(params, account, 20000, 500,true);
System.out.println(result);

public static String invokeContract(byte[] params, Account payerAcct, long gaslimit, long gasprice, boolean preExec) throws Exception{
    if(payerAcct == null){
        throw new SDKException("params should not be null");
    }
    if(gaslimit < 0 || gasprice< 0){
        throw new SDKException("gaslimit or gasprice should not be less than 0");
    }
    Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(contractAddress),null,params,payerAcct.getAddressU160().toBase58(),gaslimit,gasprice);
    ontSdk.addSign(tx, payerAcct);
    Object result = null;
    if(preExec) {
        result = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
    }else {
        result = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        return tx.hash().toString();
    }
    return result.toString();
}

```



###  Invocation of a WASM smart contract

Process overview
  1. Construct the parameters required by the method in the calling contract;
  2. Structure transaction;
  3. Generate transaction signature (no signature required for pre-execution);
  4. send Transaction.

##### Example:
```
//set codeAddress
ontSdk.getSmartcodeTx().setCodeAddress(codeAddress);
String funcName = "add";
//The parameters needed to construct a contract function
String params = ontSdk.getSmartcodeTx().buildWasmContractJsonParam(new Object[]{20,30});
//Specify a virtual machine type to construct the transaction
Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(ontSdk.getSmartcodeTx().getCodeAddress(),funcName,params.getBytes(),VmType.WASMVM.value(),payer,gaslimit,gasprice);
//send transaction
ontSdk.getConnect().sendRawTransaction(tx.toHexString());

```

## Process smart contract push notifications

##### Example to create a WebSocket thread and analyze the push notification.

* Set WebSocket link.

```
//lock global variable, synchronization lock
public static Object lock = new Object();

//Get ont instance
String ip = "http://127.0.0.1";
String wsUrl = ip + ":" + "20335";
OntSdk wm = OntSdk.getInstance();
wm.setWesocket(wsUrl, lock);
wm.setDefaultConnect(wm.getWebSocket());
wm.openWalletFile("OntAssetDemo.json");
```


* Start WebSocket thread.

```
//false means not printing callback function information
ontSdk.getWebSocket().startWebsocketThread(false);
```

* Start result processing thread.

```
Thread thread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        waitResult(lock);
                    }
                });
        thread.start();
        //Take out the data in the MsgQueue print
        public static void waitResult(Object lock) {
                try {
                    synchronized (lock) {
                        while (true) {
                            lock.wait();
                            for (String e : MsgQueue.getResultSet()) {
                                System.out.println("RECV: " + e);
                                Result rt = JSON.parseObject(e, Result.class);
                                //TODO
                                MsgQueue.removeResult(e);
                                if (rt.Action.equals("getblockbyheight")) {
                                    Block bb = Serializable.from(Helper.hexToBytes((String) rt.Result), Block.class);
                                    //System.out.println(bb.json());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
```


* Send a heartbeat every 6 seconds to maintain the socket link.

```
for (;;){
                Map map = new HashMap();
                if(i >0) {
                    map.put("SubscribeEvent", true);
                    map.put("SubscribeRawBlock", false);
                }else{
                    map.put("SubscribeJsonBlock", false);
                    map.put("SubscribeRawBlock", true);
                }
                //System.out.println(map);
                ontSdk.getWebSocket().setReqId(i);
                ontSdk.getWebSocket().sendSubscribe(map);     
            Thread.sleep(6000);
        }
```


* Push result case details


**Deposit certificate example**，The certificate abi.json document is as follows.

```
{
    "hash":"0x27f5ae9dd51499e7ac4fe6a5cc44526aff909669",
    "entrypoint":"Main",
    "functions":
    [
        
    ],
    "events":
    [
        {
            "name":"putRecord",
            "parameters":
            [
                {
                    "name":"arg1",
                    "type":"String"
                },
                {
                    "name":"arg2",
                    "type":"ByteArray"
                },
                {
                    "name":"arg3",
                    "type":"ByteArray"
                }
            ],
            "returntype":"Void"
        }
    ]
}
```

When the put function is called to save the data, the putRecord event is fired. The result of the WebSocket push is the hexadecimal string of {"putRecord", "arg1", "arg2", "arg3"}.

##### Example:

```
RECV: {"Action":"Log","Desc":"SUCCESS","Error":0,"Result":{"Message":"Put","TxHash":"8cb32f3a1817d88d8562fdc0097a0f9aa75a926625c6644dfc5417273ca7ed71","ContractAddress":"80f6bff7645a84298a1a52aa3745f84dba6615cf"},"Version":"1.0.0"}
RECV: {"Action":"Notify","Desc":"SUCCESS","Error":0,"Result":[{"States":["7075745265636f7264","507574","6b6579","7b2244617461223a7b22416c6772697468656d223a22534d32222c2248617368223a22222c2254657874223a2276616c75652d7465737431222c225369676e6174757265223a22227d2c2243416b6579223a22222c225365714e6f223a22222c2254696d657374616d70223a307d"],"TxHash":"8cb32f3a1817d88d8562fdc0097a0f9aa75a926625c6644dfc5417273ca7ed71","ContractAddress":"80f6bff7645a84298a1a52aa3745f84dba6615cf"}],"Version":"1.0.0"}
```


## FAQ
#### What is a contractAddress? 
A contractAddress is the unique identifier of a smart contract

#### Example of how to retrieve a smart contracts codeAddress

```
InputStream is = new FileInputStream("IdContract.avm");
byte[] bys = new byte[is.available()];
is.read(bys);
is.close();
code = Helper.toHexString(bys);
System.out.println("Code:" + Helper.toHexString(bys));
System.out.println("CodeAddress:" + Address.AddressFromVmCode(code).toHexString());
```

Note: When you are attempting to get the codeAddress, you need to set which virtual machine the contract needs to run on. The currently supports Java SDK virtual machines are NEO and WASM.


### Outline of the invokeTransaction function for a smart contract
```
//Firstly, convert the parameters of the smart contract into the vm-recognizable opcode 
Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(ontContractAddr, null, contract.toArray(), VmType.Native.value(), sender.toBase58(),gaslimit，gasprice);

//Sign the transaction
ontSdk.signTx(tx, info1.address, password);

//Send the transaction
ontSdk.getConnect().sendRawTransaction(tx.toHexString());
```

### Why do we need to pass the account and its password when invoking?

The Users signature, which is generated by the private key, is neccesary in the process of invoking a smart contract. The private key is encrypted and stored in the wallet and needs the password to decrypt.

### What is the pre-execution of a smart contract when querying the assert and do I use it?

Some operations of a smart contract, such as get, do not need to go through any consensus node. They read data directly from the storage of the smart contract, execute on the current node and return the result. We can call the pre-execution interface while sending transactions for these smart contracts.

##### Example
```
String result = (String) sdk.getConnect().sendRawTransactionPreExec(txHex);
```