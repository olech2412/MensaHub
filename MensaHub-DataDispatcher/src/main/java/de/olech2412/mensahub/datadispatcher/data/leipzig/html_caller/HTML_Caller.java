package de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.parser.ParserError;
import de.olech2412.mensahub.models.result.errors.parser.ParserErrors;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
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
import java.util.Map;
import java.util.concurrent.*;

@Log4j2
public class HTML_Caller {

    private final String notAvailableSign = Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign");
    private final Counter callCounterSuccess;
    private final Counter callCounterFailure;
    private final Timer parsingTimer;

    public HTML_Caller(Counter callCounterSuccess, Counter callCounterFails) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        this.callCounterSuccess = callCounterSuccess;
        this.callCounterFailure = callCounterFails;
        this.parsingTimer = Metrics.timer("mensahub.dataDispatcher.parsing.time");
    }

    public Result<Map<Mensa, List<Meal>>, ParserError> callDataFromStudentenwerk(String url, LocalDate servingDate, List<Mensa> mensas) {
        return parsingTimer.record(() -> {
            long startTimeParseAndCall = System.nanoTime();

            Map<Mensa, List<Meal>> mensaMealsMap = new ConcurrentHashMap<>();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            try {
                Document doc = Jsoup.connect(url).get();

                long startTimeParsing = System.nanoTime();
                // Select all Mensa headers (Mensa names)
                Elements mensaHeaders = doc.select("h3");
                List<Future<?>> futures = new ArrayList<>();

                for (Element mensaHeader : mensaHeaders) {
                    futures.add(executor.submit(() -> {
                        String mensaName = mensaHeader.text().trim();

                        // Find the corresponding Mensa from the database
                        Mensa mensa = mensas.stream()
                                .filter(m -> m.getName().equals(mensaName))
                                .findFirst()
                                .orElse(null);

                        if (mensa == null) {
                            log.error("Cannot find Mensa {} in database. This mensa will be ignored.", mensaName);
                            return;
                        }

                        // Find the corresponding meals for this Mensa
                        Element mealOverview = mensaHeader.nextElementSibling();
                        List<Meal> mealsList = new ArrayList<>();

                        Elements meals = mealOverview.select(".card.type--meal");

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

                            mealObject.setServingDate(servingDate);

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
                                        subMealName = subMealName.replace("oder", "").trim();
                                        subMeal.setName(subMealName);
                                    }

                                    // Sub meal allergens and additives
                                    Element subAllergensElement = subItem.selectFirst("p");
                                    if (subAllergensElement != null) {
                                        subMeal.setAllergens(subAllergensElement.text().replace(":", "").trim());
                                    } else {
                                        subMeal.setAllergens(notAvailableSign);
                                    }

                                    subMeal.setDescription(notAvailableSign);

                                    // Inherit other properties from main meal
                                    subMeal.setCategory(mealObject.getCategory());
                                    subMeal.setPrice(mealObject.getPrice());
                                    subMeal.setMensa(mensa);
                                    if (servingDate != null) {
                                        subMeal.setServingDate(servingDate);
                                    }

                                    mealsList.add(subMeal);
                                }
                            } else {
                                // Add main meal if no subitems exist
                                mealObject.setMensa(mensa);
                                mealsList.add(mealObject);
                            }
                        }

                        mensaMealsMap.put(mensa, mealsList);
                    }));
                }

                // Wait for all threads to finish
                for (Future<?> future : futures) {
                    future.get(); // Block until the thread is finished
                }


                long endTimeParse = System.nanoTime();
                long durationInMillisParse = TimeUnit.NANOSECONDS.toMillis(endTimeParse - startTimeParsing);
                log.info("Time taken to parse {} ms", durationInMillisParse);

                long endTimeParseAndCall = System.nanoTime();
                long durationInMillis = TimeUnit.NANOSECONDS.toMillis(endTimeParseAndCall - startTimeParseAndCall);
                log.info("Parser finished in {} ms and found: {} meals across {} mensas", durationInMillis, mensaMealsMap.values().stream().mapToInt(List::size).sum(), mensaMealsMap.size());

                callCounterSuccess.increment();
                return Result.success(mensaMealsMap);
            } catch (IOException | InterruptedException | ExecutionException e) {
                callCounterFailure.increment();
                log.fatal("Error while parsing the HTML document: {}", e.getMessage());
                return Result.error(new ParserError("Error while parsing the HTML document: " + e.getMessage(), ParserErrors.UNKNOWN));
            } finally {
                executor.shutdown();
            }
        });
    }
}