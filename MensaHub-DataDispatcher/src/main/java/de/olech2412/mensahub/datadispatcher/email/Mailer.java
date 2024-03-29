package de.olech2412.mensahub.datadispatcher.email;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.data.tools.AllergeneComparator;
import de.olech2412.mensahub.models.Leipzig.Allergene;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import lombok.extern.log4j.Log4j2;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Log4j2
public class Mailer {

    private static final String notAvailableSign;

    static {
        try {
            notAvailableSign = Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign");
        } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends an email to the given email address with the current menu
     *
     * @throws MessagingException
     */
    public void sendSpeiseplan(MailUser emailTarget, List<? extends Meal> menu, Mensa mensa, List<Allergene> allergenes, boolean update) throws MessagingException, IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        String deactivateUrl = Config.getInstance().getProperty("mensaHub.dataDispatcher.junction.address") + "/deactivate?code=" + emailTarget.getDeactivationCode().getCode();
        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.senderMail")));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget.getEmail()));

        String msg = "";
        if (!update) {
            msg = createEmail(menu, emailTarget.getFirstname(), deactivateUrl, mensa, allergenes);
            message.setSubject("Speiseplan " +
                    LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                    mensa.getName());
        } else {
            msg = createUpdateEmail(menu, emailTarget.getFirstname(), deactivateUrl, mensa, allergenes);
            message.setSubject("Update zu deinem Speiseplan " +
                    LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                    mensa.getName());
        }

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);
        Transport.send(message);
        log.debug("Email sent to " + emailTarget.getEmail());

    }

    private String createUpdateEmail(List<? extends Meal> menu, String firstname, String deactivateUrl, Mensa mensa, List<Allergene> allergenes) throws IOException {
        StringBuilder menuText = new StringBuilder();

        if (!menu.isEmpty()) {

            List<List<Meal>> sublists = new ArrayList<>();
            for (int i = 0; i <= menu.size(); i++) {
                i = 0;
                Meal meal = menu.get(i);
                List<Meal> sublist = new ArrayList<>();
                for (int x = 0; x < menu.size(); x++) {
                    if (meal.getCategory().equals(menu.get(x).getCategory())) {
                        sublist.add(menu.get(x));
                    }
                }
                for (Meal meal2 : sublist) {
                    menu.remove(meal2);
                }
                sublists.add(sublist);
            }

            for (List<Meal> meals : sublists) {

                String menuString = StaticEmailText.FOOD_TEXT;
                String categoryString = StaticEmailText.FOOD_CATEGORY;
                if (meals.size() == 1) {
                    categoryString = createCategoryString(meals, categoryString);
                    menuString = menuString.replaceFirst("%s", meals.get(0).getName() +
                            " (" + meals.get(0).getDescription() + ")" + " - " + meals.get(0).getPrice());
                    if (meals.get(0).getAllergens().equals(notAvailableSign) && meals.get(0).getAdditives().equals(notAvailableSign)) {
                        menuString = menuString.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                    } else {
                        if (meals.get(0).getAllergens().equals(notAvailableSign) && !meals.get(0).getAdditives().isEmpty())
                            menuString = menuString.replaceFirst("%s", "Keine Allergene - Zusatzstoffe: " + meals.get(0).getAdditives());
                        else if (meals.get(0).getAdditives().equals(notAvailableSign))
                            menuString = menuString.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Keine Zusatzstoffe");
                        else
                            menuString = menuString.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Zusatzstoffe: " + meals.get(0).getAdditives());
                    }

                } else {
                    categoryString = createCategoryString(meals, categoryString);
                    StringBuilder mealBuilder = new StringBuilder();
                    for (Meal meal : meals) {
                        String groupMeal = menuString.replaceFirst("%s", meal.getName() +
                                " (" + meal.getDescription() + ")" + " - " + meal.getPrice());
                        if (meals.get(0).getAllergens().equals(notAvailableSign) && meals.get(0).getAdditives().equals(notAvailableSign)) {
                            groupMeal = groupMeal.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                        } else {
                            if (meals.get(0).getAllergens().equals(notAvailableSign) && !meals.get(0).getAdditives().isEmpty())
                                groupMeal = groupMeal.replaceFirst("%s", "Keine Allergene - Zusatzstoffe: " + meals.get(0).getAdditives());
                            else if (meals.get(0).getAdditives().equals(notAvailableSign))
                                groupMeal = groupMeal.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Keine Zusatzstoffe");
                            else
                                groupMeal = groupMeal.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Zusatzstoffe: " + meals.get(0).getAdditives());
                        }
                        mealBuilder.append(groupMeal + "\n");
                    }
                    menuString = mealBuilder.toString();
                }

                menuText.append(categoryString + menuString);
            }
        } else {
            log.warn("No meals found for Mensa: " + mensa.getName() + " --> send empty mail to: " + firstname);
            menuText.append(StaticEmailText.FOOD_TEXT.replaceFirst("%s", "Wir haben für deine Mensa " +
                    "heute leider keine Gerichte für dich gefunden :("));
            menuText.append(StaticEmailText.FOOD_TEXT.replaceFirst("%s", " "));
        }

        String header = StaticEmailText.FOOD_PLAN_TEXT;
        header = header.replaceFirst("%s", "Update zu deinem Speiseplan " +
                LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                mensa.getName());
        header = header.replaceFirst("%s", "Eine Änderung wurde für deine Mensa " + mensa.getName() + " gefunden.");
        header = header.replaceFirst("%s", getRandomFunnyWelcomeText());
        header = header.replaceFirst("%s", firstname);
        header = header.replaceFirst("%s", "wir haben eine Änderung des Speiseplans festgestellt. Dabei kann es sich um diverse größere oder kleinere Änderungen handeln." + "\n" +
                "Diese E-Mail wurde automatisch um " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + " Uhr erstellt.");

        String footer = StaticEmailText.FOOD_PLAN_FOOTER;
        footer = footer.replaceFirst("%s", getRandomGreetingsText());
        footer = footer.replaceFirst("%s", deactivateUrl);

        StringBuilder allergeneText = new StringBuilder();
        allergeneText.append("Allergene und Zusatzstoffe: ");
        List<Allergene> allergene = new ArrayList<>();
        allergenes.forEach(allergene::add);
        int count = 0;
        for (Allergene allergene1 : allergene.stream().sorted(new AllergeneComparator()).collect(Collectors.toList())) {
            allergeneText.append(allergene1.getToken()).append(": ").append(allergene1.getAllergen());
            if (count < allergene.size() - 1) {
                allergeneText.append(", ");
            }
            count++;
        }
        footer = footer.replaceFirst("%s", allergeneText.toString());


        String msg = header +
                menuText +
                footer;

        return msg;
    }

    private String createEmail(List<? extends Meal> menu, String firstName, String deactivateUrl, Mensa mensa, List<Allergene> allergenes) throws IOException {
        StringBuilder menuText = new StringBuilder();

        if (!menu.isEmpty()) {

            List<List<Meal>> sublists = new ArrayList<>();
            for (int i = 0; i <= menu.size(); i++) {
                i = 0;
                Meal meal = menu.get(i);
                List<Meal> sublist = new ArrayList<>();
                for (int x = 0; x < menu.size(); x++) {
                    if (meal.getCategory().equals(menu.get(x).getCategory())) {
                        sublist.add(menu.get(x));
                    }
                }
                for (Meal meal2 : sublist) {
                    menu.remove(meal2);
                }
                sublists.add(sublist);
            }

            for (List<Meal> meals : sublists) {

                String menuString = StaticEmailText.FOOD_TEXT;
                String categoryString = StaticEmailText.FOOD_CATEGORY;
                if (meals.size() == 1) {
                    categoryString = createCategoryString(meals, categoryString);
                    menuString = menuString.replaceFirst("%s", meals.get(0).getName() +
                            " (" + meals.get(0).getDescription() + ")" + " - " + meals.get(0).getPrice());
                    if (meals.get(0).getAllergens().equals(notAvailableSign) && meals.get(0).getAdditives().equals(notAvailableSign)) {
                        menuString = menuString.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                    } else {
                        if (meals.get(0).getAllergens().equals(notAvailableSign) && !meals.get(0).getAdditives().isEmpty())
                            menuString = menuString.replaceFirst("%s", "Keine Allergene - Zusatzstoffe: " + meals.get(0).getAdditives());
                        else if (meals.get(0).getAdditives().equals(notAvailableSign))
                            menuString = menuString.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Keine Zusatzstoffe");
                        else
                            menuString = menuString.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Zusatzstoffe: " + meals.get(0).getAdditives());
                    }

                } else {
                    categoryString = createCategoryString(meals, categoryString);
                    StringBuilder mealBuilder = new StringBuilder();
                    for (Meal meal : meals) {
                        String groupMeal = menuString.replaceFirst("%s", meal.getName() +
                                " (" + meal.getDescription() + ")" + " - " + meal.getPrice());
                        if (meals.get(0).getAllergens().equals(notAvailableSign) && meals.get(0).getAdditives().equals(notAvailableSign)) {
                            groupMeal = groupMeal.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                        } else {
                            if (meals.get(0).getAllergens().equals(notAvailableSign) && !meals.get(0).getAdditives().isEmpty())
                                groupMeal = groupMeal.replaceFirst("%s", "Keine Allergene - Zusatzstoffe: " + meals.get(0).getAdditives());
                            else if (meals.get(0).getAdditives().equals(notAvailableSign))
                                groupMeal = groupMeal.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Keine Zusatzstoffe");
                            else
                                groupMeal = groupMeal.replaceFirst("%s", "Allergene: " + meals.get(0).getAllergens() + " - Zusatzstoffe: " + meals.get(0).getAdditives());
                        }
                        mealBuilder.append(groupMeal + "\n");
                    }
                    menuString = mealBuilder.toString();
                }

                menuText.append(categoryString + menuString);
            }
        } else {
            log.warn("No meals found for Mensa: " + mensa.getName() + " --> send empty mail to: " + firstName);
            menuText.append(StaticEmailText.FOOD_TEXT.replaceFirst("%s", "Wir haben für deine Mensa " +
                    "heute leider keine Gerichte für dich gefunden :("));
            menuText.append(StaticEmailText.FOOD_TEXT.replaceFirst("%s", " "));
        }

        String header = StaticEmailText.FOOD_PLAN_TEXT;
        header = header.replaceFirst("%s", "Speiseplan " +
                LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                mensa.getName());
        header = header.replaceFirst("%s", getRandomFunnyText());
        header = header.replaceFirst("%s", getRandomFunnyWelcomeText());
        header = header.replaceFirst("%s", firstName);
        header = header.replaceFirst("%s", "nachfolgend findest du den Speiseplan für heute.");

        String footer = StaticEmailText.FOOD_PLAN_FOOTER;
        footer = footer.replaceFirst("%s", getRandomGreetingsText());
        footer = footer.replaceFirst("%s", deactivateUrl);

        StringBuilder allergeneText = new StringBuilder();
        allergeneText.append("Allergene und Zusatzstoffe: ");
        List<Allergene> allergene = new ArrayList<>();
        allergenes.forEach(allergene::add);
        int count = 0;
        for (Allergene allergene1 : allergene.stream().sorted(new AllergeneComparator()).collect(Collectors.toList())) {
            allergeneText.append(allergene1.getToken()).append(": ").append(allergene1.getAllergen());
            if (count < allergene.size() - 1) {
                allergeneText.append(", ");
            }
            count++;
        }
        footer = footer.replaceFirst("%s", allergeneText.toString());


        String msg = header +
                menuText +
                footer;

        return msg;
    }

    private String createCategoryString(List<Meal> meals, String categoryString) {
        if (meals.get(0).getCategory().contains("Fleisch")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD69");
        } else if (meals.get(0).getCategory().contains("Fisch")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDDA3");
        } else if (meals.get(0).getCategory().contains("Vegetarisch") || meals.get(0).getCategory().contains("Veggie")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD66");
        } else if (meals.get(0).getCategory().contains("Vegan")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD5F");
        } else if (meals.get(0).getCategory().contains("Beilage")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDDC1");
        } else if (meals.get(0).getCategory().contains("Suppe")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF73");
        } else if (meals.get(0).getCategory().contains("Dessert")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF6B");
        } else if (meals.get(0).getCategory().contains("Salat")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD57");
        } else if (meals.get(0).getCategory().contains("Getränk")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF7A");
        } else if (meals.get(0).getCategory().contains("Snack")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF6A");
        } else if (meals.get(0).getCategory().contains("Menü")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF5F");
        } else if (meals.get(0).getCategory().contains("Pizza")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF55");
        } else if (meals.get(0).getCategory().contains("Klima")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF0D");
        } else if (meals.get(0).getCategory().contains("Bio")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF3E");
        } else if (meals.get(0).getCategory().contains("Regional")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDFE0");
        } else if (meals.get(0).getCategory().contains("Fairtrade")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF10");
        } else if (meals.get(0).getCategory().contains("Geflügel")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83D\uDC13");
        } else if (meals.get(0).getCategory().contains("Schwein")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83D\uDC16");
        } else if (meals.get(0).getCategory().contains("Rind")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83D\uDC2E");
        } else if (meals.get(0).getCategory().contains("Lamm")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83D\uDC0F");
        } else if (meals.get(0).getCategory().contains("Wild")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83D\uDC10");
        } else if (meals.get(0).getCategory().contains("Ei")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD5A");
        } else if (meals.get(0).getCategory().contains("Pasta")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF5D");
        } else if (meals.get(0).getCategory().contains("WOK")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF72");
        } else if (meals.get(0).getCategory().contains("Smoothie")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD64");
        } else if (meals.get(0).getCategory().contains("Burger")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF54");
        } else if (meals.get(0).getCategory().contains("Schnitzel")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF56");
        } else if (meals.get(0).getCategory().contains("Kartoffel")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83E\uDD54");
        } else if (meals.get(0).getCategory().contains("Pommes")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF5F");
        } else if (meals.get(0).getCategory().contains("Nudeln")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF5D");
        } else if (meals.get(0).getCategory().contains("Reis")) {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory() + " \uD83C\uDF5A");
        } else {
            categoryString = categoryString.replaceFirst("%s", meals.get(0).getCategory());
        }
        return categoryString;
    }

    private String getRandomFunnyWelcomeText() {
        List<String> welcome = new ArrayList<>();
        welcome.add("Hallo");
        welcome.add("Servus");
        welcome.add("Moin");
        welcome.add("Hi");
        welcome.add("Guten Tag");
        welcome.add("Guten Morgen");
        welcome.add("Ahoi");
        welcome.add("Grüß Gott");
        welcome.add("Grüß di");
        welcome.add("Aloha");
        welcome.add("Alles cool im Pool");
        welcome.add("Alles fit im Schritt");
        welcome.add("Alles klar in Kanada");
        welcome.add("Alles Roger in Kambodscha");
        welcome.add("Buenas Tardes");
        welcome.add("Gib Flosse");
        welcome.add("Grüetzi");
        welcome.add("Grüßli Müsli");
        welcome.add("Hallihallohallöle");
        welcome.add("Hallöchen");
        welcome.add("Hallöchen Popöchen");
        welcome.add("Hallöle");
        welcome.add("Heroin-spaziert");
        welcome.add("Hola");
        welcome.add("Holla die Waldfee");
        welcome.add("Howdy");
        welcome.add("Huhu");
        welcome.add("Huhu wie gehts");
        welcome.add("Juten Tach");
        welcome.add("Juten Tag");
        welcome.add("Namasté");
        welcome.add("Whazuuuuuuuup");
        welcome.add("Yo Moinsen");
        welcome.add("Yo");
        welcome.add("Mohoin");

        return welcome.get((int) (Math.random() * welcome.size()));
    }

    private String getRandomGreetingsText() {
        List<String> greetings = new ArrayList<>();
        greetings.add("Viele Grüße, ");
        greetings.add("Liebe Grüße, ");
        greetings.add("Mit freundlichen Grüßen, ");
        greetings.add("Mit besten Grüßen, ");
        greetings.add("Tschüssikowski, ");
        greetings.add("Bis bald, ");
        greetings.add("Bis dann, ");
        greetings.add("Auf Wiederhörnchen, ");
        greetings.add("Bis zum nächsten Mal, ");
        greetings.add("Tschausen, ");
        greetings.add("Bis baldrian, ");
        greetings.add("Tschö mit ö, ");
        greetings.add("Mach’s gut, Knut, ");
        greetings.add("Bis baldo, Ronaldo, ");
        greetings.add("Paris – Athen, Auf Wiedersehn, ");
        greetings.add("See you later, alligator!, ");
        greetings.add("Good Bye, Hawaii, ");
        greetings.add("In diesem Sinne: Ab in die Rinne, ");
        greetings.add("Ende im Gelände, ");
        greetings.add("So sieht’s aus im Schneckenhaus, ");
        greetings.add("Finger in Po – Mexiko, ");
        greetings.add("Jetzt geht’s los, Spätzle mit Soß, ");
        greetings.add("In a while, crocodile, ");
        greetings.add("Auf Wiedersehen, ");
        greetings.add("Bis denne, ");
        greetings.add("Bis denne Antenne, ");
        greetings.add("Au revoir, ");
        greetings.add("Arrivederci, ");
        greetings.add("До свидания, ");
        greetings.add("안녕히 가세요, ");
        greetings.add("再见, ");
        greetings.add("Ciao, ");
        greetings.add("Ciao Kakao, ");

        return greetings.get((int) (Math.random() * greetings.size()));
    }

    private String getRandomFunnyText() {
        List<String> funnyTexts = new ArrayList<>();
        funnyTexts.add("Guten Appetit!");
        funnyTexts.add("Guten Hunger!");
        funnyTexts.add("Guten Appetit und guten Hunger!");
        funnyTexts.add("Mahlzeit!");
        funnyTexts.add("Wir sind hier nicht bei “wünsch dir was”, sondern bei ” so isses”!");
        funnyTexts.add("Guten Appetit und guten Hunger! Und wenn du nicht satt bist, dann iss nochmal!");
        funnyTexts.add("Satz mit x, das war wohl nichts!");
        funnyTexts.add("Ich denke, ich werde meine Mittagspause heute zum Mittagessen benutzen.");
        funnyTexts.add("Ich denke, ich werde meine Mittagspause heute für ein köstliches Mittagessen opfern.");
        funnyTexts.add("Ein gutes Mittagessen ist das Geheimnis, um den Uni-Tag durchzustehen.");
        funnyTexts.add("Ohne Mittagessen wäre der Uni-Tag nur halb so erträglich.");
        funnyTexts.add("Ohne Mittagessen wäre der Uni-Tag nur halb so erträglich.");
        funnyTexts.add("Ich denke nur an mein Mittagessen, wenn ich in der Vorlesung sitze.");
        funnyTexts.add("Ich würde für ein gutes Mittagessen jede Vorlesung besuchen.");
        funnyTexts.add("Ich denke nur ans Mittagessen, wenn ich in einer langweiligen Vorlesung sitze.");
        funnyTexts.add("Ohne Mittagessen wäre die Uni nur ein Ort voller Trägheit und Müdigkeit.");
        funnyTexts.add("Nimm lieber Salz und Pfeffer mit.");
        funnyTexts.add("Mittagessen ist wie ein Mini-Urlaub von der Uni.");

        return funnyTexts.get((int) (Math.random() * funnyTexts.size()));
    }
}
