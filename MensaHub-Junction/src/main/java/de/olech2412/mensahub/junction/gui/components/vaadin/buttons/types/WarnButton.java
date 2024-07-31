package de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

public class WarnButton extends Button {

    public WarnButton(String text) {
        super(text);
        setIcon(VaadinIcon.WARNING.create());
        addClassName("warn-button");
    }

}
