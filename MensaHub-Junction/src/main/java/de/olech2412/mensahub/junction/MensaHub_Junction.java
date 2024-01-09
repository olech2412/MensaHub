package de.olech2412.mensahub.junction;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import de.olech2412.mensahub.junction.config.Config;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.io.IOException;

/**
 * The entry point of the Spring Boot application.
 * <p>
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 */
@SpringBootApplication
@Theme("mensaHub-theme")
@NpmPackage(value = "line-awesome", version = "1.3.0")
@PWA(name = "MensaHub-Junction", shortName = "MensaHub")
@EntityScan(basePackages = {"de.olech2412.mensahub.models.authentification", "de.olech2412.mensahub.models.Leipzig"})
public class MensaHub_Junction implements AppShellConfigurator {

    public static void main(String[] args) throws IOException {
        configureEnvironment();
        SpringApplication.run(MensaHub_Junction.class, args);
    }

    private static void configureEnvironment() throws IOException {
        // configure settings for the server
        System.setProperty("server.port", Config.getInstance().getProperty("mensaHub.junction.port"));
        System.setProperty("server.servlet.context-path", Config.getInstance().getProperty("mensaHub.junction.contextPath"));

        // configure settings for the logging
        System.setProperty("logging.file.name", Config.getInstance().getProperty("mensaHub.junction.log.location") + "/mensaHubJunction.log");
        System.setProperty("logging.level.root", Config.getInstance().getProperty("mensaHub.junction.log.level"));

        // configure settings for the database
        System.setProperty("spring.datasource.url", "jdbc:mariadb://" + Config.getInstance().getProperty("mensaHub.database.location") + "/" + Config.getInstance().getProperty("mensaHub.database.name"));
        System.setProperty("spring.datasource.username", Config.getInstance().getProperty("mensaHub.database.username"));
        System.setProperty("spring.datasource.password", Config.getInstance().getProperty("mensaHub.database.password"));

        // configure mail settings
        System.setProperty("adminMail", Config.getInstance().getProperty("mensaHub.junction.mail.adminMail"));
    }
}
