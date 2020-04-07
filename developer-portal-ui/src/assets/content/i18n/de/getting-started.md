<div class="divider">
</div>

# Einleitung

[Die Payment Service Directive 2]((https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN)) (PSD2) schreibt Banken (ASPSPs = Account Servicing Payment Service Providers) vor bis März 2020 eine standardisierte und voll leistungsfähige Account-Schnittstelle für Drittanbieter (TPPs = Third Payment Providers) bereitzustellen. Diese wird Access-to-Account oder kurz "XS2A" genannt. XS2A setzt sich aus folgenden Banking-Funktionalitäten zusammen: Zahlungsinitiierung (PIS = Payment Initiation Service), Kontodatenabfrage (AIS = Account Information Service) und Abfrage von verfügbarem Kapital (PIIS = confirmation of the availability of funds). Um die Einhaltung der Frist zu gewährleisten und sowohl Fehlerbehebungen als auch Anpassungen berücksichtigen zu können, verpflichtet PSD2 die Banken eine Testversion der Schnittstelle bereits im Juni 2019 zur Verfügung zu stellen.

Unsere **XS2ASandbox** ist eine dynamische Sandboxumgebung, die vollständig die PSD2-Anforderungen bezüglich API-Bereitstellung für Drittanbieter (TPPs = Third Payment Providers) erfüllt. Zusätzlich erfüllt sie auch alle regulatorischen Anforderungen der NextGen PSD2-Spezifikation der Berlin Group bezüglich Account Schnittstellen (XS2A = Access-to-Account) und ist NISP konform.

Ziel des Developer-Portals ist es TPP-Entwicklern bei der Arbeit mit unserer XS2ASandbox zu unterstützen.

<div class="divider">
</div>

# XS2ASandbox Architektur und Module

Die XS2ASandbox setzt sich aus ihren Komponenten wie in Abbildung 1.1 zusammen.

![Abbildung 1.1](../../assets/images/Graphic_XS2A_Sandbox.jpg)

Abbildung 1.1: Komponenten der XS2ASandbox

<div class="divider">
</div>

# XS2A-Schnittstelle

Der Zentrale Bestandteil der **XS2ASandbox** ist die XS2A-Schnittstelle, die gemäß der Spezifikation der Berlin Group [NextGenPSD2](https://www.berlin-group.org/psd2-access-to-bank-accounts) (Version 1.3) implementiert wurde und auf Testdaten basiert. Mehr Details finden Sie in unsere [XS2A Swagger UI](https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/) und der [OpenSource XS2A Schnittstelle auf Github](https://github.com/adorsys/xs2a).

# ASPSP-Profile

Neben der Schnittstelle ist es für Banken ebenfalls vorgeschrieben eine kostenfreie technische Dokumentation zur Verfügung zu stellen, die über unterstützte Produkte und Zahlungsdienstleistungen informiert. Die Daten werden in **ASPSP-bzw** bank-Profilen gespeichert, einem Service, der es Banken ermöglicht durch eine .yaml-Datei verfügbare Zahlungsprodukte, Zahlungsdienste, unterstützte SCA-Ansätze und weitere bankspezifische Einstellungen bereitzustellen.

# TPP Zertifikat-Service

Zukünftig wird es für einen TPP notwendig sein, sich bei der BAFIN zu registrieren und von einem Treuhanddienstleister (TSP = Trust Service Provider) ein [eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) Zertifikat ausstellen zu lassen, um XS2A-Dienstleistungen bei Banken nutzen zu können. Da das Ausstellen eines echten Zertifikates für Testzwecke einen sehr hohen Aufwand bedeuten würde, wird im Rahmen der **XS2ASandbox** ein TSP simuliert und eine Funktion zur Erstellung eines qualifizierten Website-Authentifizierungs-Zertifikates (QWAC) bereitgestellt. Ein QWAC ist Teil der eIDAS und wird auch als [X.509](https://www.ietf.org/rfc/rfc3739.txt) Zertifikat bezeichnet. Für PSD2-Zwecke wird das Zertifikat durch ein QcStatement erweitert, welche Informationen wie die Rolle des Drittanbieters beinhaltet (siehe auch [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

Nach der Einbettung des QWAC in die eigentliche XS2A-Anfrage werden sowohl die Rolle als auch die Signatur in einem zentralen Reverse-Proxy validiert, bevor sie schließlich an die für die Banklogik verantwortliche Schnittstelle übergeben werden.

<div class="divider">
</div>

# TPP Benutzerschnittstelle

TPP - Entwickler können sich im System registrieren und somit ein Zertifikat erhalten . Mit diesem Zertifikat und den vorgegeben Daten in der TPP Benutzeroberfläche können Sie Test - Daten für ihre TPP - Anwendung herunterladen


## Wie legt man ein Benutzerkonto für den TPP an:

1. Öffnen Sie die Login - Seite in der TPP Benutzeroberfläche.

2. Wählen Sie unten die Option “Registrieren” aus.

3. Geben Sie die notwendigen Informationen ein: TPP Autorisierungsnummer, Email, Login und Passwort.

4. Bei Bedarf können Sie ein QWAC Testzertifikat zum Testen der XS2A Schnittstelle generieren.


## Wie werden Benutzer angelegt:

Benutzer können manuell angelegt werden oder über das Hochladen einer .yaml Datei.

### Einen Benutzer manuell erstellen:

1. Gehen Sie zu “Meine Benutzerliste” und klicken Sie auf “Neuen Benutzer anlegen”.

2. Geben Sie Email, Login und Pin für den neuen Benutzer ein.

3. Wählen Sie eine Authentifizierungsmethode aus (derzeit ist nur Email gültig) und geben Sie eine gültige Email ein. Wenn Sie die Option "Statische TAN in SandboxModus verwenden" wählen, werden statische Test TANs verwendet. Das bedeutet Ihre Test TAN muss eingeben werden. Sie können mehrere SCA Methoden pro Benutzer hinzufügen


### Benutzerkonten manuell erstellen:

1 Wählen Sie einen Benutzer in der Benutzerliste aus und klicken Sie auf "Konto erstellen"

2. Wählen Sie Kontoart, Verwendung und Kontostatus aus. IBAN kann automatisch generiert werden. Bitte beachten Sie, dass nur valide IBANs von der XS2A Schnittstelle akzeptiert werden.

3. Nach dem das Konto erstellt wurde, können Sie die Funktion "Bargeld einzahlen" verwenden, um zu Testzwecken einen beliebigen Betrag zu den Salden hinzuzufügen. Alle erstellten Konten sind im Reiter "Benutzerkonten" verfügbar

Um Benutzer und Konten automatisch zu erstellen, können Sie Testdaten mit dem Reiter "Testdaten generieren" generieren, damit werden NISP konforme Testdaten generiert. Wenn Sie Ihre eigene .yaml Testdatei hochladen möchten, verwenden Sie den Reiter "Dateien hochladen".


### Wie werden Test - Zahlungen, -Zustimmungen und -Transaktionen erstellt:

Um Test - Zahlungen zu erstellen, müssen Sie Test - Zahlungsdaten im Reiter "Testdaten generieren" generieren, wobei die Option "Zahlungsdaten generieren" als wahr markiert werden muss. Darüber hinaus ist das Hochladen einer benutzerdefinierten .yaml Datei mit Zahlungen im Reiter "Dateien hochladen" möglich. Zustimmungen und Transaktionen können nur mit dem Hochladen einer korrekten .yaml Datei erstellt werden.

<div class="divider">
</div>

# Online Banking
Im Falle des REDIRECT SCA-Ansatzes möchte ein Nutzer seine Zustimmung zur Verwendung seiner Kontoinformationen oder zur Zahlungsinitiierung/-stornierung erteilen. Online-Banking ist eine Benutzerschnittstelle zur Erteilung der Zustimmung zu einer Bank. Links für eine Einwilligungsbestätigung und Zahlungsinitiierung oder -stornierung werden in der Rückmeldung der entsprechenden Endpunkte angegeben.

<div class="divider">
</div>

# Links zu den Umgebungen

| Service                   |           Lokale Umgebung              |                                                                   XS2ASandbox Umgebung |
| ------------------------- | :------------------------------------: | -------------------------------------------------------------------------------------: |
| XS2A Interface Swagger    | http://localhost:8089/swagger-ui.html  |                                     https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/ |
| Developer portal          |         http://localhost:4206          |                        https://demo-dynamicsandbox-developerportalui.cloud.adorsys.de/ |
| Consent management system | http://localhost:38080/swagger-ui.html |                                       https://demo-dynamicsandbox-cms.cloud.adorsys.de |
| Ledgers                   | http://localhost:8088/swagger-ui.html  |                                   https://demo-dynamicsandbox-ledgers.cloud.adorsys.de |
| ASPSP-Profile Swagger     | http://localhost:48080/swagger-ui.html |                              https://demo-dynamicsandbox-aspspprofile.cloud.adorsys.de |
| TPP User Interface        |         http://localhost:4205          |                               https://demo-dynamicsandbox-tppui.cloud.adorsys.de/login |
| Online banking UI         |         http://localhost:4400          | https://demo-dynamicsandbox-onlinebankingui.cloud.adorsys.de/account-information/login |
| Online banking backend    | http://localhost:8090/swagger-ui.html  |             https://demo-dynamicsandbox-onlinebanking.cloud.adorsys.de/swagger-ui.html |
| Certificate Generator     | http://localhost:8092/swagger-ui.html  |      https://demo-dynamicsandbox-certificategenerator.cloud.adorsys.de/swagger-ui.html |

<div class="divider">
</div>

# Herunterladen, Einrichten und Ausführen des Projekts
## Voraussetzungen

Das XS2ASandbox Projekt läuft mit docker-compose, das sich in docker-compose.yml und Makefile im Projektverzeichnis befindet. Bevor Sie das Projekt ausführen lassen, überprüfen Sie mit:
 
_make check_

Ob Sie alle notwendigen Programme installiert haben. Die fehlenden müssen Sie lokal auf Ihren Rechner installieren, da die XS2ASandbox ansonsten nicht erfolgreich gebaut werden kann. Hier ist eine Liste aller Programme und derer Links die Sie installiert haben müssen:

| Dependency          |                  Link                   |
| ------------------- | :-------------------------------------: |
| Java 8              |    https://openjdk.java.net/install/    |
| Node.js 11.x        |     https://nodejs.org/en/download      |
| Angular CLI 7.x     |   https://angular.io/guide/quickstart   |
| Asciidoctor 2.0     |         https://asciidoctor.org         |
| jq 1.6              | https://stedolan.github.io/jq/download  |
| Docker 1.17         |   https://www.docker.com/get-started    |
| Docker Compose 1.24 | https://docs.docker.com/compose/install |
| Maven 3.5           |  https://maven.apache.org/download.cgi  |
| PlantUML 1.2019.3   |     http://plantuml.com/en/starting     |

Beenden Sie laufende Prozesse im Terminal mit der Tastenkombination _Control + C_.

Es empfiehlt sich bereits vorhandene Docker Container zu entfernen, um Fehler bezüglich der Ports zu vermeiden. Nutzen Sie dafür folgende Befehle:

_docker-compose rm -s -f -v_

## Anmerkung 1

Bitte, verwenden Sie eine Node.js unter Version 12 (z.B. 10.x.x oder 11.x.x). Andernfalls kann die Angular Anwendung wegen Versionskonflikten nicht gebaut werden.

## Anmerkung 2

Überprüfen Sie wie viel Speicher Docker zur Verfügung steht (öffnen Sie Docker Desktop -> Preferences -> Advanced -> Memory). Um einen schnellen und problemlosen Start aller Dienste zu ermöglichen, sollte Docker auf mindestens 5 GB Speicher Zugriff haben.

## Download XS2ASandbox

Laden Sie das Projekt direkt von GitHub herunter oder nutzen Sie folgenden Befehl:

_git clone https://github.com/adorsys/XS2A-Sandbox_

## Starten der XS2ASandbox

Nach dem erfolgreichen Herunterladen des Projektes gehen Sie in das Projektverzeichnis:

_cd XS2A-Sandbox_

Danach können Sie XS2ASandbox auf zwei Arten bauen und ausführen - mit einem Docker-Befehl oder mit Makefile-Befehlen.

Falls Sie es wollen benutze einen ersten Weg:

1. Bauen Sie alle Dienste mit folgendem Befehl:

_make_

2. Nach dem Gebäude der Dienste können Sie XS2ASandbox mit einem einfachen Docker-Befehl ausführen:

_docker-compose up_

In Makefile können Sie einen von drei Befehlen verwenden:

• Führen Sie Dienste ohne Build über Docker Hub aus:

_make run_

• Bauen und starten der Dienste:

_make all_

• Starten der Dienste ohne Bauen:

_make start_

Sobald Sie das Projekt gebaut haben, können Sie es ausführen, ohne es das nächste Mal zu bauen - Befehl docker-compose up oder make start aus Makefile

Denken Sie daran, dass Sie das Projekt nach dem Aktualisieren neu erstellen sollten - Befehl make oder make all aus Makefile

<div class="divider">
</div>

# Fehlerbehebung

Hier erklären wir Ihnen mögliche Fehler, die beim Starten der XS2ASandbox auftreten können, und wie Sie diese beheben können.

## Liquibase changelog Fehler

Dieser Fehler kann auftreten, wenn ein vorheriger Start der XS2ASandbox fehlschlug. Hier ein Beispiel eines möglichen Stacktrace:

```yaml
ledgers | 2019-05-02 13:54:29.410 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:54:39.697 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers | 2019-05-02 13:54:55.137 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:55:35.940 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers | 2019-05-02 13:55:45.995 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:55:46.967 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers | 2019-05-02 13:55:59.167 INFO 1 --- [ main] liquibase.executor.jvm.JdbcExecutor : SELECT LOCKED FROM ledgers.databasechangeloglock WHERE ID=1
ledgers | 2019-05-02 13:57:38.705 INFO 1 --- [ main] l.lockservice.StandardLockService : Waiting for changelog lock....
ledgers exited with code 137
```

Mögliche Lösung:

Finden und löschen Sie alle Ordner mit Namen “ledgerdbs” und “xs2adbs”. Zusätzlich löschen Sie alle Docker Container mit folgenden Befehl:

_docker-compose rm -s -f -v_

Versuchen Sie nun alle Services erneut zu starten.

## Node Version Fehler

Wenn Sie eine NodeJs Version nutzen, die höher als 11.x ist, kann es zu diesem Fehler kommen. Hier ein Beispiel eines möglichen Stacktrace:

```yaml
gyp ERR! build error
gyp ERR! stack Error: `make` failed with exit code: 2
gyp ERR! stack at ChildProcess.onExit (/Users/rpo/XS2A-Sandbox/developer-portal-ui/node_modules/node-gyp/lib/build.js:262:23)
gyp ERR! stack at ChildProcess.emit (events.js:196:13)
gyp ERR! stack at Process.ChildProcess.\_handle.onexit (internal/child_process.js:256:12)
gyp ERR! System Darwin 17.7.0
gyp ERR! command "/usr/local/Cellar/node/12.1.0/bin/node" "/Users/rpo/XS2A-Sandbox/developer-portal-ui/node_modules/node-gyp/bin/node-gyp.js" "rebuild" "--verbose" "--libsass_ext=" "--libsass_cflags=" "--libsass_ldflags=" "--libsass_library="
gyp ERR! cwd /Users/rpo/XS2A-Sandbox/developer-portal-ui/node_modules/@angular-devkit/build-angular/node_modules/node-sass
gyp ERR! node -v v12.1.0
gyp ERR! node-gyp -v v3.8.0
gyp ERR! not ok
```

Mögliche Lösung:

Überprüfen mit folgenden Befehl Ihre NodeJs Version:

_node -v_

Sollte Ihre NodeJs Version höher als 11.x sein, ändern Sie diese zu einer früheren Version.

<div class="divider">
</div>

# TPP registrieren und testen

1. Öffnen Sie die TPP User Interface Login Seite.
2. Falls Sie kein Konto haben, wechseln Sie mit Hilfe des "Register" Button zu der Registrierungsansicht.
3. Registrieren und loggen Sie sich ein.
4. Laden Sie Ihre Testdaten hoch und starten Sie Ihren Testlauf.
In Abbildung 1.2 ist der vollständige Ablauf wie man als TPP die Arbeit mit der XS2ASandbox beginnt veranschaulicht:

![Abbildung 1.2](../../assets/images/Flow.png)

Abbildung 1.2: TPP Ablauf Schritt für Schritt

<div class="divider">
</div>

# Anpassen der UI des Developer-Portals

1. Erstellen Sie eine .json Datei mit dem Namen UITheme.

Hier ein Json Beispiel:

```json
{
  "globalSettings": {
    "logo": "Logo_XS2ASandbox.png",
    "facebook": "https://www.facebook.com/adorsysGmbH/",
    "linkedIn": "https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/"
  },
  "contactInfo": {
    "img": "Rene.png",
    "name": "René Pongratz",
    "position": "Software Architect & Expert for API Management",
    "email": "psd2@adorsys.de"
  },
  "officesInfo": [
    {
      "city": "Nürnberg",
      "company": "adorsys GmbH & Co. KG",
      "addressFirstLine": "Fürther Str. 246a, Gebäude 32 im 4.OG",
      "addressSecondLine": "90429 Nürnberg",
      "phone": "+49(0)911 360698-0",
      "email": "psd2@adorsys.de"
    },
    {
      "city": "Frankfurt",
      "company": "adorsys GmbH & Co. KG",
      "addressFirstLine": "Frankfurter Straße 63 - 69",
      "addressSecondLine": "65760 Eschborn",
      "email": "frankfurt@adorsys.de",
      "facebook": "https://www.facebook.com/adorsysGmbH/",
      "linkedIn": "https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/"
    }
  ],
  "supportedLanguages": ["en", "de", "es", "ua"],
  "supportedApproaches": ["redirect", "embedded"],
  "currency": "EUR",
  "tppSettings": {
    "tppDefaultNokRedirectUrl": "https://www.google.com",
    "tppDefaultRedirectUrl": "https://adorsys-platform.de/solutions/xs2a-sandbox/"
  }
}
```

Json Felder:

- globalSettings - benötigt
  - logo - benötigt, Wert: string, http url oder Dateiname mit Endung oder ' '
  - favicon - optional
    - type - benötigt, Wert: string
    - href - benötigt, Wert: string, http url
  - facebook - optional, Wert: string, http url
  - linkedIn - optional, Wert: string, http url
  - cssVariables - optional
    - colorPrimary - optional, Wert: string, hex
    - colorSecondary - optional, Wert: string, hex
    - fontFamily - optional, Wert: string, font-name or font-name, font-family
    - bodyBG - optional, Wert: string, hex
    - headerBG - optional, Wert: string, hex
    - headerFontColor - optional, Wert: string, hex
    - mainBG - optional, Wert: string, hex
    - footerBG - optional, Wert: string, hex
    - footerFontColor - optional, Wert: string, hex
    - anchorFontColor - optional, Wert: string, hex
    - anchorFontColorHover - optional, Wert: string, hex
    - heroBG - optional, Wert: string, hex
    - stepBG - optional, Wert: string, hex
    - contactsCardBG - optional, Wert: string, hex
    - testCasesLeftSectionBG - optional, Wert: string, hex
    - testCasesRightSectionBG - optional, Wert: string, hex
- contactInfo - benötigt
  - name - benötigt, Wert: string
  - position - benötigt, Wert: string
  - img - benötigt, Wert: string, http url oder Dateiname mit Endung
  - email - optional, Wert: string
  - phone - optional, Wert: string
- officesInfo - benötigt. Array mit 2 Elementen.
  - city - benötigt, Wert: string
  - company - benötigt, Wert: string
  - addressFirstLine - benötigt, Wert: string
  - addressSecondLine - benötigt, Wert: string
  - phone - optional, Wert: string
  - email - optional, Wert: string
  - facebook - optional, Wert: string, http url
  - linkedIn - optional, Wert: string, http url
- supportedLanguages – benötigt. Dieser Array speichert die unterstützten Sprachen, unsere Standardeinstellung ist ["en", "de", "es", "ua"]
- supportedApproaches - benötigt. Dieser Array beinhaltet die unterstützten SCA Ansätze, unsere Standardeinstellung ist ["redirect", "embedded"]
- currency - benötigt. Unsere Standardeinstellung ist "EUR"

2. Wenn Sie die .json Datei erstellt haben (bsp.: UITheme.json) und alle Felder ausgefüllt bzw. angepasst haben, fügen Sie das Logo (bsp.: logo.png) und Kontaktperson (bsp.: contact.png) in den dafür vorgesehenen Bilderordner ./developer-portal-ui/src/assets/UI/custom/.
3. Wenn Sie eine JSON/XML unserer Testfälle im Developer Portal anpassen möchten, erstellen Sie einen neuen Ordner "jsons"/"xmls" in ./developer-portal-ui/src/assets/UI/custom/examples/.
4. Fügen Sie in diesen Ordner alle JSON/XML Dateien ein, die Sie anpassen möchten. Übernehmen Sie dieselben Namen der Beispieldateien für Ihre Anpassung. Die Beispiele und Standard-JSONs mit ihren Eigennamen sind im folgenden Ordner gespeichert ./developer-portal-ui/src/assets/UI/examples/jsons/.
5. Die Beispiele und Standard-XMLs mit ihren Eigennamen sind im folgenden Ordner gespeichert ./developer-portal-ui/src/assets/UI/examples/xmls/.

Herzlichen Glückwunsch, Sie haben Ihre Anpassung abgeschlossen!

<div class="divider">
</div>

# Nächsten Schritte

Wenn Sie alle oben genannten Schritte durchgeführt haben, können Sie unter Testfälle weitergehende Tests nachlesen und durchführen. Dort finden Sie von uns erstellte Postman tests, Beschreibung der XS2A Interface API und Erklärungen der XS2ASandbox Tests mit Swagger.
