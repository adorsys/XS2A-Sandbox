<div class="divider">
</div>

# Empezando

[La Directiva de Servicios de Pago 2](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN) (PSD2) instruye a los bancos (Proveedores de Servicios de Pago de Servicio de Cuentas o ASPSP) para que proporcionen una interfaz de Acceso a la Cuenta (XS2A) completamente productiva a Terceros Proveedores (TPP) hasta marzo de 2020. El propio XS2A consiste en servicios bancarios para iniciar pagos (PIS), solicitar datos de cuenta (AIS) y obtener la confirmación de la disponibilidad de fondos (PIIS). Para garantizar el cumplimiento de este plazo debido a las adaptaciones y los errores, PSD2 reclama a los bancos que proporcionen un Sandbox dinámica y funcional que ofrece los servicios XS2A en un entorno no productivo hasta junio de 2019.

El **XS2ASandbox** es un entorno de entorno de pruebas dinámico que cumple completamente los requisitos de PSD2 para proporcionar API a Terceros Proveedores(TPP). Basado en la especificación NextGen PSD2 del Grupo de Berlín para el acceso a cuentas (XS2A),

Este portal de desarrolladores se crea para ayudar a los desarrolladores de TPP a comenzar a trabajar con XS2ASandbox.

<div class="divider">
</div>

# Arquitectura y módulos de XS2ASandbox

Los componentes de XS2ASandbox con sus conexiones entre sí se muestran en la Figura 1.1.

![Figure 1.1](../../assets/images/Graphic_XS2A_Sandbox.jpg)

Figura 1.1: Componentes de la XS2ASandbox

<div class="divider">
</div>

# Interfaz XS2A

El componente central de **XS2ASandbox** es la interfaz XS2A que cumple con los requisitos del Grupo de Berlín [NextGenPSD2](https://www.berlin-group.org/psd2-access-to-bank-accounts) (versión 1.3) y se basa en datos de prueba. Puede visitar nuestra interfaz de usuario [XS2A Swagger UI](https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/) o encontrar la interfaz completa de [OpenSource XS2A en Github](https://github.com/adorsys/xs2a).

<div class="divider">
</div>

# Perfil ASPSP

Además de la interfaz real, PSD2 instruye a los ASPSP para que ofrezcan una documentación técnica gratuita que contenga, entre otros, información sobre productos de pago admitidos y servicios de pago. Esta información se almacena en **ASPSP-profile** (perfil de banco), un servicio basado en el archivo yaml donde un banco puede proporcionar productos de pago, servicios de pago, enfoques SCA compatibles y otras configuraciones específicas del banco.

<div class="divider">
</div>

# Servicio de Certificación TPP

Generalmente, antes de acceder a los servicios XS2A, un TPP tendría que registrarse en su Autoridad Nacional Competente (NCA) y solicitar un certificado [eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) en un Proveedor de Servicios de Fideicomiso (TSP) apropiado. Sería demasiado esfuerzo emitir un certificado real solo para fines de prueba, por lo que **XS2ASandbox** simula además un TSP ficticio que emite Certificados de autenticación de sitios web calificados (QWAC). Un QWAC es parte de eIDAS y podría ser mejor conocido como certificado [X.509](https://www.ietf.org/rfc/rfc3739.txt) Para propósitos de PSD2, el certificado se extiende por QcStatement que contiene los valores apropiados, como los roles del PSP (consulte [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

Después de incrustar el QWAC en la solicitud XS2A real, el rol y la firma se validan en un proxy central inverso antes de pasar finalmente a la interfaz donde tiene lugar la lógica bancaria.

<div class="divider">
</div>

# Interfaz de usuario TPP

Los desarrolladores de TPP pueden registrarse en el sistema, obtener un certificado descargar datos de prueba para su aplicación TPP utilizando el certificado generado y los datos preparados en la interfaz de usuario de TPP.

## Cómo crear una cuenta para TPP:

1. Abra la página de inicio de sesión de TPP UI

2. Elija la opción "Registrarse " a continuación.

3. Proporcione toda la información necesaria: número de autorización TPP, correo electrónico, nombre de usuario y contraseña.

4. Puede generar un certificado QWAC de prueba para usarlo con la interfaz XS2A si es necesario

## Cómo crear usuarios:

Los usuarios pueden crearse manualmente o cargando el archivo .yaml

### Para crear un usuario manualmente:

1. Vaya a "Mi lista de usuarios" y haga clic en "Crear nuevo usuario ".

2. Ingrese correo electrónico, inicio de sesión y pin para un nuevo usuario

3. Elija el método de autenticación (solo el correo electrónico es válido por ahora) e ingrese un correo electrónico válido. Si elige la opción " Usar TAN estático en SandboxMode ", se usará un TAN estático falso para las pruebas. Entonces usted tiene para ingresar su TAN simulado también. Puede agregar múltiples métodos SCA a cada usuario

### Para crear cuentas manualmente:

1 Elija un usuario en su lista de usuarios y haga clic en "Crear cuenta de depósito".

2. Seleccione el tipo de cuenta, el uso y el estado de la cuenta. IBAN podría generarse automáticamente. Tenga en cuenta que la interfaz XS2A solo acepta ibans válidos

3. Una vez creada la cuenta, puede usar la función "Depositar efectivo " para agregar cualquier cantidad a los saldos con fines de prueba. Todas las cuentas creadas están disponibles en la pestaña "Cuentas de usuarios".

Para crear usuarios y cuentas automáticamente, genere datos de prueba con la pestaña "Generar datos de prueba", y se generarían datos de prueba compatibles con NISP. Si desea cargar su propio archivo de prueba .yaml, use la pestaña "Subir archivos" .

## Cómo crear pagos de prueba, consentimientos y transacciones:

Para crear pagos de prueba, debe generar datos de pago de prueba en la pestaña "Generar datos de prueba", marcando la opción "Generar datos de pago" como verdadero. Además, la carga de un archivo .yaml personalizado con los pagos son posibles en la pestaña "Subir archivos". Los consentimientos y las transacciones solo pueden crearse con la carga del archivo .yaml adecuado.

<div class="divider">
</div>

# Online Banking

En el caso de un enfoque de REDIRECT SCA, un usuario desea dar su consentimiento para utilizar la información de su cuenta o para confirmar / cancelar el pago. La banca en línea es una interfaz de usuario para proporcionar consentimiento a un banco. Los enlaces para la confirmación de consentimiento y la confirmación o cancelación de pago se proporcionan en la respuesta de los puntos finales correspondientes.

<div class="divider">
</div>

# Enlaces a entornos

| Service                   |           Local environment            |                                                                       Demo environment |
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

# Cómo descargar, configurar y ejecutar el proyecto

## Prerrequisitos

Este sandbox se ejecuta con el docker-compose que se puede encontrar en docker-compose.yml y Makefile en el directorio del proyecto. Pero antes de ejecutar XS2ASandbox, primero verifique si todas las dependencias de compilación están instaladas:

_make check_

Si falta algo, instálelo en su máquina local, de lo contrario la compilación fallará. Lista de dependencias que se requieren para usar XS2ASandbox: Java 11, NodeJs, CLI angular, Asciidoctor, jq, Docker, Docker Compose, Maven, PlantUML. Aquí están los enlaces donde puede instalar las dependencias necesarias:

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

Puede eliminar todos los contenedores de Sandbox de Docker con el siguiente comando:

_docker-compose rm -s -f -v_

## Nota 1

Utilice la versión Node.js inferior a 12 (por ejemplo, 10.x.x o 11.x.x). De lo contrario, las aplicaciones angular no se construirían debido a conflictos de versión.

## Nota 2

Verifique la cantidad de memoria asignada a Docker (Abra Docker Desktop -> Preferencias -> Avanzado -> Memoria). Para un inicio rápido e indoloro de todos los servicios, no debe ser inferior a 5 GB.

## Descargar XS2ASandbox

Descargue el proyecto directamente desde GitHub o use el comando:

_git clone https://github.com/adorsys/XS2A-Sandbox_

## Construye y ejecuta XS2ASandbox

Después de descargar el proyecto vaya al directorio del proyecto:

_cd XS2A-Sandbox_

Después de eso, puede construir y ejecutar XS2ASandbox de dos maneras: con un comando docker o con comandos Makefile.

Si quieres, usa una primera manera:

1. Construye todos los servicios con el comando:

_make_

2. Después de edificio servicios, puede ejecutar XS2ASandbox con un simple comando docker:

_docker-compose up_

En Makefile puede usar uno de los tres comandos:

• Ejecute servicios desde Docker Hub sin construir:

_make run_

• Construir y ejecutar servicios:

_make all_

• Ejecutar servicios sin construir:

_make start_

Una vez que hayas construido el proyecto puedes ejecutarlo sin construir la próxima vez - comando docker-compose up o make start desde Makefile.

Recuerde que después de actualizar el proyecto debe reconstruirlo - comando make o make all desde Makefile.

<div class="divider">
</div>

# Solución de problemas

Estos son errores comunes que puede obtener durante el inicio de XS2ASandbox y una instrucción sobre cómo deshacerse de él:

## Error de lista de cambios de liquibase

Este error puede producirse si tuvo un inicio incorrecto de XS2ASandbox anteriormente. Ejemplo de posible stack trace:

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

Solución posible:

Busque y elimine todas las carpetas "ledgerdbs" y "xs2adbs". Borrar todos los contenedores docker con el comando:

_docker-compose rm -s -f -v_

Reinicie todos los servicios.

## Error de versión del Node

Este error se puede producir debido a una versión incorrecta de NodeJs (versión superior a 11.x). Ejemplo de posible stack trace:

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

Solución posible:

Primero, verifique su versión de NodeJs con el comando:

_node -v_

Si la versión es superior a 11.x, cambie la versión de NodeJs a una anterior.

<div class="divider">
</div>

# Cómo registrar TPP y comenzar testing

1. Abra la página de inicio de sesión de la interfaz de usuario de TPP.
2. Si Usted no tiene nombre de usuario y contraseña, regístrese Usted mismo haciendo clic en el botón "Registrarse".
3. Regístrese, cree un certificado e inicie sesión en el sistema. Nota: la identificación de TPP debe constar de al menos 8 dígitos, no se permiten letras ni otros signos.
4. Sube los datos de prueba y comienza a probar.

El flujo completo para que los TPP comiencen su trabajo con XS2ASandbox se muestra en la Figura 1.2:

![Figure 1.2](../../assets/images/Flow.png)

Figura 1.2: flujo de TPP paso a paso

<div class="divider">
</div>

# Cómo personalizar el portal de desarrolladores

1. Crea un archivo .json con el nombre UITheme.

Ejemplo de Json:

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

Campos de Json:

- globalSettings - requeridos
  - logo - requeridos, value: string, http url or file name with extension or ' '
  - favicon - optional
  - type - required, value: string
  - href - required, value: string, http url
  - facebook - optional, value: string, http url
  - linkedIn - optional, value: string, http url
  - cssVariables - optional
    - colorPrimary - optional, value: string, hex
    - colorSecondary - optional, value: string, hex
    - fontFamily - optional, value: string, font-name or font-name, font-family
    - bodyBG - optional, value: string, hex
    - headerBG - optional, value: string, hex
    - headerFontColor - optional, value: string, hex
    - mainBG - optional, value: string, hex
    - footerBG - optional, value: string, hex
    - footerFontColor - optional, value: string, hex
    - anchorFontColor - optional, value: string, hex
    - anchorFontColorHover - optional, value: string, hex
    - heroBG - optional, value: string, hex
    - stepBG - optional, value: string, hex
    - contactsCardBG - optional, value: string, hex
    - testCasesLeftSectionBG - optional, value: string, hex
    - testCasesRightSectionBG - optional, value: string, hex
- contactInfo - requeridos
  - name - requeridos, value: string
  - position - requeridos, value: string
  - img - requeridos, value: string, http url or file name with extension
  - email - optional, value: string
  - phone - optional, value: string
- officesInfo - requeridos. Array de 2 elementos.
  - city - requeridos, value: string
  - company - requeridos, value: string
  - addressFirstLine - requeridos, value: string
  - addressSecondLine - requeridos, value: string
  - phone - optional, value: string
  - email - optional, value: string
  - facebook - optional, value: string, http url
  - linkedIn - optional, value: string, http url
- supportedLanguages - obligatorio. Matriz de idiomas admitidos. La configuración predeterminada es ["en", "de", "es", "ua"]
- supportedApproaches - obligatorio. Matriz de enfoques SCA compatibles. La configuración predeterminada es ["redirect", "embedded"]
- currency - obligatorio. La configuración predeterminada es "EUR".

2. Cuando cree el archivo .json (por ejemplo, UITheme.json) y complete todos los campos requeridos, coloque este archivo con el logotipo (por ejemplo, logo.png) y la persona de contacto (por ejemplo, contact.png) en la carpeta ./developer-portal-ui/src/assets/UI/custom/.
3. Si desea personalizar jsons/xmls, que están presentes como ejemplos en las secciones de Casos de prueba del portal para desarrolladores, simplemente cree una carpeta "jsons"/"xmls" en ./developer-portal-ui/src/assets/UI/custom/examples/.
4. Ponga todos los jsons que desee personalizar en esta carpeta. Mantenga los nombres exactos de los archivos como en los ejemplos. Los ejemplos y los jsons predeterminados con los nombres adecuados se pueden encontrar en la carpeta ./developer-portal-ui/src/assets/UI/examples/jsons/.
5. Los ejemplos y los xmls predeterminados con los nombres adecuados se pueden encontrar en la carpeta ./developer-portal-ui/src/assets/UI/examples/xmls/.

Personalización esta completada, felicitaciones!

<div class="divider">
</div>

# ¿Que sigue?

Cuando haya terminado con todos los pasos del Manual de introducción consulte la sección Casos de prueba para realizar más pruebas. Allí encontrará las pruebas preparadas de Postman, la descripción de la interfaz de la interfaz XS2A y las instrucciones para probar XS2ASandbox con Swagger.
