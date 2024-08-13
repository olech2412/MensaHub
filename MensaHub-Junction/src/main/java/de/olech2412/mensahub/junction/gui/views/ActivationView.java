package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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
import de.olech2412.mensahub.junction.email.Mailer;
import de.olech2412.mensahub.junction.gui.components.own.Divider;
import de.olech2412.mensahub.junction.gui.components.own.boxes.MealBox;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.junction.jpa.services.RatingService;
import de.olech2412.mensahub.junction.jpa.services.meals.MealsService;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.API_User;
import de.olech2412.mensahub.models.authentification.ActivationCode;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Route("activate")
@PageTitle("Aktivierung")
@AnonymousAllowed
public class ActivationView extends VerticalLayout implements BeforeEnterObserver {

    private final API_UserRepository apiUserRepository;
    private final DeactivationCodeRepository deactivationCodeRepository;
    private final Mailer mailer;
    private final ActivationCodeRepository activationCodeRepository;
    private final MailUserRepository mailUserRepository;
    private final MealsService mealsService;
    private final RatingService ratingService;
    Logger logger = LoggerFactory.getLogger(ActivationView.class);
    private int currentMealIndex = 0;
    private List<Meal> meals;
    private MealBox currentMealBox;
    private MailUser mailUser;
    private Button prevButton;
    private Button nextButton;
    private String activationCode;

    private Text indexDisplay; // New Text component for index display

    public ActivationView(ActivationCodeRepository activationCodeRepository, MailUserRepository mailUserRepository,
                          MealsService mealsService, RatingService ratingService, API_UserRepository apiUserRepository, DeactivationCodeRepository deactivationCodeRepository, Mailer mailer) {
        this.activationCodeRepository = activationCodeRepository;
        this.mailUserRepository = mailUserRepository;
        this.mealsService = mealsService;
        this.ratingService = ratingService;
        this.apiUserRepository = apiUserRepository;
        this.deactivationCodeRepository = deactivationCodeRepository;
        this.mailer = mailer;
    }

    private void addUserMealsAndDivider(MailUser activatedUser) {
        add(new Divider());
        setAlignItems(Alignment.CENTER);
        HorizontalLayout mealLayout = new HorizontalLayout();

        meals = mealsService.findTop20DistinctMealsExcludingGoudaForSubscribedMensa(activatedUser.getId());

        if (meals.isEmpty()) {
            add(new Text("Keine Gerichte verfügbar."));
            return;
        }

        prevButton = new Button(VaadinIcon.ARROW_LEFT.create());
        prevButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        prevButton.addClickListener(e -> showPreviousMeal(mealLayout));
        if (currentMealIndex == 0) {
            prevButton.setEnabled(false);
        }

        nextButton = new Button(VaadinIcon.ARROW_RIGHT.create());
        nextButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        nextButton.addClickListener(e -> showNextMeal(mealLayout));
        if (currentMealIndex == meals.size() - 1) {
            nextButton.setEnabled(false);
        }
        // Initialize index display
        indexDisplay = new Text("Gericht " + (currentMealIndex + 1) + " von " + meals.size());
        HorizontalLayout indexLayout = new HorizontalLayout(prevButton, indexDisplay, nextButton);
        indexLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        indexLayout.setAlignItems(Alignment.CENTER);
        indexLayout.setWidthFull();
        add(indexLayout);

        // Create layout to hold meal and navigation buttons
        mealLayout.setWidthFull();
        mealLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        mealLayout.setWidth(50, Unit.PERCENTAGE);

        // Set the user for further rating logic
        this.mailUser = activatedUser;

        // Create and set up MealBox
        currentMealBox = createMealBox(meals.get(currentMealIndex));


        mealLayout.add(currentMealBox);

        add(mealLayout);
        logger.info("User activated Account successfully: {}", activatedUser.getEmail());
    }

    private MealBox createMealBox(Meal meal) {
        MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory());

        if (mailUser == null) {
            mealBox.getRatingComponent().setEnabled(false);
            mealBox.getRatingButton().setEnabled(false);
        } else {
            Result<List<Rating>, JPAError> ratings = ratingService.findAllByMailUserAndMealName(mailUser, meal.getName());

            if (ratings.isSuccess()) {
                List<Rating> ratingList = ratings.getData();
                for (Rating rating : ratingList) {
                    if (rating.getMealName().equals(meal.getName()) && rating.getMeal().getServingDate().equals(meal.getServingDate())
                            && rating.getMeal().getMensa().getId().equals(meal.getMensa().getId())) {
                        mealBox.getRatingButton().setEnabled(false);
                        mealBox.getRatingComponent().setEnabled(false);
                        mealBox.getRatingComponent().setRating(rating.getRating());
                    }
                }
            } else {
                NotificationFactory.create(NotificationType.ERROR, "Wir haben Schwierigkeiten, deine Ratings " +
                        "abzurufen. Die Funktion ist daher deaktiviert.").open();
            }

            mealBox.getRatingButton().addClickListener(buttonClickEvent -> {
                if (mealBox.getRatingComponent().getRating() != 0) {
                    Rating rating = new Rating();
                    rating.setMeal(meal);
                    rating.setRating(mealBox.getRatingComponent().getRating());
                    rating.setMealName(meal.getName());
                    rating.setMailUser(mailUser);
                    if (ratingService.saveRating(rating).isSuccess()) {
                        NotificationFactory.create(NotificationType.SUCCESS, "Bewertung gespeichert!").open();
                    } else {
                        NotificationFactory.create(NotificationType.ERROR, "Fehler beim Speichern der Bewertung!").open();
                    }
                    mealBox.getRatingButton().setEnabled(false);
                    mealBox.getRatingComponent().setEnabled(false);

                    Result<List<Rating>, JPAError> ratingsForNotification = ratingService.findAllByMailUser(mailUser);
                    if (ratingsForNotification.isSuccess()) {
                        List<Rating> ratingList = ratingsForNotification.getData();
                        if (ratingList.size() >= meals.size()) {
                            NotificationFactory.create(NotificationType.SUCCESS, "Vielen Dank für deine Bewertungen!").open();
                            mailUser.setActivationCode(null);
                            mailUserRepository.save(mailUser);
                            activationCodeRepository.delete(activationCodeRepository.findByCode(activationCode).get(0));
                        }
                    }
                }
            });
        }

        mealBox.setWidth("50%");  // Set width of the MealBox to 50%
        return mealBox;
    }

    private void showNextMeal(HorizontalLayout mealLayout) {
        if (currentMealIndex < meals.size() - 1) {
            currentMealIndex++;
            updateMealBox(mealLayout);
            updateIndexDisplay();// Update index display when showing the next meal
            if (currentMealIndex == meals.size() - 1) {
                nextButton.setEnabled(false);
            }
            if (!prevButton.isEnabled()) {
                prevButton.setEnabled(true);
            }
        }
    }

    private void showPreviousMeal(HorizontalLayout mealLayout) {
        if (currentMealIndex > 0) {
            currentMealIndex--;
            updateMealBox(mealLayout);
            updateIndexDisplay(); // Update index display when showing the previous meal
            if (currentMealIndex == 0) {
                prevButton.setEnabled(false);
            }
            if (!nextButton.isEnabled()) {
                nextButton.setEnabled(true);
            }
        }
    }

    private void updateMealBox(HorizontalLayout mealLayout) {
        mealLayout.remove(currentMealBox);
        currentMealBox = createMealBox(meals.get(currentMealIndex));
        mealLayout.add(currentMealBox);  // Add MealBox back to the center position
    }

    private void updateIndexDisplay() {
        if (indexDisplay != null) {
            indexDisplay.setText("Gericht " + (currentMealIndex + 1) + " von " + meals.size());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            Map<String, List<String>> params = event.getLocation().getQueryParameters().getParameters();
            activationCode = params.get("code").get(0);

            if (activationCodeRepository.findByCode(activationCode).isEmpty()) {
                add(new Text("Dein Code ist ungültig :( - Wahrscheinlich hast du den Code bereits verwendet oder er existiert nicht."));
            } else {
                if (apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(activationCode).get(0)).isPresent()) {
                    handleAPIUserActivation(activationCode);
                } else {
                    handleMailUserActivation(activationCode);
                }
            }
        } catch (NullPointerException nullPointerException) {
            logger.warn("User tried to navigate to ActivationView but there is no code");
            UI.getCurrent().navigate("login");
        }
    }

    private void handleAPIUserActivation(String activationCode) {
        API_User apiUser = apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(activationCode).get(0)).get();

        if (apiUser.getVerified_email()) {
            addAdminReviewLayout(apiUser, activationCode);
        } else {
            activateAPIUser(apiUser, activationCode);
        }
    }

    private void activateAPIUser(API_User apiUser, String activationCode) {
        add(new Text("Freischaltung erfolgreich :). Du hast deine E-Mail erfolgreich verifiziert und kannst dich somit in der Webanwendung mit deinem Account anmelden."));
        add(new Text("Im nächsten Schritt prüft der Administrator deine Anfrage"));

        ActivationCode newActivationCode = new ActivationCode(RandomStringUtils.randomAlphanumeric(32));
        activationCodeRepository.save(newActivationCode);

        apiUser.setActivationCode(newActivationCode);
        apiUser.setVerified_email(true);
        apiUserRepository.save(apiUser);

        activationCodeRepository.delete(activationCodeRepository.findByCode(activationCode).get(0));
        logger.info("User activated API-Account successfully: {} admin review required", apiUser.getEmail());

        try {
            mailer.sendAPIAdminRequest(newActivationCode.getCode());
        } catch (Exception exception) {
            logger.error("Admin request could not send due to: {}", exception);
        }

        logger.info("API admin-request sent for user: {}", apiUser.getEmail());
    }

    private void addAdminReviewLayout(API_User apiUser, String activationCode) {
        add(new H3("Hallo Admin! Prüfe die folgende Anfrage:"));
        add(new Text("Nutzername: " + apiUser.getApiUsername()));
        add(new Text("E-Mail: " + apiUser.getEmail()));
        add(new Text("Erstellungsdatum: " + apiUser.getCreationDate()));
        add(new Text("Beschreibung: " + apiUser.getDescription()));
        add(new Text("Rolle: " + apiUser.getRole()));
        add(new Text("E-Mail verifiziert: " + apiUser.getVerified_email()));
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
            activationCodeRepository.delete(activationCodeRepository.findByCode(activationCode).get(0));

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
            activationCodeRepository.delete(activationCodeRepository.findByCode(activationCode).get(0));
            deactivationCodeRepository.delete(apiUser.getDeactivationCode());

            try {
                mailer.sendAPIAdminRequestDecline(apiUser.getApiUsername(), apiUser.getEmail());
            } catch (Exception exception) {
                logger.error("User tried to activate API-Account but: " + exception.getMessage());
            }

            logger.info("API admin request declined for user: {}", apiUser.getEmail());
            Notification notification = new Notification("Ablehnung wurde gespeichert und der User informiert! Alle Daten des Nutzers werden gelöscht", 6000);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            decline.setEnabled(false);
            accept.setEnabled(false);
        });

        add(new HorizontalLayout(accept, decline));
    }

    private void handleMailUserActivation(String activationCode) {
        add(new Text("Freischaltung erfolgreich :). Du bist nun im Email-Verteiler."));
        Paragraph info = new Paragraph("Bitte tue uns noch einen Gefallen und bewerte die folgenden Gerichte, damit wir dir bestimmte Gerichte aufgrund der Bewertungen der Community empfehlen können. Die Vorschläge kannst du dann im Speiseplan, als auch im Newsletter sehen.");
        info.setWidth(50, Unit.PERCENTAGE);
        add(info);
        MailUser activatedUser = mailUserRepository.findByActivationCode_Code(activationCode);
        activatedUser.setEnabled(true);
        addUserMealsAndDivider(activatedUser);
    }
}
