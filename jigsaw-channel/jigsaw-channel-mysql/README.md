# MySQL用户库

这是提供给jigsaw-rpc-user模块使用的mysql镜像

## 一、开发指南

使用fabric8的maven插件docker-maven-plugin来管理镜像
- 构建镜像：       mvn docker:build 或者 mvn clean install
- 非阻塞启动镜像：  mvn docker:start, 开发其他模块时，请使用这个命令 
- 阻塞启动：	 mvn  docker:run
- 停止镜像：       mvn docker:stop 
- 删除镜像：       mvn -Ddocker.removeAll docker:remove 
- 查看镜像日志：    mvn docker:logs 

## 二、TODO
1. 完善数据表设置
