#!/usr/bin/env bash
export DOLLAR='$'

envsubst < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

envsubst < /usr/share/nginx/html/assets/settings.template.json > /usr/share/nginx/html/assets/settings.json

nginx -g "daemon off;"
