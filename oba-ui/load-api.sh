#!/usr/bin/env bash

RELEASES_REPO="http://localhost:8090/v2/api-docs?group=PSU-API"

if [[ ! -f src/app/api/swagger/oba.json ]]; then
    curl --create-dirs -k -o src/app/api/swagger/oba.json \
        "${RELEASES_REPO}" -sS
fi
