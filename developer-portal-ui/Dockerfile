# Stage SERVE
FROM nginx:1.17.3
LABEL maintainer="https://github.com/adorsys/xs2a"

ENV NGINX_ACCESS_LOG_DEBUG combined
ENV NGINX_ERROR_LOG_DEBUG error

COPY ./templates/nginx.template.conf /etc/nginx/conf.d/default.conf.template
COPY entry.sh /opt/entry.sh
ADD dist/developer-portal /usr/share/nginx/html
COPY ./templates/trackingId.template.json /usr/share/nginx/html/assets/content

RUN chgrp -R root /var/cache/nginx && \
    find /var/cache/nginx -type d -exec chmod 775 {} \; && \
    find /var/cache/nginx -type f -exec chmod 664 {} \; && \
    chmod 775 /var/run && \
    chmod 775 /opt/entry.sh && \
    chmod 777 /usr/share/nginx/html/assets/content/trackingId.json && \
    chmod -R 777 /etc/nginx/conf.d

EXPOSE 4206

ENTRYPOINT /opt/entry.sh
