# MensaHub DataDispatcher
The **MensaHub DataDispatcher** is a Java-based application that uses the **Spring Boot Framework** to retrieve menu data from Studentenwerk Leipzig and send it to authenticated users from a **MariaDB database**. 

This program is an integral part of the MensaHub family. It is important to note that the MensaHub DataDispatcher works in collaboration with other parts of the MensaHub system. Therefore, it is recommended to operate the entire system.

## Technologies

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Apache](https://img.shields.io/badge/apache-%23D42029.svg?style=for-the-badge&logo=apache&logoColor=white) ![MariaDB](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

## Installation

### Requirements

- Java 17
- MariaDB
- Maven
- Mail Server
  - For development purposes it is not necessary
  - For production purposes it is necessary

### Prepare Maven

To run the application the [MensaHub-Models Dependency](https://github.com/olech2412/MensaHub/packages/) is required.
This Package is located here: https://maven.pkg.github.com/olech2412/MensaHub.
To access the package you have to add these lines in your Maven 'settings.xml':

```xml

<server>
  <id>github</id>
  <username>olech2412</username>
  <password>$PACKAGE_READ_KEY</password>
</server>
```

**To get the key please create an issue with the 'key request' label.**

### Prepare mensaHub.settings

You have to provide the mensaHub.settings under the path: ```/$USER_HOME/mensaHub/mensaHub.settings```
To run the application you have to provide the following settings:

```properties
mensaHub.database.name=$YOUR_DATABASE_NAME
# for example: 127.0.0.1:3306
mensaHub.database.location=$YOUR_DATABASE_LOCATION
mensaHub.database.password=$YOUR_DATABASE_PASSWORD
mensaHub.database.username=$YOUR_DATABASE_USERNAME
# you have to provide a mail server
mensaHub.dataDispatcher.mail.smtpHost=localhost
mensaHub.dataDispatcher.mail.smtpPort=25
mensaHub.dataDispatcher.mail.senderMail=noreply_mensahub@olech2412.de
# false is required because auth is currently not supported
mensaHub.dataDispatcher.mail.smtpAuth=false
# be careful with this setting
mensaHub.dataDispatcher.config.reloadInterval=3600000
mensaHub.dataDispatcher.log.location=/tmp
# the address of MensaHub-Junction
mensaHub.dataDispatcher.junction.address=https://mensahub.olech2412.de
# Days to fetch from Studentenwerk
mensaHub.dataDispatcher.fetchDays=14
# the sign that is used when an attribute is not available
mensaHub.dataDispatcher.notAvailable.sign=N/A
```

### Provide the encryption key

Because the config file contains sensitive data, the application encrypts the file and checks that on every start.
To encrypt and decrypt the file you have to provide an encryption key. This is done by environment variables.
The easiest way to do this is to configure the start of the application like this:

```bash
java -jar mensaHub-dataDispatcher-x.x.x.jar -Dencryption.key=your_encryption_key
```

Rules for the encryption key:

- The key should be 32 characters long (due to [AES-256](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard))
- Example: ```S5o3qev2RKxYWNjhXSpuzkmwF3ZqvM!E```

### Prepare the database

The application uses a MariaDB database to store the user data. Please create the database (recommended name: mensaHub)
and write the settings in the mensaHub.settings file. The application will create the tables.
To add the links for the crawler you have to execute the following SQL statements:

```mariadb
INSERT INTO mensaHub.cafeteria_dittrichring (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=153&date=$date&criteria=&meal_type=all',
        'Cafeteria Dittrichring');
INSERT INTO mensaHub.mensa_academica (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=118&date=$date&criteria=&meal_type=all',
        'Mensa Academica');
INSERT INTO mensaHub.mensa_am_elsterbecken (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=115&date=$date&criteria=&meal_type=all',
        'Mensa am Elsterbecken');
INSERT INTO mensaHub.mensa_am_medizincampus (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=162&date=$date&criteria=&meal_type=all',
        'Mensa am Medizincampus');
INSERT INTO mensaHub.mensa_am_park (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=106&date=$date&criteria=&meal_type=all',
        'Mensa am Park');
INSERT INTO mensaHub.mensa_peterssteinweg (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=111&date=$date&criteria=&meal_type=all',
        'Mensa Peterssteinweg');
INSERT INTO mensaHub.mensa_schoenauer_str (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=140&date=$date&criteria=&meal_type=all',
        'Mensa Schönauer Straße');
INSERT INTO mensaHub.mensa_tierklinik (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=170&date=$date&criteria=&meal_type=all',
        'Mensa Tierklinik');
INSERT INTO mensaHub.menseria_am_botanischen_garten (id, api_url, name)
VALUES (1,
        'https://www.studentenwerk-leipzig.de/mensen-cafeterien/speiseplan?location=127&date=$date&criteria=&meal_type=all',
        'Menseria am Botanischen Garten');
```

## Operation

### Docker 🐳

The operation of the application is very easy with Docker.
The newest image is always available on [Docker Hub](https://hub.docker.com/r/olech2412/mensahub-datadispatcher).

Pull the image:

```bash
docker pull olech2412/mensahub-datadispatcher:latest
```

Start the container:

- $systemFolder: The path to the folder where the mensaHub.settings file is located on your system
- your_encryption_key: The encryption key to encrypt and decrypt the mensaHub.settings file
  see [Provide the encryption key](#provide-the-encryption-key)

```bash
docker run -v $systemFolder:/root/mensaHub/ --env encryption.key=your_encryption_key --name MensaHub-DataDispatcher mensahub-datadispatcher:x.x.x
```

### Manual

To run the application manually you have to build the application with Maven:

```bash
mvn clean package
```

After that you can run the application with:

```bash
java -jar mensaHub-dataDispatcher-x.x.x.jar -Dencryption.key=your_encryption_key
```