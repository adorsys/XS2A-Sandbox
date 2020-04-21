<div class="divider">
</div>

# Вступ

[Директива про надання платіжних послуг 2](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32015L2366&from=EN) (The Payment Service Directive 2 - PSD2) вимагає від банків - постачальників платіжних послуг (Account Servicing Payment Service Providers - ASPSP) надати повністю робочий інтерфейс доступу до облікового запису (XS2A) третім особам (TPP) до Березня 2020 року. XS2A інтерфейс складається з банківських сервісів, які можуть ініціювати платежі (PIS), запитувати дані облікового запису (AIS) та отримувати підтвердження наявності коштів (PIIS). Для того, щоб банки гарантовано встигнули перейти на XS2A інтерфейс, PSD2 вимагає від банків створити повністю робочу динамічну пісочницю(Sandbox), яка забезпечує роботу XS2A інтерфейсу у невиробничому середовищі, до червня 2019 року.

**XS2ASandbox** є динамічним середовищем, яке повністю відповідає вимогам специфікації PSG2 NextGen від Berlin Group для доступу до рахунків (XS2A).

Цей портал розробника створений для того, щоб допомогти розробникам TPP розпочати роботу з XS2ASandbox.

<div class="divider">
</div>

# Архітектура та модулі XS2ASandbox

Компоненти XS2ASandbox з їх зв'язками показані на рисунку 1.1.

![Figure 1.1](../../assets/images/Graphic_XS2A_Sandbox.jpg)
Рисунок 1.1: Компоненти XS2ASandbox

<div class="divider">
</div>

# Інтерфейс XS2A

Центральним компонентом **XS2ASandbox** є інтерфейс XS2A, який відповідає вимогам специфікації [NextGenPSD2](<(https://www.berlin-group.org/psd2-access-to-bank-accounts)>) (версія 1.3) від Berlin Group і базується на тестових даних. Ви можете відвідати наш [XS2A Swagger UI](https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/) або ознайомитись з [опенсорсною імплементацією інтерфейсу XS2A на Github](https://github.com/adorsys/xs2a).

<div class="divider">
</div>

# ASPSP-профіль

Крім фактичного інтерфейсу, PSD2 інструктує ASPSPs надавати безкоштовну технічну документацію, що містить, серед іншого, інформацію про підтримувані платіжні продукти та платіжні послуги. Ця інформація зберігається в **ASPSP-профілі** (банківський профіль), створеного на основі yaml-файлу, де банк може надавати доступні продукти оплати (payment products), платіжні послуги (payment services), підтримувані підходи авторизації користувачів (SCA) та інші специфічні для банку параметри.

<div class="divider">
</div>

# Сервіс сертифікації TPP

Зазвичай, перед тим, як отримати доступ до XS2A, TPP повинен буде зареєструватися у своєму національному компетентному органі (NCA) і отримати сертифікат [eIDAS](https://eur-lex.europa.eu/legal-content/EN/TXT/PDF/?uri=CELEX:32014R0910&from=EN) у відповідного Trust Service Provider (TSP). Отримання реального сертифіката тільки для цілей тестування потребує надто багато зусиль, тому XS2ASandbox додатково імітує вигаданий TSP, який видає сертифікати автентифікації веб-сайту (QWAC). QWAC є частиною eIDAS і може бути більш відомий як [X.509](https://www.ietf.org/rfc/rfc3739.txt) сертифікат. Для цілей PSD2 сертифікат розширюється QcStatement, що містить відповідні значення, такі, як ролі PSP (див. [ETSI](https://www.etsi.org/deliver/etsi_ts/119400_119499/119495/01.01.02_60/ts_119495v010102p.pdf)).

Після вбудовування QWAC у фактичний запит XS2A роль та підпис перевіряються на центральному зворотному проксі, перш ніж остаточно передати запит до інтерфейсу.

<div class="divider">
</div>

# Інтерфейс для TPP

Розробники TPP можуть зареєструватися в системі, отримати сертифікат і завантажити тестові дані для своєї програми, використовуючи згенерований сертифікат і підготовлені дані в інтерфейсі TPP.

## Як створити обліковий запис для TPP:

1. Відкрийте сторінку входу в інтерфейс TPP.

2. Оберіть опцію "Зареєструватися".

3. Надайте всю необхідну інформацію: номер TPP, електронну пошту, логін та пароль.

4. Ви можете створити тестовий сертифікат QWAC для того, щоб використовувати його при роботі з інтерфейсом XS2A, якщо потрібно.

## Як створити користувачів:

Користувачів можна створити самостійно або за допомогою завантаження .yaml файлу.

### Створення користувачів самостійно:

1. Перейдіть до "Список моїх користувачів" та натисніть "Створити нового користувача".

2. Введіть електронну пошту, логін та PIN-код для нового користувача.

3. Оберіть метод SCA (наразі діє лише електронна пошта) та введіть дійсну електронну пошту. Якщо ви виберете опцію "Використовувати статичний TAN", для тестування буде використано лише статичний TAN. Якщо ця опція обрана, вам також потрібно ввести свій статичний TAN. Ви можете додати кілька методів SCA для кожного користувача

### Створення облікових записів самостійно:

1 Оберіть користувача у своєму списку користувачів і натисніть "Створити депозитний рахунок"

2. Оберіть тип облікового запису, тип використання та статус облікового запису. IBAN можна згенерувати автоматично. Будь ласка, зауважте, що інтерфейс XS2A може працювати лише з дійсними ibans.

3. Після створення облікового запису ви можете скористатися функцією "Внести готівку ", щоб додати для тестування будь-яку суму до балансу. Усі створені рахунки доступні у вкладці "Акаунти користувачів ".

Щоб автоматично створювати користувачів та акаунти, згенеруйте тестові дані у вкладці "Створити тестові дані". Якщо ви хочете завантажити свій `.yaml` файл, скористайтесь вкладкою "Завантажити файли ".

## Як створити тестові платежі, згоди та транзакції:

Щоб створити тестові платежі, вам потрібно генерувати дані тестових платежів у вкладці " Згенерувати тестові дані", також обравши опцію " Генерувати дані платежу ". Завантаження користувальницького файлу .yaml з платежі можливо у вкладці "Завантажити файли ". Згоди та транзакції можна створити лише через завантаження відповідного файлу `.yaml`.

<div class="divider">
</div>

# Онлайн-банкінг

У випадку підходу REDIRECT SCA користувач хоче надати згоду на використання інформації про свій рахунок або підтвердити/скасувати платіж. Онлайн-банкінг - це інтерфейс для надання згоди банку. Посилання для підтвердження згоди та підтвердження оплати або скасування надаються у відповіді відповідних ендпоінтів в XS2A.

<div class="divider">
</div>

# Посилання на середовища

| Сервіс                    |          Локальне середовище           |                                                                        Демо-середовище |
| ------------------------- | :------------------------------------: | -------------------------------------------------------------------------------------: |
| Інтерфейс XS2A Swagger    | http://localhost:8089/swagger-ui.html  |                                     https://demo-dynamicsandbox-xs2a.cloud.adorsys.de/ |
| Портал розробника         |         http://localhost:4206          |                        https://demo-dynamicsandbox-developerportalui.cloud.adorsys.de/ |
| Consent management system | http://localhost:38080/swagger-ui.html |                                       https://demo-dynamicsandbox-cms.cloud.adorsys.de |
| Ledgers                   | http://localhost:8088/swagger-ui.html  |                                   https://demo-dynamicsandbox-ledgers.cloud.adorsys.de |
| ASPSP-профіль Swagger     | http://localhost:48080/swagger-ui.html |                              https://demo-dynamicsandbox-aspspprofile.cloud.adorsys.de |
| Інтерфейс TPP             |         http://localhost:4205          |                               https://demo-dynamicsandbox-tppui.cloud.adorsys.de/login |
| Онлайн-банкінг            |         http://localhost:4400          | https://demo-dynamicsandbox-onlinebankingui.cloud.adorsys.de/account-information/login |
| Сервер онлайн-банкінгу    | http://localhost:8090/swagger-ui.html  |             https://demo-dynamicsandbox-onlinebanking.cloud.adorsys.de/swagger-ui.html |
| Генератор сертифікатів    | http://localhost:8092/swagger-ui.html  |      https://demo-dynamicsandbox-certificategenerator.cloud.adorsys.de/swagger-ui.html |

<div class="divider">
</div>

# Як завантажити, налаштувати і запустити проект

## Вимоги до програмного забезпечення

XS2ASandbox запускається за допомогою Docker Compose файлу, який можна знайти у docker-compose.yml, та Makefile у директорії проекту. Але перед тим, як запустити XS2ASandbox, спочатку перевірте, чи встановлені всі залежності:

_make check_

Якщо чогось не вистачає, встановіть програму на локальну машину, інакше зібрати проект не вдасться. Список залежностей, які потрібні для використання XS2ASandbox: Java 11, NodeJs, Angular CLI, Asciidoctor, jq, Docker, Docker Compose, Maven, PlantUML. Ось посилання, де можна встановити необхідні залежності:

| Залежності          |                Посилання                |
| ------------------- | :-------------------------------------: |
| Java 11             |    https://openjdk.java.net/install/    |
| Node.js 11.x        |     https://nodejs.org/en/download      |
| Angular CLI 9.x     |   https://angular.io/guide/quickstart   |
| Asciidoctor 2.0     |         https://asciidoctor.org         |
| jq 1.6              | https://stedolan.github.io/jq/download  |
| Docker 1.17         |   https://www.docker.com/get-started    |
| Docker Compose 1.24 | https://docs.docker.com/compose/install |
| Maven 3.5           |  https://maven.apache.org/download.cgi  |
| PlantUML 1.2019.3   |     http://plantuml.com/en/starting     |

Зупиніть працюючі контейнери в терміналі комбінацією клавіш _Control + C_.

Ви можете видалити всі контейнери XS2ASandbox з Docker наступною командою:

_docker-compose rm -s -f -v_

## Примітка 1

Будь ласка, використовуйте версію Node.js нижче 12 (наприклад, 10.xx або 11.xx). Інакше Angular програми не будуть побудовані через конфлікти версій.

## Примітка 2

Перевірте кількість пам'яті, надану Docker (Відкрити Docker Desktop -> Налаштування -> Додатково -> Пам'ять). Для швидкого і безболісного запуску всіх сервісів кількість наданої пам'яті повинна бути не менше 5 Гб.

## Завантаження XS2ASandbox

Завантажте проект безпосередньо з GitHub або скористайтеся командою:

_git clone https://github.com/adorsys/XS2A-Sandbox_

## Збірка та запуск XS2ASandbox

Після завантаження проекту перейдіть до головної директорії проекту:

_cd XS2A-Sandbox_

Виконати збірку та запуск XS2ASandbox можна двома способами - за допомогою docker команди або команд з Makefile.

Якщо ви хочете скористатися першим способом:

1. Збірка усіх сервісів за допомогою команди:

_make_

2. Після виконання збірки ви можете запустити XS2ASandbox за допомогою простої docker команди:

_docker-compose up_

У Makefile ви можете використати одну з трьох команд:

• Запустити сервіси з Docker Hub без виконання збірки:

_make run_

• Виконати збірку та запуск сервісів:

_make all_

• Запустити сервіси без збірки:

_make start_

Після того, як ви виконали збірку проекту, ви можете запустити його, не виконуючи збірки наступного разу - команда docker-compose up або make start з Makefile.

Пам'ятайте, що після оновлення проекту необхідно виконати повторну збірку - команда make або make all з Makefile.

<div class="divider">
</div>

# Вирішення проблем

Далі зібрані типові помилки, які ви можете отримати під час запуску XS2ASandbox, і надано інструкцію, як їх позбутися:

## Помилка Liquibase

Цю помилку можна отримати, якщо не вдалося запустити XS2ASandbox раніше. Приклад можливого stack trace:

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

Можливе рішення:

Знайдіть і видаліть усі папки "ledgerdbs" і "xs2adbs". Видаліть усі докер-контейнери за допомогою команди:

_docker-compose rm -s -f -v_

Перезапустіть усі сервіси.

## Помилка версії NodeJs

Цю помилку можна отримати через неправильну версію NodeJs (версія вище, ніж 11.x) Приклад можливого stack trace:

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

Можливе рішення:

Спочатку перевірте версію NodeJs за допомогою команди:

_node -v_

Якщо версія вище, ніж 11.x - змініть версію NodeJs на попередню.

<div class="divider">
</div>

# Як зареєструвати TPP і почати тестування

1. Відкрийте сторінку входу до інтерфейсу користувача TPP.
2. Якщо у вас немає логіна та пароля - зареєструйтеся, натиснувши кнопку "Реєстрація".
3. Зареєструйтеся, створіть сертифікат і увійдіть до системи. Примітка: Ідентифікатор ТРР повинен складатися принаймні з 8 цифр, не допускається жодних букв або інших знаків.
4. Завантажте тестові дані та почніть тестування.

Весь порядок дій TPP для того, щоб почати свою роботу з XS2ASandbox, відображений на рисунку 1.2:

![Figure 1.2](../../assets/images/Flow.png)

Рисунок 1.2: Робочий процес для TPP

<div class="divider">
</div>

# Як кастомізувати портал розробників

У цьому порталі можливо кастомізувати тексти, меню, кількість та контент сторінок, стилі та кольори елементів. Дізнайтеся, як це зробити, прочитавши [Гайд по кастомізації](../../../../assets/files/UIs_customization_guide.pdf).

<div class="divider">
</div>

# Підтримка Google Analytics

Щоб підключити свій обліковий запис Google Analytics, у UITheme.json у папці `custom-content` Developer Portal UI додайте `googleAnalyticsTrackingId` з ідентифікатором свого облікового запису Google Analytics. Потім запустіть застосунок, і обліковий запис Google Analytics буде підключено автоматично.

```json
{
  "globalSettings": {
    "googleAnalyticsTrackingId": "YOUR_TRACKING_ID"
  }
}
```

Портал розробника надає Google Analytics інформацію про кожне відвідування сторінки та деякі події. Ці події - це кожне випробування ендпоінту API у розділі "Сценарії для тестування" (подія відправляється у Google Analytics з кожним натисканням кнопки "Надіслати") та завантаження тестів Postman (подія відправляється у Google Analytics з кожним натисканням кнопки "Завантажити").

<div class="divider">
</div>

# Що далі?

Після завершення всіх кроків з розділу Початок роботи перейдіть до розділу Сценарії для тестування. Там ви знайдете підготовлені тести Postman, опис API інтерфейсу XS2A та інструкції по тестуванню XS2ASandbox зі Swagger.
