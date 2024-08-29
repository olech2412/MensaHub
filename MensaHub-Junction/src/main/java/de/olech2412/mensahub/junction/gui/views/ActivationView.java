package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
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
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.CookieNotification;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.jpa.repository.ActivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.DeactivationCodeRepository;
import de.olech2412.mensahub.junction.jpa.repository.MailUserRepository;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
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

/**
 * This view is used to activate a user account. The user receives an activation code via email and can activate his account by clicking on the link in the email.
 * The user is then redirected to this view where he can rate meals. The user can skip the rating and activate his account anyway.
 * The view is also used to activate API users. API users have to be verified by an administrator before they can use the API.
 */
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
    private final MailUserService mailUserService;
    Logger logger = LoggerFactory.getLogger(ActivationView.class);
    private int currentMealIndex = 0;
    private List<Meal> mealList;
    private MealBox currentMealBox;
    private MailUser mailUser;
    private Button prevButton;
    private Button nextButton;
    private Button activateAnyway;
    private String activationCode;
    private Text indexDisplay; // New Text component for index display

    public ActivationView(ActivationCodeRepository activationCodeRepository, MailUserRepository mailUserRepository,
                          MealsService mealsService, RatingService ratingService, API_UserRepository apiUserRepository, DeactivationCodeRepository deactivationCodeRepository, Mailer mailer, MailUserService mailUserService) {
        this.activationCodeRepository = activationCodeRepository;
        this.mailUserRepository = mailUserRepository;
        this.mealsService = mealsService;
        this.ratingService = ratingService;
        this.apiUserRepository = apiUserRepository;
        this.deactivationCodeRepository = deactivationCodeRepository;
        this.mailer = mailer;

        new CookieNotification(); // check if cookies are already accepted or show the cookie banner
        this.mailUserService = mailUserService;
    }

    /**
     * This method is used to add the meals to the view and to add a divider between the meals and the activation button.
     * The user can rate the meals and skip the rating to activate his account anyway.
     *
     * @param activatedUser The user that's activating his account
     */
    private void addUserMealsAndDivider(MailUser activatedUser) {

        // if user alr ratings for all meals, skip activation


        add(new Divider());
        setAlignItems(Alignment.CENTER);
        HorizontalLayout mealLayout = new HorizontalLayout();

        Result<List<Meal>, JPAError> mealListResult = mealsService.findTop20DistinctMealsExcludingGoudaForSubscribedMensa(activatedUser.getId());

        if (!mealListResult.isSuccess()) {
            NotificationFactory.create(NotificationType.ERROR, "Wir haben Schwierigkeiten Essensdaten aus der Datenbank zu laden.").open();
            return;
        }

        mealList = mealListResult.getData();

        if (mealList.isEmpty()) {
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
        if (currentMealIndex == mealList.size() - 1) {
            nextButton.setEnabled(false);
        }
        // Initialize index display
        indexDisplay = new Text("Gericht " + (currentMealIndex + 1) + " von " + mealList.size());
        HorizontalLayout indexLayout = new HorizontalLayout(prevButton, indexDisplay, nextButton);
        indexLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        indexLayout.setAlignItems(Alignment.CENTER);
        indexLayout.setWidthFull();
        add(indexLayout);

        // Create a layout to hold meal and navigation buttons
        mealLayout.setWidthFull();
        mealLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        // if device width below 750 pixels set specific class
        mealLayout.addClassName("activationview-mealbox");

        // Set the user for further rating logic
        this.mailUser = activatedUser;

        // Create and set up MealBox
        currentMealBox = createMealBox(mealList.get(currentMealIndex));


        mealLayout.add(currentMealBox);

        add(mealLayout);

        // Add a button to activate the user anyway
        activateAnyway = new Button("Bewertung überspringen und sofort aktivieren");
        activateAnyway.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        activateAnyway.addClickListener(buttonClickEvent -> {
            handleRemovingActivationCode(mailUser);
            NotificationFactory.create(NotificationType.SUCCESS, "Aktivierung übersprungen!").open();
            activateAnyway.setEnabled(false);
        });
        activateAnyway.setTooltipText("Wenn du keine Lust hast, die Gerichte zu bewerten, kannst du dennoch deinen Account aktivieren. Du kannst die Gerichte auch später im Speiseplan bewerten.");
        add(activateAnyway);

    }

    /**
     * This method is used to create a MealBox for a given meal. The MealBox contains the meal's name, description, price, allergens, category, and a rating component.
     * The rating component is used to rate the meal. The rating button is used to save the rating to the database.
     *
     * @param meal The meal for which the MealBox should be created
     * @return The created MealBox
     */
    private MealBox createMealBox(Meal meal) {
        MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory(), meal.getId().intValue());

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
                        if (ratingList.size() >= mealList.size()) {
                            NotificationFactory.create(NotificationType.SUCCESS, "Vielen Dank für deine Bewertungen!").open();
                            handleRemovingActivationCode(mailUser);
                        }
                    }
                }
            });
        }

        mealBox.setWidth("50%");  // Set width of the MealBox to 50%
        return mealBox;
    }

    /**
     * This method is used to show the next meal in the meal list. The method updates the currentMealIndex and the MealBox.
     * The method also updates the index display and enables/disables the navigation buttons.
     *
     * @param mealLayout The layout that holds the MealBox
     */
    private void showNextMeal(HorizontalLayout mealLayout) {
        if (currentMealIndex < mealList.size() - 1) {
            currentMealIndex++;
            updateMealBox(mealLayout);
            updateIndexDisplay();// Update the index display when showing the next meal
            if (currentMealIndex == mealList.size() - 1) {
                nextButton.setEnabled(false);
            }
            if (!prevButton.isEnabled()) {
                prevButton.setEnabled(true);
            }
        }
    }

    /**
     * This method is used to show the previous meal in the meal list. The method updates the currentMealIndex and the MealBox.
     * The method also updates the index display and enables/disables the navigation buttons.
     *
     * @param mealLayout The layout that holds the MealBox
     */
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

    /**
     * This method is used to update the MealBox in the mealLayout. The method removes the current MealBox from the mealLayout and adds the new MealBox to the center position.
     *
     * @param mealLayout The layout that holds the MealBox
     */
    private void updateMealBox(HorizontalLayout mealLayout) {
        mealLayout.remove(currentMealBox);
        currentMealBox = createMealBox(mealList.get(currentMealIndex));
        mealLayout.add(currentMealBox);  // Add MealBox back to the center position
    }

    /**
     * This method is used to update the index display. The method updates the index display to show the current meal index and the total number of meals.
     * The method is called when showing the next or previous meal.
     */

    private void updateIndexDisplay() {
        if (indexDisplay != null) {
            indexDisplay.setText("Gericht " + (currentMealIndex + 1) + " von " + mealList.size());
        }
    }

    /**
     * This method is used to handle the activation of a user account. The method is called when the user navigates to the ActivationView.
     * The method checks if the activation code is valid and if the user is an API user or a mail user.
     * If the user is an API user, the method checks if the user's email is verified. If the email is verified, the method activates the user's account.
     * If the email is not verified, the method activates the user's email and sends an administrator request to verify the user's account.
     * If the user is a mail user, the method activates the user's email and adds the user's meals to the view.
     * The user can rate the meals and skip the rating to activate his account anyway.
     * The method also adds a divider between the meals and the activation button.
     * The method is called when the user navigates to the ActivationView.
     *
     * @param event The BeforeEnterEvent that is triggered when the user navigates to the ActivationView
     */
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

    /**
     * This method is used to handle the activation of an API user. The method checks if the user's email is verified.
     *
     * @param activationCode The activation code of the user
     */
    private void handleAPIUserActivation(String activationCode) {
        API_User apiUser = apiUserRepository.findAPI_UserByActivationCode(activationCodeRepository.findByCode(activationCode).get(0)).get();

        if (apiUser.getVerified_email()) {
            addAdminReviewLayout(apiUser, activationCode);
        } else {
            activateAPIUser(apiUser, activationCode);
        }
    }

    /**
     * This method is used to activate an API user. The method activates the user's email and sends an administrator request to verify the user's account.
     *
     * @param apiUser        The API user that's activating his account
     * @param activationCode The activation code of the user
     */
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

    /**
     * This method is used to add an administrator review layout to the view. The layout is used to review the user's request.
     *
     * @param apiUser        The API user that's activating his account
     * @param activationCode The activation code of the user
     */
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

    /**
     * This method is used to handle the activation of a mail user. The method activates the user's email and adds the user's meals to the view.
     *
     * @param activationCode The activation code of the user
     */
    private void handleMailUserActivation(String activationCode) {
        MailUser activatedUser = mailUserRepository.findByActivationCode_Code(activationCode);
        add(new H2("Hallo: " + activatedUser.getFirstname() + "!"));
        Paragraph p = new Paragraph("Ein Schritt fehlt noch zur erfolgreichen Aktivierung deines Accounts. Bitte bewerte die folgenden Gerichte, um personalisierte Empfehlungen und oder den Newsletter zu erhalten.");
        p.setMaxWidth(90, Unit.PERCENTAGE);
        add(p);
        Paragraph info = new Paragraph("Bitte tue uns/dir noch einen Gefallen und bewerte die folgenden Gerichte, damit wir dir bestimmte Gerichte aufgrund deiner Bewertungen und Bewertungen der Community empfehlen können. Die Vorschläge kannst du dann im Speiseplan, als auch im Newsletter sehen. Wenn du darauf keine Lust hast, kannst du unten auf den Überspringen-Button klicken.");
        info.setWidth(90, Unit.PERCENTAGE);
        add(info);
        addUserMealsAndDivider(activatedUser);
    }

    /**
     * This method is used to handle the removal of the activation code. The method removes the activation code from the user and deletes the activation code from the database.
     *
     * @param mailUser The user that's activating his account
     */
    private void handleRemovingActivationCode(MailUser mailUser) {
        logger.info("User activated Account successfully: {}", mailUser.getEmail());
        mailUser.setActivationCode(null);
        mailUser.setEnabled(true);
        mailUserRepository.save(mailUser);
        activationCodeRepository.delete(activationCodeRepository.findByCode(activationCode).get(0));
    }
}
