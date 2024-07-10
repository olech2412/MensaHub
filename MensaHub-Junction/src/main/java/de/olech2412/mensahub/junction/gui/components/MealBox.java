package de.olech2412.mensahub.junction.gui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MealBox extends VerticalLayout {

    /**
     * Creates a new highlight component.
     *
     * @param mealName       the mealName of the highlight
     * @param category the category of the percentage value for the highlight
     */
    public MealBox(String mealName, String description, String price, String allergens, String category) {
        String prefix = "";
        String theme = "badge";

        H4 h4 = new H4(mealName);
        h4.addClassNames(LumoUtility.FontWeight.NORMAL, LumoUtility.Margin.NONE, LumoUtility.TextColor.SECONDARY, LumoUtility.FontSize.XSMALL);

        Accordion accordion = new Accordion();

        Span allergenText = new Span(allergens);

        VerticalLayout allergenLayout = new VerticalLayout(allergenText);
        allergenLayout.setSpacing(false);
        allergenLayout.setPadding(false);

        accordion.add("Personal information", allergenLayout);

        Span badge = new Span(new Span(category));
        badge.getElement().getThemeList().add(theme);

        add(new HorizontalLayout(badge), h4, new Text(description), new Text(price), accordion);
        addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10);
        setPadding(false);
        setSpacing(false);
        getStyle().setBorder("1px solid red");
    }
}