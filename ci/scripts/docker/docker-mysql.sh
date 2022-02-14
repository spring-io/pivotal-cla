#!/bin/sh
# https://hub.docker.com/_/mysql
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
docker run --rm --name pcla-mysql -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 -v "$DIR/mysql-init.sh:/docker-entrypoint-initdb.d/mysql-init.sh" mysql:5.7.37