FROM openjdk:17-alpine
LABEL authors="olech2412"
ADD target/mensahub-gateway*.jar mensaHub-gateway.jar
ENTRYPOINT ["java", "-jar", "/mensaHub-gateway.jar"]