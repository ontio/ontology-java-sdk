# 接口

包括如下接口：

* 初始化接口
* 与链交互接口
* 钱包管理接口
* 数字资产接口
* 数字身份接口
* 智能合约部署与调用接口

### 初始化接口：

包括打开钱包文件和设置与链交互方式
 ```
     |                    Function                   |     Description            
 ----|-----------------------------------------------|------------------------
   1 | sdk.setRpc(rpcUrl)                            |   设置rpc            
   2 | sdk.setRestful(restUrl)                       |   设置restful
   3 | sdk.setWesocket(wsUrl, lock)                  |   设置websocket
   4 | wm.setDefaultConnect(wm.getWebSocket());     |    设置默认与链交互方式 
   5 | wm.openWalletFile("OntAssetDemo.json");        |   打开钱包
 ```

### 与链交互接口：

* 交互接口列表：
```

      |                     Main   Function                      |           Description            
 -----|----------------------------------------------------------|---------------------------------------------
    1 | ontSdk.getConnect().getGenerateBlockTime()               |  查询DBFT出块时间       
    2 | ontSdk.getConnect().getNodeCount()                       |  查询节点数量
    3 | ontSdk.getConnect().getBlock(15)                         |  查询块
    4 | ontSdk.getConnect().getBlockJson(15)                     |  查询块    
    5 | ontSdk.getConnect().getBlockJson("txhash")               |  查询块    
    6 | ontSdk.getConnect().getBlock("txhash")                   |  查询块     
    7 | ontSdk.getConnect().getBlockHeight()                     |  查询当前块高
    8 | ontSdk.getConnect().getTransaction("txhash")             |  查询交易                                     
    9 | ontSdk.getConnect().getStorage("contractaddress", key)   |  查询智能合约存储
   10 | ontSdk.getConnect().getBalance("address")                |  查询余额
   11 | ontSdk.getConnect().getContractJson("contractaddress")   |  查询智能合约          
   12 | ontSdk.getConnect().getSmartCodeEvent(59)                |  查询智能合约事件
   13 | ontSdk.getConnect().getSmartCodeEvent("txhash")          |  查询智能合约事件
   14 | ontSdk.getConnect().getBlockHeightByTxHash("txhash")     |  查询交易所在高度
   15 | ontSdk.getConnect().getMerkleProof("txhash")             |  获取merkle证明
   16 | ontSdk.getConnect().sendRawTransaction("txhexString")    |  发送交易
   17 | ontSdk.getConnect().sendRawTransaction(Transaction)      |  发送交易
   18 | ontSdk.getConnect().sendRawTransactionPreExec()          |  发送预执行交易
   18 | ontSdk.getConnect().getAllowance("ont","from","to")      |  查询允许使用值
```  

### 钱包管理接口：

包括数字资产和数字身份管理
 
* 数字资产账户：

```  
     |                        Main   Function                                     |     Description            
 ----|----------------------------------------------------------------------------|------------------------ 
   1 | Account importAccount(String encryptedPrikey, String pwd,String address)   |   导入资产账户
   2 | Account createAccount(String password)                                     |   创建资产账户
   3 | Account createAccountFromPriKey(String password, String prikey)            |   根据私钥创建
   4 | AccountInfo createAccountInfo(String password)                             |   根据私钥创建
   5 | AccountInfo createAccountInfoFromPriKey(String password, String prikey)    |   根据私钥创建
   6 | AccountInfo getAccountInfo(String address, String password)                |   获取账号信息
   7 | List<Account> getAccounts()                                                |   查询所有账号
   8 | Account getAccount(String address)                                         |   获取账户
   9 | Account getDefaultAccount()                                                |   获取默认账户
```  

* 数字身份：
```  
     |                        Main   Function                                     |     Description            
 ----|----------------------------------------------------------------------------|------------------------ 
   1 | Identity importIdentity(String encryptedPrikey, String pwd,String address) |   导入身份
   2 | Identity createIdentity(String password)                                   |   创建身份
   3 | Identity createIdentityFromPriKey(String password, String prikey)          |   根据私钥创建
   4 | IdentityInfo createIdentityInfo(String password)                           |   创建身份
   5 | IdentityInfo createIdentityInfoFromPriKey(String password, String prikey)  |   根据私钥创建
   6 | IdentityInfo getIdentityInfo(String ontid, String password)                |   查询身份信息  
   7 | List<Identity> getIdentitys()                                              |   查询所有身份 
   8 | Identity getIdentity(String ontid)                                         |   获取身份 
   9 | Identity getDefaultIdentity()                                              |   获取默认身份
  10 | Identity addOntIdController(String ontid, String key, String id)           |   添加控制人 
```        

### 数字资产：
1.原生数字资产
2.Nep-5智能合约数字资产

* 原生数字资产：
```

      |                                         Main   Function                                                     |           Description            
 -----|-------------------------------------------------------------------------------------------------------------|---------------------------------------------
    1 | String sendTransfer(String assetName, String sendAddr, String pwd, String recvAddr, long amount,long gas)   |  转账
    2 | long queryBalanceOf(String assetName, String address)                                                       |  查询余额                              
    3 | long queryAllowance(String assetName,String fromAddr,String toAddr)                                         |  查询Allowance
    4 | String sendApprove(String assetName ,String sendAddr, String pwd, String recvAddr, long amount,long gas)    |  发送Approve    
    5 | String sendTransferFrom(String asset,String sendAddr,String pwd,String from,String to,long amount,long gas) |  发送TransferFrom 
    6 | String queryName(String assetName)                                                                          |  查询资产名
    7 | String querySymbol(String assetName)                                                                        |  查询资产Symbol
    8 | long queryDecimals(String assetName)                                                                        |  查询精度
    9 | long queryTotalSupply(String assetName)                                                                     |  查询总供应量
   10 | String cliamOng(String sendAddr, String password, String to, long amount,long gas)                          |  提取ong
   11 | String unclaimOng(String address)                                                                           |  查询未提取的ong
      
      
      |                                         other   Function                                                       |           Description            
 -----|----------------------------------------------------------------------------------------------------------------|---------------------------------------------
    1 | String sendTransferToMany(String asset,String sendAddr, String pwd,String[] recvAddr,long[] amount,long gas)   |  转给多个地址
    2 | String sendTransferFromMany(String asset,String[] sendAddr,String[] pwd,String recvAddr,long[] amount,long gas)|  多个地址转给一个地址
```   

* Nep-5智能合约数字资产:

```  
      |                                         Main   Function                                       |           Description            
 -----|-----------------------------------------------------------------------------------------------|---------------------------------------------
    1 | String sendInit(String payer,String password,long gas)                                        |  初始化
    1 | String sendInitPreExec()                                                                      |  预执行初始化
    2 | String sendTransfer(String sendAddr, String pwd, String recvAddr, int amount,long gas)        |  转账
    3 |  String sendTransferPreExec(String sendAddr, String pwd, String recvAddr, int amount,long gas)|  预执行转账                              
    4 | String queryBalanceOf(String addr)                                                            |  查询余额
    5 | String queryTotalSupply()                                                                     |  查询总供应量 
    6 | String queryName()                                                                            |  查询名字
    7 | String queryDecimals()                                                                        |  查询精度
    8 | String querySymbol()                                                                          |  查询资产Symbol

```  

### 数字身份：
1.数字身份包括注册、公钥、属性、恢复人等操作。
2.claim接口包括颁发和验证
3.claim存证接口

* ontid功能接口：
```
      |                                         Main   Function                                                     |           Description            
 -----|-------------------------------------------------------------------------------------------------------------|---------------------------------------------
    1 | String getContractAddress()                                                                                 |  查询合约地址
    2 | Identity sendRegister(Identity ident, String password,String payer,String payerpassword,long gas)           |  注册ontid
    3 | Identity sendRegisterPreExec(Identity ident, String password,String payerpassword,String payer, long gas)   |  预执行注册ontid                              
    4 | Identity sendRegisterWithAttrs(Identity ident,String pwd,Map attrsMap,String payer,String payerpwd,long gas)|  注册ontid并添加属性
    5 | String sendAddPubKey(String ontid, String password, String newpubkey,String payer,String payerpwd,long gas) |  添加公钥    
    6 | String sendGetPublicKeys(String ontid)                                                                      |  获取公钥
    7 | String sendRemovePubKey(String ontid, String pwd, String removePubkey,String payer,String payerpwd,long gas)|  删除公钥
    8 | String sendGetKeyState(String ontid,int index)                                                              |  获取某公钥状态
    9 | String sendAddAttributes(String ontid, String pswd, Map attrsMap,String payer,String payerpassword,long gas)|  添加属性
   10 | String sendGetAttributes(String ontid)                                                                      |  查询属性
   11 | String sendRemoveAttribute(String ontid,String password,String path,String payer,String payerpwd,long gas)  |  删除属性
   12 | String sendAddRecovery(String ontid, String pwd, String recovery,String payer,String payerpwd,long gas)     |  添加恢复人
   13 | String sendChangeRecovery(String ontid, String newRecovery, String oldRecovery, String password,long gas)   |  修改恢复人
   14 | String sendGetDDO(String ontid)                                                                             |  查询DDO  
   
```

* 构造交易接口：

 ```  
     |                                           Make Transaction  Function                                                |     Description            
 ----|---------------------------------------------------------------------------------------------------------------------|------------------------ 
   1 | Transaction makeRegister(String ontid,String password,String payer,long gas)                                        |   构造注册交易
   2 | Transaction makeRegisterWithAttrs(String ontid,String password,Map<String, Object> attrsMap,String payer,long gas)  |   构造注册ontid并添加属性交易
   3 | Transaction makeAddPubKey(String ontid,String password,String newpubkey,String payer,long gas)                      |   构造添加公钥交易
   4 | Transaction makeRemovePubKey(String ontid, String password, String removePubkey,String payer,long gas)              |   构造删除公钥交易
   5 | Transaction makeAddAttributes(String ontid, String password, Map<String, Object> attrsMap,String payer,long gas)    |   构造添加属性交易
   6 | Transaction makeRemoveAttribute(String ontid,String password,String path,String payer,long gas)                     |   构造删除属性交易
   7 | Transaction makeAddRecovery(String ontid, String password, String recovery,String payer,long gas)                   |   构造添加恢复人交易
   
  ```
  
* Cliam相关接口：
  
 ```
     |                                           Claim Function                                                                      |     Description            
 ----|-------------------------------------------------------------------------------------------------------------------------------|------------------------
   1 | boolean verifyMerkleProof(String claim)                                                                                       |   验证merkle证明                
   2 | String createOntIdClaim(String signerOntid, String pwd, String context, Map claimMap, Map metaData,Map clmRevMap,long expire) |   创建claim
   3 | boolean verifyOntIdClaim(String claim)                                                                                        |   验证claim
  
 ```
 
* Cliam存证接口：
  
 ```
     |                                            Function                                           |     Description            
 ----|-----------------------------------------------------------------------------------------------|------------------------
   1 | String sendCommit(String issuerOntid,String pwd,String subjectOntid,String claimId,long gas)  |   存储claim              
   2 | String sendRevoke(String ontid,String password,String claimId,long gas)                       |   吊销
   3 | String sendGetStatus(String ontid,String password,String claimId)                             |   获取状态
  
 ```
 
 ### 智能合约部署与调用接口
 
 部署与调用
  ```
      |                                            Function                                                                                                                             |     Description            
  ----|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------
    1 | DeployCode makeDeployCodeTransaction(String code, boolean needStorage, String name, String version, String author, String email, String desp, byte vmtype,String payer,long gas)|   部署              
    2 | InvokeCode makeInvokeCodeTransaction(String codeAddr,String method,byte[] params, byte vmtype, String payer,long gas)                                                           |   调用
   
  ```

 ### 权限管理合约

* 权限管理功能接口：
 ```
       |                                            Function                                                                                                                               |     Description
   ----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------
     1 | String sendTransfer(String adminOntId,String password,String contractAddr, String newAdminOntID,int key,String payer,String payerpwd,long gaslimit,long gasprice)                 |   合约管理员转让合约管理权限
     2 | String assignFuncsToRole(String adminOntID,String password,String contractAddr,String role,String[] funcName,int key,String payer,String payerpwd,long gaslimit,long gasprice)    |   为角色分配函数
     3 | String assignOntIDsToRole(String adminOntId,String password,String contractAddr,String role,String[] ontIDs, int key,String payer,String payerpwd,long gaslimit,long gasprice)    |   绑定角色到实体身份
     4 | String delegate(String ontid,String password,String contractAddr,String toOntId,String role,int period,int level,int key,String payer,String payerpwd,long gaslimit,long gasprice)|   将合约调用权代理给其他人
     5 | String withdraw(String initiatorOntid,String password,String contractAddr,String delegate, String role,int key,String payer,String payerpwd,long gaslimit,long gasprice)          |   收回合约调用权
 ```

 * 构造交易接口：
```
       |                                            Function                                                                                                                               |     Description
   ----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------
     1 | Transaction makeTransfer(String adminOntID,String contractAddr, String newAdminOntID,int key,String payer,long gaslimit,long gasprice)                    |   合约管理员转让合约管理权限
     2 | Transaction makeAssignFuncsToRole(String adminOntID,String contractAddr,String role,String[] funcName,int key,String payer,long gaslimit,long gasprice)   |   为角色分配函数
     3 | Transaction makeAssignOntIDsToRole(String adminOntId,String contractAddr,String role,String[] ontIDs, int key,String payer,long gaslimit,long gasprice)   |   绑定角色到实体身份
     4 | Transaction makeDelegate(String ontid,String contractAddr,String toAddr,String role,int period,int level,int key,String payer,long gaslimit,long gasprice)|   将合约调用权代理给其他人
     5 | Transaction makeWithDraw(String ontid,String contractAddr,String delegate, String role,int key,String payer,long gaslimit,long gasprice)                  |   收回合约调用权
 ```