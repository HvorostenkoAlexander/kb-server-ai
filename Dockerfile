FROM openjdk:11.0.7-jre

COPY target/ /usr/local/service/

ENV JAVA_TOOL_OPTIONS "-XX:InitialRAMPercentage=10 -XX:MinRAMPercentage=50 -XX:MaxRAMPercentage=80"

ENTRYPOINT ["java", "-jar", "/usr/local/service/kb-server.jar"]
