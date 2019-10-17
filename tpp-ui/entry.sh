#!/usr/bin/env bash
export DOLLAR='$'

erb /etc/nginx/conf.d/default.conf.erb > /etc/nginx/conf.d/default.conf

envsubst < /usr/share/nginx/html/assets/settings.template.json > /usr/share/nginx/html/assets/settings.json

nginx -g "daemon off;"
