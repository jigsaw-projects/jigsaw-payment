## 功能

这是RPC引擎，使用Apache Thrift 作为容器， Google Protocol Buffer 作为输入输出。相对于dubbo、 纯Apache Thrift等RPC容器，优势在于：

1.  高性能，Apache Thrift是已知RPC容器中性能最好的。 
2.  传输效率高， Google Protocol Buffer 的压缩率相对Apache Thrift 的strut 结构 可以节省20% 空间。 
3.  可扩展性好，得益于Protobuf优越的兼容性设计，对接口参数进行调整时，对老接口仍然可以保持很好的兼容。 

这个引擎是对Apache Thrift 的极简轻量级封装，可靠，易于使用。 和Spring 良好集成，易于开发。 

## TODO

1. 支持consul作为服务注册中心。   
2. 支持按照服务器端的能力进行分片访问。 


## Finished

1. 使用zookeeper作为服务注册中心。 
2. 支持简单的轮询式分片访问。 