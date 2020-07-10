# Stage SERVE
FROM nginx:1.17.3
LABEL maintainer="https://github.com/adorsys/xs2a"

ENV NGINX_ACCESS_LOG_DEBUG combined
ENV NGINX_ERROR_LOG_DEBUG error

RUN \
  apt-get update && \
  apt-get install -y ruby2.5=2.5.5-3+deb10u1 --no-install-recommends \
  && rm -rf /var/lib/apt/lists/*

COPY ./nginx.conf.erb /etc/nginx/conf.d/default.conf.erb
COPY entry.sh /opt/entry.sh
COPY dist/tpp-ui /usr/share/nginx/html
COPY info.json /usr/share/nginx/html

RUN chgrp -R root /var/cache/nginx && \
    find /var/cache/nginx -type d -exec chmod 775 {} \; && \
    find /var/cache/nginx -type f -exec chmod 664 {} \; && \
    chmod 775 /var/run && \
    chmod 775 /opt/entry.sh && \
    chmod -R 777 /etc/nginx/conf.d && \
    chmod 777 /usr/share/nginx/html/assets/settings.json

EXPOSE 4205

ENTRYPOINT [ "/opt/entry.sh" ]
