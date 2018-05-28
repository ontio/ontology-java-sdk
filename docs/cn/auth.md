<h1 align="center"> Ontology Java SDK User Guide </h1>
<p align="center" class="version">Version 0.7.0 </p>

# 权限管理
具体请参考https://github.com/kunxian-xia/ontology/blob/auth_doc/docs/specifications/native_contract/auth_zh.md#%E5%BA%94%E7%94%A8%E5%90%88%E7%BA%A6%E8%B0%83%E7%94%A8%E6%9D%83%E9%99%90%E7%AE%A1%E7%90%86

接口列表

1. String sendTransfer(String adminOntId,String password,String contractAddr, String newAdminOntID,int key,String payer,String payerpwd,long gaslimit,long gasprice)

    功能说明： 合约管理员转让合约管理权限

    参数说明：

    adminOntId：合约管理员ontid

    password： 合约管理员密码

    contractAddr ： 合约地址

    newAdminOntID ： 新的管理员

    key ： 合约管理员的公钥编号

    payer：付费账号地址

    payerpwd：付费账号密码

    gaslimit：用于计算消耗的gas，gaslimit*gasprice等于消耗的gas数量

    gasprice：gas价格

    返回值：交易hash

2. String assignFuncsToRole(String adminOntID,String password,String contractAddr,String role,String[] funcName,int key,String payer,String payerpwd,long gaslimit,long gasprice)

    功能说明： 为角色分配函数

    参数说明：

    adminOntId：合约管理员ontid

    password： 合约管理员密码

    contractAddr ： 合约地址

    role ： 角色

    funcName：函数名数组

    key ： 合约管理员的公钥编号

    payer：付费账号地址

    payerpwd：付费账号密码

    gaslimit：用于计算消耗的gas，gaslimit*gasprice等于消耗的gas数量

    gasprice：gas价格

    返回值：交易hash

3. String assignOntIDsToRole(String adminOntId,String password,String contractAddr,String role,String[] ontIDs, int key,String payer,String payerpwd,long gaslimit,long gasprice)

     功能说明： 绑定角色到实体身份

     必须由合约管理者调用，ontIDs数组中的ONT ID被分配role角色，最后返回true。 在当前实现中，权限token的级别level默认等于2。

     参数说明：

     adminOntId：合约管理员ontid

     password： 合约管理员密码

     contractAddr ： 合约地址

     role ： 角色

     ontIDs：ontid数组

     key ： 合约管理员的公钥编号

     payer：付费账号地址

     payerpwd：付费账号密码

     gaslimit：用于计算消耗的gas，gaslimit*gasprice等于消耗的gas数量

     gasprice：gas价格

     返回值：交易hash

4. String delegate(String ontid,String password,String contractAddr,String toOntId,String role,int period,int level,int key,String payer,String payerpwd,long gaslimit,long gasprice)

     功能说明： 将合约调用权代理给其他人

     角色拥有者可以将角色代理给其他人，from是转让者的ONT ID，to是代理人的ONT ID，role表示要代理的角色，period参数指定委托任期时间（以second为单位）。

     代理人可以再次将其角色代理给更多的人，level参数指定委托层次深度。例如，

         level = 1: 此时代理人就无法将其角色再次代理出去；当前实现只支持此情况。


     参数说明：

     ontid：拥有合约中某个函数调用权的ontid

     password： ontid的密码

     contractAddr ： 合约地址

     toOntId：接收合约调用权的ontid

     role ： 角色

     period：以秒为单位的时间

     key ： ontid的公钥编号

     payer：付费账号地址

     payerpwd：付费账号密码

     gaslimit：用于计算消耗的gas，gaslimit*gasprice等于消耗的gas数量

     gasprice：gas价格

     返回值：交易hash

5. String withdraw(String initiatorOntid,String password,String contractAddr,String delegate, String role,int key,String payer,String payerpwd,long gaslimit,long gasprice)

     功能说明： 收回合约调用权（配合delegate使用）

     角色拥有者可以提前将角色代理提前撤回，initiatorOntid是发起者，delegate是角色代理人，initiator将代理给delegate的角色提前撤回。

     参数说明：

     initiatorOntid：将合约调用权转让给其他人的ontid，

     password： ontid的密码

     contractAddr ： 合约地址

     delegate：代理人ontid

     role ： 角色

     key ： ontid的公钥编号

     payer：付费账号地址

     payerpwd：付费账号密码

     gaslimit：用于计算消耗的gas，gaslimit*gasprice等于消耗的gas数量

     gasprice：gas价格

     返回值：交易hash