FROM  centos:7.4.1708
MAINTAINER Shamphone Lee <shamphone@gmail.com>

ENV JAVA_HOME=/usr/java/default

COPY jdk1.8.0_152 /usr/java/jdk1.8.0_152

RUN export JAVA_DIR=/usr/java/jdk1.8.0_152 && \
    ln -s $JAVA_DIR /usr/java/latest && \
    ln -s $JAVA_DIR /usr/java/default && \
    alternatives --install /usr/bin/java java $JAVA_DIR/bin/java 20000 && \
    alternatives --install /usr/bin/javac javac $JAVA_DIR/bin/javac 20000 && \
    alternatives --install /usr/bin/jar jar $JAVA_DIR/bin/jar 20000 && \
    mkdir /usr/jigsaw

