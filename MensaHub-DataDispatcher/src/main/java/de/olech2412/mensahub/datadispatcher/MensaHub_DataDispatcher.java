package de.olech2412.mensahub.datadispatcher;

import de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher.LeipzigDataDispatcher;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
@Log4j2
@EnableScheduling
@EnableAsync
@EntityScan(basePackages = {"de.olech2412.mensahub.models.authentification", "de.olech2412.mensahub.models.Leipzig"})
public class MensaHub_DataDispatcher {

    @Autowired
    LeipzigDataDispatcher leipzigDataDispatcher;

    public static void main(String[] args) throws MessagingException, IOException {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(MensaHub_DataDispatcher.class, args);
        LeipzigDataDispatcher leipzigDataDispatcher = configurableApplicationContext.getBean(LeipzigDataDispatcher.class);

        if (Arrays.stream(args).toList().contains("sendMailManual")) {
            log.info("Sending emails manually");
            leipzigDataDispatcher.sendEmails();
        }

        leipzigDataDispatcher.callData();
        leipzigDataDispatcher.fetchAllergenes();
    }
}