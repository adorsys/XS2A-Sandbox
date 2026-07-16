# Release notes v.14.1

## Current version consists of following components:

1. **Ledgers v.7.1**
2. **XS2A v.22.2**
3. **ASPSP-Profile v.22.2**
4. **Consent-Management v.22.2**
5. **XS2A Connector-Examples v.22.1**

## Security patches — dependency versions were bumped up:

### Spring ecosystem

> **Note:** The Keycloak Maven dependency overrides above are **client-side only**. In the current reactor, `de.adorsys.ledgers:keycloak-client:7.0` still resolves transitive Keycloak 25.0.3 artifacts for `keycloak-adapter-core`, `keycloak-crypto-default`, and `keycloak-server-spi` in `auth-commons` and the modules that depend on it. The Keycloak server Docker image is also provided by the `keycloak-and-ledgers-plugin:7.0` image (defined in `docker-compose.yml`), which bundles its own Keycloak version controlled by the Ledgers project. To fully remediate the Keycloak CVEs at runtime, the Ledgers client dependency and the `keycloak-and-ledgers-plugin` Docker image both need to be updated to versions that ship Keycloak ≥ 26.5.2 / 26.6.0.
