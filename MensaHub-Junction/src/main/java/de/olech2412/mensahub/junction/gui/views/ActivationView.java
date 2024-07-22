package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.models.authentification.API_User;
import de.olech2412.mensahub.models.authentification.ActivationCode;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Route("activate")
@PageTitle("Aktivierung")
@AnonymousAllowed
public class ActivationView extends Composite implements BeforeEnterObserver {
    private final ActivationCodeRepository activationCodeRepository;
    private final MailUserRepository mailUserRepository;

    @Autowired
    API_UserRepository apiUserRepository;

    @Autowired
    DeactivationCodeRepository deactivationCodeRepository;

    @Autowired
    Mailer mailer;

    Logger logger = LoggerFactory.getLogger(ActivationView.class);
    private VerticalLayout layout;

    public ActivationView(ActivationCodeRepository activationCodeRepository, MailUserRepository mailUserRepository) {
        this.activationCodeRepository = activationCodeRepository;
        this.mailUserRepository = mailUserRepository;
    }


    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();

            String code = params.get("code").get(0);

            if (activationCodeRepository.findByCode(code).isEmpty()) {
                layout.add(new Text("Dein Code ist ungültig :( - Wahrscheinlich hast du den Code bereits verwendet oder er existiert nicht."));
            } else {
                if (apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(code).get(0)).isPresent()) {
                    if (apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(code).get(0)).get().getVerified_email()) {
                        API_User apiUser = apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(code).get(0)).get();
                        layout.add(new H3("Hallo Admin! Prüfe die folgende Anfrage:"));
                        layout.add(new Span(""));
                        layout.add(new Text("Nutzername: " + apiUser.getApiUsername()));
                        layout.add(new Span(""));
                        layout.add(new Text("E-Mail: " + apiUser.getEmail()));
                        layout.add(new Span(""));
                        layout.add(new Text("Erstellungsdatum: " + apiUser.getCreationDate()));
                        layout.add(new Span(""));
                        layout.add(new Text("Beschreibung: " + apiUser.getDescription()));
                        layout.add(new Span(""));
                        layout.add(new Text("Rolle: " + apiUser.getRole()));
                        layout.add(new Span(""));
                        layout.add(new Text("E-Mail verifiziert: " + apiUser.getVerified_email()));
                        layout.add(new Span(""));
                        layout.add(new Text("Durch Admin verifiziert: " + apiUser.getEnabledByAdmin()));

                        Button accept = new Button("Anfrage bestätigen");
                        accept.setIcon(VaadinIcon.CHECK.create());
                        Button decline = new Button("Anfrage ablehnen");
                        decline.setIcon(VaadinIcon.BAN.create());

                        accept.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
                        accept.addClickListener(buttonClickEvent -> {
                            apiUser.setEnabledByAdmin(true);
                            apiUser.setActivationCode(null);
                            apiUserRepository.save(apiUser);
                            activationCodeRepository.delete(activationCodeRepository.findByCode(code).get(0));
                            try {
                                mailer.sendAPIAdminRequestSuccess(apiUser.getApiUsername(), apiUser.getEmail(), apiUser.getDeactivationCode().getCode());
                            } catch (Exception exception) {
                                logger.error("User tried to activate API-Account but: " + exception.getMessage());
                            }
                            logger.info("API adminrequest accepted for user: " + apiUser.getEmail());
                            Notification notification = new Notification("Anfrage angenommen! Nutzer ist freigeschaltet", 6000);
                            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                            notification.open();
                            decline.setEnabled(false);
                            accept.setEnabled(false);
                        });

                        decline.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
                        decline.addClickListener(buttonClickEvent -> {
                            apiUserRepository.delete(apiUser);
                            activationCodeRepository.delete(activationCodeRepository.findByCode(code).get(0));
                            deactivationCodeRepository.delete(apiUser.getDeactivationCode());
                            try {
                                mailer.sendAPIAdminRequestDecline(apiUser.getApiUsername(), apiUser.getEmail());
                            } catch (Exception exception) {
                                logger.error("User tried to activate API-Account but: " + exception.getMessage());
                            }
                            logger.info("API adminrequest declined for user: {}", apiUser.getEmail());
                            Notification notification = new Notification("Ablehnung wurde gespeichert und der User informiert! Alle Daten des Nutzers werden gelöscht", 6000);
                            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                            notification.open();
                            decline.setEnabled(false);
                            accept.setEnabled(false);
                        });
                        layout.add(new HorizontalLayout(accept, decline));

                    } else {
                        layout.add(new Text("Freischaltung erfolgreich :). Du hast deine E-Mail erfolgreich verifiziert und kannst dich somit in der Webanwendung mit deinem Account anmelden."));
                        layout.add(new Text("Im nächsten Schritt prüft der Administrator deine Anfrage"));
                        API_User activatedUser = apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(code).get(0)).get();
                        ActivationCode activationCode = new ActivationCode(RandomStringUtils.randomAlphanumeric(32));
                        activationCodeRepository.save(activationCode);
                        activatedUser.setActivationCode(activationCode);
                        activatedUser.setVerified_email(true);
                        apiUserRepository.save(activatedUser);
                        activationCodeRepository.delete(activationCodeRepository.findByCode(code).get(0));
                        logger.info("User activated API-Account successfully: {} admin review required", activatedUser.getEmail());
                        try {
                            mailer.sendAPIAdminRequest(activationCode.getCode());
                        } catch (Exception exception) {
                            logger.error("Admin request could not send due to: {}", exception);
                        }
                        logger.info("API admin-request sent for user: {}", activatedUser.getEmail());
                    }
                } else {
                    layout.add(new Text("Freischaltung erfolgreich :). Du bist nun im Email-Verteiler."));
                    MailUser activatedUser = mailUserRepository.findByActivationCode_Code(code);
                    activatedUser.setActivationCode(null);
                    activatedUser.setEnabled(true);
                    mailUserRepository.save(activatedUser);
                    activationCodeRepository.delete(activationCodeRepository.findByCode(code).get(0));
                    logger.info("User activated Account successfully: {}", activatedUser.getEmail());
                }
            }
        } catch (NullPointerException nullPointerException) {
            logger.warn("User tried to navigate to ActivationView but there is no code");
            UI.getCurrent().navigate("login");
        }

    }

    @Override
    protected Component initContent() {
        layout = new VerticalLayout();
        return layout;
    }

}