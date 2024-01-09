package de.olech2412.mensahub.junction.views.main;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import de.olech2412.mensahub.junction.JPA.repository.API_UserRepository;
import de.olech2412.mensahub.junction.JPA.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.JPA.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.models.authentification.API_User;
import de.olech2412.mensahub.models.authentification.ActivationCode;
import de.olech2412.mensahub.models.authentification.DeactivationCode;
import jakarta.annotation.security.PermitAll;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Route("registerDev")
@PageTitle("MensaHub-Dev")
@PermitAll
public class DeveloperRegisterView extends Composite implements BeforeEnterObserver {
    Logger logger = LoggerFactory.getLogger(MailSettingsView.class);
    @Autowired
    ActivationCodeRepository activationCodeRepository;
    @Autowired
    DeactivationCodeRepository deactivationCodeRepository;
    @Autowired
    API_UserRepository apiUserRepository;
    private VerticalLayout layout;
    private EmailField emailField;
    private TextField apiUsername;
    private PasswordField passwordField;
    private Button registerButton;
    private TextArea description;
    private Checkbox accept;

    public DeveloperRegisterView() {

    }

    private VerticalLayout init() throws IOException {
        apiUsername = new TextField("Dein API Username");
        apiUsername.setMaxLength(100);
        apiUsername.setMinLength(2);
        apiUsername.setErrorMessage("Bitte gib mindestens 2 und höchstens 100 Zeichen ein");
        apiUsername.setRequired(true);
        apiUsername.setRequiredIndicatorVisible(true);
        apiUsername.setPlaceholder("apiuser_Max_App");

        passwordField = new PasswordField("Dein API Passwort");
        passwordField.setRequired(true);
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setMaxLength(255);
        passwordField.setMinLength(8);
        passwordField.setErrorMessage("Dein Passwort sollte mindestens 8 und höchstens 255 Zeichen haben");

        emailField = new EmailField("Deine E-Mail-Adresse wo wir dich erreichen können");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setClearButtonVisible(true);
        emailField.setErrorMessage("Bitte gib eine gültige E-Mail-Adresse ein!");
        emailField.setMaxLength(255);
        emailField.setPlaceholder("email@example.de");
        emailField.setWidth(100f, Unit.PERCENTAGE);

        description = new TextArea("Erkläre kurz, wozu du einen Zugang benötigst");
        description.setMaxLength(255);
        description.setMinLength(10);
        description.setErrorMessage("Bitte gib mindestens 10 und höchstens 255 Zeichen ein");
        description.setRequired(true);
        description.setRequiredIndicatorVisible(true);
        description.setClearButtonVisible(true);
        description.setPlaceholder("Ich möchte eine App entwickeln um der Welt Speiseplan Daten bereitzustellen.");
        description.setWidth(100f, Unit.PERCENTAGE);

        registerButton = new Button("Registriere dich");

        registerButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        registerButton.setIcon(new Icon("vaadin", "envelope-open-o"));

        registerButton.addClickListener(e -> {
            try {
                if (accept.getValue()) {
                    registerUser();
                } else {
                    Notification notification = new Notification("Bitte Nutzungsbedingungen bestätigen", 6000);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.open();
                }
            } catch (IOException | InterruptedException | MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
        registerButton.addClickShortcut(Key.ENTER);

        H1 header = new H1("Erstelle deinen persönlichen API Zugang");
        StreamResource logoStream = new StreamResource("mensaHub_logo.png", () -> getClass().getResourceAsStream("/static/img/MensaHub_logo.PNG"));
        Image logoImage = new Image(logoStream, "Logo");
        HorizontalLayout image = new HorizontalLayout(logoImage);
        image.setWidth(100f, Unit.PERCENTAGE);
        image.setAlignItems(FlexComponent.Alignment.START);
        image.setSpacing(false);

        VerticalLayout mainLayout = new VerticalLayout(image, header); // the mainlayout is the layout of the whole page
        FormLayout formLayout = new FormLayout(apiUsername, passwordField, emailField, description);
        VerticalLayout inputLayout = new VerticalLayout(formLayout, createInfoText(),
                registerButton); // the inputlayout is the layout of the input fields

        inputLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        inputLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        if (isMobileDevice()) {
            inputLayout.setWidth(100f, Unit.PERCENTAGE);
        } else {
            inputLayout.setWidth(50f, Unit.PERCENTAGE);
        }


        mainLayout.add(inputLayout, createFooter());
        return mainLayout;
    }

    private void registerUser() throws IOException, InterruptedException, MessagingException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(20))
                .uri(URI.create(Config.getInstance().getProperty("mensaHub.junction.gateway.address") + "/mensaHub/auth/register"))
                .POST(HttpRequest.BodyPublishers.ofString("{\n" +
                        "      \"apiUsername\": \"" + apiUsername.getValue() + "\",\n" +
                        "      \"password\": \"" + passwordField.getValue() + "\",\n" +
                        "      \"description\": \"" + description.getValue() + "\",\n" +
                        "      \"email\": \"" + emailField.getValue() + "\",\n" +
                        "      \"enabledByAdmin\":" + "false" + ",\n" +
                        "      \"verified_email\":" + "false" + "\n" +
                        "    }"))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> futureResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(futureResponse);
        System.out.println(futureResponse.statusCode());
        if (futureResponse.statusCode() != 200) {
            if (futureResponse.statusCode() == 400) {
                Notification notification = new Notification("Ungültige Eingaben. Bitte prüfen!", 6000);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
                logger.error("Error occurred while register an user for the api: " + request);
            }
        } else {
            sendRegistrationMail(emailField.getValue(), apiUsername.getValue());
        }
    }

    private void sendRegistrationMail(String email, String username) throws MessagingException, IOException {
        ActivationCode activationCode = new ActivationCode(RandomStringUtils.randomAlphanumeric(32));
        DeactivationCode deactivationCode = new DeactivationCode(RandomStringUtils.randomAlphanumeric(32));
        activationCodeRepository.save(activationCode);
        deactivationCodeRepository.save(deactivationCode);
        API_User apiUser = apiUserRepository.findAPI_UserByApiUsername(username).get();
        apiUser.setActivationCode(activationCode);
        apiUser.setDeactivationCode(deactivationCode);
        apiUserRepository.save(apiUser);

        Mailer mailer = new Mailer();
        mailer.sendAPIActivationEmail(username, email,
                activationCode.getCode(), deactivationCode.getCode());
        logger.info("Mail was sent successfully");
        Notification notification = new Notification("Account gespeichert. Bitte prüfe dein Postfach und bestätige deine E-Mail Adresse", 6000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    private VerticalLayout createInfoText() {
        Span infoText = new Span("Erstelle deinen persönlichen API Zugang. Bitte fülle alle Felder aus und erkläre kurz," +
                " warum du einen Zugang brauchst. Anschließend musst du nur noch deine E-Mail bestätigen und die Freischaltung" +
                " eines Administrators abwarten.");
        infoText.getStyle().set("text-align", "center");

        accept = new Checkbox("Ich stimme zu, dass meine personenbezogenen Daten - wie in der " +
                "Datenschutzerklärung beschrieben - zur Zusendung der E-Mail verarbeitet werden. Diese Zustimmung " +
                "kann ich jederzeit mit Wirkung für die Zukunft widerrufen.");
        accept.setRequiredIndicatorVisible(true);
        accept.getStyle().set("text-align", "center");

        VerticalLayout infoLayout = new VerticalLayout(new H3("Informationen zu deiner Registrierung: "), infoText, accept);
        infoLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        return infoLayout;
    }

    @Override
    protected Component initContent() {
        VerticalLayout mainLayout = null;
        try {
            mainLayout = init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mainLayout.setWidth(100f, Unit.PERCENTAGE);
        mainLayout.setHeight(100f, Unit.PERCENTAGE);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout = new VerticalLayout(mainLayout);
        return layout;
    }

    private Component createFooter() throws IOException {
        Button privacy = new Button("Datenschutzerklärung");
        privacy.addClickListener(e -> {
            logger.info("Privacy button clicked");
            UI.getCurrent().navigate("datenschutzerklärung");
        });
        privacy.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        privacy.setIcon(new Icon("vaadin", "bookmark-o"));


        VerticalLayout footer = new VerticalLayout(new HorizontalLayout(privacy), new H6("Made with ❤️ " +
                "by  christopho"), new Span(Config.getInstance().getProperty("mensaHub.junction.footer.cp")));
        footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setAlignSelf(FlexComponent.Alignment.CENTER);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footer.setSizeFull();
        return footer;
    }

    /**
     * Check if client is mobile or desktop
     */
    public boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }
}