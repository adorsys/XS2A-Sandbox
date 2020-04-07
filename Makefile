.PHONY: all run test clean start

VERSION=$(shell jq -r .version developer-portal-ui/package.json)
ARC42_SRC = $(shell find arc42/src)
PLANTUML_SRC = $(shell find arc42/diagrams -type f -name '*.puml')
DEPENDENCIES = jq npm plantuml asciidoctor docker-compose mvn docker

build-services: build-java-services build-ui-services build-arc-42  ## Build all services

## Install section ##
install:   ##Install developer tools

ifeq ($(shell uname),Darwin)
	make install-for-MacOS
else ifeq ($(shell uname),Linux)
	make install-for-Linux
else
	@echo "Doesn't support your OS"
endif

# Install developer tools for Linux
install-for-Linux:
	sudo apt-get install jq
	sudo apt-get install plantuml
	sudo apt-get install -y asciidoctor

# Install developer tools for MacOS
install-for-MacOS:
	curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install | ruby
	brew install jq
	brew install plantuml
	brew install asciidoctor

## Run section ##
run:  ## Run services from Docker Hub without building:
	docker-compose pull && docker-compose up

start: ## Run everything with docker-compose without building
	docker-compose -f docker-compose-build.yml up

all: build-services ## Run everything with docker-compose after building
	docker-compose -f docker-compose-build.yml up --build

## Build section ##
build-java-services: ## Build java services
	mvn -DskipTests clean package

build-ui-services: npm-install-tpp-ui npm-install-oba-ui npm-install-developer-portal-ui ## Build ui services
	cd tpp-ui && npm run build
	cd oba-ui && npm run build
	cd developer-portal-ui && npm run build

npm-install-tpp-ui: tpp-ui/package.json tpp-ui/package-lock.json ## Install TPP-UI NPM dependencies
	cd tpp-ui && npm install

npm-install-oba-ui: oba-ui/package.json oba-ui/package-lock.json ## Install OBA-UI NPM dependencies
	cd oba-ui && npm install

npm-install-developer-portal-ui: developer-portal-ui/package.json developer-portal-ui/package-lock.json ## Install DEV-PORTAL-UI NPM dependencies
	cd developer-portal-ui && npm install

## Build arc42
build-arc-42: arc42/images/generated $(ARC42_SRC) arc42/xs2a-sandbox-arc42.adoc developer-portal-ui/package.json ## Generate arc42 html documentation
	cd arc42 && asciidoctor -a acc-version=$(VERSION) xs2a-sandbox-arc42.adoc

arc42/images/generated: $(PLANTUML_SRC) ## Generate images from .puml files
# Note: Because plantuml doesnt update the images/generated timestamp we need to touch it afterwards
	cd arc42 && mkdir -p images/generated && plantuml -o "../images/generated" diagrams/*.puml && touch images/generated

## Tests section ##
test: test-java-services ## Run all tests

test-java-services: ## Run java tests
	mvn test

test-ui-services: ## Run tests in UI
	cd tpp-ui && npm run test-single-headless
	cd oba-ui && npm run test-single-headless
	cd developer-portal-ui && npm run test-single-headless

## Clean section ##
clean: clean-java-services clean-ui-services ## Clean everything

clean-java-services: ## Clean services temp files
	mvn clean

clean-ui-services: ## Clean UI temp files
	cd tpp-ui && rm -rf dist
	cd oba-ui && rm -rf dist
	cd developer-portal-ui && rm -rf dist

## Check section ##
check: ## Check required dependencies ("@:" hides nothing to be done for...)
	@: $(foreach exec,$(DEPENDENCIES),\
          $(if $(shell command -v $(exec) 2> /dev/null ),$(info (OK) $(exec) is installed),$(info (FAIL) $(exec) is missing)))

## Help section ##
help: ## Display this help
	@awk 'BEGIN {FS = ":.*##"; printf "\nUsage:\n  make \033[36m<target>\033[0m\n\nTargets:\n"} /^[a-zA-Z0-9_\-\/\. ]+:.*?##/ { printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)
