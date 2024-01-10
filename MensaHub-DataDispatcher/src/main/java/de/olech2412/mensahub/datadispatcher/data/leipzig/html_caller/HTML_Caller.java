package de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller;

import de.olech2412.mensahub.datadispatcher.config.Config;
import de.olech2412.mensahub.models.Generic_Meal;
import de.olech2412.mensahub.models.Meal;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class HTML_Caller {

    private final String notAvailableSign = Config.getInstance().getProperty("mensaHub.dataDispatcher.notAvailable.sign");

    public HTML_Caller() throws IOException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
    }

    public List<Meal> callDataFromStudentenwerk(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements meals = doc.select(".meals .accordion__item");

            List<Meal> mealsList = new ArrayList<>();

            for (Element meal : meals) {
                Element mealNameElement = meal.selectFirst(".meals__name");
                Meal mealObject = new Generic_Meal();
                if (mealNameElement != null) {
                    String mealName = mealNameElement.text();
                    mealObject.setName(mealName.trim());

                    Element priceElement = meal.selectFirst(".meals__price");
                    if (priceElement != null) {
                        String price = priceElement.text().replaceAll("\\s+", " ");
                        price = price.replace("Preise: ", "");
                        mealObject.setPrice(price.trim());
                    } else {
                        mealObject.setPrice(notAvailableSign);
                    }

                    Element additiveElement = meal.selectFirst(".meals__badges");
                    if (additiveElement != null) {
                        Elements additiveIcons = additiveElement.select(".meals__badge");
                        StringBuilder additivesBuilder = new StringBuilder();
                        for (Element additiveIcon : additiveIcons) {
                            String additive = additiveIcon.attr("title");
                            additivesBuilder.append(additive).append(", ");
                        }
                        String additivesString = additivesBuilder.toString().trim();
                        additivesString = additivesString.substring(0, additivesString.length() - 1); // Remove the trailing comma
                        mealObject.setAdditionalInfo(additivesString.trim());
                    } else {
                        mealObject.setAdditionalInfo(notAvailableSign);
                    }

                    Element allergenElement = meal.selectFirst(".meals__desc table tr:nth-child(2) td");
                    if (allergenElement != null) {
                        String allergen = allergenElement.text();
                        mealObject.setAllergens(allergen.trim());
                    } else {
                        mealObject.setAllergens(notAvailableSign);
                    }

                    Element sideElement = meal.selectFirst(".meals__desc ul");
                    if (sideElement != null) {
                        Elements sideElements = sideElement.select("li");
                        StringBuilder sidesBuilder = new StringBuilder();
                        for (Element sideelement : sideElements) {
                            String side = sideelement.text();
                            sidesBuilder.append(side).append(" & ");
                        }
                        String sidesString = sidesBuilder.toString().trim();
                        if (sidesString.endsWith("&")) {
                            sidesString = sidesString.substring(0, sidesString.length() - 1); // Remove the trailing " & "
                        }
                        mealObject.setDescription(sidesString.trim());
                    } else {
                        mealObject.setDescription(notAvailableSign);
                    }

                    // Extract the category directly from the nearest "h3" element containing the category name
                    Element categoryElement = meal.parent().previousElementSibling();
                    while (categoryElement != null && !categoryElement.tagName().equals("h3")) {
                        categoryElement = categoryElement.previousElementSibling();
                    }
                    if (categoryElement != null) {
                        String category = categoryElement.text();
                        mealObject.setCategory(category.trim());
                    } else {
                        mealObject.setCategory(notAvailableSign);
                    }

                    String[] urlParts = url.split("&date=");
                    if (urlParts.length > 1) {
                        String dateString = urlParts[1];
                        String[] dateParts = dateString.split("&");
                        if (dateParts.length > 0) {
                            String date = dateParts[0];
                            mealObject.setServingDate(LocalDate.parse(date));
                        }
                    }

                    // Fetch Zusatzstoffe and Allergene
                    Element detailsElement = meal.selectFirst(".accordion__content");
                    if (detailsElement != null) {
                        Element tableElement = detailsElement.selectFirst("table.table-def");
                        if (tableElement != null) {
                            Elements rows = tableElement.select("tr");
                            for (Element row : rows) {
                                Elements thElements = row.select("th");
                                Elements tdElements = row.select("td");
                                if (thElements.size() > 0 && tdElements.size() > 0) {
                                    String title = thElements.first().text().trim();
                                    String value = tdElements.first().text().trim();
                                    if (title.equalsIgnoreCase("Zusatzstoffe:")) {
                                        mealObject.setAdditives(value.trim());
                                    } else if (title.equalsIgnoreCase("Allergene:")) {
                                        mealObject.setAllergens(value.trim());
                                    }
                                }
                            }
                        } else {
                            mealObject.setAdditives(notAvailableSign);
                            mealObject.setAllergens(notAvailableSign);
                        }
                    } else {
                        mealObject.setAdditives(notAvailableSign);
                        mealObject.setAllergens(notAvailableSign);
                    }

                    if (mealObject.getAdditives() == null) {
                        mealObject.setAdditives(notAvailableSign);
                    }
                    if (mealObject.getAllergens() == null) {
                        mealObject.setAllergens(notAvailableSign);
                    }

                    mealsList.add(mealObject);
                }
            }

            log.info("Parser found: " + mealsList.size() + " individual meals");

            return mealsList;
        } catch (IOException e) {
            log.fatal("Error while parsing the HTML document" + e.getMessage());
        }
        return null;
    }

    public Map<String, String> fetchAllergensandAdditives(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Map<String, String> dataMap = new HashMap<>();

        // Parse Zusatzstoffe
        Element zusatzstoffeElement = doc.selectFirst("h3.title-delta:contains(Zusatzstoffe)");
        if (zusatzstoffeElement != null) {
            Element tableElement = zusatzstoffeElement.nextElementSibling();
            assert tableElement != null;
            Elements rows = tableElement.select("tr");
            for (Element row : rows) {
                Elements thElements = row.select("th");
                Elements tdElements = row.select("td");
                if (thElements.size() > 0 && tdElements.size() > 0) {
                    String number = thElements.first().text().trim();
                    String description = tdElements.first().text().trim();
                    dataMap.put(number, description);
                }
            }
        }

        // Parse Allergene
        Element allergeneElement = doc.selectFirst("h3.title-delta:contains(Allergene)");
        if (allergeneElement != null) {
            Element tableElement = allergeneElement.nextElementSibling();
            Elements rows = tableElement.select("tr");
            for (Element row : rows) {
                Elements thElements = row.select("th");
                Elements tdElements = row.select("td");
                if (thElements.size() > 0 && tdElements.size() > 0) {
                    String number = thElements.first().text().trim();
                    String description = tdElements.first().text().trim();
                    dataMap.put(number, description);
                }
            }
        }
        return dataMap;
    }
}