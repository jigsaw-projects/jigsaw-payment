## 功能

这是RPC引擎，使用Apache Thrift 作为容器， Google Protocol Buffer 作为输入输出。相对于dubbo、 纯Apache Thrift等RPC容器，优势在于：

1.  高性能，Apache Thrift是已知RPC容器中性能最好的。
2.  传输效率高， Google Protocol Buffer 的压缩率相对Apache Thrift 的strut 结构 可以节省20% 空间。
3.  可扩展性好，得益于Protobuf优越的兼容性设计，对接口参数进行调整时，对老接口仍然可以保持很好的兼容。

这个引擎是对Apache Thrift 的极简轻量级封装，可靠，易于使用。 和Spring 良好集成，易于开发。

## TODO 
|No.|功能|负责人|
|---------|---------|---------|
|1|需要支持consul作为服务注册中心｜梁燕东|
|2|server注册到注册中心时，需要申明自己的能力，也就是可以承受的QPS|泽西|
|3|支持json/http访问内部服务|杜雷|
|4|追加失败策|yifeng|
|5|Mock返回功能server ｜待定|
|6|支持按照服务器端的能力进行分片访问|待定|


## Finished

1. 使用zookeeper作为服务注册中心。
2. 支持简单的轮询式分片访问。
