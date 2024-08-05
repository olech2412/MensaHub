package de.olech2412.mensahub.datadispatcher;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher.LeipzigDataDispatcher;
import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.meals.MealsRepository;
import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.MensasRepository;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@SpringBootApplication
@Log4j2
@EnableScheduling
@EnableAsync
@EntityScan(basePackages = {"de.olech2412.mensahub.models.authentification", "de.olech2412.mensahub.models"})
public class MensaHub_DataDispatcher {

    static Mensa mensa;

    public static void main(String[] args) throws Exception {
        configureEnvironment();
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(MensaHub_DataDispatcher.class, args);
        LeipzigDataDispatcher leipzigDataDispatcher = configurableApplicationContext.getBean(LeipzigDataDispatcher.class);

        if (Arrays.stream(args).toList().contains("sendMailManual")) {
            log.info("Sending emails manually");
            leipzigDataDispatcher.sendEmails();
        }
    }

    private static void configureEnvironment() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String encryptionKey = System.getenv("encryption.key");
        if (encryptionKey == null) {
            log.error("No encryption key provided. Exiting...");
            log.error("Please provide the encryption key as environment variable 'encryption.key' by using the -D flag.");
            log.error("if you are using docker, please use the --env flag.");
            System.exit(1);
        }

        // configure settings for the database read from the config file
        System.setProperty("spring.datasource.url", "jdbc:mariadb://" + Config.getInstance().getProperty("mensaHub.database.location") + "/" + Config.getInstance().getProperty("mensaHub.database.name"));
        System.setProperty("spring.datasource.username", Config.getInstance().getProperty("mensaHub.database.username"));
        System.setProperty("spring.datasource.password", Config.getInstance().getProperty("mensaHub.database.password"));

        // configure settings for the logging
        System.setProperty("logging.file.name", Config.getInstance().getProperty("mensaHub.dataDispatcher.log.location") + "/mensaHubDataDispatcher.log");
        System.setProperty("logging.level.root", Config.getInstance().getProperty("mensaHub.dataDispatcher.log.level"));
    }
}