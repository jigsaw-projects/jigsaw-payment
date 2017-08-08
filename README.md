# jigsaw-payment

jigsaw payment 支付系统，面向互联网商户提供的支付系统。 支付系统几乎是互联网应用必不可少的模块，也是各个互联网应用中少数可以标准化的系统。 
本系统是基于[凤凰牌老熊的系列文章](http://blog.lixf.cn) 而设计的，采用微服务架构。 命名为Jigsaw的目的是让使用者可以像搭积木一样，易于定制、易于扩展。 

## 入门

- 关于支付的领域知识，请阅读[凤凰牌老熊的系列文章](http://blog.lixf.cn) 
- 关于微服务架构，请参考[Martin叔叔说微服务](https://martinfowler.com/articles/microservices.html)

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

RPC引擎服务器端示例。 展示如何通过springframework的@Component标签来实现一个新的接口。 

### jigsaw-rpc-example-client

RPC引擎客户端示例，和jigsaw-rpc-example-server配合使用。 

### jigsaw-rpc-schema

支付数据规范，使用Google Protocol Buffer格式来定义。 

