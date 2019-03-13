# ObaUi

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 7.3.0.

## how to start the frontend Project

1. navigate into the backend project `xs2a-connector-examples` make sure you are in develop and run in the terminal or console the following command to start the server:
    1. => `mvn clean install`
    2. => `docker-compose up`
2. navigate into the frontend project `oba-ui` branch and then run in the terminal or console the following command:
    1. => `npm install`
    2. => `npm start`
3. navigate into the repository `xs2a-connector-redirect-test` and execute the command:
    1. `mvn test -Dtest=de.adorsys.ledgers.xs2a.test.ctk.redirect.ConsentRedirectOneScaApp`


## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4400/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Running unit tests

Run `npm run test-dev` to execute the unit tests in `development mode` via [Karma](https://karma-runner.github.io).

Run `npm run test` to execute the unit tests in `production mode` via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).


# Code rules
1. Component
	1. each component name must be in english
	2. Unit-Tests description in english
	3. Use of `single quote`and not `double quote` for string object
	4. code formatting must have 4 spaces or tap
