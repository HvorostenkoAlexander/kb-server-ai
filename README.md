# Product Attestation: Backend Part

## Kafka broker Module

Модуль подключения к Kafka НЛМК.

## Сборка проекта

Проект _собирается_ в корневой директории командой:

`mvn clean package`

Артефакты будут находиться в директории `target`.

## Генарация классов согласно AVRO схемам:

`mvn generate-sources`

Файлы avro схем находятся в директории: ${project.basedir}/src/main/resources/avro/
указанной в настройках avro-maven-plugin в pom.xml

Классы будут находиться в директории ${project.basedir}/src/main/java указанной в pom.xml.

## Запуск сервера (модуля)

Запуск можно производить 2 способами:

* `java -jar kb-server-<version>.jar`
* `./kb-server-<version>.jar`

Дополнительно рядом с jar файлом можно положить файл `application.properties` с нужными настройками. 
 
