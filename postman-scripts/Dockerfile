FROM postman/newman_alpine33
MAINTAINER https://github.com/adorsys/xs2a

RUN apk add --update nodejs

ENV NEWMAN_VERSION 4.5.0

USER 0
RUN npm config set unsafe-perm true
RUN npm install -g newman@${NEWMAN_VERSION};
RUN npm install -g newman-reporter-html

WORKDIR /etc/newman

ENTRYPOINT ["newman"]
