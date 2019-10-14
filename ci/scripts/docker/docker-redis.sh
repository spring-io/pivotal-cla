#!/bin/sh
# https://hub.docker.com/_/redis
docker run --rm --name pcla-redis -p 6379:6379 redis:5.0.6