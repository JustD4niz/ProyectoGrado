#!/bin/sh
PORT="${PORT:-8080}"
if [ "$PORT" != "8080" ]; then
  sed -i "s/port=\"8080\"/port=\"$PORT\"/" /usr/local/tomcat/conf/server.xml
fi
exec "$@"
