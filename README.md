# MensaHub-Gateway

[![Release](https://github.com/olech2412/MensaHub-Gateway/actions/workflows/Release.yml/badge.svg)](https://github.com/olech2412/MensaHub-Gateway/actions/workflows/Release.yml)
[![Daily build and test](https://github.com/olech2412/MensaHub-Gateway/actions/workflows/maven.yml/badge.svg)](https://github.com/olech2412/MensaHub-Gateway/actions/workflows/maven.yml)
[![Test CI/CD Pipeline](https://github.com/olech2412/MensaHub-Gateway/actions/workflows/TestPipeline.yml/badge.svg)](https://github.com/olech2412/MensaHub-Gateway/actions/workflows/TestPipeline.yml)

Dies ist ein privates Studentenprojekt. Die API steht nicht für die Öffentlichkeit zur Verfügung und wird nicht zu kommerziellen Zwecken genutzt.

"MensaHub-Gateway" ist eine SpringBoot Anwendung, die mithilfe von Maven und Java 11 erstellt wurde. Sie stellt Informationen bereit, die aus dem "MensaHub-DataDispatcher" in eine Datenbank geschrieben wurden, um andere Programmierprojekte zu ermöglichen.

## Installation

1. Laden Sie das Projekt von GitHub herunter.
2. Stellen Sie sicher, dass Sie Java 11 und Maven installiert haben.
3. Navigieren Sie in der Kommandozeile in das Verzeichnis des Projekts.
4. Führen Sie den Befehl `mvn clean install` aus, um die Abhängigkeiten zu installieren.
5. Starten Sie die Anwendung mit dem Befehl `mvn spring-boot:run`.
6. Die API sollte jetzt unter http://localhost:8083 erreichbar sein.

## Verwendung

Die API stellt verschiedene Endpunkte zur Verfügung, um auf die Daten in der Datenbank zugreifen zu können. Eine ausführliche Dokumentation der verfügbaren Endpunkte finden Sie im Code oder in der API-Dokumentation. Im realen Produktionsbetrieb findet sich die API Doumentation auch im "MensaHub-Junction" im Developer Portal wieder.
Der Zugriff auf die API ist nur mit einem vorher erstellten API-Account möglich und verwendet JWT zur authentifizierung!

## Das Konzept
![MensaHub-Architektur (1)](https://github.com/olech2412/MensaHub-Junction/assets/76694468/f916f0ff-b0ac-48ee-8f80-5876b9b46e19)
