package de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.parser.ParserError;
import de.olech2412.mensahub.models.result.errors.parser.ParserErrors;
import io.micrometer.core.instrument.Counter;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class HTML_Caller {

    private final String notAvailableSign = Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign");
    private final Counter callCounterSuccess;
    private final Counter callCounterFailure;

    public HTML_Caller(Counter callCounterSuccess, Counter callCounterFails) throws IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        this.callCounterSuccess = callCounterSuccess;
        this.callCounterFailure = callCounterFails;
    }

    public Result<List<Meal>, ParserError> callDataFromStudentenwerk(String url, Mensa mensa) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements meals = doc.select(".meal-overview .card.type--meal");

            List<Meal> mealsList = new ArrayList<>();

            for (Element meal : meals) {
                Meal mealObject = new Meal();

                // Meal name
                Element mealNameElement = meal.selectFirst("h4");
                if (mealNameElement != null) {
                    String mealName = mealNameElement.text().trim();
                    mealObject.setName(mealName);
                }

                // Meal category from the first tag in .meal-tags
                Element categoryElement = meal.selectFirst(".meal-tags .tag");
                if (categoryElement != null) {
                    mealObject.setCategory(categoryElement.text().trim());
                } else {
                    mealObject.setCategory(notAvailableSign);
                }

                // Meal description (components)
                Element componentElement = meal.selectFirst(".meal-components");
                if (componentElement != null && !componentElement.text().isEmpty()) {
                    mealObject.setDescription(componentElement.text().trim());
                } else {
                    mealObject.setDescription(notAvailableSign);
                }

                // Prices
                Elements priceElements = meal.select(".meal-prices");
                if (!priceElements.isEmpty()) {
                    List<String> prices = priceElements.eachText();
                    mealObject.setPrice(String.join(" / ", prices).trim());
                } else {
                    mealObject.setPrice(notAvailableSign);
                }

                // Allergens and additives
                Element allergensElement = meal.selectFirst(".meal-allergens p");
                if (allergensElement != null) {
                    mealObject.setAllergens(allergensElement.text().trim());
                } else {
                    mealObject.setAllergens(notAvailableSign);
                }

                // Date handling from URL if available
                String[] urlParts = url.split("&date=");
                if (urlParts.length > 1) {
                    String dateString = urlParts[1];
                    String[] dateParts = dateString.split("&");
                    if (dateParts.length > 0) {
                        mealObject.setServingDate(LocalDate.parse(dateParts[0]));
                    }
                }

                // Check for subitems (variations)
                Element subItemsContainer = meal.selectFirst(".meal-subitems");
                if (subItemsContainer != null) {
                    Elements subItems = subItemsContainer.select(".meal-subitem");
                    for (Element subItem : subItems) {
                        Meal subMeal = new Meal();

                        // Sub meal name
                        Element subMealNameElement = subItem.selectFirst("h5");
                        if (subMealNameElement != null) {
                            String subMealName = subMealNameElement.childNodes().get(0).toString();
                            // remove any "oder"
                            subMealName = subMealName.replace("oder", "");
                            subMeal.setName(subMealName);
                        }

                        // Sub meal description (allergens and additives)
                        Element subAllergensElement = subItem.selectFirst("p");
                        if (subAllergensElement != null) {
                            subMeal.setAllergens(subAllergensElement.childNodes().get(1).toString().replace(":", ""));
                        } else {
                            subMeal.setAllergens(notAvailableSign);
                        }

                        subMeal.setDescription(notAvailableSign);

                        // Set other properties like category, price, allergens, etc., if applicable
                        subMeal.setCategory(mealObject.getCategory());
                        subMeal.setPrice(mealObject.getPrice());
                        subMeal.setMensa(mensa);
                        subMeal.setServingDate(mealObject.getServingDate());

                        mealsList.add(subMeal);
                    }
                } else {
                    // If there are no subitems, add the main meal
                    mealObject.setMensa(mensa);
                    mealsList.add(mealObject);
                }
            }

            log.info("Parser found: {} individual meals", mealsList.size());
            callCounterSuccess.increment();
            return Result.success(mealsList);
        } catch (IOException e) {
            callCounterFailure.increment();
            log.fatal("Error while parsing the HTML document: {}", e.getMessage());
            return Result.error(new ParserError("Error while parsing the HTML document: " + e.getMessage(), ParserErrors.UNKNOWN));
        }
    }
}