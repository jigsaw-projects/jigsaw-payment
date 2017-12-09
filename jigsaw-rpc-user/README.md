## 用户管理模块

## 1. 依赖

-jigsaw-zookeeper： 使用这个项目来运行zookeeper的docker。
-jigsaw-mysql-user: 使用这个项目来建立和运行该模块需要的mysql数据库docker。注意，所有初始化的sql语句也都在这个项目中管理。 

默认的日志打印在/var/log/jigsaw目录下，注意设置这个目录的权限。 

## 2. 注意
在运行测试时，必须先启动jigsaw-zookeeper和jigsaw-mysql-user的docker。 启动方法参见原项目说明。 
