#!/bin/sh
find /usr/share/nginx/html -name '*.js' -exec sed -i \
  -e "s|__KEYCLOAK_URL__|${KEYCLOAK_URL:-http://localhost:8080}|g" \
  -e "s|__PUBLIC_URL__|${PUBLIC_URL:-http://localhost}|g" \
  -e "s|__MP_PUBLIC_KEY__|${MP_PUBLIC_KEY:-APP_USR-112696f3-3603-4175-826f-167cf58606b2}|g" \
  {} +

exec nginx -g 'daemon off;'
