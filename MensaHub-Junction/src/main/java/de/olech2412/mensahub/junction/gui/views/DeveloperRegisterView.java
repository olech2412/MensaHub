package de.olech2412.mensahub.junction.gui.views;

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
import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.UsersRepository;
import de.olech2412.mensahub.models.authentification.*;
import jakarta.annotation.security.RolesAllowed;
import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

@Route("registerDev")
@PageTitle("MensaHub-Dev")
@RolesAllowed(value = {Role.Names.ROLE_ADMIN, Role.Names.ROLE_API_USER, Role.Names.ROLE_LOGIN_USER, Role.Names.ROLE_SUPER_ADMIN})
@Log4j2
public class DeveloperRegisterView extends Composite implements BeforeEnterObserver {
    @Autowired
    ActivationCodeRepository activationCodeRepository;
    @Autowired
    DeactivationCodeRepository deactivationCodeRepository;
    @Autowired
    API_UserRepository apiUserRepository;
    @Autowired
    UsersRepository usersRepository;

    private VerticalLayout layout;
    private EmailField emailField;
    private TextField apiUsername;
    private PasswordField passwordField;
    private Button registerButton;
    private TextArea description;
    private Checkbox accept;

    public DeveloperRegisterView() {

    }

    /**
     * Initialize the view
     *
     * @return VerticalLayout with all components
     * @throws IOException               if the mail could not be sent
     * @throws NoSuchPaddingException    if the padding is not available
     * @throws IllegalBlockSizeException if the block size is invalid
     * @throws NoSuchAlgorithmException  if the algorithm is not available
     * @throws BadPaddingException       if the padding is invalid
     * @throws InvalidKeyException       if the key is invalid
     */
    private VerticalLayout init() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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

                    API_User apiUser = new API_User();
                    apiUser.setApiUsername(apiUsername.getValue());
                    apiUser.setPassword(new BCryptPasswordEncoder().encode(passwordField.getValue()));
                    apiUser.setEmail(emailField.getValue());
                    apiUser.setDescription(description.getValue());
                    apiUser.setVerified_email(false);
                    apiUser.setEnabledByAdmin(false);

                    Users user = usersRepository.findByUsername(apiUsername.getValue());

                    if (user != null) {
                        NotificationFactory.create(NotificationType.ERROR, "Nutzerkennung bereits belegt").open();
                        return;
                    }

                    if (validate(apiUser)) {
                        registerUser(apiUser);
                    } else {
                        Notification notification = new Notification("Ungültige Eingaben. Bitte korrigiere", 6000);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.open();
                    }
                } else {
                    Notification notification = new Notification("Bitte Nutzungsbedingungen bestätigen", 6000);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.open();
                }
            } catch (IOException | InterruptedException | MessagingException | NoSuchPaddingException |
                     IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                     InvalidKeyException ex) {
                throw new RuntimeException(ex);
            }
        });
        registerButton.addClickShortcut(Key.ENTER);

        H1 header = new H1("Erstelle deinen persönlichen API Zugang");
        StreamResource logoStream = new StreamResource("mensaHub_logo.webp", () -> getClass().getResourceAsStream("/static/img/MensaHub_logo.webp"));
        Image logoImage = new Image(logoStream, "Logo");
        HorizontalLayout image = new HorizontalLayout(logoImage);
        image.setWidth(100f, Unit.PERCENTAGE);
        image.setAlignItems(FlexComponent.Alignment.CENTER);
        image.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
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

    private void registerUser(API_User apiUser) throws IOException, InterruptedException, MessagingException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {

        if (apiUserRepository.findAPI_UserByApiUsername(apiUser.getApiUsername()).isPresent()) {
            Notification notification = new Notification("API Username bereits vergeben", 6000);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }
        sendRegistrationMail(apiUser);
    }

    private void sendRegistrationMail(API_User apiUser) {
        ActivationCode activationCode = new ActivationCode(RandomStringUtils.randomAlphanumeric(32));
        DeactivationCode deactivationCode = new DeactivationCode(RandomStringUtils.randomAlphanumeric(32));
        activationCodeRepository.save(activationCode);
        deactivationCodeRepository.save(deactivationCode);

        log.info(String.format("Generated activationcode: %s and deactivationcode: %s", activationCode.getCode(), deactivationCode.getCode()));

        apiUser.setActivationCode(activationCode);
        apiUser.setDeactivationCode(deactivationCode);
        apiUserRepository.save(apiUser);

        Notification notification = new Notification("Account gespeichert. Bitte prüfe dein Postfach und bestätige deine E-Mail Adresse", 6000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();

        try {
            Mailer mailer = new Mailer();
            mailer.sendAPIActivationEmail(apiUser.getApiUsername(), apiUser.getEmail(),
                    activationCode.getCode(), deactivationCode.getCode());
            log.info("Mail was sent successfully");
        } catch (Exception exception) {
            log.error("Error while sending activation mail to user: {}", exception.getMessage());
            Notification mailErrorNotification = new Notification("E-Mail konnte nicht versendet werden. Wende dich bitte an den Administrator", 6000);
            mailErrorNotification.addThemeVariants(NotificationVariant.LUMO_WARNING);
            mailErrorNotification.open();
        }
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
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        mainLayout.setWidth(100f, Unit.PERCENTAGE);
        mainLayout.setHeight(100f, Unit.PERCENTAGE);
        mainLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout = new VerticalLayout(mainLayout);
        return layout;
    }

    private Component createFooter() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Button privacy = new Button("Datenschutzerklärung");
        privacy.addClickListener(e -> {
            log.info("Privacy button clicked");
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

    private boolean validate(API_User user) {
        // Erzeuge einen Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Überprüfe die Validierung für den übergebenen Benutzer
        Set<ConstraintViolation<API_User>> violations = validator.validate(user);

        // Überprüfe, ob es Verletzungen gibt
        if (!violations.isEmpty()) {
            for (ConstraintViolation<API_User> violation : violations) {
                log.info("Violation detected: " + violation.getMessage());
            }
            return false; // Validierung fehlgeschlagen
        }

        return true;
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