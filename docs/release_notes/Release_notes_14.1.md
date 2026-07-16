# Release notes v.14.1

## Current version consists of following components:

1. **Ledgers v.7.1**
2. **XS2A v.22.2**
3. **ASPSP-Profile v.22.2**
4. **Consent-Management v.22.2**
5. **XS2A Connector-Examples v.22.1**

## Security patches — dependency versions were bumped up:

### Spring ecosystem
- **Spring Boot**: 3.3.13 → **3.5.14** (CVE-2026-22733 — Actuator authentication bypass)
- **Spring Cloud**: 2023.0.3 → **2025.0.2** (compatibility with Spring Boot 3.5.x)
- **Spring Security**: 6.3.10 → **6.5.10** (CVE-2026-22732 — HTTP response headers not written; CVE-2026-22753, CVE-2026-22754 — servlet-path authorization bypass)
- **Spring Framework / Spring Test**: 6.1.14 → **6.2.18** (CVE-2025-41249 — annotation detection bypass on generic superclasses)

### Jackson
- **jackson-core / jackson-databind / jackson-datatype / jackson-dataformat**: 2.18.1 → **2.21.1** (WS-2026-0003 — async parser bypasses maxNumberLength constraint, DoS)
- **jackson-annotations**: explicitly managed at **2.21** (correct version for the 2.21.x release line)

### Mail
- **angus-mail** (org.eclipse.angus): → **2.0.5** (CVE-2025-7962 — SMTP injection via CR/LF characters)

### Cryptography
- **bcprov-jdk18on** (BouncyCastle): 1.74 → **1.84** (CVE-2024-29857, CVE-2024-30171, CVE-2024-30172, CVE-2024-34447 — DoS/infinite loop/DNS poisoning; CVE-2025-8885, CVE-2025-8916 — resource exhaustion; CVE-2026-0636 — LDAP injection)

### Testing
- **assertj-core**: 3.25.3 → **3.27.7** (CVE-2026-24400 — XXE vulnerability in XML assertions)

### Keycloak (client-side Maven overrides)
- **keycloak-core**: 26.0.2 → **26.5.2** (CVE-2024-10039, CVE-2026-1180, CVE-2024-11736, CVE-2024-4028, CVE-2025-5416)
- **keycloak-server-spi-private**: 25.0.3 → **26.6.0** (CVE-2026-2603, CVE-2024-11734, CVE-2026-0871, CVE-2025-13881)

### Angular version update
The UI applications (`admin-ui`, `tpp-ui`, `oba-ui`, `developer-portal-ui`) have been migrated to Angular 20 and Node 20:
- **Angular Core / Common / Compiler / Forms / Router**: 18.2.13 → **20.3.26**
- **Angular CLI / Build Angular**: 18.2.20 → **20.3.32**
- **TypeScript**: 5.5.4 → **5.9.2**
- **RxJS**: 7.8.x → **7.8.1**
- **Zone.js**: 0.14.4 → **0.15.1**
- **ng-bootstrap**: 17.0.0 → **19.0.1**
- **ngx-bootstrap**: 18.1.3 → **19.0.0**
- **NodeJS runtime**: 18 → **20.19.0** (configured via `.nvmrc`)

### Vulnerability fixes (Maven dependency bumps)
- **Tomcat**: 10.1.55 → **10.1.57**
- **Spring Security**: 6.5.10 → **6.5.11** 
- **Spring Framework**: 6.2.18 → **6.2.19** 
- **PostgreSQL**: 42.7.11 → **42.7.13** 
- **commons-lang3**: 3.17.0 → **3.18.0** 

### Suppressed vulnerabilities
- **Jackson 2.21.x**: False positive matches on serialization packages; polymorphic deserialization (`activateDefaultTyping`) is not used in the application.
- **httpcore 4.x**: False positive match against Apache httpcore5 CVEs.
- **kotlin-stdlib**: Build-daemon cache issues and deprecated APIs not packaged in deployed binaries.
- **log4j-api**: API only; the vulnerable `log4j-core` runtime is not present on the classpath.
- **Spring Framework 6.2.18 residuals**: Suppressed temporary mismatches during BOM override resolution.
- **DOMPurify** (swagger-ui RetireJS/GHSA): Swagger UI is only accessible to authenticated internal developers.
- **keycloak-client-common-synced**: Server-side privilege assignment issue, suppressed client-side in line with keycloak policy.

> **Note:** The Keycloak Maven dependency overrides above are **client-side only**. In the current reactor, `de.adorsys.ledgers:keycloak-client:7.0` still resolves transitive Keycloak 25.0.3 artifacts for `keycloak-adapter-core`, `keycloak-crypto-default`, and `keycloak-server-spi` in `auth-commons` and the modules that depend on it. The Keycloak server Docker image is also provided by the `keycloak-and-ledgers-plugin:7.0` image (defined in `docker-compose.yml`), which bundles its own Keycloak version controlled by the Ledgers project. To fully remediate the Keycloak CVEs at runtime, the Ledgers client dependency and the `keycloak-and-ledgers-plugin` Docker image both need to be updated to versions that ship Keycloak ≥ 26.5.2 / 26.6.0.
