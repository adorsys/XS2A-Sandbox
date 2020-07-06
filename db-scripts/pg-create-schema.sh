#!/usr/bin/env bash

# create consent schema and give permissions to cms user. Needed for
# docker-compose so  that we can start the DB with the schema already being present
set -e
echo "Create schema='consent' for local postgres installation"
psql -U postgres -d consent -c 'CREATE SCHEMA IF NOT EXISTS consent AUTHORIZATION cms;'

echo "Create schema='ledgers' for local postgres installation"
psql -U postgres -d consent -c 'CREATE SCHEMA IF NOT EXISTS ledgers AUTHORIZATION cms;'

echo "Create schema='tpp_db' for local postgres installation"
psql -U postgres -d consent -c 'CREATE SCHEMA IF NOT EXISTS tpp_db AUTHORIZATION cms;'
