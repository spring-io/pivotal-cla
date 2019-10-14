#!/bin/sh
DIR="$( cd "$(dirname "$0")" ; pwd -P )"
$DIR/docker/docker-mysql.sh &
$DIR/docker/docker-redis.sh &