# Jigsaw Zookeeper配置

## 一、开发指南

使用fabric8的maven插件docker-maven-plugin来管理镜像
- 构建镜像：       mvn docker:build 或者 mvn clean install
- 启动镜像：       mvn docker:start 非阻塞启动或者 docker：run， 注意，这个操作是阻塞的。 
- 停止镜像：       mvn docker:stop  
- 删除镜像：       mvn -Ddocker.removeAll docker:remove  
- 查看镜像日志：    mvn docker:logs 
