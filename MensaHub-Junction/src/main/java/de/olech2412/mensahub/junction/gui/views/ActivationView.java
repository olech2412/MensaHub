package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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
import de.mekaso.vaadin.addon.compani.animation.Animation;
import de.mekaso.vaadin.addon.compani.animation.AnimationBuilder;
import de.mekaso.vaadin.addon.compani.animation.AnimationTypes;
import de.mekaso.vaadin.addon.compani.effect.EntranceEffect;
import de.mekaso.vaadin.addon.compani.effect.ExitEffect;
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.junction.gui.components.own.Divider;
import de.olech2412.mensahub.junction.gui.components.own.boxes.MealBox;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.junction.jpa.services.meals.MealsService;
import de.olech2412.mensahub.models.Meal;
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
@StyleSheet(Animation.STYLES)
public class ActivationView extends VerticalLayout implements BeforeEnterObserver {

    private final ActivationCodeRepository activationCodeRepository;
    private final MailUserRepository mailUserRepository;
    private final MealsService mealsService;

    @Autowired
    API_UserRepository apiUserRepository;
    @Autowired
    DeactivationCodeRepository deactivationCodeRepository;
    @Autowired
    Mailer mailer;

    Logger logger = LoggerFactory.getLogger(ActivationView.class);

    public ActivationView(ActivationCodeRepository activationCodeRepository, MailUserRepository mailUserRepository, MealsService mealsService) {
        this.activationCodeRepository = activationCodeRepository;
        this.mailUserRepository = mailUserRepository;
        this.mealsService = mealsService;
    }

    private void addUserMealsAndDivider(MailUser activatedUser) {
        HorizontalLayout row2 = new HorizontalLayout();
        add(row2);
        Button startAnimation = new Button("Entrance Animation");
        add(startAnimation);

        Button fadeOutAnimation = new Button("Exit Animation");
        add(fadeOutAnimation);

        Paragraph label = new Paragraph("Willkommen bei MensaHub! Hier sind die Top 20 Gerichte der Mensen, die du abonniert hast:");

        startAnimation.addClickListener(buttonClickEvent -> {
            AnimationBuilder
                    .createBuilderFor(label)
                    .create(AnimationTypes.EntranceAnimation.class)
                    .withEffect(EntranceEffect.fadeInRight)
                    .register();
            row2.add(label);
        });
        fadeOutAnimation.addClickListener(buttonClickEvent -> AnimationBuilder
                .createBuilderFor(label)
                .create(AnimationTypes.ExitAnimation.class)
                .withEffect(ExitEffect.fadeOutLeft)
                .remove());


        add(new Divider());

        List<Meal> meals = mealsService.findTop20DistinctMealsExcludingGoudaForSubscribedMensa(activatedUser.getId());

        HorizontalLayout row = new HorizontalLayout();
        row.setJustifyContentMode(JustifyContentMode.CENTER);
        row.setWidthFull();
        row.getStyle().set("flex-wrap", "wrap");
        row.addClassName("meal-content");
        row.addClassName("meal-row");


        for (Meal meal : meals) {
            MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory());
            row.add(mealBox);
        }

        add(row);
        logger.info("User activated Account successfully: {}", activatedUser.getEmail());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
            String code = params.get("code").get(0);

            if (activationCodeRepository.findByCode(code).isEmpty()) {
                add(new Text("Dein Code ist ungültig :( - Wahrscheinlich hast du den Code bereits verwendet oder er existiert nicht."));
            } else {
                if (apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(code).get(0)).isPresent()) {
                    handleAPIUserActivation(event, code);
                } else {
                    handleMailUserActivation(code);
                }
            }
        } catch (NullPointerException nullPointerException) {
            logger.warn("User tried to navigate to ActivationView but there is no code");
            UI.getCurrent().navigate("login");
        }
    }

    private void handleAPIUserActivation(BeforeEnterEvent event, String code) {
        API_User apiUser = apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(code).get(0)).get();

        if (apiUser.getVerified_email()) {
            addAdminReviewLayout(apiUser, code);
        } else {
            activateAPIUser(apiUser, code);
        }
    }

    private void activateAPIUser(API_User apiUser, String code) {
        add(new Text("Freischaltung erfolgreich :). Du hast deine E-Mail erfolgreich verifiziert und kannst dich somit in der Webanwendung mit deinem Account anmelden."));
        add(new Text("Im nächsten Schritt prüft der Administrator deine Anfrage"));

        ActivationCode newActivationCode = new ActivationCode(RandomStringUtils.randomAlphanumeric(32));
        activationCodeRepository.save(newActivationCode);

        apiUser.setActivationCode(newActivationCode);
        apiUser.setVerified_email(true);
        apiUserRepository.save(apiUser);

        activationCodeRepository.delete(activationCodeRepository.findByCode(code).get(0));
        logger.info("User activated API-Account successfully: {} admin review required", apiUser.getEmail());

        try {
            mailer.sendAPIAdminRequest(newActivationCode.getCode());
        } catch (Exception exception) {
            logger.error("Admin request could not send due to: {}", exception);
        }

        logger.info("API admin-request sent for user: {}", apiUser.getEmail());
    }

    private void addAdminReviewLayout(API_User apiUser, String code) {
        add(new H3("Hallo Admin! Prüfe die folgende Anfrage:"));
        add(new Span(""));
        add(new Text("Nutzername: " + apiUser.getApiUsername()));
        add(new Span(""));
        add(new Text("E-Mail: " + apiUser.getEmail()));
        add(new Span(""));
        add(new Text("Erstellungsdatum: " + apiUser.getCreationDate()));
        add(new Span(""));
        add(new Text("Beschreibung: " + apiUser.getDescription()));
        add(new Span(""));
        add(new Text("Rolle: " + apiUser.getRole()));
        add(new Span(""));
        add(new Text("E-Mail verifiziert: " + apiUser.getVerified_email()));
        add(new Span(""));
        add(new Text("Durch Admin verifiziert: " + apiUser.getEnabledByAdmin()));

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

        add(new HorizontalLayout(accept, decline));
    }

    private void handleMailUserActivation(String code) {
        add(new Text("Freischaltung erfolgreich :). Du bist nun im Email-Verteiler."));
        MailUser activatedUser = mailUserRepository.findByActivationCode_Code(code);

        // activatedUser.setActivationCode(null);
        activatedUser.setEnabled(true);
        mailUserRepository.save(activatedUser);
        // activationCodeRepository.delete(activationCodeRepository.findByCode(code).get(0));
        addUserMealsAndDivider(activatedUser);
    }
}
