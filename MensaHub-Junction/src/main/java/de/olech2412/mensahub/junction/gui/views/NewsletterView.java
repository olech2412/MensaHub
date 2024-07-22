package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.*;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.jpa.services.mensen.MensaService;
import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.ActivationCode;
import de.olech2412.mensahub.models.authentification.DeactivationCode;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Role;
import jakarta.annotation.security.RolesAllowed;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@PageTitle("MensaHub")
@Route(value = "newsletter")
@RolesAllowed({Role.Names.ADMIN, Role.Names.SUPER_ADMIN, Role.Names.LOGIN_USER})
public class NewsletterView extends HorizontalLayout implements BeforeEnterObserver {

    private final String welcomeText = "Willkommen bei MensaHub";
    private final MensaService mensaService;
    Logger logger = LoggerFactory.getLogger(NewsletterView.class);
    private EmailField emailField;
    private TextField firstName;
    private TextField lastName;
    private Button registerButton;
    private Checkbox accept;
    @Autowired
    private ActivationCodeRepository activationCodeRepository;
    @Autowired
    private DeactivationCodeRepository deactivationCodeRepository;
    @Autowired
    private MailUserService mailUserService;
    private Checkbox wantUpdates;


    public NewsletterView(MensaService mensaService) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.mensaService = mensaService;

        VerticalLayout mainLayout = init();

        mainLayout.setWidth(100f, Unit.PERCENTAGE);
        mainLayout.setHeight(100f, Unit.PERCENTAGE);
        mainLayout.setAlignItems(Alignment.CENTER);

        SystemMessagesProvider systemMessagesProvider = systemMessagesInfo -> {
            CustomizedSystemMessages messages = new CustomizedSystemMessages();
            messages.setSessionExpiredNotificationEnabled(true);
            try {
                messages.setSessionExpiredURL(Config.getInstance().getProperty("mensaHub.junction.address") + "/login");
            } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException |
                     NoSuchAlgorithmException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
            return messages;
        };

        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(10 * 60);
        UI.getCurrent().getSession().getSession().setMaxInactiveInterval(10 * 60);
        VaadinService.getCurrent().setSystemMessagesProvider(systemMessagesProvider);

        add(mainLayout);
        add(mainLayout);
    }

    private VerticalLayout init() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        List<Mensa> mensas = mensaService.getAllMensas();

        MultiSelectComboBox<Mensa> multiSelectComboBox = new MultiSelectComboBox<>();
        multiSelectComboBox.setLabel("Wähle deine Mensen aus");
        multiSelectComboBox.setItems(mensas);
        multiSelectComboBox.setItemLabelGenerator(Mensa::getName);
        multiSelectComboBox.setPlaceholder("Wähle deine Mensen aus");

        firstName = new TextField("Dein Vorname");
        firstName.setMaxLength(255);
        firstName.setRequired(true);
        firstName.setRequiredIndicatorVisible(true);
        firstName.setPlaceholder("Max");

        lastName = new TextField("Dein Nachname");
        lastName.setRequired(true);
        lastName.setRequiredIndicatorVisible(true);
        lastName.setPlaceholder("Mustermann");
        lastName.setMaxLength(255);

        emailField = new EmailField("Deine E-Mail-Adresse");
        emailField.setRequiredIndicatorVisible(true);
        emailField.setClearButtonVisible(true);
        emailField.setErrorMessage("Bitte gib eine gültige E-Mail-Adresse ein!");
        emailField.setMaxLength(255);
        emailField.setPlaceholder("email@example.de");
        emailField.setWidth(100f, Unit.PERCENTAGE);

        registerButton = new Button("Registriere dich");
        registerButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        registerButton.setIcon(new Icon("vaadin", "envelope-open-o"));

        registerButton.addClickListener(e -> {
            try {
                validateInput(emailField.getValue(), firstName.getValue(), lastName.getValue(), multiSelectComboBox.getValue());
            } catch (InterruptedException | IOException ex) {
                throw new RuntimeException(ex);
            } catch (MessagingException ex) {
                logger.error("Error while sending email", ex);
                throw new RuntimeException(ex);
            }
        });
        registerButton.addClickShortcut(Key.ENTER);

        H1 header = new H1(welcomeText);
        StreamResource logoStream = new StreamResource("MensaHub_logo.png", () -> getClass().getResourceAsStream("/static/img/MensaHub_logo.PNG"));
        Image logoImage = new Image(logoStream, "Logo");
        HorizontalLayout image = new HorizontalLayout(logoImage);
        image.setWidth(100f, Unit.PERCENTAGE);
        image.setAlignItems(Alignment.CENTER);
        image.setJustifyContentMode(JustifyContentMode.CENTER);
        image.setSpacing(false);

        VerticalLayout mainLayout = new VerticalLayout(image, header); // the mainlayout is the layout of the whole page
        FormLayout formLayout = new FormLayout(firstName, lastName, emailField, multiSelectComboBox);
        VerticalLayout inputLayout = new VerticalLayout(formLayout, createDevInfoText(), createInfoText(),
                registerButton); // the inputlayout is the layout of the input fields

        inputLayout.setAlignItems(Alignment.CENTER);
        inputLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        if (isMobileDevice()) {
            inputLayout.setWidth(100f, Unit.PERCENTAGE);
        } else {
            inputLayout.setWidth(50f, Unit.PERCENTAGE);
        }

        mainLayout.add(inputLayout, createFooter());
        return mainLayout;
    }

    private Component createFooter() throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        Button privacy = new Button("Datenschutzerklärung");
        privacy.addClickListener(e -> {
            logger.info("Privacy button clicked");
            UI.getCurrent().navigate("datenschutzerklärung");
        });
        privacy.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
        privacy.setIcon(new Icon("vaadin", "bookmark-o"));


        VerticalLayout footer = new VerticalLayout(new HorizontalLayout(privacy), new H6("Made with ❤️ " +
                "by  christopho"), new Span(Config.getInstance().getProperty("mensaHub.junction.footer.cp")));
        footer.setAlignItems(Alignment.CENTER);
        footer.setAlignSelf(Alignment.CENTER);
        footer.setJustifyContentMode(JustifyContentMode.CENTER);
        footer.setSizeFull();
        return footer;
    }

    private VerticalLayout createInfoText() {
        Span infoText = new Span("Durch Anklicken des Buttons \"Registriere dich\" erklärst du dich damit einverstanden, " +
                "dass dir täglich der Speiseplan der ausgewählten Mensa/Mensen per E-Mail zuschickt. Du kannst dich " +
                "jederzeit von diesem Newsletter abmelden.");
        infoText.getStyle().set("text-align", "center");

        accept = new Checkbox("Ich stimme zu, dass meine personenbezogenen Daten - wie in der " +
                "Datenschutzerklärung beschrieben - zur Zusendung der E-Mail verarbeitet werden. Diese Zustimmung " +
                "kann ich jederzeit mit Wirkung für die Zukunft widerrufen."); // TODO: add link to privacy policy
        accept.setRequiredIndicatorVisible(true);
        accept.getStyle().set("text-align", "center");

        wantUpdates = new Checkbox("Möchtest du benachrichtigt werden, wenn Änderungen am Speiseplan festgestellt werden?");
        wantUpdates.setTooltipText("Bei jeder Änderung senden wird dir eine E-Mail zu. Wenn du diese Updates nicht " +
                "empfangen möchtest, kannst du diese Einstellung jederzeit in den Newsletter-Einstellungen widerrufen.");
        wantUpdates.setRequiredIndicatorVisible(true);
        wantUpdates.setValue(true);

        VerticalLayout infoLayout = new VerticalLayout(new H3("Informationen zu deiner Registrierung: "), infoText, wantUpdates, accept);
        infoLayout.setAlignItems(Alignment.CENTER);

        return infoLayout;
    }

    private VerticalLayout createDevInfoText() {
        Span infoText = new Span("Du bist ein Entwickler und suchst nach einem Weg die EssensGetter API in dein System zu " +
                "integrieren? Klicke hier um dir deinen persönlichen Zugang zur API zu erstellen.");
        infoText.getStyle().set("text-align", "center");

        Button redirectToDevView = new Button("Zur API Anmeldung");
        redirectToDevView.addClickListener(buttonClickEvent -> {
            UI.getCurrent().navigate(DeveloperRegisterView.class);
        });
        redirectToDevView.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        redirectToDevView.getStyle().set("text-align", "center");
        redirectToDevView.setIcon(new Icon(VaadinIcon.CLOUD_O));

        VerticalLayout infoLayout = new VerticalLayout(infoText, redirectToDevView);
        infoLayout.setAlignItems(Alignment.CENTER);

        return infoLayout;
    }

    /**
     * Validates the input of the user
     *
     * @param email the email of the user
     */
    private void validateInput(String email, String firstname, String lastname, Set<Mensa> mensa) throws InterruptedException, MessagingException, IOException {
        Pattern specialEmail = Pattern.compile("[!#$%&*()=|<>?{}\\[\\]~]"); // regex for special characters
        Pattern special = Pattern.compile("[!#$%&*@()=|<>?{}\\[\\]~]");
        Matcher hasSpecial = specialEmail.matcher(email); // checks if the email contains special characters
        if (!email.isEmpty() && !hasSpecial.find() && !emailField.isInvalid()) { // if the email is not empty and does not contain special characters and is valid
            if (email.length() <= 254) {
                if (firstName.getValue().length() <= 255 && lastName.getValue().length() <= 255
                        && !special.matcher(firstname).find() && !special.matcher(lastname).find()
                        && !firstName.isEmpty() && !lastName.isEmpty()) { // same step for lastname and firstname
                    if (mailUserService.findMailUserByEmail(email).isEmpty()) {
                        if (accept.getValue()) {
                            try {
                                createRegistratedUser(email, firstname, lastname, mensa, wantUpdates.getValue());
                                Notification notification = new Notification("Deine E-Mail-Adresse wurde erfolgreich registriert!. Klicke auf den Link in deiner Bestätigungsmail", 6000);
                                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                notification.open();
                            } catch (Exception e) {
                                logger.error("Error while creating Account: ", e);
                                logger.error("User input: {} {} {}", email, firstname, lastname);
                                throw new RuntimeException(e);
                            }
                        } else {
                            Notification notification = new Notification("Bitte stimme der Datenschutzerklärung zu", 3000, Notification.Position.BOTTOM_START);
                            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                            notification.open();
                        }
                    } else {
                        Notification notification = new Notification("Hmm... diese E-Mail scheint bereits registriert zu sein", 3000);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.open();
                        emailField.clear();
                        emailField.setInvalid(true);
                        logger.warn("Email is already registered: " + email);
                    }
                } else {
                    Notification notification = new Notification("Überprüfe die Eingabe des Vor- oder Nachnamen", 3000, Notification.Position.BOTTOM_START);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.open();
                    if (firstName.getValue().length() > 255 || special.matcher(firstname).find() || firstname.isEmpty()) {
                        firstName.setInvalid(true);
                    } else {
                        lastName.setInvalid(true);
                    }
                }
            } else {
                Notification notification = new Notification("Deine E-Mail-Adresse ist zu lang!", 3000, Notification.Position.BOTTOM_START);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();
            }
        } else {
            Notification notification = new Notification("Bitte gib eine gültige E-Mail-Adresse ein!", 3000, Notification.Position.BOTTOM_START);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
    }

    /**
     * Save User in Database
     * User is not enabled because he didn't verify the email
     *
     * @param email
     */
    private void createRegistratedUser(String email, String firstname, String lastname, Set<Mensa> mensa, boolean wantUpdates) {

        try {
            ActivationCode activationCode = new ActivationCode(RandomStringUtils.randomAlphanumeric(32));
            DeactivationCode deactivationCode = new DeactivationCode(RandomStringUtils.randomAlphanumeric(32));
            logger.info("Saved new User: {} {} {}", email, firstname, lastname);

            Mailer mailer = new Mailer();
            try {
                mailer.sendActivationEmail(firstname, email,
                        activationCode.getCode(), deactivationCode.getCode());
                logger.info("Mail was sent successfully");
            } catch (Exception exception) {
                logger.error("Error while sending mail: ", exception);
                Notification notification = new Notification("Es ist ein Fehler beim versenden der Mail aufgetreten. Bitte wende dich an den Administrator", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
                notification.open();
            }

            activationCodeRepository.save(activationCode);
            deactivationCodeRepository.save(deactivationCode);

            MailUser mailUser = new MailUser();
            mailUser.setEmail(email);
            mailUser.setFirstname(firstname);
            mailUser.setLastname(lastname);
            mailUser.setEnabled(false);
            mailUser.setActivationCode(activationCode);
            mailUser.setDeactivationCode(deactivationCode);
            mailUser.setWantsUpdate(wantUpdates);

            mailUser.setMensas(mensa);

            mailUserService.saveMailUser(mailUser); // save the user in the database, not enabled because he didn't verify the email
        } catch (Exception e) {
            logger.trace("Error while saving user: ", e);
            logger.trace("User input: {} {} {}", email, firstname, lastname);
            Notification notification = new Notification("Es ist ein Fehler aufgetreten. Bitte versuche es später erneut", 3000);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return;
        }

        Notification notification = new Notification("Alles erledigt :). Du kannst die Seite nun verlassen", 6000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();
    }

    /**
     * log access on the website
     * IP and information about the webbrowser is logged
     *
     * @param beforeEnterEvent
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        logger.info("User accessed the main page: {} IP: {}", UI.getCurrent().getSession().getBrowser().getBrowserApplication(), UI.getCurrent().getSession().getBrowser().getAddress());
    }

    /**
     * Check if client is mobile or desktop
     */
    public boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }
}
