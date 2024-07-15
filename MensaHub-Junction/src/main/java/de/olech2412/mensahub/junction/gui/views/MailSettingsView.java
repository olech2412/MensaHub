package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.JPA.repository.API_UserRepository;
import de.olech2412.mensahub.junction.JPA.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.JPA.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.JPA.repository.MailUserRepository;
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.junction.gui.components.vaadin.GermanDatePicker;
import de.olech2412.mensahub.models.authentification.API_User;
import de.olech2412.mensahub.models.authentification.MailUser;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route("deactivate")
@PageTitle("Verwaltung deiner E-Mail-Einstellungen")
@AnonymousAllowed
public class MailSettingsView extends Composite implements BeforeEnterObserver {
    private final DeactivationCodeRepository deactivationCodeRepository;
    private final MailUserRepository mailUserRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final VerticalLayout content = new VerticalLayout();
    @Autowired
    API_UserRepository apiUserRepository;
    Logger logger = LoggerFactory.getLogger(MailSettingsView.class);
    private VerticalLayout layout;

    public MailSettingsView(DeactivationCodeRepository deactivationCodeRepository, MailUserRepository mailUserRepository, ActivationCodeRepository activationCodeRepository) {
        this.deactivationCodeRepository = deactivationCodeRepository;
        this.mailUserRepository = mailUserRepository;
        this.activationCodeRepository = activationCodeRepository;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setAlignSelf(FlexComponent.Alignment.CENTER);
        content.setSizeFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setAlignSelf(FlexComponent.Alignment.CENTER);

        StreamResource logoStream = new StreamResource("MensaHub_logo.png", () -> getClass().getResourceAsStream("/static/img/MensaHub_logo.PNG"));
        Image logoImage = new Image(logoStream, "Logo");
        HorizontalLayout image = new HorizontalLayout(logoImage);
        image.setWidth(100f, Unit.PERCENTAGE);
        image.setAlignItems(FlexComponent.Alignment.CENTER);
        image.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        image.setSpacing(false);

        layout.add(image);

        try {
            Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();

            String code = params.get("code").get(0);

            if (deactivationCodeRepository.findByCode(code).isEmpty()) {
                content.add(new Text("Hast du dich gerade abgemeldet? Wenn ja, dann ist alles in Ordnung! "));
                content.add(new Text("Wenn nicht, ist etwas schief gelaufen. Bitte kontaktiere im Zweifelsfall den Administrator!"));
                layout.add(content);
            } else {
                initDeactivationView(code);
            }
        } catch (NullPointerException nullPointerException) {
            logger.warn("User tried to navigate to DeactivationView but there is no code");
            UI.getCurrent().navigate("login");
        } catch (MessagingException e) {
            logger.error("Error while sending deactivation email");
            throw new RuntimeException(e);
        }
    }

    private void initDeactivationView(String code) throws MessagingException {
        H3 headlineDelete = new H3("Du möchtest keine weiteren Emails von uns oder deine Einstellungen bearbeiten? Hier sind deine Optionen...");
        Text explanationDelete = new Text("Der klick auf \"Vollständig Deaktivieren\" hat eine sofortige Löschung deiner Daten zur Folge." +
                " Durch klick auf \"Zeitweise Deaktivieren\" kannst du deinen Account für gewisse Zeit deaktivieren und anschließend weiter nutzen");
        Button deactivate = new Button("Vollständig Deaktivieren");
        deactivate.setIcon(VaadinIcon.TRASH.create());
        deactivate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        deactivate.addThemeVariants(ButtonVariant.LUMO_ERROR);
        Button deactivateForTime = new Button("Zeitweise Deaktivieren");
        deactivateForTime.setIcon(VaadinIcon.CLOCK.create());
        deactivateForTime.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        FormLayout formLayout = new FormLayout(headlineDelete, explanationDelete, deactivate, deactivateForTime);

        content.add(formLayout);
        layout.add(content);

        MailUser mailUser = mailUserRepository.findByDeactivationCode_Code(code);

        if (!mailUser.isEnabled() && mailUser.getDeactviatedUntil() != null) {
            formLayout.remove(deactivateForTime);
            Button reactivate = new Button("Account reaktivieren");
            reactivate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            reactivate.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            reactivate.setIcon(VaadinIcon.REFRESH.create());
            formLayout.add(reactivate);

            reactivate.addClickListener(buttonClickEvent -> {
                mailUser.setEnabled(true);
                mailUser.setDeactviatedUntil(null);
                mailUserRepository.save(mailUser);
                Notification notification = new Notification("Du hast deinen Account erfolgreich wieder freigeschaltet.", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.open();
            });
        }

        if (mailUser == null) {
            logger.info("User tried to deactivate account but there is no user with the code: {}", code);
            // check if it's an api user
            Optional<API_User> apiUser = apiUserRepository.findAPI_UserByDeactivationCodeCode(code);
            if (apiUser.isPresent()) {
                logger.info("User is an API User");
                apiUserRepository.delete(apiUser.get());
                deactivationCodeRepository.delete(apiUser.get().getDeactivationCode());
                logger.info("Deleted apiUser {}", apiUser.get().getApiUsername());
                // were done print message and exit
                Notification notification = new Notification("Du hast deinen Account und alle zugehörigen Daten erfolgreich gelöscht!", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.open();

                // remove all elements from UI
                content.removeAll();

                return;
            }
        }

        deactivateForTime.addClickListener(buttonClickEvent -> {
            addTimeChoosePanel(mailUser);
        });

        deactivate.addClickListener(buttonClickEvent -> {
            try {
                deleteAccount(mailUser);
            } catch (MessagingException | IOException | NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void addTimeChoosePanel(MailUser mailUser) {
        content.removeAll();
        Text explanation = new Text("Alles klar. Du kannst entweder deinen Account automatisch in 3 Monaten wieder aktivieren" +
                " oder du wählst selber den Zeitpunkt. Die Entscheidung kann jederzeit rückgängig gemacht werden.");
        Span deleteInfo = new Span("Eine Löschung ist trotzdem jederzeit möglich!");
        deleteInfo.getElement().getThemeList().add("badge");

        GermanDatePicker datePicker = new GermanDatePicker();
        datePicker.setLabel("Wähle den Zeitpunkt der Reaktivierung");
        if (mailUser.getDeactviatedUntil() != null) {
            datePicker.setValue(mailUser.getDeactviatedUntil());
        }
        datePicker.setMin(LocalDate.now().plusDays(1));
        datePicker.setPlaceholder("Ich möchte wieder aktiviert werden am...");
        Button threeMonths = new Button("Ich bin in 3 Monaten wieder da...");
        threeMonths.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        threeMonths.addClickListener(buttonClickEvent -> {
            datePicker.setValue(LocalDate.now().plusMonths(3));
        });

        Button save = new Button("Speichern");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        save.addClickListener(buttonClickEvent -> {
            if (datePicker.isInvalid()) {
                Notification notification = new Notification("Bitte gib ein gültiges Datum ein", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.open();
                return;
            }

            if (datePicker.getValue() != null) {
                mailUser.setEnabled(false);
                mailUser.setDeactviatedUntil(datePicker.getValue());
                mailUserRepository.save(mailUser);

                Notification notification = new Notification("Du wurdest erfolgreich temporär deaktiviert. Alle wichtige Informationen senden wir dir per Mail zu.", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.open();
                Mailer mailer = null;
                try {
                    mailer = new Mailer();
                } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException |
                         NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                try {
                    mailer.sendTemporaryDeactivationEmail(mailUser.getFirstname(), mailUser.getEmail(), mailUser.getDeactivationCode().getCode(), mailUser.getDeactviatedUntil());
                } catch (Exception exception) {
                    logger.error("Error while sending deactivation email: " + exception.getMessage());
                    Notification notification_error_mail = new Notification("Es ist ein Fehler beim Versenden der Email aufgetreten. Bitte kontaktiere den Administrator", 3000);
                    notification_error_mail.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification_error_mail.setPosition(Notification.Position.BOTTOM_START);
                    notification_error_mail.open();
                }
            } else {
                datePicker.setInvalid(true);
                datePicker.setErrorMessage("Bitte wähle ein Datum aus");
                Notification notification = new Notification("Bitte wähle ein Datum aus", 3000);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.setPosition(Notification.Position.BOTTOM_START);
                notification.open();

            }
        });

        VerticalLayout selection = new VerticalLayout();
        selection.add(datePicker);
        selection.add(new Text("-- oder -- "));
        selection.add(threeMonths);
        selection.add(save);
        selection.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        selection.setAlignItems(FlexComponent.Alignment.CENTER);

        content.add(explanation, deleteInfo, selection);
    }

    private void deleteAccount(MailUser activatedUser) throws MessagingException, IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        mailUserRepository.delete(activatedUser);
        deactivationCodeRepository.delete(activatedUser.getDeactivationCode());

        if (activatedUser.getActivationCode() != null) { // if user is not activated
            activationCodeRepository.delete(activatedUser.getActivationCode());
        }

        logger.info("User deactivated Account successfully: {}", activatedUser.getEmail());
        Mailer mailer = new Mailer();
        try {
            mailer.sendDeactivationEmail(activatedUser.getFirstname(), activatedUser.getEmail());
        } catch (Exception exception) {
            logger.error("Error while sending deactivation email: " + exception.getMessage());
        }
        logger.info("User deactivated Account successfully");
        Notification notification = new Notification("Du hast deinen Account und alle zugehörigen Daten erfolgreich gelöscht!", 3000);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.BOTTOM_START);
        notification.open();
    }

    @Override
    protected Component initContent() {
        layout = new VerticalLayout();
        return layout;
    }

}
