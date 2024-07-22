package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.html.Hr;

public class Divider extends Hr {

    public Divider() {
        setClassName("divider");
        getElement().getStyle().setBorder("1px solid #ccc");
    }
}