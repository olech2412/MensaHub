# MensaHub 🍝

[![Build-CVE-Pipeline](https://github.com/olech2412/MensaHub/actions/workflows/Build-CVE-Pipeline.yml/badge.svg)](https://github.com/olech2412/MensaHub/actions/workflows/Build-CVE-Pipeline.yml)
![Docker Image Version (latest semver)](https://img.shields.io/docker/v/olech2412/mensahub-datadispatcher?label=MensaHub-DataDispatcher%20version%3A&link=https%3A%2F%2Fhub.docker.com%2Fr%2Folech2412%2Fmensahub-datadispatcher)
![Docker Image Version (latest semver)](https://img.shields.io/docker/v/olech2412/mensahub-gateway?label=MensaHub-Gateway%20version%3A&link=https%3A%2F%2Fhub.docker.com%2Fr%2Folech2412%2Fmensahub-gateway)
![Docker Image Version (latest semver)](https://img.shields.io/docker/v/olech2412/mensahub-junction?label=MensaHub-Junction%20version%3A&link=https%3A%2F%2Fhub.docker.com%2Fr%2Folech2412%2Fmensahub-junction)

## Technologies

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Apache](https://img.shields.io/badge/apache-%23D42029.svg?style=for-the-badge&logo=apache&logoColor=white) ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## Description 📖

### What is MensaHub?

MensaHub is an application family that consists out of three parts:

- MensaHub-Gateway
- MensaHub-DataDispatcher
- MensaHub-Junction

### What is MensaHub-Gateway?

MensaHub-Gateway is an REST-API that provides access to data of the canteens of the Studentenwerk Leipzig.
It can be used to get information about the canteens and the meals.

### What is MensaHub-DataDispatcher?

MensaHub-DataDispatcher is a service that is responsible for fetching data from the Studentenwerk Leipzig and storing it
in a database.
It is the heart of the MensaHub application family and feeds the other services with data.
Th DataDispatcher is also responsible for sending an email newsletter to the users that subscribed to it.

### What is MensaHub-Junction?

MensaHub-Junction is a web application that allows users to subscribe to a newsletter that is sent out by the
DataDispatcher.
It also allows to create an api user that can be used to send votes for meals to the Gateway.

## Information for developers 👨‍💻

### How to setup the development environment?

#### Requirements

- Java 17
- Maven
- Docker (optional)
- MariaDB

#### Setup

1. Clone the repository
   '''bash
   git clone https://github.com/olech2412/MensaHub.git
   '''
2. Setup mavens settings.xml
   1. Add the following lines to your settings.xml
   ```xml
    <server>
      <id>github</id>
      <username>olech2412</username>
      <password>$PACKAGE_READ_KEY</password>
    </server>
    ```
   2. **To get the key please create an issue with the 'key request' label.**
3. Resolve dependencies
   1. Run the following command in the root directory of the project
   ```bash
    mvn clean install
    ```
4. Setup the configuration file
   1. Create the following file: ```/$USER_HOME/mensaHub/mensaHub.settings```
   2. Add the following content to the
      file: [mensaHub.settings](https://github.com/olech2412/MensaHub/blob/master/mensaHub.settings)
   3. Setup the file according to your needs
5. Setup the encryption key
   1. Because the config file contains sensitive data, the application encrypts the file and checks that on every
      start.
   2. To encrypt and decrypt the file you have to provide an encryption key. This is done by environment variables.
   3. Add the following environment variable to your system: ```encryption.key=$YOUR_ENCRYPTION_KEY```
   4. You can customize your key but it hast to be 32 characters long due
      to [AES-256](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)
      1. Example: ```S5o3qev2RKxYWNjhXSpuzkmwF3ZqvM!E```
   5. Add the variable to your run configuration or if you are using docker to your docker-compose file
6. Setup the database
   1. Sign in to your MariaDB instance and create a database
   ```mariadb
   CREATE DATABASE mensaHub;
   ```
   2. Create a user and grant him access to the database
   3. Because the application creates the tables on startup, you don't have to create them manually
      1. Start one application and wait until the tables are created (the startup process may propably fail)
   4. Execute the follwoing SQL script:
    ```mariadb
   INSERT INTO mensaHub.cafeteria_dittrichring (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=153&date=$date&criteria=&meal_type=all','Cafeteria Dittrichring');
   INSERT INTO mensaHub.mensa_academica (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=118&date=$date&criteria=&meal_type=all','Mensa Academica');
   INSERT INTO mensaHub.mensa_am_elsterbecken (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=115&date=$date&criteria=&meal_type=all','Mensa am Elsterbecken');
   INSERT INTO mensaHub.mensa_am_medizincampus (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=162&date=$date&criteria=&meal_type=all','Mensa am Medizincampus');
   INSERT INTO mensaHub.mensa_am_park (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=106&date=$date&criteria=&meal_type=all','Mensa am Park');
   INSERT INTO mensaHub.mensa_peterssteinweg (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=111&date=$date&criteria=&meal_type=all','Mensa Peterssteinweg');
   INSERT INTO mensaHub.mensa_schoenauer_str (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=140&date=$date&criteria=&meal_type=all','Mensa Schönauer Straße');
   INSERT INTO mensaHub.mensa_tierklinik (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=170&date=$date&criteria=&meal_type=all','Mensa Tierklinik');
   INSERT INTO mensaHub.menseria_am_botanischen_garten (id,api_url,name) VALUES (1,'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=127&date=$date&criteria=&meal_type=all','Menseria am Botanischen Garten');
   
   # This is the api user with the password: test123!
   INSERT INTO mensaHub.api_user (api_user_id,username,blocking_reason,creation_date,description,email,enabled_by_admin,last_login,password,`role`,verified_email,activation_code_id,deactivation_code_id) VALUES
	 (1,'test123',NULL,'2024-01-12','Nur für Test','test@test.de',1,'2024-01-12 09:22:38.215295','$2a$10$16jWSSzjRzEw2jvuQYxXX.VlqN1sejlEuah/PNKyC41FSkSEbg06C','ROLE_DEV',1,2,2);
   
   # This is the user to login to the Junction with the password: password
   INSERT INTO mensaHub.users (user_id,enabled,password,`role`,username) VALUES
	 (1,1,'$2a$12$tow0YBAKqgG1.4CA9L9QGuuVpKMfdGgyl6azBc1e.3g1empQJmML.','ROLE_ADMIN','user');
   ```

## MensaHub-Models 📦

The MensaHub-Models module contains all models that are used in the MensaHub application family. It is used as a
dependency in all other modules.

## Deployment 🚀

Deployment of the jar is done for the Gateway and the DataDispatcher with:

```bash
mvn clean package
```

Deployment of the Junction is done with:

```bash
mvn clean package -Pproduction
```

## Docker 🐳

All services can be deployed with docker-compose. The docker-compose file is located in the root directory of the
project
for all services including mariadb. If you want to deploy only one service, you can find the docker-compose file in the
corresponding module.

### Docker Hub 💡

- MensaHub-Gateway: [olech2412/mensahub-gateway](https://hub.docker.com/r/olech2412/mensahub-gateway)
- MensaHub-DataDispatcher: [olech2412/mensahub-datadispatcher](https://hub.docker.com/r/olech2412/mensahub-datadispatcher)
- MensaHub-Junction: [olech2412/mensahub-junction](https://hub.docker.com/r/olech2412/mensahub-junction)

## Contribution 🤝

If you want to contribute to the project you are welcome to do so. Please create an issue with the 'key request' label
to get the key for the maven settings.xml file. And lets get started! 🚀
