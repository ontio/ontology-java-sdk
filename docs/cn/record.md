<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 数字身份Claim存证

## 操作步骤


* 1.初始化SDK


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

> Note: codeAddress是存证合约地址。

下面接口文档的规范是https://github.com/kunxian-xia/ontology-DID/blob/master/docs/en/claim_spec.md。

* 2. String sendCommit(String ontid,String password,String claimId,long gas)

        功能说明： 将数据保存到链上

        参数说明：

        ontid：数字身份ontid

        password： 数字身份密码

        claimId ： 可信申明claim唯一性标志，即Claim里面的Jti字段

        gas ： gas数量

        返回值：交易hash


示例代码

```
String[] claims = claim.split("\\.");
JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));
ontSdk.neovm().claimRecord().sendCommit(ontid,password,payload.getString("jti"),0)
```

* 3. String sendGetStatus(String ontid,String password,String claimId)

        功能说明：查询可信申明的状态

        参数说明：

        ontid：数字身份ontid

        password： 数字身份密码

        claimId ： 可信申明claim唯一性标志，即Claim里面的Jti字段

        gas ： gas数量

        返回值：有两部分: 第一部分，claim的状态："Not attested", "Attested", "Attest has been revoked";第二部分是存证者的ontid


示例代码

```
String res = ontSdk.getRecordTx().sendGet("TA9WXpq7GNAc2D6gX9NZtCdybRq8ehGUxw","passwordtest","key");
```


* 4. String sendRevoke(String ontid,String password,String claimId,long gas)

        功能说明：撤销可信申明

        参数说明：

        ontid：存证人的数字身份ontid

        password： 数字身份密码

        claimId ： 可信申明claim唯一性标志，即Claim里面的Jti字段

        gas ： gas数量

        返回值：只有当该claim处于Attested状态，并且ontid是存证者的ontid时返回true，其他情况返回false
