#!/bin/sh

sudo docker run --name jigsaw-zookeeper --privileged=true -d -p 2181:2181 docker.lixf.cn/jigsaw-zookeeper:1.0.0

