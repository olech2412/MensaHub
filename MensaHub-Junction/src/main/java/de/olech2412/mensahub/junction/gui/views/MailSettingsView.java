package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
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
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.junction.gui.components.vaadin.datetimepicker.GermanDatePicker;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.MailUserSetupDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.PreferencesDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.PushNotificationDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.CookieNotification;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.SubscriptionEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.mensen.MensaRepository;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.jpa.services.PreferencesService;
import de.olech2412.mensahub.junction.jpa.services.meals.MealsService;
import de.olech2412.mensahub.junction.webpush.WebPushService;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Preferences;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Route("deactivate")
@PageTitle("Verwaltung deiner E-Mail-Einstellungen")
@AnonymousAllowed
public class MailSettingsView extends Composite implements BeforeEnterObserver {
    private final DeactivationCodeRepository deactivationCodeRepository;
    private final MailUserService mailUserService;
    private final ActivationCodeRepository activationCodeRepository;
    private final VerticalLayout content = new VerticalLayout();
    private final MensaRepository mensaRepository;
    private final MealsService mealsService;
    @Autowired
    API_UserRepository apiUserRepository;
    Logger logger = LoggerFactory.getLogger(MailSettingsView.class);
    private VerticalLayout layout;
    @Autowired
    private PreferencesService preferencesService;

    private final SubscriptionEntityRepository subscriptionEntityRepository;

    private final WebPushService webPushService;

    public MailSettingsView(DeactivationCodeRepository deactivationCodeRepository, MailUserService mailUserService,
                            ActivationCodeRepository activationCodeRepository, MensaRepository mensaRepository,
                            MealsService mealsService, WebPushService webPushService, SubscriptionEntityRepository subscriptionEntityRepository) {
        this.deactivationCodeRepository = deactivationCodeRepository;
        this.mailUserService = mailUserService;
        this.activationCodeRepository = activationCodeRepository;
        this.mensaRepository = mensaRepository;
        this.mealsService = mealsService;
        this.webPushService = webPushService;
        this.subscriptionEntityRepository = subscriptionEntityRepository;

        new CookieNotification(); // check if cookies are already accepted or show the cookie banner
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        layout.setSizeFull();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setAlignSelf(FlexComponent.Alignment.CENTER);
        content.setSizeFull();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setAlignSelf(FlexComponent.Alignment.CENTER);

        StreamResource logoStream = new StreamResource("mensaHub_logo.png", () -> getClass().getResourceAsStream("/static/img/MensaHub_logo.webp"));
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

    @Transactional
    protected void initDeactivationView(String code) throws MessagingException {
        Result<MailUser, JPAError> mailUserResult = mailUserService.findMailUserByDeactivationCode(code);

        if (!mailUserResult.isSuccess()) {
            layout.add(new Paragraph("Der Nutzer konnte nicht identifiziert werden. Wenn du der Meinung bist, es " +
                    "handelt sich um einen Fehler, kontaktiere bitte den Administrator."));
            return;
        }

        MailUser mailUser = mailUserResult.getData();

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

        Button mailUserSettings = new Button("Einstellungen");
        mailUserSettings.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        mailUserSettings.setIcon(VaadinIcon.COG.create());

        Button preferences = new Button("Präferenzen bearbeiten");
        preferences.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        preferences.setIcon(VaadinIcon.CROSS_CUTLERY.create());

        preferences.addClickListener(buttonClickEvent -> {
            PreferencesDialog preferencesDialog = new PreferencesDialog(mealsService);
            Preferences existingPreferences = mailUser.getPreferences();
            preferencesDialog.setPreferences(existingPreferences);
            preferencesDialog.open();

            preferencesDialog.getFooterButtonLayout().getAcceptButton().addClickListener(buttonClickEvent1 -> {
                Preferences newPreferences = preferencesDialog.buildPreferences();
                if (existingPreferences != null) {
                    newPreferences.setId(existingPreferences.getId());
                    Result<Preferences, JPAError> saveResult = preferencesService.save(newPreferences);
                    if (saveResult.isSuccess()) {
                        NotificationFactory.create(NotificationType.SUCCESS, "Präferenzen erfolgreich aktualisiert").open();
                        mailUser.setPreferences(saveResult.getData());
                    } else {
                        NotificationFactory.create(NotificationType.ERROR, "Fehler beim speichern der Präferenzen").open();
                    }
                } else { // then the user has no preferences at this point
                    mailUser.setPreferences(newPreferences);
                    mailUserService.saveMailUser(mailUser);
                    NotificationFactory.create(NotificationType.SUCCESS, "Präferenzen erfolgreich aktualisiert").open();
                }

            });
        });

        List<Mensa> mensen = mensaRepository.findAll();

        mailUserSettings.addClickListener(buttonClickEvent -> {
            MailUserSetupDialog mailUserSetupDialog = new MailUserSetupDialog(mailUser, mensen);
            mailUserSetupDialog.getFooterButtonLayout().getAcceptButton().addClickListener(saveEvent -> {
                if (mailUserSetupDialog.getMensaComboBox().isEmpty() || mailUserSetupDialog.getMensaComboBox().isInvalid()) {
                    NotificationFactory.create(NotificationType.ERROR, "Bitte wähle gültige Daten aus").open();
                    return;
                }
                mailUser.setWantsUpdate(mailUserSetupDialog.getWantsUpdateCheckbox().getValue());
                mailUser.setMensas(mailUserSetupDialog.getMensaComboBox().getValue());

                mailUserService.saveMailUser(mailUser);
                NotificationFactory.create(NotificationType.SUCCESS, "Änderungen wurden erfolgreich gespeichert").open();
            });
            mailUserSetupDialog.open();
        });

        Button pushNotificationDialogButton = new Button("Push Benachrichtigungen");
        pushNotificationDialogButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        pushNotificationDialogButton.setIcon(VaadinIcon.BELL_O.create());
        pushNotificationDialogButton.addClickListener(buttonClickEvent -> {
            PushNotificationDialog pushNotificationDialog = new PushNotificationDialog(webPushService, mailUserService, mailUser, subscriptionEntityRepository);
            pushNotificationDialog.open();
        });

        FormLayout formLayout = new FormLayout(headlineDelete, explanationDelete, deactivate, deactivateForTime, mailUserSettings, preferences, pushNotificationDialogButton);

        content.add(formLayout);
        layout.add(content);

        if (!mailUser.isEnabled() && mailUser.getDeactivatedUntil() != null) {
            formLayout.remove(deactivateForTime);
            Button reactivate = new Button("Account reaktivieren");
            reactivate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            reactivate.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            reactivate.setIcon(VaadinIcon.REFRESH.create());
            formLayout.add(reactivate);

            reactivate.addClickListener(buttonClickEvent -> {
                mailUser.setEnabled(true);
                mailUser.setDeactivatedUntil(null);
                mailUserService.saveMailUser(mailUser);
                NotificationFactory.create(NotificationType.SUCCESS, "Du hast deinen Account erfolgreich wieder freigeschaltet").open();
            });
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
        datePicker.setWidth(30f, Unit.PERCENTAGE);
        datePicker.setMinWidth(320f, Unit.PIXELS);
        datePicker.setLabel("Wähle den Zeitpunkt der Reaktivierung");
        if (mailUser.getDeactivatedUntil() != null) {
            datePicker.setValue(mailUser.getDeactivatedUntil());
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
                mailUser.setDeactivatedUntil(datePicker.getValue());
                mailUserService.saveMailUser(mailUser);

                NotificationFactory.create(NotificationType.SUCCESS, "Du wurdest erfolgreich temporär deaktiviert. Alle wichtige Informationen senden wir dir per Mail zu").open();
                Mailer mailer = null;
                try {
                    mailer = new Mailer();
                } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException |
                         NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new RuntimeException(e);
                }
                try {
                    mailer.sendTemporaryDeactivationEmail(mailUser.getFirstname(), mailUser.getEmail(), mailUser.getDeactivationCode().getCode(), mailUser.getDeactivatedUntil());
                } catch (Exception exception) {
                    logger.error("Error while sending deactivation email: " + exception.getMessage());
                    NotificationFactory.create(NotificationType.ERROR, "Es ist ein Fehler beim Versenden der Email aufgetreten. Bitte kontaktiere den Administrator").open();
                }
            } else {
                datePicker.setInvalid(true);
                datePicker.setErrorMessage("Bitte wähle ein Datum aus");
                NotificationFactory.create(NotificationType.ERROR, "Bitte wähle ein Datum aus").open();
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
        mailUserService.deleteMailUser(activatedUser);
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
        NotificationFactory.create(NotificationType.SUCCESS, "Du hast deinen Account und alle zugehörigen Daten erfolgreich gelöscht!").open();
    }

    @Override
    protected Component initContent() {
        layout = new VerticalLayout();
        return layout;
    }

}
