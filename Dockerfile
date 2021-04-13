FROM openjdk:11.0.7-jre

COPY src/main/resources/application-prod.properties /user/local/service/
COPY target/ /usr/local/service/
COPY kafkaSsl/*.* /kafkaSsl/

ENTRYPOINT ["java", "-jar", "/usr/local/service/kb-server.jar", "--spring.profiles.active=container", "--spring.config.location=optional:classpath:/application-prod.properties"]
