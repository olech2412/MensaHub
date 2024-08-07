package de.olech2412.mensahub.junction.gui.components.own.boxes;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import de.olech2412.mensahub.junction.gui.components.own.RatingComponent;
import de.olech2412.mensahub.models.addons.predictions.PredictionResult;
import lombok.Getter;

@Getter
public class MealBox extends VerticalLayout {

    Button ratingButton = new Button("Bewerten");

    RatingComponent ratingComponent = new RatingComponent();

    Span recommendationBadge = new Span();

    String mealName;

    public MealBox(String mealName, String description, String price, String allergens, String category) {
        addClassName("meal-box");
        this.mealName = mealName;

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

        HorizontalLayout categoryLayout = new HorizontalLayout(badge, recommendationBadge);
        categoryLayout.addClassName("category-layout");

        Span priceText = new Span(price);
        priceText.addClassName("price");

        ratingButton.setIcon(VaadinIcon.CHECK.create());

        VerticalLayout ratingLayout = new VerticalLayout();
        ratingLayout.setAlignItems(Alignment.CENTER);
        ratingLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        ratingLayout.addClassName("rating-layout");
        ratingLayout.add(ratingComponent, ratingButton);

        add(categoryLayout, h4, new Text(description), priceText, accordion, ratingLayout);
    }

    public void showRecommendation(PredictionResult predictionResult){
        long predictionScore = Math.round(predictionResult.getPredictedRating());
        recommendationBadge.setText("Empfehlung: " + predictionScore + "/5" + " | " + predictionResult.getTrustScore());
        recommendationBadge.addClassName("prediction");
        Tooltip tooltip = Tooltip.forComponent(recommendationBadge) // important, dont remove!
                .withText("Diese Angaben wurden für dich persönlich auf Grundlage deiner und den Bewertungen anderer Nutzer für dich berechnet. " +
                        "Die Angabe der Genauigkeit (" + predictionResult.getTrustScore() + ")" + " gibt an, wie wahrscheinlich die Prognose ist.")
                .withPosition(Tooltip.TooltipPosition.TOP_START);
        tooltip.setManual(isMobileDevice());
        recommendationBadge.addClickListener(spanClickEvent -> {
            tooltip.setOpened(!tooltip.isOpened());
        });
    }

    public  boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }
}