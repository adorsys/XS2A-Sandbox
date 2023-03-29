#!/usr/bin/env bash

# create consent schema and give permissions to cms user. Needed for
# docker-compose so  that we can start the DB with the schema already being present
set -e
echo "Create schema='consent' for local postgres installation"
psql -U postgres -d consent -c 'CREATE SCHEMA IF NOT EXISTS consent AUTHORIZATION cms;'

echo "Create schema='ledgers' for local postgres installation"
psql -U postgres -d consent -c 'CREATE SCHEMA IF NOT EXISTS ledgers AUTHORIZATION cms;'

echo "Create schema='tpp' for local postgres installation"
psql -U postgres -d consent -c 'CREATE SCHEMA IF NOT EXISTS tpp AUTHORIZATION cms;'

echo "Create keycloak database and user"
echo "SELECT 'CREATE DATABASE keycloak' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'keycloak')\gexec" | psql
if psql -t -c '\du' | cut -d \| -f 1 | grep -qw keycloak;
 then
  echo "User keycloak exists"
else
 echo "Create User keycloak"
 psql -c "CREATE USER keycloak WITH PASSWORD 'keycloak';"
 psql -c "GRANT ALL ON DATABASE keycloak TO keycloak;"
fi
