package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import lombok.Getter;

@Getter
public class RatingComponent extends VerticalLayout {

    private int rating = 0;
    private final Button[] stars = new Button[5];

    public RatingComponent() {
        setSpacing(false);
        setPadding(false);
        setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout starLayout = new HorizontalLayout();
        starLayout.setSpacing(false);
        starLayout.setPadding(false);
        starLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        for (int i = 0; i < 5; i++) {
            final int starValue = i + 1;
            stars[i] = new Button("★", click -> setRating(starValue));
            stars[i].addThemeVariants(ButtonVariant.LUMO_ICON);
            stars[i].getElement().getStyle().set("font-size", "36px");
            stars[i].getElement().getStyle().set("color", "var(--lumo-contrast-60pct)");
            stars[i].getElement().getStyle().set("background", "none");
            stars[i].getElement().getStyle().set("border", "none");
            starLayout.add(stars[i]);
        }

        stars[0].setTooltipText("Sehr schlecht");
        stars[1].setTooltipText("Schlecht");
        stars[2].setTooltipText("Ok");
        stars[3].setTooltipText("Gut");
        stars[4].setTooltipText("Sehr gut");

        add(starLayout);
    }

    public void setRating(int newRating) {
        rating = newRating;
        for (int i = 0; i < 5; i++) {
            if (i < rating) {
                stars[i].getElement().getStyle().set("color", "var(--lumo-primary-color)");
                stars[i].addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            } else {
                stars[i].getElement().getStyle().set("color", "var(--lumo-contrast-60pct)");
                stars[i].removeThemeVariants(ButtonVariant.LUMO_PRIMARY);
            }
        }
    }

    public void setEnabled(boolean enabled){
        for (Button b : stars) {
            b.setEnabled(enabled);
        }
    }
}