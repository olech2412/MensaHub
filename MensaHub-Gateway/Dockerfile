FROM arm64v8/openjdk:11-ea-11-jdk-slim
LABEL authors="olech"
EXPOSE 8083
ADD target/MensaHub-Gateway-1.0.1.jar MensaHub-Gateway-1.0.1.jar
ENTRYPOINT ["java", "-jar", "/MensaHub-Gateway-1.0.1.jar"]