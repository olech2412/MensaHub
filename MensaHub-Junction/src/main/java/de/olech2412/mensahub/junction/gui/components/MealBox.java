package de.olech2412.mensahub.junction.gui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

import javax.sound.sampled.Line;

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

        SvgIcon categoryIcon = getIconForCategory(category);
        categoryIcon.addClassName(LumoUtility.IconSize.SMALL);
        categoryIcon.addClassName("category-icon");

        HorizontalLayout categoryLayout = new HorizontalLayout(badge, categoryIcon);
        categoryLayout.addClassName("category-layout");

        Span priceText = new Span(price);
        priceText.addClassName("price");

        add(categoryLayout, h4, new Text(description), priceText, accordion);
    }

    private SvgIcon getIconForCategory(String category) {
        return switch (category) {
            case "Veganes Gericht" -> LineAwesomeIcon.LEAF_SOLID.create();
            case "Pastateller" -> LineAwesomeIcon.UTENSILS_SOLID.create();
            case "Hauptkomponente" -> LineAwesomeIcon.UTENSILS_SOLID.create();
            case "Schneller Teller" -> LineAwesomeIcon.RUNNING_SOLID.create();
            case "Gemüsebeilage" -> LineAwesomeIcon.CARROT_SOLID.create();
            case "Sättigungsbeilage" -> LineAwesomeIcon.COOKIE_SOLID.create();
            case "Fleischgericht" -> LineAwesomeIcon.DRUMSTICK_BITE_SOLID.create();
            case "WOK" -> LineAwesomeIcon.PEPPER_HOT_SOLID.create();
            case "Fischgericht" -> LineAwesomeIcon.FISH_SOLID.create();
            case "Pizza" -> LineAwesomeIcon.PIZZA_SLICE_SOLID.create();
            case "Vegetarisches Gericht" -> LineAwesomeIcon.SEEDLING_SOLID.create();
            case "Grill" -> LineAwesomeIcon.BACON_SOLID.create();
            case "Regio-Teller" -> LineAwesomeIcon.MAP_MARKED_ALT_SOLID.create();
            case "Suppe / Eintopf" -> LineAwesomeIcon.MORTAR_PESTLE_SOLID.create();
            default -> LineAwesomeIcon.APPLE_ALT_SOLID.create();
        };
    }
}