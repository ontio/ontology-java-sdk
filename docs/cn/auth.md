<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 权限管理

当前，智能合约的函数可以被任何人调用，这显然不符合现实要求。基于角色的权限管理的基本思想是，每个角色可以调用部分函数，每个实体可以被赋予多种角色（实体是由其ONT ID来标识）。

如果智能合约需要增加权限管理功能，那就必须记录合约中分配的角色，以及角色可调用的函数，哪些实体具有该角色等等信息。这个工作比较繁琐，可交由一个系统合约来管理。

具体请参考https://github.com/ontio/ontology-smartcontract/blob/master/smartcontract/native/auth/auth.md


* 权限管理合约管理

* 接口列表

### 权限管理合约管理

Auth合约负责管理应用合约的函数调用权限，功能有合约管理员可以转让合约管理权限，合约管理员为角色分配函数，合约管理员绑定角色到实体身份，有合约函数调用权的实体将合约调用权代理给其他人，合约管理员收回合约调用权，实体验证合约调用token的有效性。

接口列表

1. String sendTransfer(String adminOntId, String password, byte[] salt, String contractAddr, String newAdminOntID, long keyNo, Account payerAcct, long gaslimit, long gasprice)

    |说明||描述|
    |:--|:--|:--|
    |功能说明|合约管理员转让合约管理权限|此函数必须由合约管理员调用，即将会以adminOntID名下编号为keyNo的公钥来验证交易签名是否合法。|
    |参数说明|字段|描述|
    ||adminOntId|合约管理员ontid|
    ||password|合约管理员密码|
    ||slat |私钥解密参数|
    ||contractAddr|合约地址|
    ||newAdminOntID|新的管理员|
    ||keyNo|合约管理员的公钥编号|
    ||payerAcct|付费账户|
    ||gaslimit|gas价格|
    ||gasprice|gas价格|
    |返回值说明|交易hash||

2. String assignFuncsToRole(String adminOntID,String password,byte[] salt,String contractAddr,String role,String[] funcName,long keyNo,Account payerAcct,long gaslimit,long gasprice)

    |说明||描述|
    |:--|:--|:--|
    |功能说明|为角色分配函数|必须由合约管理者调用，将所有函数自动绑定到role，若已经绑定，自动跳过，最后返回true。|
    |参数说明|字段|描述|
    ||adminOntId|合约管理员ontid|
    ||password|合约管理员密码|
    ||salt|私钥解密参数|
    ||contractAddr|合约地址|
    ||role|角色|
    ||funcName|函数名数组|
    ||keyNo|合约管理员的公钥编号|
    ||payerAcct|付费账户|
    ||gaslimit|gas价格|
    ||gasprice|gas价格|
    |返回值说明|交易hash||

3. String assignOntIDsToRole(String adminOntId,String password,byte[] salt, String contractAddr,String role,String[] ontIDs,long keyNo,Account payerAcct,long gaslimit,long gasprice)

     |说明||描述|
     |:--|:--|:--|
     |功能说明|绑定角色到实体身份|必须由合约管理者调用，ontIDs数组中的ONT ID被分配role角色，最后返回true。 在当前实现中，权限token的级别level默认等于2。|
     |参数说明|字段|描述|
     ||adminOntId|合约管理员ontid|
     ||password|合约管理员密码|
     ||salt|私钥解密参数|
     ||contractAddr|合约地址|
     ||role|角色|
     ||ontIDs|ontid数组|
     ||keyNo|合约管理员的公钥编号|
     ||payerAcct|付费账户|
     ||gaslimit|gas价格|
     ||gasprice|gas价格|
     |返回值说明|交易hash||

4. String delegate(String ontid,String password,byte[] salt,String contractAddr,String toOntId,String role,long period,long level,long keyNo,Account payerAcct,long gaslimit,long gasprice)

     角色拥有者可以将角色代理给其他人，from是转让者的ONT ID，to是代理人的ONT ID，role表示要代理的角色，period参数指定委托任期时间（以second为单位）。

     代理人可以再次将其角色代理给更多的人，level参数指定委托层次深度。例如，

         level = 1: 此时代理人就无法将其角色再次代理出去；当前实现只支持此情况。

     |说明||描述|
     |:--|:--|:--|
     |功能说明|将合约调用权代理给其他人||
     |参数说明|字段|描述|
     ||ontid|拥有合约中某个函数调用权的ontid|
     ||password|ontid密码|
     ||salt|私钥解密参数|
     ||contractAddr|合约地址|
     ||toOntId|接收合约调用权的ontid|
     ||role|角色|
     ||period|以秒为单位的时间|
     ||keyNo|ontid的公钥编号|
     ||payerAcct|付费账户|
     ||gaslimit|gas价格|
     ||gasprice|gas价格|
     |返回值说明|交易hash||

5. String withdraw(String initiatorOntid,String password,byte[] salt,String contractAddr,String delegate, String role,long keyNo,Account payerAcct,long gaslimit,long gasprice)

     角色拥有者可以提前将角色代理提前撤回，initiatorOntid是发起者，delegate是角色代理人，initiator将代理给delegate的角色提前撤回。

     |说明||描述|
     |:--|:--|:--|
     |功能说明|收回合约调用权（配合delegate使用）||
     |参数说明|字段|描述|
     ||initiatorOntid|将合约调用权转让给其他人的ontid|
     ||password|ontid密码|
     ||salt|私钥解密参数|
     ||contractAddr|合约地址|
     ||delegate|代理人ontid|
     ||role|角色|
     ||keyNo|ontid的公钥编号|
     ||payerAcct|付费账户|
     ||gaslimit|gas价格|
     ||gasprice|gas价格|
     |返回值说明|交易hash||

6. String verifyToken(String ontid,String password,byte[] salt,String contractAddr,String funcName,long keyNo)

      |说明||描述|
      |:--|:--|:--|
      |功能说明|验证合约调用token的有效性||
      |参数说明|字段|描述|
      ||ontid|验证的ontid|
      ||password|ontid密码|
      ||salt|私钥解密参数|
      ||contractAddr|合约地址|
      ||funcName|函数名|
      ||keyNo|ontid的公钥编号|
      |返回值说明|交易hash||