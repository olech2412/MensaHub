package de.olech2412.mensahub.junction.gui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

public class MealBox extends VerticalLayout {

    public MealBox(String mealName, String description, String price, String allergens, String category) {
        addClassName("meal-box");

        H4 h4 = new H4(mealName);
        h4.addClassName("meal-box-title");

        Accordion accordion = new Accordion();
        accordion.close();

        Span allergenText = new Span(allergens);

        VerticalLayout allergenLayout = new VerticalLayout(allergenText);
        allergenLayout.setSpacing(false);
        allergenLayout.setPadding(false);
        allergenLayout.addClassName("accordion-content");

        accordion.add("Allergene", allergenLayout);
        accordion.addClassName("accordion-summary");

        Span badge = new Span(category);
        badge.addClassNames("category", category.replace(" ", "-"));

        Icon categoryIcon = new Icon(getIconForCategory(category));
        categoryIcon.addClassName("category-icon");

        HorizontalLayout categoryLayout = new HorizontalLayout(badge, categoryIcon);
        categoryLayout.setAlignItems(Alignment.CENTER);
        categoryLayout.addClassName("category-layout");

        Span priceText = new Span(price);
        priceText.addClassName("price");

        add(categoryLayout, h4, new Text(description), priceText, accordion);
    }

    private VaadinIcon getIconForCategory(String category) {
        return switch (category) {
            case "Veganes Gericht" -> VaadinIcon.QUESTION;
            case "Pastateller" -> VaadinIcon.SPOON;
            case "Hauptkomponente" -> VaadinIcon.CUTLERY;
            case "Schneller Teller" -> VaadinIcon.FLASH;
            case "Gemüsebeilage" -> VaadinIcon.QUESTION;
            case "Sättigungsbeilage" -> VaadinIcon.ARCHIVES;
            case "Fleischgericht" -> VaadinIcon.QUESTION;
            case "WOK" -> VaadinIcon.PYRAMID_CHART;
            case "Fischgericht" -> VaadinIcon.QUESTION;
            case "Pizza" -> VaadinIcon.QUESTION;
            case "Vegetarisches Gericht" -> VaadinIcon.QUESTION;
            case "Grill" -> VaadinIcon.QUESTION;
            case "Regio-Teller" -> VaadinIcon.MAP_MARKER;
            case "Suppe / Eintopf" -> VaadinIcon.QUESTION;
            default -> VaadinIcon.QUESTION;
        };
    }
}