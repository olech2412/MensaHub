package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.vaadin.lineawesome.LineAwesomeIcon;

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

        HorizontalLayout categoryLayout = new HorizontalLayout(badge);
        categoryLayout.addClassName("category-layout");

        Span priceText = new Span(price);
        priceText.addClassName("price");

        add(categoryLayout, h4, new Text(description), priceText, accordion);
    }
}