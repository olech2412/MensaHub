# MensaHub-Gateway

[![Java CI with Maven](https://github.com/olech2412/EssensGetter-API/actions/workflows/maven.yml/badge.svg)](https://github.com/olech2412/EssensGetter-API/actions/workflows/maven.yml)
[![Test CI/CD Pipeline](https://github.com/olech2412/EssensGetter-API/actions/workflows/TestPipeline.yml/badge.svg?branch=master)](https://github.com/olech2412/EssensGetter-API/actions/workflows/TestPipeline.yml)
[![Release](https://github.com/olech2412/EssensGetter-API/actions/workflows/Release.yml/badge.svg)](https://github.com/olech2412/EssensGetter-API/actions/workflows/Release.yml)

Dies ist ein privates Studentenprojekt. Die API steht nicht für die Öffentlichkeit zur Verfügung und wird nicht zu kommerziellen Zwecken genutzt.

MensaHub-Gateways ist eine SpringBoot Anwendung, die mithilfe von Maven und Java 11 erstellt wurde. Sie stellt Informationen bereit, die aus dem MensaHub-DataDispatcher in eine Datenbank geschrieben wurden, um andere Programmierprojekte zu ermöglichen.

## Installation

1. Laden Sie das Projekt von GitHub herunter.
2. Stellen Sie sicher, dass Sie Java 11 und Maven installiert haben.
3. Navigieren Sie in der Kommandozeile in das Verzeichnis des Projekts.
4. Führen Sie den Befehl `mvn clean install` aus, um die Abhängigkeiten zu installieren.
5. Starten Sie die Anwendung mit dem Befehl `mvn spring-boot:run`.
6. Die API sollte jetzt unter http://localhost:8083 erreichbar sein.

## Verwendung

Die API stellt verschiedene Endpunkte zur Verfügung, um auf die Daten in der Datenbank zugreifen zu können. Eine ausführliche Dokumentation der verfügbaren Endpunkte finden Sie im Code oder in der API-Dokumentation.
Der Zugriff auf die API ist nur mit einem Zugriffstoken möglich!

## Das Konzept
![EssensGetter SoftwareArchitektur drawio (1)](https://user-images.githubusercontent.com/76694468/212769942-63c4dd74-2664-4111-9736-429c27f669c5.png)

Ich hoffe, dass Sie die Anwendung trotzdem nützlich finden.
