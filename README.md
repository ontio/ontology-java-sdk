[中文版](README_cn.md)

<p align="center">
  <img
    src="https://ont.io/static/img/firstpagelogo.b81628b.jpg"
    width="125px"
  >
</p>

<h1 align="center">JAVA SDK For Ontology </h1>


## Overview


JAVA SDK For Ontology主要包括的功能如下，

* 本地钱包管理
* 数字身份管理
* 存证管理
* 智能合约调用
* 数字资产管理
* 与节点通信
* 错误码

更多请参考[接口详细说明](docs/en/api_desc.md)。

## Getting started


### 环境准备

请配置JRE 8及以上版本。

> **Note:** 由于SDK中使用的密钥长度大于128，由于安全策略文件受限的原因，需要去官网下载local_policy.jar和US_export_policy.jar，替换jre目录中${java_home}/jre/lib/security原有的与安全策略这两个jar即可。

下载地址：
>http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html


### 编译

```
mvn clean install
```

### 运行前准备

* 请确定Ontology Node已经部署OK，其RPC端口已经打开，SDK将此连接地址作为URL参数进行初始化参数。
* 请确定本地钱包的存储路径，钱包用于存储数字身份信息和数字资产相关的密钥控制信息，可以文件形式，也可以是数据库形式。


## Contribution

`ont-sdk-java` always encourages community code contribution. Before contributing please read the [contributor guidelines](.github/CONTRIBUTING.md) and search the issue tracker as your issue may have already been discussed or fixed. To contribute, fork `ont-sdk-java`, commit your changes and submit a pull request.

By contributing to `ont-sdk-java`, you agree that your contributions will be licensed under its MIT license.

## License

* Open-source [MIT](LICENSE.md).
