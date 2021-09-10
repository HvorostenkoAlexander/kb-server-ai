# Product Attestation: Backend Part

## Kafka broker Module

Модуль подключения к Kafka НЛМК.

## Сборка проекта

Проект _собирается_ в корневой директории командой:

`mvn clean package`

Артефакты будут находиться в директории `target`.

## Запуск сервера (модуля)

Запуск можно производить 2 способами:

* `java -jar kb-server-<version>.jar`
* `./kb-server-<version>.jar`

Дополнительно рядом с jar файлом можно положить файл `application.properties` с нужными настройками. 

## Swagger
* Ссылка: http://host:8080/swagger-ui.html
* Для авторизации в swagger в https://sso-test.dp.nlmk.com/auth реалм Apcs-test создана сервисная учетка для разработки:  
  clientId: apcs-dev  
  client_secret: cacb5d3e-8f5e-4a71-a6f8-6dcbd747900a  
  