# Stage SERVE
FROM nginx:1.22.1
LABEL maintainer="https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox"

ENV NGINX_ACCESS_LOG_DEBUG combined
ENV NGINX_ERROR_LOG_DEBUG error

RUN \
  apt-get update && \
  apt-get install -y ruby2.7=2.7.4-1+deb11u1 --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

COPY ./nginx.conf.erb /etc/nginx/conf.d/default.conf.erb
COPY entry.sh /opt/entry.sh
COPY dist/admin-ui /usr/share/nginx/html
COPY info.json /usr/share/nginx/html

RUN chgrp -R root /var/cache/nginx && \
    find /var/cache/nginx -type d -exec chmod 775 {} \; && \
    find /var/cache/nginx -type f -exec chmod 664 {} \; && \
    chmod 775 /var/run && \
    chmod 775 /opt/entry.sh && \
    chmod -R 777 /etc/nginx/conf.d && \
    chmod 777 /usr/share/nginx/html/assets/settings.json

EXPOSE 4207

ENTRYPOINT [ "/opt/entry.sh" ]
