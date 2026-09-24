#!/usr/bin/env bash
set -euo pipefail

REDIS_PORT=6380
PID_FILE=/tmp/ss17-hw05-redis.pid

start_redis() {
  redis-server --port "$REDIS_PORT" --daemonize yes --pidfile "$PID_FILE"
  sleep 1
}

stop_redis() {
  redis-cli -p "$REDIS_PORT" shutdown nosave >/dev/null 2>&1 || true
  sleep 1
}

echo "1. Redis đang chạy"
start_redis
redis-cli -p "$REDIS_PORT" ping
curl -s -o /tmp/hw05-running.json -w 'HTTP %{http_code}\n' http://localhost:8080/menu/1
cat /tmp/hw05-running.json

echo "2. Tắt Redis và gọi lại API"
stop_redis
redis-cli -p "$REDIS_PORT" ping 2>&1 || true
curl -s -o /tmp/hw05-stopped.json -w 'HTTP %{http_code}\n' http://localhost:8080/menu/1
cat /tmp/hw05-stopped.json

echo "3. Bật Redis trở lại"
start_redis
curl -s -o /tmp/hw05-recovered.json -w 'HTTP %{http_code}\n' http://localhost:8080/menu/1
cat /tmp/hw05-recovered.json
redis-cli -p "$REDIS_PORT" GET 'menuCache::1'
