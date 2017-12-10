## 一、功能

jigsaw-thrift-protobuf模块是jigsaw项目的RPC容器和客户端连接池的实现。目前由于这两个功能耦合度较高，都放在一个项目中实现。

RPC容器，支持：
1. 服务注册： 这个版本是注册到zookeeper上，之后会提供对consul的支持。
2. Spring MVC支持，将thrift service实现映射到Spring Controller上，通过controller的name来locate服务。 

客户端提供RPC连接池，支持：
1. 服务发现
2. 负载均衡：在客户端实现。

## 二、技术栈

使用Apache Thrift 作为容器， Google Protocol Buffer 作为输入输出。相对于dubbo、 纯Apache Thrift等RPC容器，优势在于：

1.  高性能，Apache Thrift是已知RPC容器中性能最好的。
2.  传输效率高， Google Protocol Buffer 的压缩率相对Apache Thrift 的strut 结构 可以节省20% 空间。
3.  可扩展性好，得益于Protobuf优越的兼容性设计，对接口参数进行调整时，对老接口仍然可以保持很好的兼容。

这个引擎是对Apache Thrift 的极简轻量级封装，可靠，易于使用。 和Spring 良好集成，易于开发。

## 三、项目结构

     .
     ├── src/main
     │    ├── gen                       # 由rpc_service.thrift自动编译出来的代码
     │    │    └── ..  
     │    ├── org/jigsaw/payment/rpc
     │    │    ├── register            # 服务注册
     │    │    ├── server              # 服务器端的实现框架
     │    │    └── sharder             # 客户端的rpc连接池   
     │    └── resources
     │          ├── META-INF
     │          │     └── srping.factories  
     │          └── rpc_service.thrif
     ├── CHANGELOG.md
     ├── jigsaw-thrift-protobuf.iml
     ├── pom.xm
     └── README.md

## 四、关联模块

 这两个模块是用来测试这个模块的服务端和客户端  
jigsaw-rpc-example-client  
jigsaw-rpc-example-server  

## 五、Docker支持

这个模块输出的docker是其他rpc模块的base image，注意：
1. 这个image是基于centos来构建的。 
2. 预装oracle server jre。 

注意：
**这个版本支持jdk1.8.0_152版本，必须先下载这个版本，并解压缩后，放到src/main/docker下，目录为 src/main/docker/jdk1.8.0_152**

TODO： 
1. 进一步优化base image， 删除不必要的模块。 
2. 增加监控和日志收集组件。



## TODO

|No.|功能|负责人|github 账号|
|---------|---------|---------|---------|
|1|需要支持consul作为服务注册中心|梁燕东|liangyd1024|
|2|server注册到注册中心时，需要申明自己的能力，也就是可以承受的QPS|泽西||
|3|支持json/http访问内部服务|杜雷|Tony-dulei|
|4|追加失败策|yifeng|yifeng0898|
|5|Mock返回功能server|待定||
|6|支持按照服务器端的能力进行分片访问|待定||


## Finished

1. 使用zookeeper作为服务注册中心。
2. 支持简单的轮询式分片访问。