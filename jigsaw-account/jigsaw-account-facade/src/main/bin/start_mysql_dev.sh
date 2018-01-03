#!/bin/sh

sudo docker run --name jigsaw-mysql-user --privileged=true -v /var/data/jigsaw-mysql-user:/var/lib/mysql -d -p 3306:3306 docker.lixf.cn/jigsaw-user:1.0.0

