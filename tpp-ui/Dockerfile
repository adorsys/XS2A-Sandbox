# Stage SERVE
FROM nginx:1.25.4
LABEL maintainer="https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox"

ENV NGINX_ACCESS_LOG_DEBUG combined
ENV NGINX_ERROR_LOG_DEBUG error

RUN \
  apt-get update && \
  apt-get install -y \
    ruby3.1=3.1.2-7+deb12u1 --no-install-recommends \
  && apt-get remove -y curl \
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
    chmod -R 775 /etc/nginx/conf.d && \
    chmod 644 /usr/share/nginx/html/assets/settings.json

  RUN chown -R nginx:nginx /var/cache/nginx /var/run /etc/nginx/conf.d /usr/share/nginx/html /opt/entry.sh

  USER nginx

EXPOSE 4205

ENTRYPOINT [ "/opt/entry.sh" ]
