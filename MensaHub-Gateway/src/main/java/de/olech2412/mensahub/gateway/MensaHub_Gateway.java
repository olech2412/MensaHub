package de.olech2412.mensahub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"de.olech2412.mensahub.models.authentification", "de.olech2412.mensahub.models.Leipzig"})
public class MensaHub_Gateway {

    /**
     * Starts the application with the given arguments
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MensaHub_Gateway.class, args);
    }

}
