# Stage SERVE
FROM nginx:1.25.4
LABEL maintainer="https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox"

ENV NGINX_ACCESS_LOG_DEBUG combined
ENV NGINX_ERROR_LOG_DEBUG error

COPY templates/nginx.template.conf /etc/nginx/conf.d/default.conf.template
COPY entry.sh /opt/entry.sh
COPY dist/developer-portal /usr/share/nginx/html
COPY info.json /usr/share/nginx/html

RUN chgrp -R root /var/cache/nginx && \
    find /var/cache/nginx -type d -exec chmod 775 {} \; && \
    find /var/cache/nginx -type f -exec chmod 664 {} \; && \
    chmod 775 /var/run && \
    chmod 775 /opt/entry.sh && \
    chmod -R 775 /etc/nginx/conf.d

RUN chown -R nginx:nginx /var/cache/nginx /var/run /etc/nginx/conf.d /usr/share/nginx/html /opt/entry.sh

USER nginx

EXPOSE 4206

ENTRYPOINT [ "/opt/entry.sh" ]
