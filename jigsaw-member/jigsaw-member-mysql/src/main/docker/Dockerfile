FROM mysql:5.7
MAINTAINER jigsaw paymentrpc@gmail.com
ARG character-set-server=utf8mb4  
ARG collation-server=utf8mb4_unicode_ci  
ARG max_allowed_packet=32M

ENV MYSQL_DATABASE=jigsaw-member  \
    MYSQL_ROOT_PASSWORD=zaq1XSW@ \
    MYSQL_USER=jigsaw  \
    MYSQL_PASSWORD=zaq1XSW@  

COPY *.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
