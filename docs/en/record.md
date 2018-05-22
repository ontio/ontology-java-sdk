<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# Attest Attestation

## Step


* 1. SDK init


```
String ip = "http://127.0.0.1";
String restUrl = ip + ":" + "20384";
String rpcUrl = ip + ":" + "20386";
String wsUrl = ip + ":" + "20385";
OntSdk wm = OntSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setDefaultConnect(wm.getRestful());
wm.openWalletFile("RecordTxDemo.json");
wm.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");
```

> Note: codeAddress is the address of the record contract。


The specification of the following interface document is https://github.com/kunxian-xia/ontology-DID/blob/master/docs/en/claim_spec.md。

* 2. String sendCommit(String ontid,String password,String claimId,long gas)

        function description： Save data to the chain

        parameter description：

        ontid：identity ontid

        password： identity password

        claimId ： trusted claims claim uniqueness mark, ie Jti field in Claim

        gas ： gas amount

        return value：交易hash


示例代码

```
String[] claims = claim.split("\\.");
JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));
ontSdk.neovm().claimRecord().sendCommit(ontid,password,payload.getString("jti"),0)
```

* 3. String sendGetStatus(String ontid,String password,String claimId)

        function description：query status of trusted claim

        parameter description：

        ontid：identity ontid

        password： identity password

        claimId ： trusted claims claim uniqueness mark, ie Jti field in Claim

        gas ： gas amount

        return value：There are two parts: In the first part, the status of the claim: "Not attested", "Attested", "Attest has been revoked"; the second part is the certificate's ontid



```
String res = ontSdk.getRecordTx().sendGet("TA9WXpq7GNAc2D6gX9NZtCdybRq8ehGUxw","passwordtest","key");
```


* 4. String sendRevoke(String ontid,String password,String claimId,long gas)

        function description：Repeal of a trust claim

        parameter description：

        ontid：attester's ontid

        password： attester's ontid password

        claimId ： Trusted claims claim uniqueness mark, ie Jti field in Claim

        gas ： gas amount

        return value：This function will return true if and only if the claim is attested, and the revokerOntId is equal to the attester's ONT identity; Otherwise, it will return false.