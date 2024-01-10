package de.olech2412.mensahub.gateway;

import de.olech2412.mensahub.gateway.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
@EntityScan(basePackages = {"de.olech2412.mensahub.models.authentification", "de.olech2412.mensahub.models.Leipzig"})
@Slf4j
public class MensaHub_Gateway {

    /**
     * Starts the application with the given arguments
     *
     * @param args The arguments
     */
    public static void main(String[] args) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        configureEnvironment();
        SpringApplication.run(MensaHub_Gateway.class, args);
    }

    /**
     * Configures the environment of the application
     *
     * @throws IOException If the configuration file could not be read
     */
    private static void configureEnvironment() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptionKey = System.getenv("encryption.key");
        if (encryptionKey == null) {
            log.error("No encryption key provided. Exiting...");
            log.error("Please provide the encryption key as environment variable 'encryption.key' by using the -D flag.");
            log.error("if you are using docker, please use the --env flag.");
            System.exit(1);
        }

        // configure settings for the server
        System.setProperty("server.port", Config.getInstance().getProperty("mensaHub.api.port"));
        System.setProperty("server.servlet.context-path", Config.getInstance().getProperty("mensaHub.api.contextPath"));

        // configure settings for the database
        System.setProperty("spring.datasource.url", "jdbc:mariadb://" + Config.getInstance().getProperty("mensaHub.database.location") + "/" + Config.getInstance().getProperty("mensaHub.database.name"));
        System.setProperty("spring.datasource.username", Config.getInstance().getProperty("mensaHub.database.username"));
        System.setProperty("spring.datasource.password", Config.getInstance().getProperty("mensaHub.database.password"));

        // configure settings for the logging
        System.setProperty("logging.file.name", Config.getInstance().getProperty("mensaHub.api.log.location") + "/mensaHubAPI.log");
        System.setProperty("logging.level.root", Config.getInstance().getProperty("mensaHub.api.log.level"));

        // configure settings for the management endpoint
        System.setProperty("management.endpoint.info.enabled", Config.getInstance().getProperty("mensaHub.api.management.info.enabled"));
        System.setProperty("management.endpoints.web.exposure.include", Config.getInstance().getProperty("mensaHub.api.management.endpoints.web.exposure.include"));

        // configure settings for the security
        System.setProperty("security.jwt.secret-key", Config.getInstance().getProperty("mensaHub.api.security.jwt.secret-key"));
        System.setProperty("security.jwt.expiration-time", Config.getInstance().getProperty("mensaHub.api.security.jwt.expiration-time"));
    }

}
