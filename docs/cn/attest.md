<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 数字身份Claim存证

## 操作步骤


* 1. 初始化SDK


```
String ip = "http://127.0.0.1";
String restUrl = ip + ":" + "20334";
String rpcUrl = ip + ":" + "20336";
String wsUrl = ip + ":" + "20335";
OntSdk wm = OntSdk.getInstance();
wm.setRpc(rpcUrl);
wm.setRestful(restUrl);
wm.setDefaultConnect(wm.getRestful());
wm.openWalletFile("RecordTxDemo.json");
wm.setCodeAddress("803ca638069742da4b6871fe3d7f78718eeee78a");
```

> Note: codeAddress是存证合约地址。

下面接口文档的规范是https://github.com/ontio/ontology-DID/blob/master/docs/cn/claim_spec_cn.md


* 2. String sendCommit(String issuerOntid, String password,byte[] salt, String subjectOntid, String claimId, Account payerAcct, long gaslimit, long gasprice)

        功能说明： 将数据保存到链上

        参数说明：

        issuerOntid：可信申明签发者数字身份ontid

        password： 数字身份密码

        subjectOntid： 可信申明申请者ontid

        claimId ： 可信申明claim唯一性标志，即Claim里面的Jti字段

        payerAcct： 交易费用支付者

        gaslimit ： gaslimit

        gasprice: gas price

        返回值：交易hash


示例代码

```
String[] claims = claim.split("\\.");
JSONObject payload = JSONObject.parseObject(new String(Base64.getDecoder().decode(claims[1].getBytes())));
String commitHash = ontSdk.neovm().claimRecord().sendCommit(dids.get(0).ontid,password,dids.get(1).ontid,payload.getString("jti"),account1,ontSdk.DEFAULT_GAS_LIMIT,0)
```

* 3. String sendGetStatus(String claimId)

        功能说明：查询可信申明的状态

        参数说明：

        claimId ： 可信申明claim唯一性标志，即Claim里面的Jti字段

        返回值：有两部分: 第一部分，claim的状态："Not attested", "Attested", "Attest has been revoked";第二部分是存证者的ontid


示例代码

```
String getstatusRes2 = ontSdk.neovm().claimRecord().sendGetStatus(payload.getString("jti"));
```


* 4. String sendRevoke(String issuerOntid,String password,byte[] salt,String claimId,Account payerAcct,long gaslimit,long gas)

        功能说明：撤销可信申明

        参数说明：

        issuerOntid：可信申明签发者数字身份ontid

        password： 数字身份密码

        claimId ： 可信申明claim唯一性标志，即Claim里面的Jti字段

        payerAcct： 交易费用支付者

        gaslimit ： gaslimit

        gasprice: gas price

        返回值：交易hash

示例代码

```
String revokeHash = ontSdk.neovm().claimRecord().sendRevoke(dids.get(0).ontid,password,salt,payload.getString("jti"),account1,ontSdk.DEFAULT_GAS_LIMIT,0);
```
