# 用户管理模块

## 1. 开发环境配置

-jigsaw-zookeeper： 使用这个项目来运行zookeeper的docker。  
-jigsaw-mysql-user: 使用这个项目来建立和运行该模块需要的mysql数据库docker。注意，所有初始化的sql语句也都在这个项目中管理。   

默认的日志打印在/var/log/jigsaw目录下，注意设置这个目录的权限为当前用户可读写。 

开发时，首先进入jigsaw-zookeeper目录，运行：
```bash
[jigsaw@workspace jigsaw-zookeeper]$ mvn docker:build
```
构建镜像。 之后运行

```bash
[jigsaw@workspace jigsaw-zookeeper]$ mvn docker:start
```
启动镜像。 

对 jigsaw-mysql-user也采用相同的命令来构建和启动镜像。 

接着动手吧～

