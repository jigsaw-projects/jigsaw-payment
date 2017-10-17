# jigsaw-payment

jigsaw payment 支付系统，面向互联网商户提供的支付系统。 支付系统几乎是互联网应用必不可少的模块，也是各个互联网应用中少数可以标准化的系统。 
本系统是基于[凤凰牌老熊的系列文章](http://blog.lixf.cn) 而设计的，采用微服务架构。

## 项目起源

2017年8月份，Goldman（高盛）公司发表了一份题为《The Rise of China FinTech》的报告，指出支付是中国互联网金融的网关，也是互联网公司的基础设施。 
各个互联网公司在收单和代付方面上的需求是有共性的，可以作为一个通用系统来开发。 

 目前多数公司的支付系统是在公司早期技术力量比较薄弱的时候建立的，
也有不少系统是从银行或者第三方支付公司的支付系统基础上建立起来的，技术上比较保守，架构上设计不合理。 很多系统大量地使用Map类型数据来做接口的输入输出，
这导致支付系统的开发和维护难度很高。

支付对交易的安全和可靠性要求高。除了从设计上需要尽可能地保证外，还需要从使用经验中吸取教训，避免重复踩坑。 每个坑都意味着公司的经济上的损失。 
通过这种方式，我们可以汇总各种场景下的使用问题，并以此来更新这些软件，避免问题的扩散。  

命名为Jigsaw(七巧板)的目的是让使用者可以像搭积木一样，易于定制、容易扩展。我们希望建立一个开源系统， 使用新的、成熟的微服务架构，合理引入新技术，
以产品需求为主导，实现一个架构合理、代码优雅、易于使用的支付系统。 

项目文档参见 http://jigsaw.lixf.cn。 这里仅是一个简单的入门介绍。 

## 入门

- 关于支付的领域知识，请阅读[凤凰牌老熊的系列文章](http://blog.lixf.cn) 
- 关于微服务架构，请参考[Martin叔叔说微服务](https://martinfowler.com/articles/microservices.html)

## 需要安装的软件

1. Apache Thrift
2. Google Protocol Buffer
3. MySQL, 新建用户payment，密码123456，新建库jigasw_payment_0, jigsaw_payment_1, 导入相关的sql文件； 
4. redis
5. zookeeper，新增用户payment,密码123456，节点/payment/rpc

请自行查阅相关文件来安装。

## 构建 

你可以直接使用[已发布的版本](http://repo.lixf.cn)。如果需要验证最新的版本，可以下载本项目的代码，使用JDK1.8，运行如下命令：

```javascript
mvn clean install
```

## 模块列表

### jigsaw-thrift-protobuf

这是RPC引擎，使用Apache Thrift 作为容器， Google Protocol Buffer 作为输入输出。相对于dubbo、 纯Apache Thrift等RPC容器，优势在于：

1.  高性能，Apache Thrift是已知RPC容器中性能最好的。 
2.  传输效率高， Google Protocol Buffer 的压缩率相对Apache Thrift 的strut 结构 可以节省20% 空间。 
3.  可扩展性好，得益于Protobuf优越的兼容性设计，对接口参数进行调整时，对老接口仍然可以保持很好的兼容。 

这个引擎是对Apache Thrift 的极简轻量级封装，可靠，易于使用。 和Spring 良好集成，易于开发。 

### jigsaw-rpc-example-server

RPC引擎服务器端示例。 展示如何通过springframework的@Component标签来实现一个新的

### jigsaw-rpc-example-client

RPC引擎客户端示例，和jigsaw-rpc-example-server配合使用。 

### jigsaw-rpc-schema

支付数据规范，使用Google Protocol Buffer格式来定义。 

## 支持jigsaw payment 项目开发

### 如果您是开发人员

本项目在2017年9月份启动，如果您是：
1. 每周有4小时以上的时间可以投入；
2. 有Java软件开发经验，熟悉Linux；
3.  （或）熟悉微服务系统的基础设施建设；
我们特别期待您的加入。 请微信关注“凤凰牌老熊”的公众号并留言，在留言中说明现在所在的公司、从事的工作、每天可以投入的时间，以及期待从事的开发模块。
从[这里了解现有模块列表](http://jigsaw.lixf.cn/dev/2017/10/07/github-2/)。

### 期待您的资金支持

Jigsaw Payment 需要您的支持来维持正常的运转，特别是托管服务器采购的费用。 
微信捐赠支持，请扫码：
![微信收款码](http://jigsaw.lixf.cn/img/in-post/pay.jpg)

