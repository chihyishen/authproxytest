#!/bin/sh
set -e

echo "=== Manual Auth Proxy Startup ==="
echo "Instance: $INSTANCE_CONNECTION_NAME"

# 啟動 Cloud SQL Proxy
./cloud_sql_proxy \
    -instances=$INSTANCE_CONNECTION_NAME=tcp:5432 \
    -log_debug_stdout=true &
PROXY_PID=$!

echo "Waiting for Auth Proxy to be ready..."
for i in $(seq 1 60); do
    if nc -z 127.0.0.1 5432 2>/dev/null; then
        echo "✅ Auth Proxy is ready!"
        break
    fi
    echo "⏳ Attempt $i/60..."
    sleep 2

    # 檢查 proxy 是否還在運行
    if ! kill -0 $PROXY_PID 2>/dev/null; then
        echo "❌ Auth Proxy died! Checking logs..."
        wait $PROXY_PID
        exit 1
    fi
done

if ! nc -z 127.0.0.1 5432; then
    echo "❌ Auth Proxy timeout after 120 seconds"
    exit 1
fi

echo "=== Starting Application ==="
exec java $JAVA_OPTS -Dserver.port=$PORT -jar app.jar