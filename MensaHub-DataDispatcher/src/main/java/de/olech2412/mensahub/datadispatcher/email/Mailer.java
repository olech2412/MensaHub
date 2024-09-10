package de.olech2412.mensahub.datadispatcher.email;

import de.olech2412.mensahub.APIConfiguration;
import de.olech2412.mensahub.CollaborativeFilteringAPIAdapter;
import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.jpa.services.leipzig.meals.MealsService;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.addons.predictions.PredictionRequest;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.api.APIError;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class Mailer {

    private static final String notAvailableSign;
    private static final CollaborativeFilteringAPIAdapter collaborativeFilteringAPIAdapter;

    static {
        try {
            notAvailableSign = Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign");
            APIConfiguration apiConfiguration = new APIConfiguration();
            apiConfiguration.setBaseUrl(Config.getInstance().getProperty("mensaHub.junction.collaborative.filter.api.baseUrl"));
            collaborativeFilteringAPIAdapter = new CollaborativeFilteringAPIAdapter(apiConfiguration);
        } catch (IOException | IllegalBlockSizeException | NoSuchPaddingException | BadPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private final MealsService mealsService;

    public Mailer(MealsService mealsService) {
        this.mealsService = mealsService;
    }

    private static PrivateKey loadPrivateKey(String filename) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)))) {
            String key = reader.lines()
                    .filter(line -> !line.contains("BEGIN") && !line.contains("END"))
                    .collect(Collectors.joining());
            byte[] keyBytes = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTarget.getEmail()));

            String msg = createEmailContent(menu, emailTarget, deactivateUrl, mensa, update);
            message.setSubject(getSubject(update, mensa.getName()));

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            message.setContent(multipart);

            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) loadPrivateKey(Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.dkim_priv_path"));
            DkimSigner signer = new DkimSigner(Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.dkim_signing_domain"),
                    Config.getInstance().getProperty("mensaHub.dataDispatcher.mail.dkim_seperator"), rsaPrivateKey);
            DkimMessage dkimMessage = new DkimMessage(message, signer);

            Transport.send(dkimMessage);
            // HTML-Datei speichern
            saveEmailToFile(emailTarget, msg, mensa.getName(), update);
            return Result.success(emailTarget);
        } catch (Exception exception) {
            log.error("Error while sending email for user {}", emailTarget.getEmail(), exception);
            return Result.error(new MailError("Error while sending email for user " + emailTarget.getEmail() + " with" +
                    " error: " + exception.getMessage(), MailErrors.UNKNOWN));
        }
    }

    private String createEmailContent(List<Meal> menu, MailUser emailTarget, String deactivateUrl, Mensa mensa, boolean update) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        StringBuilder menuText = new StringBuilder();

        if (!menu.isEmpty()) {
            List<List<Meal>> sublists = groupMealsByCategory(menu);
            sublists.forEach(meals -> {
                String menuString = StaticEmailText.FOOD_TEXT;
                String categoryString = createCategoryString(meals, StaticEmailText.FOOD_CATEGORY);
                if (meals.size() == 1) {
                    try {
                        menuString = buildSingleMealStringWithPrediction(menuString, meals.get(0), emailTarget.getId().intValue());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    menuString = buildMultipleMealsStringWithPrediction(menuString, meals, emailTarget.getId().intValue());
                }
                menuText.append(categoryString).append(menuString);
            });
        } else {
            log.warn("No meals found for Mensa: {} --> send empty mail to: {}", mensa.getName(), emailTarget.getFirstname());
            menuText.append(StaticEmailText.FOOD_TEXT.replaceFirst("%s", "Wir haben für deine Mensa " +
                    "heute leider keine Gerichte für dich gefunden :("));
            menuText.append(StaticEmailText.FOOD_TEXT.replaceFirst("%s", " "));
        }

        String header = StaticEmailText.FOOD_PLAN_TEXT;
        header = header.replaceFirst("%s", (update ? "Update zu deinem Speiseplan " : "Speiseplan ") +
                LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                mensa.getName());
        header = header.replaceFirst("%s", (update? "Eine Änderung wurde für deine Mensa " + mensa.getName() + " gefunden." : getRandomFunnyText()));
        header = header.replaceFirst("%s", getRandomFunnyWelcomeText());
        header = header.replaceFirst("%s", emailTarget.getFirstname());
        header = header.replaceFirst("%s", (update ? "wir haben eine Änderung des Speiseplans festgestellt. Dabei kann es sich um diverse größere oder kleinere Änderungen handeln." + "\n" +
                "Diese E-Mail wurde automatisch um " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + " Uhr erstellt." : "nachfolgend findest du den Speiseplan für heute."
        ));

        String footer = StaticEmailText.FOOD_PLAN_FOOTER;
        footer = footer.replaceFirst("%s", getRandomGreetingsText());
        footer = footer.replaceFirst("%s", deactivateUrl);
        footer = footer.replaceFirst("%s", Config.getInstance().getProperty("mensaHub.dataDispatcher.junction.address")
                + "/mealPlan?date=today&mensa=" + mensa.getId() + "&userCode=" + emailTarget.getDeactivationCode().getCode());


        return header +
                menuText +
                footer;
    }

    private String getRightEmoticonForCategory(String category) {
        if (category.contains("Fleisch")) {
            return " \uD83E\uDD69";
        } else if (category.contains("Fisch")) {
            return " \uD83E\uDDA3";
        } else if (category.contains("Vegetarisch") || category.contains("Veggie")) {
            return " \uD83E\uDD66";
        } else if (category.contains("Vegan")) {
            return " \uD83E\uDD5F";
        } else if (category.contains("Beilage")) {
            return " \uD83E\uDDC1";
        } else if (category.contains("Suppe")) {
            return " \uD83C\uDF73";
        } else if (category.contains("Dessert")) {
            return " \uD83C\uDF6B";
        } else if (category.contains("Salat")) {
            return " \uD83E\uDD57";
        } else if (category.contains("Getränk")) {
            return " \uD83C\uDF7A";
        } else if (category.contains("Snack")) {
            return " \uD83C\uDF6A";
        } else if (category.contains("Menü")) {
            return " \uD83C\uDF5F";
        } else if (category.contains("Pizza")) {
            return " \uD83C\uDF55";
        } else if (category.contains("Klima")) {
            return " \uD83C\uDF0D";
        } else if (category.contains("Bio")) {
            return " \uD83C\uDF3E";
        } else if (category.contains("Regional")) {
            return " \uD83C\uDFE0";
        } else if (category.contains("Fairtrade")) {
            return " \uD83C\uDF10";
        } else if (category.contains("Geflügel")) {
            return " \uD83D\uDC13";
        } else if (category.contains("Schwein")) {
            return " \uD83D\uDC16";
        } else if (category.contains("Rind")) {
            return " \uD83D\uDC2E";
        } else if (category.contains("Lamm")) {
            return " \uD83D\uDC0F";
        } else if (category.contains("Wild")) {
            return " \uD83D\uDC10";
        } else if (category.contains("Ei")) {
            return " \uD83E\uDD5A";
        } else if (category.contains("Pasta")) {
            return " \uD83C\uDF5D";
        } else if (category.contains("WOK")) {
            return " \uD83C\uDF72";
        } else if (category.contains("Smoothie")) {
            return " \uD83E\uDD64";
        } else if (category.contains("Burger")) {
            return " \uD83C\uDF54";
        } else if (category.contains("Schnitzel")) {
            return " \uD83C\uDF56";
        } else if (category.contains("Kartoffel")) {
            return " \uD83E\uDD54";
        } else if (category.contains("Pommes")) {
            return " \uD83C\uDF5F";
        } else if (category.contains("Nudeln")) {
            return " \uD83C\uDF5D";
        } else if (category.contains("Reis")) {
            return " \uD83C\uDF5A";
        } else {
            return "";
        }
    }

    private String getSubject(boolean update, String mensaName) {
        return (update ? "Update zu deinem Speiseplan " : "Speiseplan ") +
                LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                mensaName;
    }

    private List<List<Meal>> groupMealsByCategory(List<Meal> menu) {
        return menu.parallelStream()
                .collect(Collectors.groupingByConcurrent(Meal::getCategory))
                .values().stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }

    private String buildSingleMealStringWithPrediction(String template, Meal meal, int mailUserId) throws IOException {
        String predictionText = getPredictionText(meal, mailUserId);
        return buildMealString(template, meal, predictionText);
    }

    private String buildMultipleMealsStringWithPrediction(String template, List<Meal> meals, int mailUserId) {
        return meals.stream()
                .map(meal -> {
                    try {
                        return buildSingleMealStringWithPrediction(template, meal, mailUserId);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.joining("\n"));
    }

    private String buildMealString(String template, Meal meal, String predictionText) {
        return template.replaceFirst("%s", meal.getName() + " (" + meal.getDescription() + ") - " + meal.getPrice() + predictionText)
                .replaceFirst("%s", meal.getAllergens().equals(notAvailableSign)
                        ? "Keine Allergene oder Zusatzstoffe enthalten (kontaktiere bitte die Mensa/Cafeteria, falls du dir unsicher bist)"
                        : meal.getAllergens());
    }

    private String getPredictionText(Meal meal, int mailUserId) throws IOException {
        if (collaborativeFilteringAPIAdapter.isAPIAvailable()) {
            PredictionRequest request = new PredictionRequest(mailUserId, meal.getName(), meal.getId().intValue());
            Result<List<Result<PredictionResult, APIError>>, APIError> predictionResults = collaborativeFilteringAPIAdapter.predict(List.of(request));

            if (predictionResults != null && predictionResults.isSuccess()) {
                PredictionResult predictionResult = predictionResults.getData().get(0).getData();
                return " - Empfehlung: " + Math.round(predictionResult.getPredictedRating()) + "/5";
            }
        }
        return "";
    }

    private String createCategoryString(List<Meal> meals, String categoryString) {
        Map<String, String> categoryEmojis = Map.ofEntries(
                Map.entry("Fleisch", " \uD83E\uDD69"),
                Map.entry("Fisch", " \uD83E\uDDA3"),
                Map.entry("Vegetarisch", " \uD83E\uDD66"),
                Map.entry("Vegan", " \uD83E\uDD5F"),
                Map.entry("Beilage", " \uD83E\uDDC1"),
                Map.entry("Suppe", " \uD83C\uDF73"),
                Map.entry("Dessert", " \uD83C\uDF6B"),
                Map.entry("Salat", " \uD83E\uDD57"),
                Map.entry("Getränk", " \uD83C\uDF7A"),
                Map.entry("Snack", " \uD83C\uDF6A"),
                Map.entry("Menü", " \uD83C\uDF5F"),
                Map.entry("Pizza", " \uD83C\uDF55"),
                Map.entry("Klima", " \uD83C\uDF0D"),
                Map.entry("Bio", " \uD83C\uDF3E"),
                Map.entry("Regional", " \uD83C\uDFE0"),
                Map.entry("Fairtrade", " \uD83C\uDF10"),
                Map.entry("Geflügel", " \uD83D\uDC13"),
                Map.entry("Schwein", " \uD83D\uDC16"),
                Map.entry("Rind", " \uD83D\uDC2E"),
                Map.entry("Lamm", " \uD83D\uDC0F"),
                Map.entry("Wild", " \uD83D\uDC10"),
                Map.entry("Ei", " \uD83E\uDD5A"),
                Map.entry("Pasta", " \uD83C\uDF5D"),
                Map.entry("WOK", " \uD83C\uDF72"),
                Map.entry("Smoothie", " \uD83E\uDD64"),
                Map.entry("Burger", " \uD83C\uDF54"),
                Map.entry("Schnitzel", " \uD83C\uDF56"),
                Map.entry("Kartoffel", " \uD83E\uDD54"),
                Map.entry("Pommes", " \uD83C\uDF5F"),
                Map.entry("Nudeln", " \uD83C\uDF5D"),
                Map.entry("Reis", " \uD83C\uDF5A")
        );

        for (Map.Entry<String, String> entry : categoryEmojis.entrySet()) {
            if (meals.get(0).getCategory().contains(entry.getKey())) {
                return categoryString.replaceFirst("%s", meals.get(0).getCategory() + entry.getValue());
            }
        }
        return categoryString.replaceFirst("%s", meals.get(0).getCategory());
    }

    // Helper methods to generate random text strings
    private String getRandomFunnyWelcomeText() {
        List<String> welcome = List.of("Hallo", "Servus", "Moin", "Hi", "Guten Tag", "Guten Morgen", "Ahoi", "Grüß Gott", "Grüß di", "Aloha", "Alles cool im Pool", "Alles fit im Schritt",
                "Alles klar in Kanada", "Alles Roger in Kambodscha", "Buenas Tardes", "Gib Flosse", "Grüetzi", "Grüßli Müsli", "Hallihallohallöle", "Hallöchen", "Hallöchen Popöchen", "Hallöle",
                "Heroin-spaziert", "Hola", "Holla die Waldfee", "Howdy", "Huhu", "Huhu wie gehts", "Juten Tach", "Juten Tag", "Namasté", "Whazuuuuuuuup", "Yo Moinsen", "Yo", "Na was geht",
                "Grüß Gottle", "Meddl", "Sei gegrüßt", "Wie gehts", "Was geht ab", "Schön dich wiederzusehen", "Ich glaube mein Schwein pfeift", "Wow siehst du heute gut aus", "Das gibts ja nicht, der einzig wahre",
                "Das gibts ja nicht, der aller echte Hase", "\uD83C\uDFB6 So wie Photoshop, so wie, so wie...\uD83C\uDFB6 oh ... sry", "\uD83C\uDFB6 Vöööllig losgelöst von der EEERDE...\uD83C\uDFB6 oh ... sry",
                "\uD83C\uDFB6 Mit 66 Jahren da fängt das Leben an...\uD83C\uDFB6 oh ... sry", "\uD83C\uDFB6 Ein Rudi Völler, es gibt nur ein... \uD83C\uDFB6 ups ... sry", "Mohoin");

        return welcome.get(new Random().nextInt(welcome.size()));
    }

    private String getRandomGreetingsText() {
        List<String> greetings = List.of("Viele Grüße, ", "Liebe Grüße, ", "Mit freundlichen Grüßen, ", "Mit besten Grüßen, ", "Tschüssikowski, ", "Bis bald, ", "Bis dann, ", "Auf Wiederhörnchen, ",
                "Bis zum nächsten Mal, ", "Tschausen, ", "Bis baldrian, ", "Tschö mit ö, ", "Mach’s gut, Knut, ", "Bis baldo, Ronaldo, ", "Paris – Athen, Auf Wiedersehn, ", "See you later, alligator!, ",
                "Good Bye, Hawaii, ", "In diesem Sinne: Ab in die Rinne, ", "Ende im Gelände, ", "So sieht’s aus im Schneckenhaus, ", "Finger in Po – Mexiko, ", "Jetzt geht’s los, Spätzle mit Soß, ",
                "In a while, crocodile, ", "Auf Wiedersehen, ", "Bis denne, ", "Bis denne Antenne, ", "Au revoir, ", "Arrivederci, ", "До свидания, ", "안녕히 가세요, ", "再见, ", "Ciao, ", "Ciao Kakao, ",
                "Servus, ", "Adios, ", "Hasta luego, ", "Hasta la vista, ", "Leb wohl, ", "Take care, ", "Farewell, ", "Mach’s gut, ", "Hau rein, ", "Bis die Tage, ", "Cheerio, ", "Peace out, ", "Alles Gute, ",
                "Bleib gesund, ", "Schönen Tag noch, ", "Bis danni, ", "Machs jut, ", "Pfiat di, ", "Schönen Feierabend, ", "Guten Rutsch, ", "Bis morgen, ", "Bis nächste Woche, ", "Man sieht sich, ",
                "Bis demnächst, ", "Adieu, ", "Bye bye, ", "Bis gleich, ", "Viel Erfolg, ", "Gute Reise, ", "Haut rein, ", "Bis zur nächsten Runde, ", "Bis später, ", "Bis in Kürze, ", "Bis nachher, ", "Gute Nacht, ",
                "Schlaf gut, ", "Schönes Wochenende, ", "Bis Montag, ", "Bis Freitag, ", "Bis Dienstag, ", "Gute Fahrt, ", "Schöne Ferien, ", "Schöne Feiertage, ", "Bis bald im Wald, ", "Bis die Tage, ",
                "Tschüssikowski, ", "Mach’s gut, bis bald, ", "Auf Wiederhören, ", "Bis nächstes Mal, ", "Take it easy, ", "Bis denne Antenne, ", "See ya, ", "Bis die Tage, ");

        return greetings.get(new Random().nextInt(greetings.size()));
    }

    private String getRandomFunnyText() {
        List<String> funnyTexts = List.of("Guten Appetit!", "Guten Hunger!", "Guten Appetit und guten Hunger!", "Mahlzeit!", "Wir sind hier nicht bei “wünsch dir was”, sondern bei ” so isses”!",
                "Guten Appetit und guten Hunger! Und wenn du nicht satt bist, dann iss nochmal!", "Satz mit x, das war wohl nichts!", "Ich denke, ich werde meine Mittagspause heute zum Mittagessen benutzen.",
                "Ich denke, ich werde meine Mittagspause heute für ein köstliches Mittagessen opfern.", "Ein gutes Mittagessen ist das Geheimnis, um den Uni-Tag durchzustehen.", "Ohne Mittagessen wäre der Uni-Tag nur halb so erträglich.",
                "Ich denke nur an mein Mittagessen, wenn ich in der Vorlesung sitze.", "Ich würde für ein gutes Mittagessen jede Vorlesung besuchen.", "Ich denke nur ans Mittagessen, wenn ich in einer langweiligen Vorlesung sitze.",
                "Ohne Mittagessen wäre die Uni nur ein Ort voller Trägheit und Müdigkeit.", "Nimm lieber Salz und Pfeffer mit.", "Mittagessen ist wie ein Mini-Urlaub von der Uni.", "Das beste am Studium ist die Mittagspause.",
                "Ohne Essen keine Leistung.", "Ich studiere, damit ich mir mein Mittagessen leisten kann.", "Essen ist wichtiger als jede Vorlesung.", "Das Essen ist der heimliche Star der Uni.", "Wenn der Magen knurrt, hilft nur ein Mittagessen.",
                "Essen hält Leib und Seele zusammen.", "Mittagspause: Das Highlight des Tages.", "Ohne gutes Essen keine guten Noten.", "Essen ist das beste Mittel gegen Langeweile in der Uni.", "Wer gut isst, studiert besser.",
                "Essen macht glücklich.", "Essen ist die beste Belohnung für einen langen Uni-Tag.", "Das Geheimnis eines erfolgreichen Studiums: gute Mahlzeiten.", "Ein leerer Magen studiert nicht gern.", "Ein gutes Mittagessen kann Wunder wirken.",
                "Essen ist die beste Motivation.", "Ein hungriger Student ist ein unproduktiver Student.", "Essen ist der Treibstoff für den Uni-Alltag.", "Ohne Essen keine Konzentration.", "Essen ist das beste Anti-Stress-Mittel.",
                "Das beste Rezept gegen Langeweile: ein leckeres Mittagessen.", "Essen ist die schönste Nebensache der Welt.", "Wer gut isst, hat gut lachen.", "Ohne Essen keine Energie.", "Ein gutes Mittagessen macht den Tag perfekt.",
                "Essen ist das Highlight des Tages.", "Ein gutes Essen ist die beste Medizin.", "Essen verbindet.", "Essen ist Liebe.", "Essen ist Genuss.", "Essen ist Leben.", "Ein gutes Essen kann Wunder wirken.",
                "Essen ist das beste Mittel gegen schlechte Laune.", "Wer gut isst, lebt besser.", "Essen ist das beste Mittel gegen Langeweile.", "Essen ist das Beste, was man in der Mittagspause machen kann.",
                "Essen ist das Beste, was man in der Uni machen kann.", "Essen ist die beste Belohnung für einen langen Uni-Tag.", "Essen ist das Highlight des Uni-Tages.", "Essen ist das Beste am Studium.",
                "Essen ist das Wichtigste im Leben eines Studenten.", "Essen ist das Einzige, was im Uni-Alltag wirklich zählt.", "Essen ist das Einzige, was mich bei Laune hält.", "Essen ist das Einzige, was mich durch den Uni-Tag bringt.",
                "Essen ist das Einzige, was mich motiviert.", "Essen ist das Einzige, was mich glücklich macht.");

        return funnyTexts.get(new Random().nextInt(funnyTexts.size()));
    }

    public Result<MailUser, MailError> sendCollaborationMail(MailUser emailTarget, List<PredictionResult> predictions, Mensa mensa, LocalDate servingDate) {
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

            String msg = createEmailCollabInfo(predictions, emailTarget.getFirstname(), deactivateUrl, mensa, emailTarget.getDeactivationCode().getCode(), servingDate);
            message.setSubject("Mensa Empfehlungen " +
                    servingDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                    mensa.getName());


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
            // HTML-Datei speichern
            saveEmailToFile(emailTarget, msg, mensa.getName() + "_collaboration", false);
            return Result.success(emailTarget);
        } catch (Exception exception) {
            log.error("Error while sending email for user {}", emailTarget.getEmail(), exception);
            return Result.error(new MailError("Error while sending email for user " + emailTarget.getEmail() + " with" +
                    " error: " + exception.getMessage(), MailErrors.UNKNOWN));
        }
    }

    //     List<? extends Meal> menu, String firstName, String deactivateUrl, String userDeactivateCode
    private String createEmailCollabInfo(List<PredictionResult> predictions, String firstName, String deactivateUrl, Mensa mensa, String userDeactivateCode, LocalDate servingDate) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        StringBuilder menuText = new StringBuilder();

        for (PredictionResult prediction : predictions) {
            String mealString = StaticEmailText.MAIL_TEXT_COLLAB;
            Meal meal = mealsService.findMealById((long) prediction.getMealId());
            mealString = mealString.replaceFirst("%Kategorie %Emoticon", meal.getCategory() + " " + getRightEmoticonForCategory(meal.getCategory()));
            mealString = mealString.replaceFirst("%MealName - %MealPrices", (prediction.getMealName() + " - " + meal.getPrice()));
            mealString = mealString.replaceFirst("%PredictedRating", Math.round(prediction.getPredictedRating()) + "/5");
            menuText.append(mealString);
        }


        String header = StaticEmailText.FOOD_PLAN_TEXT;
        header = header.replaceFirst("%s", "Mensa Empfehlungen " +
                servingDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + " - " +
                mensa.getName());
        header = header.replaceFirst("%s", getRandomFunnyText());
        header = header.replaceFirst("%s", getRandomFunnyWelcomeText());
        header = header.replaceFirst("%s", firstName);
        header = header.replaceFirst("%s", "nachfolgend werden dir mögliche Empfehlungen für Mensagerichte für morgen in der " + mensa.getName() + " angezeigt.");


        String footer = StaticEmailText.FOOD_PLAN_FOOTER;
        footer = footer.replaceFirst("%s", getRandomGreetingsText());
        footer = footer.replaceFirst("%s", deactivateUrl);
        footer = footer.replaceFirst("%s", Config.getInstance().getProperty("mensaHub.dataDispatcher.junction.address") +
                "/mealPlan?date=today&userCode=" + userDeactivateCode);

        return header + menuText + footer;
    }

    private void saveEmailToFile(MailUser emailTarget, String content, String mensaName, boolean update) throws IOException {
        // Verzeichnispfad für das Archiv erstellen
        String archiveDirPath = System.getProperty("user.home") + "/mensaHub/archiv";
        File archiveDir = new File(archiveDirPath);

        // Verzeichnis erstellen, falls es nicht existiert
        if (!archiveDir.exists()) {
            boolean createdArchiveDir = archiveDir.mkdirs();
            if (createdArchiveDir) {
                log.info("Archive directory created: {}", archiveDirPath);
            } else {
                log.error("Archive directory did not exist but cant be created: {}", archiveDirPath);
            }
        }

        // Dateinamen generieren
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
        String fileName = emailTarget.getEmail() + "_" + formattedDate + "_" + mensaName + (update ? "_update" : "_regular") + ".html";

        // Pfad zur Datei
        File file = new File(archiveDir, fileName);

        // Dateiinhalt schreiben
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
