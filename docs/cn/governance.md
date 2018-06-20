# 节点接口

## 与后台交互接口

#### 检查ONTID和钱包地址是否通过KYC
在点击节点质押功能的按钮时，向后台发送当前的ONTID和钱包地址，如果检查没有通过KYC就提示其进行KYC或者切换身份，不然就直接进入。

#### 检查节点申请状况的接口
要给节点申请者一个实时的节点申请状况展示，需要一个返回申请状态的接口，或者主动推送状态给设备

## 与合约交互接口

#### 申请加入集群
说明：抵押一定的ONT，消耗一定的额外ONG，申请成为候选节点。
```text
方法名："registerCandidate"

String registerCandidate(Account account, String peerPubkey, int initPos, String ontid,String ontidpwd,byte[] salt,  long keyNo, Account payerAcct, long gaslimit, long gasprice)

参数：
0       Account         节点付账账户
1       String          节点公钥
2       int             抵押的ONT数量
3       String          调用者的OntID（必须是已经注册过并且有权限的ontid）
4       String          调用者ontId密码
5       byte[]          解密需要的salt
6       long            调用者公钥序号
7       Account         支付交易费用的账户
8       long            gaslimit
9       long            gasprice

返回值：String,交易hash
```

示例：
```
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String txhash = sdk.nativevm().governance().registerCandidate(account,Helper.toHexString(account8.serializePublicKey()),10000,identity.ontid,password,identity.controls.get(0).getSalt(),1,payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
```

#### 取消申请
说明：取消申请成为候选节点，解冻抵押的ONT。
```text
方法名："unRegisterCandidate"

String unRegisterCandidate(Account account, String peerPubkey,Account payerAcct, long gaslimit, long gasprice)

参数：
0       Account      抵押的账户地址
1       String       节点公钥
2       Account      支付交易费用的账户
3       long         gaslimit
4       long         gasprice

返回值：String,交易hash
```
示例：
```
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String txhash = sdk.nativevm().governance().unRegisterCandidate(account,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
```

#### 申请退出集群
说明：节点申请退出集群，进入正常退出流程，钱包地址要与申请时相同。如果该节点是共识节点，将在下下个周期解冻抵押，如果是候选节点，将在下个周期解冻抵押。
```text
方法名："quitNode"

String quitNode(Account account,String peerPubkey,Account payerAcct,long gaslimit,long gasprice)

参数：
0       Account      节点抵押的钱包账户地址
1       String       节点公钥
2       Account      支付交易费用的账户
3       long         gaslimit
4       long         gasprice

返回值：String, 交易hash
```

示例：
```
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String txhash = sdk.nativevm().governance().quitNode(account,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
```

#### 提取抵押的ONT
说明：取出处于未冻结状态的抵押ONT。
```text
方法名："withdraw"

String withdraw(Account account,String peerPubkey[],long[] withdrawList,Account payerAcct,long gaslimit,long gasprice)

参数：
0       Account         节点抵押的钱包账户地址
1       String[]        要从哪些节点取抵押的列表
2       long[]          要从节点取出抵押数
3       Account         支付交易费用的账户
4       long            gaslimit
5       long            gasprice

返回值：String,交易hash
```

示例：
```
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String txhash = sdk.nativevm().governance().withdraw(account,new String[]{Helper.toHexString(account8.serializePublicKey())},new long[]{10000},payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
```

#### 查询节点状态
说明：查询注册节点状态
```text
方法名 getPeerInfo

String getPeerInfo(String peerPubkey)

参数：
0    String    节点公钥

返回值：String, PeerPoolItem对象实例的json字符串
```

```
public class PeerPoolItem implements Serializable {
    public int index;//节点index
    public String peerPubkey;//节点公钥
    public Address address;//钱包地址
    public int status;//节点状态
    public long initPos;//抵押的ONT数量
    public long totalPos;//接受投票总共的ont数量
  }

  节点状态详细信息
  0   注册候选人状态
  1   候选人状态
  2   共识状态
  3   退出共识状态
  4   退出状态
  5   黑名单状态
```

示例：
```
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String res = sdk.nativevm().governance().getPeerInfo(account8.serializePublicKey());
System.out.println(res);
```

## 管理员使用接口
#### 审核通过加入集群
说明：管理员审核通过，成为候选节点，只有管理员能够调用。
```text
方法名："approveCandidate"

String approveCandidate(Account adminAccount, String peerPubkey,Account payerAcct,long gaslimit,long gasprice)

参数：
0       Account       管理员账户地址
1       String        节点公钥
2       Account       支付交易费用的账户
3       long          gaslimit
4       long          gasprice

返回值：String,交易hash
```
示例：
```
Account adminAccount2 = new Account(Helper.hexToBytes(adminPrivateKey2),SignatureScheme.SHA256WITHECDSA);
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String txhash = sdk.nativevm().governance().approveCandidate(adminAccount2,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
```

#### 审核不通过拒绝申请
说明：管理员审核不通过，解冻抵押，只有管理员能够调用。
```text
方法名："rejectCandidate"

String rejectCandidate(Account adminAccount,String peerPubkey,Account payerAcct,long gaslimit,long gasprice)

参数：
0       Account       管理员账户地址
1       String        节点公钥
2       Account       支付交易费用的账户
3       long          gaslimit
4       long          gasprice

返回值：String,交易hash
```

示例：
```
Account adminAccount2 = new Account(Helper.hexToBytes(adminPrivateKey2),SignatureScheme.SHA256WITHECDSA);
Account account8 = new Account(Helper.hexToBytes(privatekey8),SignatureScheme.SHA256WITHECDSA);
String txhash = sdk.nativevm().governance().rejectCandidate(adminAccount2,Helper.toHexString(account8.serializePublicKey()),payerAcct,sdk.DEFAULT_GAS_LIMIT,0);
```
