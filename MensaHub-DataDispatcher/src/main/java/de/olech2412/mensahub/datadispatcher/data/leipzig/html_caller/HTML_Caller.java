package de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringConfig;
import de.olech2412.mensahub.datadispatcher.monitoring.MonitoringTags;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import java.util.Objects;
import java.util.concurrent.*;

@Log4j2
@Component
public class HTML_Caller {

    private final String notAvailableSign = Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign");
    private final Counter callCounterSuccess;
    private final Counter callCounterFailure;
    private final Timer parsingTimer;
    private final Timer mealParsingTimer;
    private final Counter mealsParsedCounter;
    private final Counter mensasParsedCounter;
    private final Counter errorsCounter;

    @Autowired
    HashStore hashStore;

    @Autowired
    MonitoringConfig monitoringConfig;

    public HTML_Caller(MonitoringConfig monitoringConfig, HashStore hashStore) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        this.hashStore = hashStore;
        this.monitoringConfig = monitoringConfig;

        this.callCounterSuccess =
                monitoringConfig.customCounter("stuwe_call_counter_success",
                        MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "How many calls were sent and parsed successfully");

        this.callCounterFailure =
                monitoringConfig.customCounter("stuwe_call_counter_failure",
                        MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "How many call or parsing failure happened");

        this.parsingTimer = Metrics.timer("mensahub.dataDispatcher.parsing.time");
        this.mealParsingTimer = Metrics.timer("mensahub.dataDispatcher.mealParsing.time");
        this.mealsParsedCounter = monitoringConfig.customCounter("meals_parsed_counter",
                MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "Total number of meals parsed");
        this.mensasParsedCounter = monitoringConfig.customCounter("mensas_parsed_counter",
                MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "Total number of mensas parsed");
        this.errorsCounter = monitoringConfig.customCounter("parsing_errors_counter",
                MonitoringTags.MENSAHUB_DATA_DISPATCHER_APPLICATION_TAG.getValue(), "Total number of errors during parsing");
    }

    public Result<Map<Mensa, List<Meal>>, ParserError> callDataFromStudentenwerk(String url, LocalDate servingDate, List<Mensa> mensas) {
        return parsingTimer.record(() -> {
            long startTimeParseAndCall = System.nanoTime();

            Map<Mensa, List<Meal>> mensaMealsMap = new ConcurrentHashMap<>();
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

            try {
                Document doc = Jsoup.connect(url).get();
                String documentContent = doc.body().text();
                int newHash = Objects.hash(documentContent);

                // Überprüfe, ob der Hash gleich ist
                if (hashStore.isSameHash(servingDate, newHash)) {
                    log.info("No changes detected for date {}, returning empty result.", servingDate);
                    return Result.success(new ConcurrentHashMap<>()); // Leere Liste zurückgeben
                }

                long startTimeParsing = System.nanoTime();
                // Select all Mensa headers (Mensa names)
                Elements mensaHeaders = doc.select("h3");
                List<Future<?>> futures = new ArrayList<>();

                for (Element mensaHeader : mensaHeaders) {
                    futures.add(executor.submit(() -> {
                        mensasParsedCounter.increment();
                        Timer.Sample mensaParsingSample = Timer.start();

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
                            Timer.Sample mealParsingSample = Timer.start();
                            mealsParsedCounter.increment();

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

                            mensaMealsMap.put(mensa, mealsList);
                            mealParsingSample.stop(mealParsingTimer);
                        }

                        mensaParsingSample.stop(parsingTimer);
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

                // neuen hash speichern
                hashStore.saveHash(servingDate, newHash);

                callCounterSuccess.increment();
                return Result.success(mensaMealsMap);
            } catch (IOException | InterruptedException | ExecutionException e) {
                errorsCounter.increment();
                callCounterFailure.increment();
                log.fatal("Error while parsing the HTML document: {}", e.getMessage());
                return Result.error(new ParserError("Error while parsing the HTML document: " + e.getMessage(), ParserErrors.UNKNOWN));
            } finally {
                executor.shutdown();
            }
        });
    }
}