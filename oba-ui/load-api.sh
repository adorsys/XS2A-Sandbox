#!/usr/bin/env bash

RELEASES_REPO="http://localhost:8090/v2/api-docs?group=PUS-API"

if [ ! -f api/kontrakte/oba.json ]; then
    curl --create-dirs -k -o api/kontrakte/oba.json \
        "${RELEASES_REPO}" -sS
fi
