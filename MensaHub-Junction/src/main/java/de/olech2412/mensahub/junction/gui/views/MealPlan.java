package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.gui.components.own.boxes.InfoBox;
import de.olech2412.mensahub.junction.gui.components.own.boxes.MealBox;
import de.olech2412.mensahub.junction.gui.components.vaadin.datetimepicker.GermanDatePicker;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.AppleDeviceUserCodeDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.CookieNotification;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.InfoWithAnchorNotification;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.RatingRepository;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.jpa.services.MealPlanService;
import de.olech2412.mensahub.junction.jpa.services.RatingService;
import de.olech2412.mensahub.junction.jpa.services.meals.MealsService;
import de.olech2412.mensahub.junction.jpa.services.mensen.MensaService;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Route("mealPlan")
@PageTitle("Speiseplan")
@AnonymousAllowed
@Slf4j
public class MealPlan extends VerticalLayout implements BeforeEnterObserver {

    private final MealsService mealsService;
    private final MensaService mensaService;

    private final ComboBox<Mensa> mensaComboBox = new ComboBox<>();
    private final GermanDatePicker datePicker = new GermanDatePicker();
    private final MealPlanService mealPlanService;
    private final Button buttonOneDayBack = new Button(VaadinIcon.CHEVRON_CIRCLE_LEFT_O.create());
    private final Button buttonOneDayForward = new Button(VaadinIcon.CHEVRON_CIRCLE_RIGHT_O.create());
    HorizontalLayout row = new HorizontalLayout();
    @Autowired
    RatingRepository ratingRepository;
    VerticalLayout headerComboboxDatePickerButtonsLayout = new VerticalLayout();
    @Autowired
    private MailUserService mailUserService;
    @Autowired
    private RatingService ratingService;
    private MailUser mailUser;

    public MealPlan(MealsService mealsService, MensaService mensaService, MealPlanService mealPlanService) {
        this.mealsService = mealsService;
        this.mensaService = mensaService;
        this.mealPlanService = mealPlanService;

        new CookieNotification(); // check if cookies are already accepted or show the cookie banner

        datePicker.setValue(LocalDate.now());

        HorizontalLayout pageSelHeader = new HorizontalLayout();
        mensaComboBox.setItems(mensaService.getAllMensas());
        mensaComboBox.setItemLabelGenerator(Mensa::getName);


        headerComboboxDatePickerButtonsLayout.setWidth(100f, Unit.PERCENTAGE);
        headerComboboxDatePickerButtonsLayout.setAlignItems(Alignment.CENTER);
        headerComboboxDatePickerButtonsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        HorizontalLayout buttonsDatePickerLayout = new HorizontalLayout();
        buttonsDatePickerLayout.setAlignItems(Alignment.CENTER);
        buttonsDatePickerLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        // set color grey
        buttonOneDayBack.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        buttonOneDayBack.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent == null || mensaComboBox.isEmpty()) {
                return;
            }
            if (mailUser != null) {
                buttonOneDayForward.setEnabled(false);
                buttonOneDayBack.setEnabled(false);
                datePicker.setEnabled(false);
            }
            buildMealPlan(datePicker.getValue().minusDays(1), mensaComboBox.getValue());
        });

        buttonOneDayForward.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        buttonOneDayForward.addClickListener(buttonClickEvent -> {
            if (buttonClickEvent == null || mensaComboBox.isEmpty()) {
                return;
            }
            if (mailUser != null) {
                buttonOneDayForward.setEnabled(false);
                buttonOneDayBack.setEnabled(false);
                datePicker.setEnabled(false);
            }
            buildMealPlan(datePicker.getValue().plusDays(1), mensaComboBox.getValue());
        });

        buttonsDatePickerLayout.add(buttonOneDayBack, datePicker, buttonOneDayForward);

        headerComboboxDatePickerButtonsLayout.add(mensaComboBox, buttonsDatePickerLayout);

        // adjust the width of the combobox and datePicker
        mensaComboBox.setWidth(60f, Unit.PERCENTAGE);
        mensaComboBox.setMinWidth(320f, Unit.PIXELS);
        datePicker.setWidth(40f, Unit.PERCENTAGE);
        datePicker.setMinWidth(220, Unit.PIXELS);

        VerticalLayout headerContent = new VerticalLayout();
        headerContent.setWidth(100f, Unit.PERCENTAGE);
        headerContent.setAlignItems(Alignment.CENTER);
        headerContent.setJustifyContentMode(JustifyContentMode.CENTER);
        headerContent.getStyle().set("text-align", "center");
        headerContent.add(new H2("MensaHub-Speiseplan"));
        headerContent.add(headerComboboxDatePickerButtonsLayout);

        pageSelHeader.add(headerContent);

        row.setJustifyContentMode(JustifyContentMode.CENTER);

        mensaComboBox.addValueChangeListener(changeEvent -> {
            if (!changeEvent.isFromClient()) return;
            if (changeEvent.getValue() == null) {
                return;
            }
            buildMealPlan(datePicker.getValue(), changeEvent.getValue());
        });

        datePicker.addValueChangeListener(changeEvent -> {
            if (!changeEvent.isFromClient()) return;
            if (changeEvent.getValue() == null || mensaComboBox.isEmpty()) {
                return;
            }
            if (mailUser != null) {
                datePicker.setEnabled(false);
            }
            buildMealPlan(changeEvent.getValue(), mensaComboBox.getValue());
        });

        add(pageSelHeader);
        // adjust the layout to center all elements in it
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    /**
     * Creates a share button with the share API
     *
     * @return the share button
     */
    private static Button getShareButton() {
        Button shareButton = new Button(VaadinIcon.SHARE.create());
        shareButton.setTooltipText("Teilen");
        shareButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        shareButton.addClickListener(event -> UI.getCurrent().getPage().executeJs(
                "const url = new URL(window.location.href);" +
                        "url.searchParams.delete('userCode');" + // Entfernt den userCode-Parameter aus der URL
                        "const shareData = { " +
                        "  title: document.title, " +
                        "  text: 'Schau dir den Speiseplan an', " +
                        "  url: url.toString() " + // Aktualisierte URL ohne userCode
                        "}; " +
                        "navigator.share(shareData)" +
                        "  .then(() => { " +
                        "    console.log('Seite hat Daten geteilt'); " +
                        "  })" +
                        "  .catch(err => { " +
                        "    console.log('Error: ' + err); " +
                        "  });"
        ));
        return shareButton;
    }

    /**
     * Is accessed by the datepicker and mensaCombox and should build the view with the given parameters
     *
     * @param servingDate the serving date of the meals
     * @param mensa       the mensa where the food is served
     */
    public void buildMealPlan(LocalDate servingDate, Mensa mensa) {
        row.removeAll();
        List<Meal> meals = mealsService.findAllMealsByServingDateAndMensa(servingDate, mensa);

        row.addClassName("meal-content");
        row.add(new InfoBox(mensaComboBox.getValue().getName(),
                mensaComboBox.getValue().getApiUrl().replace("$date", servingDate.toString())));
        row.addClassName("meal-row");
        row.setWidthFull();
        row.getStyle().set("flex-wrap", "wrap");

        try {
            updateRows(meals);
        } catch (IOException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        datePicker.setValue(servingDate);

        if (mailUser == null) {
            UI.getCurrent().getPage().getHistory().replaceState(null, String.format("/mealPlan?date=%s" +
                    String.format("&mensa=%s", mensaComboBox.getValue().getId()), servingDate));
        } else {
            UI.getCurrent().getPage().getHistory().replaceState(null, String.format("/mealPlan?date=%s" +
                    String.format("&mensa=%s", mensaComboBox.getValue().getId()) + String.format("&userCode=%s",
                    mailUser.getDeactivationCode().getCode()), servingDate));
        }
        add(row);
    }

    private void updateRows(List<Meal> meals) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        List<MealBox> mealBoxes = new ArrayList<>();
        for (Meal meal : meals) {
            MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory(), meal.getId().intValue());
            if (!isUserIdentified()) {
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
                        ratingRepository.save(rating);
                        mealBox.getRatingButton().setEnabled(false);
                        mealBox.getRatingComponent().setEnabled(false);
                        NotificationFactory.create(NotificationType.SUCCESS, "Deine Bewertung wurde gespeichert. " +
                                "Es kann einige Zeit dauern, bis deine Bewertung in die Empfehlungen einbezogen wird. Schau später nochmal vorbei...").open();
                    }
                });
            }
            row.add(mealBox);
            mealBoxes.add(mealBox);
        }

        UI currentUi = UI.getCurrent();
        // Asynchrone API-Anfragen starten
        CompletableFuture<Void> future = mealPlanService.addRecommendationScoreAsync(mealBoxes, mailUser, currentUi, buttonOneDayBack, buttonOneDayForward, datePicker);

        if (meals.isEmpty()) {
            buttonOneDayBack.setEnabled(true);
            buttonOneDayForward.setEnabled(true);
            datePicker.setEnabled(true);
        }

        future.thenAccept(voidResult -> {
        }).exceptionally(ex -> {
            log.error("Error during async recommendation score fetching", ex);
            return null;
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Map<String, List<String>> params = beforeEnterEvent.getLocation().getQueryParameters().getParameters();

        String mensaParam;
        String date = "";
        String userCode;

        // Prüfen, ob der Cookie bereits vorhanden ist
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        Cookie userCodeCookie = null;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("userCode".equals(cookie.getName())) {
                    userCodeCookie = cookie;
                    break;
                }
            }
        }

        if (params.containsKey("userCode")) {
            userCode = params.get("userCode").get(0);

            if (userCodeCookie != null) {
                // Der Cookie ist vorhanden, jetzt vergleichen wir den Wert
                if (userCode.equals(userCodeCookie.getValue())) {
                    Result<MailUser, JPAError> mailUserByCookieResult = mailUserService.findMailUserByDeactivationCode(userCodeCookie.getValue());
                    if (mailUserByCookieResult.isSuccess()) {
                        mailUser = mailUserByCookieResult.getData();
                        log.info("User identified by cookie");
                    } else {
                        log.error("Cookie is corrupt or manipulated");
                    }
                } else {
                    // Der Cookie-Wert unterscheidet sich, Cookie aktualisieren
                    Result<MailUser, JPAError> mailUserJPAErrorResult = mailUserService.findMailUserByDeactivationCode(userCode);

                    if (mailUserJPAErrorResult.isSuccess()) {
                        mailUser = mailUserJPAErrorResult.getData();
                        userCodeCookie.setValue(userCode);
                        userCodeCookie.setPath("/");
                        userCodeCookie.setMaxAge(2147483647);
                        userCodeCookie.setSecure(true);
                        userCodeCookie.setHttpOnly(true);
                        VaadinService.getCurrentResponse().addCookie(userCodeCookie);
                        log.info("User identified by deactivation code, cookie is renewed");
                    } else {
                        log.info("Error identify user by deactivation code");
                        NotificationFactory.create(NotificationType.ERROR, "Ungültige Nutzerkennung. Erweiterte Funktionen stehen nicht zur Verfügung");
                    }
                }
            } else {
                // Wenn der Cookie nicht existiert, führen wir die DB-Abfrage durch und setzen den Cookie
                Result<MailUser, JPAError> mailUserJPAErrorResult = mailUserService.findMailUserByDeactivationCode(userCode);

                if (mailUserJPAErrorResult.isSuccess()) {
                    mailUser = mailUserJPAErrorResult.getData();

                    // Setze den Cookie
                    Cookie newUserCodeCookie = new Cookie("userCode", userCode);
                    newUserCodeCookie.setPath("/");
                    newUserCodeCookie.setMaxAge(2147483647);
                    newUserCodeCookie.setSecure(true);
                    newUserCodeCookie.setHttpOnly(true);
                    VaadinService.getCurrentResponse().addCookie(newUserCodeCookie);
                } else {
                    log.error("Cannot identify user via url parameter {}", mailUser);
                    NotificationFactory.create(NotificationType.ERROR, "Ungültige Nutzerkennung. Erweiterte Funktionen stehen nicht zur Verfügung").open();
                }
            }
        } else {
            if (userCodeCookie != null) {
                Result<MailUser, JPAError> mailUserByCookieResult = mailUserService.findMailUserByDeactivationCode(userCodeCookie.getValue());
                if (mailUserByCookieResult.isSuccess()) {
                    mailUser = mailUserByCookieResult.getData();
                    log.info("User identified by cookie");
                } else {
                    log.error("Cookie is corrupt or manipulated");
                }
            }
        }
        HorizontalLayout buttonShareLayout = new HorizontalLayout();
        buttonShareLayout.setAlignItems(Alignment.CENTER);
        buttonShareLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        headerComboboxDatePickerButtonsLayout.add(buttonShareLayout);

        if (mailUser != null) {
            Button redirectToMensaHubSettings = new Button(VaadinIcon.CHEVRON_CIRCLE_RIGHT.create());
            redirectToMensaHubSettings.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            redirectToMensaHubSettings.setText("MensaHub-Einstellungen");
            redirectToMensaHubSettings.setTooltipText("Du wirst zu deinen MensaHub-Einstellungen weitergeleitet");
            redirectToMensaHubSettings.addClickListener(buttonClickEvent -> {
                UI.getCurrent().navigate(MailSettingsView.class);
            });
            buttonShareLayout.add(redirectToMensaHubSettings);
        }
        Button shareButton = getShareButton();
        buttonShareLayout.add(shareButton);

        AtomicBoolean applePWAInfoNotificationShown = new AtomicBoolean(false);
        UI.getCurrent().getPage().executeJs("return !!window.localStorage.getItem('applePWAInfoNotificationShown');")
                .then(jsonValue -> {
                    applePWAInfoNotificationShown.set(jsonValue.asBoolean());
                });

        AppleDeviceUserCodeDialog appleDeviceUserCodeDialog = new AppleDeviceUserCodeDialog();
        Cookie finalUserCodeCookie = userCodeCookie;
        UI.getCurrent().getPage().executeJs(
                "return window.matchMedia('(display-mode: standalone)').matches || window.navigator.standalone === true;" // check if pwa
        ).then(Boolean.class, isPWA -> {
            if (Boolean.TRUE.equals(isPWA) && !appleDeviceUserCodeDialog.isOpened() && finalUserCodeCookie == null && !applePWAInfoNotificationShown.get() && AppleDeviceUserCodeDialog.isAppleDevice()) { // check if pwa, info not shown and device is apple bloated
                appleDeviceUserCodeDialog.open();
            }
        });
        UI.getCurrent().getPage().executeJs("window.localStorage.setItem('applePWAInfoNotificationShown', 'true');");
        UI.getCurrent().getPage().executeJs("return !!window.localStorage.getItem('infoNotificationShown');")
                .then(jsonValue -> {
                    boolean isShown = jsonValue.asBoolean();
                    if (!isShown && !appleDeviceUserCodeDialog.isOpened()) {
                        InfoWithAnchorNotification infoNotification = new InfoWithAnchorNotification(
                                "Mit einem MensaHub-Account kannst das Essen zusätzlich bewerten und dir Empfehlungen berechnen lassen -> ",
                                "Zur Anmeldung", NewsletterView.class);

                        infoNotification.getCloseButton().addClickListener(event -> {
                            infoNotification.close();
                        });
                        infoNotification.addThemeVariants(NotificationVariant.LUMO_WARNING);

                        infoNotification.open();
                    }
                });
        UI.getCurrent().getPage().executeJs("window.localStorage.setItem('infoNotificationShown', 'true');");

        if (params.containsKey("mensa")) {
            mensaParam = params.get("mensa").get(0);
        } else {
            return;
        }

        if (params.containsKey("date")) {
            date = params.get("date").get(0);
        }

        Optional<Mensa> mensa;
        // check if mensaParam is an integer
        try {
            mensa = Optional.of(mensaService.mensaById(Long.parseLong(mensaParam)));
        } catch (NumberFormatException numberFormatException) {
            mensa = mensaService.findAll().stream().filter(mensa1 -> mensa1.getName().equals(mensaParam)).findAny();
        }

        Optional<Mensa> finalMensa = mensa;
        mensa.ifPresent(value -> mensaComboBox.setValue(finalMensa.get()));

        if (!date.isEmpty()) {
            if (date.equals("today")) {
                datePicker.setValue(LocalDate.now());
            } else {
                datePicker.setValue(LocalDate.parse(date));
            }
        }

        if (beforeEnterEvent.getTrigger().name().equals("PAGE_LOAD")) { // returns "PAGE_LOAD" if its refreshed and "HISTORY" if its refreshed by the valuechangelisteners
            if (!mensaComboBox.isEmpty() && !datePicker.isEmpty()) {
                buildMealPlan(datePicker.getValue(), mensaComboBox.getValue());
            }
        }

    }

    private boolean isUserIdentified() {
        return mailUser != null;
    }
}