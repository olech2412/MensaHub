package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class CustomDisplay extends Composite<VerticalLayout> {

    private final Div titleDisplay;
    private final Div valueDisplay;
    private final Span statusBadge;
    private int thresholdGreen;
    private int thresholdYellow;
    private int thresholdRed;

    public CustomDisplay(String title) {
        addClassName("custom-display");
        titleDisplay = new Div();
        titleDisplay.setText(title);
        titleDisplay.setClassName("title-display");

        statusBadge = new Span();
        statusBadge.setClassName("status-badge");

        HorizontalLayout titleLayout = new HorizontalLayout(titleDisplay);
        titleLayout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.START);

        valueDisplay = new Div();
        valueDisplay.setClassName("value-display");

        getContent().add(statusBadge, titleLayout, valueDisplay);
        setThresholds(10, 20, 30); // default thresholds
    }

    public void setValue(String value) {
        valueDisplay.setText(value);
        updateColor(Integer.parseInt(value));
    }

    public void setThresholds(int green, int yellow, int red) {
        this.thresholdGreen = green;
        this.thresholdYellow = yellow;
        this.thresholdRed = red;
    }

    private void updateColor(int value) {
        if (value <= thresholdGreen) {
            statusBadge.setText("Normal");
            statusBadge.getElement().getThemeList().add("badge success");
            statusBadge.getElement().getStyle().set("background-color", "white");
        } else if (value <= thresholdYellow) {
            statusBadge.setText("Achtung!");
            statusBadge.getElement().getThemeList().add("badge");
            statusBadge.getElement().getStyle().set("background-color", "white");
        } else if (value >= thresholdRed) {
            statusBadge.setText("Kritisch");
            statusBadge.getElement().getThemeList().add("badge error");
            statusBadge.getElement().getStyle().set("background-color", "white");
        }
    }
}