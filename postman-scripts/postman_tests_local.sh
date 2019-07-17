#!/usr/bin/env bash

echo "Run postman tests"
newman run postman_collection.json -e postman_environment_local.json -g postman_global_variable.json --reporters cli,html --reporter-html-template html_report_template.hbs --reporter-html-export postman-tests-report.html
