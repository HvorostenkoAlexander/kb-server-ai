FROM openjdk:11.0.7-jre

COPY target/ /usr/local/service/

ENTRYPOINT ["java", "-jar", "/usr/local/service/kb-server.jar"]
