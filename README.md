# abitmiss ftpserver


[README](README.md) | [中文文档](README_zh.md)


abitmiss ftpserver support different file system, it can be used to start a ftp server on hadoop file system, native file system. other file systems will can be added in future version.it is based on apache ftpserver(100% pure java,free,open source FTP server).

**at the current stage, abitmiss ftpserver can be install on the hadoop file system and the native file system(implemented in apache ftpserver by default)**

abitmiss ftpserver is under development and you can try it with the latest release version.Thanks to the Apache Foundation open source project ftpserver.

## Table of Contents

* [Architecture](#architecture)
* [Get Start](#Get-Start)
* [Advance](#Advance)
* [Thanks](#Thanks)
* [Concact](#Concact)
* [Donate](#Donate)


## Architecture

![architecture](/pic/architecture.png)

## Get Start

##### 1. Download the latest program from [[RELEASE](https://github.com/wangfeng0124/abitmiss-ftpserver/releases)] page.
```
curl -O https://github.com/wangfeng0124/abitmiss-ftpserver/releases/download/0.2.0/abitmiss-ftpserver-0.2.0-RELEASE.tar.gz
```
##### 2. Upload to host and Decompress tar.gz file
```
tar -zxf abitmiss-ftpserver-0.2.0-RELEASE.tar.gz
```
##### 3. modify abimiss-ftpserver/sbin/env-conf.sh
if you are using native file system, modify those config
```
NATIVE_FS_FLAG="false" --> NATIVE_FS_FLAG="true" # any word
CUS_FTPSEVER_XML=cus-hdfs-ftpd.xml --> CUS_FTPSEVER_XML=cus-native-ftpd.xml
```
if you are using hadoop file systems, modify those config
```
CUS_FS_CONF_DIR=/etc/hadoop/conf # modify this to your hadoop configuration dir which contains core-site.xml, hdfs-site.xml...
```
##### 4. modify xml config file
native file system --> abitmiss-ftpserver/conf/cus-native-ftpd.xml

hadoop file system --> abitmiss-ftpserver/conf/cus-hdfs-ftpd.xml
```
you can config ftpserver port in this config file
```
##### 5. modify ftpserver user properties
```
format: ftpserver.user.${username}.${key}=${value}

notice: password value is md5 encrypted by default
```

## Advance
this app base on apache ftpserver, any xml config is compatible with apache ftpserver. so, Many thanks to the Apache Foundation's open source project.

##### 1. xml configuration instruction
you can get more detail at [apache ftpserver document](https://mina.apache.org/ftpserver-project/documentation.html) .

## Thanks
##### 1. thanks to the Apache Foundation's open source project
##### 2. thanks to IPONWEB's github project: hdfs-over-ftp. abitmiss ftpserver drawing on its ideas
##### 3. thanks to my company, it gives me a environment to complete it.

## Concact
if you have some question or improvement suggestion to me, you can contact me with this email: wangfeng0124@gmail.com
## Donate
if abitmiss-ftpserver helps you a lot, you can support me as follow:
#### Alipay
![alipay](/pic/alipay.png)
#### Wechat Pay
![wechat](/pic/wechat.png)
#### visa Credit Cart Pay
CHINA MERCHANTS BANK
4514 6176 4383 8511
