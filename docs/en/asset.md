<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# Digital assets

## Data structure
`address` base58 encoded account address  
`label` name of account  
`isDefault`indicates whether the account is a default one, whose default value is set as "false"  
`lock` indicates whether the account is locked by client users, who cannot spend in locked account  
`algorithm` name of encryption algorithm  
`parameters` encryption parameters  
`curve` elliptic curve  
`key` NEP-2 private key, whose value can be null (in case of read-only or non-standard address)  
`contract` smart contract, whose value can be null (in case of read-only address)
`encAlg` 私钥加密的算法名称，固定为aes-256-ctr.
`extra` extra information stored by client developer, whose value can be null
`signatureScheme` `signatureScheme` is a signature scheme used for transaction signatures.
`hash` hash algorithm for derived privateKey。
`passwordHash`  password hash
```
    public class Account {
        public String label = "";
        public String address = "";
        public boolean isDefault = false;
        public boolean lock = false;
        public String algorithm = "";
        public Map parameters = new HashMap() ;
        public String key = "";
        @JSONField(name = "enc-alg")
        public String encAlg = "aes-256-ctr";
        public String hash = "sha256";
        public String signatureScheme = "SHA256withECDSA";
        public String passwordHash = "";
        public Object extra = null;
    }

```

## Digital asset account management

* Create digital asset account

```
String url = "http://127.0.0.1:20386";
OntSdk ontSdk = OntSdk.getInstance();
ontSdk.setRpcConnection(url);
ontSdk.openWalletFile("wallet.json");
Account acct = ontSdk.getWalletMgr().createAccount("password");
//any account or identity, once created, are stored in the memory only. A write api should be invoked when writing to a wallet file.
ontSdk.getWalletMgr().writeWallet();
```

* Remove digital asset account

```
ontSdk.getWalletMgr().getWallet().removeAccount(address);
//write to wallet
ontSdk.getWalletMgr().writeWallet();
```

* Set default digital asset account

```
ontSdk.getWalletMgr().getWallet().setDefaultAccount(index);
ontSdk.getWalletMgr().getWallet().setDefaultAccount("address");
```
> Note:  
> index: the account with such index number is set as the default account  
> address: the account with such address is set as the default account
----

## Native digital asset(Token)

ont and ong asset list

 1. sendTransfer(String assetName, String sendAddr, String password, String recvAddr, long amount,long gas)

    function description： Transfer a certain amount of assets from the sender to the receiver's account

    parameter description：

    assetName：asset name，ont or ong

    sendAddr： sender address

    password ： sender password

    recvAddr ： receive address

    amount ： asset amount

    gas ： gas amount

    return value： transaction hash

 2. sendTransferToMany(String assetName, String sendAddr, String password, String[] recvAddr, long[] amount,long gas)

    function description： transfer a certain amount of assets from the sender to multiple receiver accounts

    parameter description：

    assetName：asset name，ont or ong

    sendAddr： sender address

    password ： sender password

    recvAddr ： receive address array

    amount ： asset amount array

    gas ： gas amount

    return value：transaction hash

 3. sendTransferFromMany(String assetName, String[] sendAddr, String[] password, String recvAddr, long[] amount,long gas)

     function description： transfer assets from multiple senders to one receiver

     parameter description：

     assetName：asset name，ont or ong

     sendAddr： sender address array

     password ： sender password array

     recvAddr ： receive address

     amount ： array of transferred assets

     gas ： gas amount

     return value：transaction hash

 4. sendOngTransferFrom(String sendAddr, String password, String to, long amount,long gas)

      function description： extract ong to to account from sendAddr account

      parameter description：

      sendAddr： sender address

      password ： sender password

      to ： receive address

      amount ： asset amount

      gas ： gas amount

      return value：transaction hash

 5. sendApprove(String assetName ,String sendAddr, String password, String recvAddr, long amount,long gas)

       function description： sendAddr account allows recvAddr to transfer amount of assets

       parameter description：

       assetName：asset name，ont or ong

       sendAddr： sender address

       password： sender password

       recvAddr： receive address

       amount： asset amount

       gas： gas amount

       return value：transaction hash

 6. sendTransferFrom(String assetName ,String sendAddr, String password, String fromAddr, String toAddr, long amount,long gas)

        function description： The sendAddr account transfers the amount of assets from the fromAddr account to the toAddr account

        parameter description：

        assetName：asset name，ont or ong

        sendAddr： sender address

        password： sender password

        fromAddr： sender address

        toAddr： receive address

        amount： asset amount

        gas： gas amount

        return value：transaction hash

 7. sendBalanceOf(String assetName, String address)

         function description： Query the assetName asset balance of the account address

         parameter description：

         assetName：asset name，ont or ong

         address：

         return value： balance of address

 8. sendAllowance(String assetName,String fromAddr,String toAddr)

         function description： query the assetName asset balance of the account address

         ont = sdk.nativevm().ont()：

         assetName：asset name，ont or ong

         fromAddr： Authorized party address

         toAddr：authorized party address

         return value： asset amount

 9. queryName(String assetName)

          function description： query assetName asset name information

          parameter description：

          assetName：asset name

          return value：asset name detail information

 10. querySymbol(String assetName)

           function description： query AssetName Asset Symbol Information

           parameter description：

           assetName：asset name

           return value：Symbol information

 11. queryDecimals(String assetName)

            function description： query the accuracy of assetName assets

            parameter description：

            assetName：asset name

            return value：decimal

 12. queryTotalSupply(String assetName)

             function description： query the total supply of assetName assets

             parameter description：

             assetName：asset name

             return value：total Supply

Example:

```
//step1:get sdk instance
OntSdk wm = OntSdk.getInstance();
wm.setRpcConnection(url);
wm.openWalletFile("OntAssetDemo.json");
//step2:get ontAssetTx instance
ont = sdk.nativevm().ont()
//step3:transfer
ont.sendTransfer("ont",info2.address,"passwordtest",info1.address,100000000L);
ont.sendTransferToMany("ont",info1.address,"passwordtest",new String[]{info2.address,info3.address},new long[]{100L,200L});
ont.sendTransferFromMany("ont", new String[]{info1.address, info2.address}, new String[]{"passwordtest", "passwordtest"}, info3.address, new long[]{1L, 2L});
ont.sendOngTransferFrom(info1.address,"passwordtest",info2.address,100);
```

* Use Smart Contract

You also use smart contract to deal with  native digital asset.

Ontology smart contract ABI describes the functional interface of smart contract and supports parameter transfer:

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

//step3：set smart contract codeAddress
ontSdk.setCodeAddress(abiinfo.getHash());

//step4：select a function and set parameter value
AbiFunction func = abiinfo.getFunction("Transfer");
System.out.println(func.getParameters());
func.setParamsValue(param0.getBytes(),param1.getBytes(),param2.getBytes());

//setp5：invoke contract
String hash = ontSdk.getSmartcodeTx().sendInvokeSmartCodeWithSign("passwordtest",addr,func);
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

## nep-5 smart contract digital assets

nep-5 document：
>https://github.com/neo-project/proposals/blob/master/nep-5.mediawiki

digital assets template:
>https://github.com/neo-project/examples/tree/master/ICO_Template


|function|params|return value|description|
|:--|:--|:--|:--|
|sendInit    |boolean preExec|String|If true, it indicates that the pre-execution is to test whether it has been initialized. If it is false, the contract parameters are initialized.|
|sendTransfer|String sendAddr, String password, String recvAddr, int amount|String|transfer assets|
|sendBalanceOf|String addr|String|Get account balance|
|sendTotalSupply||String|Get total supply|
|sendName||String|Get name|
|sendDecimals||String|Get accuracy|
|sendSymbol||String|Query Token abbreviation|


nep-5 smartcontract template：


```
using Neo.SmartContract.Framework;
using Neo.SmartContract.Framework.Services.Neo;
using Neo.SmartContract.Framework.Services.System;
using System;
using System.ComponentModel;
using System.Numerics;

namespace Nep5Template
{
    public class Nep5Template : SmartContract
    {
        //Token Settings
        public static string Name() => "Nep5Template Token";
        public static string Symbol() => "TMP";
        public static readonly byte[] community = "AXK2KtCfcJnSMyRzSwTuwTKgNrtx5aXfFX".ToScriptHash();
        public static byte Decimals() => 8;
        private const ulong factor = 100000000; //decided by Decimals()

        //ICO Settings
        private const ulong totalAmount = 1000000000 * factor;
        private const ulong communityCap = 1000000000 * factor;

        [DisplayName("transfer")]
        public static event Action<byte[], byte[], BigInteger> Transferred;

        public static Object Main(string operation, params object[] args)
        {
            if (Runtime.Trigger == TriggerType.Application)
            {
                if (operation == "init") return Init();
                if (operation == "totalSupply") return TotalSupply();
                if (operation == "name") return Name();
                if (operation == "symbol") return Symbol();
                if (operation == "transfer")
                {
                    if (args.Length != 3) return false;
                    byte[] from = (byte[])args[0];
                    byte[] to = (byte[])args[1];
                    BigInteger value = (BigInteger)args[2];
                    return Transfer(from, to, value);
                }
                if (operation == "balanceOf")
                {
                    if (args.Length != 1) return 0;
                    byte[] account = (byte[])args[0];
                    return BalanceOf(account);
                }
                if (operation == "decimals") return Decimals();
            }
            return false;
        }

        // init
        public static bool Init()
        {
            byte[] total_supply = Storage.Get(Storage.CurrentContext, "totalSupply");
            if (total_supply.Length != 0) return false;

            Storage.Put(Storage.CurrentContext, community, communityCap);
            Transferred(null, community, communityCap);

            Storage.Put(Storage.CurrentContext, "totalSupply", totalAmount);
            return true;
        }

        // get the total token supply
        // Get the total number of issued tokens
        public static BigInteger TotalSupply()
        {
            Runtime.CheckSig(new byte[1]{ 1 },  new byte[]{2},new byte[]{ 3});
            return Storage.Get(Storage.CurrentContext, "totalSupply").AsBigInteger();
        }

        // function that is always called when someone wants to transfer tokens.
        // transfer token
        public static bool Transfer(byte[] from, byte[] to, BigInteger value)
        {
            if (value <= 0) return false;
            if (!Runtime.CheckWitness(from)) return false;
            if (from == to) return true;
            BigInteger from_value = Storage.Get(Storage.CurrentContext, from).AsBigInteger();
            if (from_value < value) return false;
            if (from_value == value)
                Storage.Delete(Storage.CurrentContext, from);
            else
                Storage.Put(Storage.CurrentContext, from, from_value - value);
            BigInteger to_value = Storage.Get(Storage.CurrentContext, to).AsBigInteger();
            Storage.Put(Storage.CurrentContext, to, to_value + value);
            Transferred(from, to, value);
            return true;
        }

        // get the account balance of another account with address
        // Get the token's balance based on the address
        public static BigInteger BalanceOf(byte[] address)
        {
            return Storage.Get(Storage.CurrentContext, address).AsBigInteger();
        }
    }
}
```


## instruction


* What is codeaddress?


```
codeaddress is the unique identifier of smart contract.
```


* Why do we need to pass the account and its password when invoking?


```
User's signature, which is generated by the private key, is neccesary in the process of invoking a smart contract. And the private key is encrypted and stored in the wallet, which needs the password to decrypt.
```


* What is the pre-execution of smart contract when querying the assert and how to use it?


```
Operations of smart contract, such as get, do not need to go through any consensus node. They read data directly from the storage of smart contract, execute at current node, and return the result.
We can call the pre-execution interface while sending transactions.。
String result = (String) sdk.getConnectMgr().sendRawTransactionPreExec(txHex);
```


* How to view the push results when transferring funds？


See smart contract using websocket connection call contract method，details[smartcontract](smartcontract.md)。
