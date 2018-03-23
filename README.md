[中文版](README_cn.md)

<h1 align="center">JAVA SDK For Ontology  </h1>
<h4 align="center">Version V0.6.0 </h4>

## Overview

The project is an ontology official Java SDK, which is a comprehensive SDK. Currently, it supports local wallet management, digital identity management, digital asset management,  deployment and envoke for Smart Contract , and communication with Ontology Blockchain. The future will also support more rich functions and applications .

## Getting started

Visit the [Getting Started](http://opendoc.ont.io/javasdk/en) to learn how to use this library.

## Installation

### Environment 

Please configure JDK 8 and above.

> **Note:** As the length of key used in SDK is greater than 128, due to the restriction of JAVA security policy files, it is necessary to download local_policy.jar and US_export_policy.jar from the official website , to replace the two jar of ${java_home}/jre/lib/security in JRE directory.
下载地址：
>http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html


### Build

```
mvn clean install
```

### Preparations

* Make sure Ontology Blockchain has deployed well,  RPC port has been opened, and SDK will connect the RPC server to initialize.





