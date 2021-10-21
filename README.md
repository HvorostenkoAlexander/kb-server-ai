# Kafka broker Module (kb-server)
Модуль подключения к топикам Kafka НЛМК.

## Сборка библиотеки `product-api`

Модуль зависит от библиотеки `product-api`.

Сборка без этого компонента вызовет _ошибку_.

1. Первый вариант, предпочитаемый, предварительно собрать в локальный репозиторий компонент `product-api`. Для этого необходимо клонировать удаленный репозиторий https://git.nlmk.com/apcs/product-api и руководствуясь файлом README.md собрать компонент.
2. Второй вариант, это задать правильно переменные окружения и получить компонент из репозитория `nlmk-apcs-central`, согласно настройкам в файле `settings.xml`.

### Сборка kb-server
Проект _собирается_ в корневой директории.

Варианты сборки:

1. Для целей тестирования и запуска в песочнице: `mvn clean package`
2. Для развертывания в контейнере. В этом случае используется инструменты GitLab.

## Запуск сервера (модуля)

1. `java -jar target/kb-server.jar` - команда запуска в песочнице.
2. `mvn clean spring-boot:run` - команда запуска в режиме отладки, с помощью модуля `spring-boot-maven-plugin`. В этом режиме процесс сборки проекта вызывает перезапуск сервиса.

Для запуска в песочнице используется файл настроек по-умолчанию `application.properties`.

Второй файл `application-prod.properties` используется для развертывания в контейнере (См. файл `Dockerfile`).

С целью возможности работы kb-server с Apache Kafka развернутой на локальной машине (в песочнице)
предусмотрено использование профиля dev для конфигурирования kafka consumers.

Профиль prod применяется для конфигурирования kafka consumers при подключении к
топикам kafka НЛМК. 

## Токен
Для выполнения запросов REST сервиса необходимо наличие токена (в большинстве случаев).

Токен получается перед выполнением запроса и помещается в заголовок `Authorization`.

Тип токена `Bearer`, формат `JWT`.

Для __тестового__ контура созданы следующие параметры генерации токена:

`realms: apcs-test`

`client_id: apcs-dev`

`client_secret: cacb5d3e-8f5e-4a71-a6f8-6dcbd747900a`

Значение токена находится в теле полученного JSON: элемент `access_token`.

Адрес тестового сервера авторизации (keycloak): https://sso-test.dp.nlmk.com/auth

### Получение токена через `curl`

```
curl --location --request POST 'https://sso-test.dp.nlmk.com/auth/realms/apcs-test/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=apcs-dev' \
--data-urlencode 'client_secret=cacb5d3e-8f5e-4a71-a6f8-6dcbd747900a' \
--data-urlencode 'grant_type=client_credentials'
```

## Swagger UI

Для обращения к UI Swagger нужно запустить сервер (модуль).

Ссылка: `http://127.0.0.1:<port>/swagger-ui.html`

Значение `<port>` указано в `application.properties`.

Для выполнения запросов через UI Swagger необходимо предварительно пройти авторизацию.

Для этого есть кнопка `Authorize`.

В поля `client_id` и `client_secret` вводятся _значения_ для __тестового__ контура.
