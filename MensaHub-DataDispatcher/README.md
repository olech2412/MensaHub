# MensaHub-DataDispatcher

## Usage

The program runs in the background and automatically communicates with the OpenMensa API to get the data and store it in the database. Authorized users will automatically receive an email with the updated menus.

## The concept
![EssensGetter SoftwareArchitektur drawio (1)](https://user-images.githubusercontent.com/76694468/212769942-63c4dd74-2664-4111-9736-429c27f669c5.png)

## Installation

### Download the package
1. Download the program from the latest release on GitHub.
```bash
git clone https://github.com/olech2412/EssensGetter-2.0.git
```

### Setting up the environment

#### Install Java
1. Check if Java is already installed
```bash
java -version
```
2. If not update your packages and start the installation
```bash
sudo apt-get update
sudo apt-get install openjdk-11-jdk

# Check if everything works fine
java -version
```

#### Install MariaDB
1. Add the MariaDB Repositorys to install the latest version
```bash
sudo apt-get install software-properties-common
sudo apt-key adv --recv-keys --keyserver hkp://keyserver.ubuntu.com:80 0xF1656F24C74CD1D8
sudo add-apt-repository 'deb [arch=amd64,arm64,ppc64el] http://mirrors.accretive-networks.net/mariadb/repo/10.5/ubuntu bionic main'
```

2. Update your packages and start the installation
```bash
sudo apt-get update
sudo apt-get install mariadb-server

# Check if everything works fine
sudo systemctl status mariadb
# You should see something like 'active (running)' if not see the MariaDB documentation
```

3. MariaDB secure installation
```bash
sudo mysql_secure_installation

# Do the configuration-steps (for more Information visit the MariaDB documentation
# set password for the root user
# remove anonymous user
# deactivate remote access for root
```

#### Setup MariaDB
**The following steps are based on the default installation and can be different to your setup. Checkout the application.properties file `src/main/resources/application.properties` for your properties**
1. Create the database
```sql
CREATE DATABASE essensGetter;
```
2. Create the user and give him all rights to interact with the database
```sql
CREATE USER 'egrAdmin'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON essensGetter.* TO 'egrAdmin'@'localhost';
FLUSH PRIVILEGES;
```
3. You are done! MariaDB was set up correctly

##### Prepare some data for the database
1. Start the program in your IDE
  * 1.1 Hibernate creates all tables with all references for us
  * 1.2 Dont worry if this throws an error
2. Insert the default activationcode - every subscriber has this code if he is enabled
```sql
INSERT INTO `activation_codes` (`id`, `code`) VALUES (1, '387UxMzB12');
```
3. Insert the data for the mensen
```sql
INSERT INTO `cafeteria_dittrichring` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/70/meals', 'Cafeteria Dittrichring');
INSERT INTO `mensa_academica` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/64/meals', 'Mensa Academica');
INSERT INTO `mensa_am_elsterbecken` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/65/meals', 'Mensa am Elsterbecken');
INSERT INTO `mensa_am_medizincampus` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/67/meals', 'Mensa am Medizincampus');
INSERT INTO `mensa_am_park` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/63/meals', 'Mensa am Park');
INSERT INTO `mensa_peterssteinweg` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/68/meals', 'Mensa Peterssteinweg');
INSERT INTO `mensa_schoenauer_str` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/69/meals', 'Mensa/Cafeteria Schönauer Straße');
INSERT INTO `mensa_tierklinik` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/66/meals', 'Mensa Tierklinik');
INSERT INTO `menseria_am_botanischen_garten` (`id`, `api_url`, `name`) VALUES (1, 'https://openmensa.org/api/v2/canteens/72/meals', 'Menseria am Botanischen Garten');
```
3. If you are also hosting the [EssensGetter-API](https://github.com/olech2412/EssensGetter-API) you have to setup an API Access
**Change the description, email, max_calls and the token to your use case**
```sql
INSERT INTO `api_access` (`id`, `calls`, `can_read`, `can_write`, `description`, `email`, `enabled`, `last_call`, `max_calls`, `token`) VALUES (1, 0, b'1', b'1', 'Your description', 'contact', b'1', '2023-02-07 10:55:34', 999999999999999, '8PLUv50emD7jBakyy9U4');
```
4. You are done! Now we can launch the program and start to fetch some data

## Launch the program
1. Navigate to the project directory in the command line
2. Run the `mvn clean install` command to install the dependencies.
3. In order to use the program with all its functionalities, you need a mail server that you can reach. If not take a look at step 4
  * 3.1 Edit the email server settings in `src/main/java/com/example/essensgetter_2_0/email/Mailer.java`
  * 3.2 Start the program with the command `mvn spring-boot:run`.
4. If you don't have a mail server and just want to collect data you can do that by running the following command:
```bash
mvn spring-boot:run dontSendEmail
```

## Deployment
1. Make sure you have correctly set all the accounts for the mail server and for the database in the corresponding files
2. Execute `mvn package`
3. Copy the files in your target directory and paste them on your server
4. Launch the program `java -jar EssensGetter_2_0-0.0.1-SNAPSHOT.jar`
5. Set up a cronjob that executes this code automatically
```cron
# Execute the default program at 9:05 am each Monday-Friday
# Execute th dontSendEmail program every 30 minutes to provide the latest data to the API

05 9 * * 1-5 java -jar /opt/essensGetter2.0/EssensGetter_2_0-0.0.1-SNAPSHOT.jar
*/30 * * * * java -jar /opt/essensGetter2.0/EssensGetter_2_0-0.0.1-SNAPSHOT.jar dontSendEmail > essensGetterNotSendingMail.log
```

## Grafana Dashboard
As a small side project I'm trying to pull interesting statistics from the collected data. For this I have set up the following Grafana dashboard, which sends various queries to the database, which on the one hand allows the software construct to be monitored well (API access, number of pending activations, etc.), but there are also opportunities to draw conclusions about the food offer.
![Screenshot 2023-01-18 145523](https://user-images.githubusercontent.com/76694468/213191027-28390bcf-e5a5-4ed3-b321-e19dc6508378.png)


## Notice

Please note that this is a private student project and the program is not available to the public. The operation of the program cannot be guaranteed and the program may not be used for commercial purposes.

I hope you still find the program useful.
