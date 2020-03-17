#!/bin/sh
export DOLLAR='$'

envsubst < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf
envsubst < /usr/share/nginx/html/assets/UI/trackingId.template.json > /usr/share/nginx/html/assets/UI/trackingId.json

nginx -g "daemon off;"
