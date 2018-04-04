## 数字存证

### 操作步骤

1.初始化SDK

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

2. 将数据保存到链上

```
String res = ontSdk.getRecordTx().sendPut("TA9WXpq7GNAc2D6gX9NZtCdybRq8ehGUxw","passwordtest","key","value");
```

> Note: key表示存到链上数据value的key，以后可以根据key获得存的数据value。

3. 根据key从链上获得数据

```
String res = ontSdk.getRecordTx().sendGet("TA9WXpq7GNAc2D6gX9NZtCdybRq8ehGUxw","passwordtest","key");
```

4. 返回值说明

```
{"Data":{"Algrithem":"SM2","Hash":"","Text":"value","Signature":""},"CAkey":"","SeqNo":"","Timestamp":0}
```