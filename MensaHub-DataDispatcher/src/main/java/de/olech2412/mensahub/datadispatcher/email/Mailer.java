package de.olech2412.mensahub.datadispatcher.email;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.mail.MailError;
import de.olech2412.mensahub.models.result.errors.mail.MailErrors;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.log4j.Log4j2;
import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

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

    private static PrivateKey loadPrivateKey(String filename) throws Exception {
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuilder keyBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains("BEGIN") || line.contains("END")) {
                continue;
            }
            keyBuilder.append(line.trim());
        }
        reader.close();

        byte[] keyBytes = Base64.getDecoder().decode(keyBuilder.toString());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Sends an email to the given email address with the current menu
     */
    public Result<MailUser, MailError> sendSpeiseplan(MailUser emailTarget, List<Meal> menu, Mensa mensa, boolean update) {
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", Boolean.parseBoolean(Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.smtpAuth")));
            prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.smtpHost"));
            prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.smtpPort"));
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.ssl.enable", "true");

            String senderMail = Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.sender");
            String senderPassword = Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.sender.password");


            Session mailSession = Session.getDefaultInstance(prop, new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderMail, senderPassword);
                }
            });


            String deactivateUrl = Config.getInstance().getProperty("mensaHub.dataDispatcher.junction.address") + "/deactivate?code=" + emailTarget.getDeactivationCode().getCode();
            MimeMessage message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderMail));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(emailTarget.getEmail()));

            String msg = "";
            if (!update) {
                msg = createEmail(menu, emailTarget.getFirstname(), deactivateUrl, mensa);
                message.setSubject("Speiseplan " +
                        LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                        mensa.getName());
            } else {
                msg = createUpdateEmail(menu, emailTarget.getFirstname(), deactivateUrl, mensa);
                message.setSubject("Update zu deinem Speiseplan " +
                        LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                        mensa.getName());
            }

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            // Lade den privaten Schlüssel
            PrivateKey privateKey = loadPrivateKey(Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.dkim_priv_path"));

            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) privateKey;

            // Erstelle den DKIM-Signer
            DkimSigner signer = new DkimSigner(Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.dkim_signing_domain"),
                    Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.dkim_seperator"), rsaPrivateKey);

            // Signiere die Nachricht mit DKIM
            DkimMessage dkimMessage = new DkimMessage(message, signer);

            Transport.send(dkimMessage);
            return Result.success(emailTarget);
        } catch (Exception exception) {
            log.error("Error while sending email for user {}", emailTarget.getEmail(), exception);
            return Result.error(new MailError("Error while sending email for user " + emailTarget.getEmail() + " with" +
                    " error: " + exception.getMessage(), MailErrors.UNKNOWN));
        }
    }

    private String createUpdateEmail(List<Meal> menu, String firstname, String deactivateUrl, Mensa mensa) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        StringBuilder menuText = new StringBuilder();

        if (!menu.isEmpty()) {

            List<List<Meal>> sublists = new ArrayList<>();
            for (int i = 0; i <= menu.size(); i++) {
                i = 0;
                Meal meal = menu.get(i);
                List<Meal> sublist = new ArrayList<>();
                for (Meal value : menu) {
                    if (meal.getCategory().equals(value.getCategory())) {
                        sublist.add(value);
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
                    if (meals.get(0).getAllergens().equals(notAvailableSign)) {
                        menuString = menuString.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                    } else {
                        menuString = menuString.replaceFirst("%s", meals.get(0).getAllergens());
                    }

                } else {
                    categoryString = createCategoryString(meals, categoryString);
                    StringBuilder mealBuilder = new StringBuilder();
                    for (Meal meal : meals) {
                        String groupMeal = menuString.replaceFirst("%s", meal.getName() +
                                " (" + meal.getDescription() + ")" + " - " + meal.getPrice());
                        if (meals.get(0).getAllergens().equals(notAvailableSign)) {
                            groupMeal = groupMeal.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                        } else {
                            groupMeal = groupMeal.replaceFirst("%s", meals.get(0).getAllergens());
                        }
                        mealBuilder.append(groupMeal).append("\n");
                    }
                    menuString = mealBuilder.toString();
                }

                menuText.append(categoryString).append(menuString);
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
        footer = footer.replaceFirst("%s", Config.getInstance().getProperty("mensaHub.dataDispatcher.junction.address") + "/mealPlan?date=today&mensa=" + mensa.getId());


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
        welcome.add("Na was geht");
        welcome.add("Grüß Gottle");
        welcome.add("Meddl");
        welcome.add("Sei gegrüßt");
        welcome.add("Wie gehts");
        welcome.add("Was geht ab");
        welcome.add("Schön dich wiederzusehen");
        welcome.add("Ich glaube mein Schwein pfeift");
        welcome.add("Wow siehst du heute gut aus");
        welcome.add("Das gibts ja nicht, der einzig wahre");
        welcome.add("Das gibts ja nicht, der aller echte Hase");
        welcome.add("\uD83C\uDFB6 So wie Photoshop, so wie, so wie...\uD83C\uDFB6 oh ... sry");
        welcome.add("\uD83C\uDFB6 Vöööllig losgelöst von der EEERDE...\uD83C\uDFB6 oh ... sry");
        welcome.add("\uD83C\uDFB6 Mit 66 Jahren da fängt das Leben an...\uD83C\uDFB6 oh ... sry");
        welcome.add("\uD83C\uDFB6 Ein Rudi Völler, es gibt nur ein... \uD83C\uDFB6 ups ... sry");
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
        greetings.add("Servus, ");
        greetings.add("Adios, ");
        greetings.add("Hasta luego, ");
        greetings.add("Hasta la vista, ");
        greetings.add("Leb wohl, ");
        greetings.add("Take care, ");
        greetings.add("Farewell, ");
        greetings.add("Mach’s gut, ");
        greetings.add("Hau rein, ");
        greetings.add("Bis die Tage, ");
        greetings.add("Cheerio, ");
        greetings.add("Peace out, ");
        greetings.add("Alles Gute, ");
        greetings.add("Bleib gesund, ");
        greetings.add("Schönen Tag noch, ");
        greetings.add("Bis danni, ");
        greetings.add("Machs jut, ");
        greetings.add("Pfiat di, ");
        greetings.add("Schönen Feierabend, ");
        greetings.add("Guten Rutsch, ");
        greetings.add("Bis morgen, ");
        greetings.add("Bis nächste Woche, ");
        greetings.add("Man sieht sich, ");
        greetings.add("Bis demnächst, ");
        greetings.add("Adieu, ");
        greetings.add("Bye bye, ");
        greetings.add("Bis gleich, ");
        greetings.add("Viel Erfolg, ");
        greetings.add("Gute Reise, ");
        greetings.add("Haut rein, ");
        greetings.add("Bis zur nächsten Runde, ");
        greetings.add("Bis später, ");
        greetings.add("Bis in Kürze, ");
        greetings.add("Bis nachher, ");
        greetings.add("Gute Nacht, ");
        greetings.add("Schlaf gut, ");
        greetings.add("Schönes Wochenende, ");
        greetings.add("Bis Montag, ");
        greetings.add("Bis Freitag, ");
        greetings.add("Bis Dienstag, ");
        greetings.add("Gute Fahrt, ");
        greetings.add("Schöne Ferien, ");
        greetings.add("Schöne Feiertage, ");
        greetings.add("Bis bald im Wald, ");
        greetings.add("Bis die Tage, ");
        greetings.add("Tschüssikowski, ");
        greetings.add("Mach’s gut, bis bald, ");
        greetings.add("Auf Wiederhören, ");
        greetings.add("Bis nächstes Mal, ");
        greetings.add("Take it easy, ");
        greetings.add("Bis denne Antenne, ");
        greetings.add("See ya, ");
        greetings.add("Bis die Tage, ");

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
        funnyTexts.add("Ich denke nur an mein Mittagessen, wenn ich in der Vorlesung sitze.");
        funnyTexts.add("Ich würde für ein gutes Mittagessen jede Vorlesung besuchen.");
        funnyTexts.add("Ich denke nur ans Mittagessen, wenn ich in einer langweiligen Vorlesung sitze.");
        funnyTexts.add("Ohne Mittagessen wäre die Uni nur ein Ort voller Trägheit und Müdigkeit.");
        funnyTexts.add("Nimm lieber Salz und Pfeffer mit.");
        funnyTexts.add("Mittagessen ist wie ein Mini-Urlaub von der Uni.");
        funnyTexts.add("Das beste am Studium ist die Mittagspause.");
        funnyTexts.add("Ohne Essen keine Leistung.");
        funnyTexts.add("Ich studiere, damit ich mir mein Mittagessen leisten kann.");
        funnyTexts.add("Essen ist wichtiger als jede Vorlesung.");
        funnyTexts.add("Das Essen ist der heimliche Star der Uni.");
        funnyTexts.add("Wenn der Magen knurrt, hilft nur ein Mittagessen.");
        funnyTexts.add("Essen hält Leib und Seele zusammen.");
        funnyTexts.add("Mittagspause: Das Highlight des Tages.");
        funnyTexts.add("Ohne gutes Essen keine guten Noten.");
        funnyTexts.add("Essen ist das beste Mittel gegen Langeweile in der Uni.");
        funnyTexts.add("Wer gut isst, studiert besser.");
        funnyTexts.add("Essen macht glücklich.");
        funnyTexts.add("Essen ist die beste Belohnung für einen langen Uni-Tag.");
        funnyTexts.add("Das Geheimnis eines erfolgreichen Studiums: gute Mahlzeiten.");
        funnyTexts.add("Ein leerer Magen studiert nicht gern.");
        funnyTexts.add("Ein gutes Mittagessen kann Wunder wirken.");
        funnyTexts.add("Essen ist die beste Motivation.");
        funnyTexts.add("Ein hungriger Student ist ein unproduktiver Student.");
        funnyTexts.add("Essen ist der Treibstoff für den Uni-Alltag.");
        funnyTexts.add("Ohne Essen keine Konzentration.");
        funnyTexts.add("Essen ist das beste Anti-Stress-Mittel.");
        funnyTexts.add("Das beste Rezept gegen Langeweile: ein leckeres Mittagessen.");
        funnyTexts.add("Essen ist die schönste Nebensache der Welt.");
        funnyTexts.add("Wer gut isst, hat gut lachen.");
        funnyTexts.add("Ohne Essen keine Energie.");
        funnyTexts.add("Ein gutes Mittagessen macht den Tag perfekt.");
        funnyTexts.add("Essen ist das Highlight des Tages.");
        funnyTexts.add("Ein gutes Essen ist die beste Medizin.");
        funnyTexts.add("Essen verbindet.");
        funnyTexts.add("Essen ist Liebe.");
        funnyTexts.add("Essen ist Genuss.");
        funnyTexts.add("Essen ist Leben.");
        funnyTexts.add("Ein gutes Essen kann Wunder wirken.");
        funnyTexts.add("Essen ist das beste Mittel gegen schlechte Laune.");
        funnyTexts.add("Wer gut isst, lebt besser.");
        funnyTexts.add("Essen ist das beste Mittel gegen Langeweile.");
        funnyTexts.add("Essen ist das Beste, was man in der Mittagspause machen kann.");
        funnyTexts.add("Essen ist das Beste, was man in der Uni machen kann.");
        funnyTexts.add("Essen ist die beste Belohnung für einen langen Uni-Tag.");
        funnyTexts.add("Essen ist das Highlight des Uni-Tages.");
        funnyTexts.add("Essen ist das Beste am Studium.");
        funnyTexts.add("Essen ist das Wichtigste im Leben eines Studenten.");
        funnyTexts.add("Essen ist das Einzige, was im Uni-Alltag wirklich zählt.");
        funnyTexts.add("Essen ist das Einzige, was mich bei Laune hält.");
        funnyTexts.add("Essen ist das Einzige, was mich durch den Uni-Tag bringt.");
        funnyTexts.add("Essen ist das Einzige, was mich motiviert.");
        funnyTexts.add("Essen ist das Einzige, was mich glücklich macht.");

        return funnyTexts.get((int) (Math.random() * funnyTexts.size()));
    }

    private String createEmail(List<? extends Meal> menu, String firstName, String deactivateUrl, Mensa mensa) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
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
                    if (meals.get(0).getAllergens().equals(notAvailableSign)) {
                        menuString = menuString.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                    } else {
                        menuString = menuString.replaceFirst("%s", meals.get(0).getAllergens());
                    }

                } else {
                    categoryString = createCategoryString(meals, categoryString);
                    StringBuilder mealBuilder = new StringBuilder();
                    for (Meal meal : meals) {
                        String groupMeal = menuString.replaceFirst("%s", meal.getName() +
                                " (" + meal.getDescription() + ")" + " - " + meal.getPrice());
                        if (meals.get(0).getAllergens().equals(notAvailableSign)) {
                            groupMeal = groupMeal.replaceFirst("%s", "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)");
                        } else {
                            groupMeal = groupMeal.replaceFirst("%s", meals.get(0).getAllergens());
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
        footer = footer.replaceFirst("%s", Config.getInstance().getProperty("mensaHub.dataDispatcher.junction.address") + "/mealPlan?date=today&mensa=" + mensa.getId());

        return header +
                menuText +
                footer;
    }

}