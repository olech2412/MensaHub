package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@CssImport("./styles/custom-display.css")
public class CustomDisplay extends Composite<VerticalLayout> {

    private Div valueDisplay;
    private int thresholdGreen;
    private int thresholdYellow;
    private int thresholdRed;

    public CustomDisplay() {
        valueDisplay = new Div();
        valueDisplay.setClassName("value-display");

        getContent().add(valueDisplay);
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
            valueDisplay.getElement().getClassList().add("green");
            valueDisplay.getElement().getClassList().remove("yellow");
            valueDisplay.getElement().getClassList().remove("red");
        } else if (value <= thresholdYellow) {
            valueDisplay.getElement().getClassList().remove("green");
            valueDisplay.getElement().getClassList().add("yellow");
            valueDisplay.getElement().getClassList().remove("red");
        } else if (value >= thresholdRed) {
            valueDisplay.getElement().getClassList().remove("green");
            valueDisplay.getElement().getClassList().remove("yellow");
            valueDisplay.getElement().getClassList().add("red");
        }
    }
}
