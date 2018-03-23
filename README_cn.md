[中文版](README_cn.md)

<h1 align="center">JAVA SDK For Ontology  </h1>
<h4 align="center">版本 V0.6.0 </h4>

## 总体介绍

该项目是本体官方Java SDK，它是一个综合性SDK，目前支持：本地钱包管理、数字身份管理、数字资产管理、智能合约部署和调用、与节点通信等。未来还将支持更丰富的功能和应用。

>>进入[开发指南](http://opendoc.ont.io/javasdk/en)开始使用。

## 如何安装

### 环境准备

请配置JDK 8及以上版本。

> **注：** 由于SDK中使用的密钥长度大于128，由于安全策略文件受限的原因，需要去官网下载local_policy.jar和US_export_policy.jar，替换jre目录中${java_home}/jre/lib/security原有的与安全策略这两个jar即可。

下载地址：
>http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html


### 编译

```
mvn clean install
```

### 运行前准备

* 请确定Ontology Blockchain已经部署OK，其RPC端口已经打开，SDK将此连接地址作为URL参数进行初始化参数。




