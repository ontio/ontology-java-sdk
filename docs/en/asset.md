
## Digital assets

### **Data structure**
`address` base58 encoded account address  
`label` name of account  
`isDefault`indicates whether the account is a default one, whose default value is set as "false"  
`lock` indicates whether the account is locked by client users, who cannot spend in locked account  
`algorithm` name of encryption algorithm  
`parameters` encryption parameters  
`curve` elliptic curve  
`key` NEP-2 private key, whose value can be null (in case of read-only or non-standard address)  
`contract` smart contract, whose value can be null (in case of read-only address)  
`extra` extra information stored by client developer, whose value can be null  

```
public class Account {
    public String label = "";
    public String address = "";
    public boolean isDefault = false;
    public boolean lock = false;
    public String algorithm = "";
    public Map parameters = new HashMap() ;
    public String key = "";
    public Contract contract = new Contract();

```
### **Digital asset account management**

**Create digital asset account**

```
String url = "http://127.0.0.1:20386";
OntSdk ontSdk = OntSdk.getInstance();
ontSdk.setRpcConnection(url);
ontSdk.openWalletFile("wallet.json");
Account acct = ontSdk.getWalletMgr().createAccount("password");
//any account or identity, once created, are stored in the memory only. A write api should be invoked when writing to a wallet file.
ontSdk.getWalletMgr().writeWallet();
```

**Remove digital asset account**

```
ontSdk.getWalletMgr().getWallet().removeAccount(address);
//write to wallet 
ontSdk.getWalletMgr().writeWallet();
```

**Set default digital asset account**

```
ontSdk.getWalletMgr().getWallet().setDefaultAccount(index);
ontSdk.getWalletMgr().getWallet().setDefaultAccount("address");
```
> Note:  
> index: the account with such index number is set as the default account  
> address: the account with such address is set as the default account

### Use digital asset

1. How to directly call the SDK packaged transfer operation？(Please use this method for transfer of ont assets.)

```
//step1:获得ontSdk实例
OntSdk wm = OntSdk.getInstance();
wm.setRpcConnection(url);
wm.openWalletFile("OntAssetDemo.json");
//step2:获得ontAssetTx实例
ontAssetTx = ontSdk.getOntAssetTx()
//step3:调用转账方法
ontAssetTx.transfer(from,to,value)
```

2. How to call the transfer method in the contract according to the contract abi file？(You can refer to this method for asset contracts deployed by users themselves.)
Ontology asset smart contract ABI describes the functional interface of smart contract and supports parameter transfer:


```
{
    "hash":"0xceab719b8baa2310f232ee0d277c061704541cfb",
    "entrypoint":"Main",
    "functions":
    [
        {
            "name":"Main",
            "parameters":
            [
                {
                    "name":"operation",
                    "type":"String"
                },
                {
                    "name":"args",
                    "type":"Array"
                }
            ],
            "returntype":"Any"
        },
        {
            "name":"Transfer",
            "parameters":
            [
                {
                    "name":"from",
                    "type":"ByteArray"
                },
                {
                    "name":"to",
                    "type":"ByteArray"
                },
                {
                    "name":"value",
                    "type":"Integer"
                }
            ],
            "returntype":"Boolean"
        },
        {
            "name":"BalanceOf",
            "parameters":
            [
                {
                    "name":"address",
                    "type":"ByteArray"
                }
            ],
            "returntype":"Integer"
        }
    ],
    "events":
    [
    ]
}
```

How to transfer assets by invoking Ontology asset smart contract?

```
//step1: read smart contract ABI
InputStream is = new FileInputStream("C:\\NeoContract1.abi.json");
byte[] bys = new byte[is.available()];
is.read(bys);
is.close();
String abi = new String(bys);

//step2：parse ABI file
AbiInfo abiinfo = JSON.parseObject(abi, AbiInfo.class);

//step3：set smart contract codehash
ontSdk.setCodeHash(abiinfo.getHash());

//step4：select a function and set parameter value
AbiFunction func = abiinfo.getFunction("Transfer");
System.out.println(func.getParameters());
func.setParamsValue(param0.getBytes(),param1.getBytes(),param2.getBytes());

//setp5：invoke contract
String hash = ontSdk.getSmartcodeTx().invokeTransaction("passwordtest",addr,func);
```

What would be the AbiInfo structure?

```
public class AbiInfo {
    public String hash;
    public String entrypoint;
    public List<AbiFunction> functions;
    public List<AbiEvent> events;
}
public class AbiFunction {
    public String name;
    public String returntype;
    public List<Parameter> parameters;
}
public class Parameter {
    public String name;
    public String type;
    public String value;
}
```

What is codehash?

```
codehash is generated by calculating sha160 of the smart contract's bytecode twice. It is also the unique identifier of smart contract.
```


Why do we need to pass the account and its password when invoking?？

```
User's signature, which is generated by the private key, is neccesary in the process of invoking a smart contract. And the private key is encrypted and stored in the wallet, which needs the password to decrypt.
```

What is the pre-execution of smart contract when querying the assert and how to use it?

```
Operations of smart contract, such as get, do not need to go through any consensus node. They read data directly from the storage of smart contract, execute at current node, and return the result. 
We can call the pre-execution interface while sending transactions.。
String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(txHex);
```

