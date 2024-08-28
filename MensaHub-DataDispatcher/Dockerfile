FROM openjdk:17-alpine
LABEL authors="olech2412"

# Kopiere die JAR-Datei in das Image
ADD target/mensaHub-dataDispatcher-*.jar mensaHub-dataDispatcher.jar

# Setze den ENTRYPOINT, um die JAR-Datei auszuführen
ENTRYPOINT ["java", "-jar", "/mensaHub-dataDispatcher.jar"]