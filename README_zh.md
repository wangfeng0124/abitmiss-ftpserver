# abitmiss ftpserver


[README](README_zh.md) | [ENGLISH DOC](README.md)

abitmiss ftpserver支持不同的文件系统，可以在hadoop文件系统，本机文件系统上启动ftp服务器。 其他文件系统将在未来版本中添加。它基于apache ftpserver（100％纯java，免费，开源FTP服务器）。

**在当前阶段，abitmiss ftpserver可以安装在hadoop文件系统和本机文件系统上（本机文件系统支持，默认情况下在apache ftpserver中实现）**

abitmiss ftpserver正在开发中，你可以尝试使用最新的发布版本。感谢Apache Foundation开源项目ftpserver。

## 目录

* [结构](#结构)
* [开始](#开始)
* [高级配置](#高级配置)
* [鸣谢](#鸣谢)
* [联系方式](#联系方式)
* [捐献](#捐献)


## 结构

![结构](/pic/architecture.png)

## 开始

##### 1. 从 [[RELEASE](https://github.com/wangfeng0124/abitmiss-ftpserver/releases)] 页面中下载最近的版本
```
curl -O https://github.com/wangfeng0124/abitmiss-ftpserver/releases/download/0.2.0/abitmiss-ftpserver-0.2.0-RELEASE.tar.gz
```
##### 2. 上传到主机并解压tar.gz文件
```
tar -zxf abitmiss-ftpserver-0.2.0-RELEASE.tar.gz
```
##### 3. 修改文件 abimiss-ftpserver/sbin/env-conf.sh
如果你使用本机文件系统，修改这些配置
```
NATIVE_FS_FLAG="false" --> NATIVE_FS_FLAG="true" # any word
CUS_FTPSEVER_XML=cus-hdfs-ftpd.xml --> CUS_FTPSEVER_XML=cus-native-ftpd.xml
```
如果你使用hadoop文件系统，修改这些配置
```
CUS_FS_CONF_DIR=/etc/hadoop/conf # modify this to your hadoop configuration dir which contains core-site.xml, hdfs-site.xml...
```
##### 4. 修改xml配置文件
本机文件系统  --> abitmiss-ftpserver/conf/cus-native-ftpd.xml

hadoop文件系统  --> abitmiss-ftpserver/conf/cus-hdfs-ftpd.xml
```
你可以配置ftpserver端口在这个配置文件中
```
##### 5. 修改ftpserver的用户属性
```
格式： ftpserver.user.${username}.${key}=${value}

注意： 默认密码是md5加密
```

## 高级配置

这个应用程序基于apache ftpserver，任何xml配置都与apache ftpserver兼容。 所以，非常感谢Apache Foundation的开源项目。

##### 1. xml 配置说明
你可以获得更多细节在：[apache ftpserver document](https://mina.apache.org/ftpserver-project/documentation.html) .

## 鸣谢
##### 1. 感谢apache基金会开源项目
##### 2. 感谢IPONWEB在github上的开源项目： hdfs-over-ftp。abitmiss ftpserver 借鉴了它的思想
##### 3. 感谢我的公司，它供给了我一个良好的环境去完成它

## 联系方式
如果你有一些问题或者提高建议给我，你可以通过email(wangfeng0124@gmail.com)的方式联系我
## 捐献
如果abitmiss-ftpserver帮助了你，你可以通过如下的方式支持我。
#### 阿里支付
![alipay](/pic/alipay.png)
#### 微信支付
![wechat](/pic/wechat.png)
#### visa信用卡支付
中国招商银行
4514 6176 4383 8511
