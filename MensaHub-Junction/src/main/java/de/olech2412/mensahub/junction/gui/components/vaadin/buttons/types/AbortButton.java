package de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class AbortButton extends Button {

    public AbortButton(String text) {
        super(text);
        setIcon(VaadinIcon.CLOSE.create());
        addThemeVariants(ButtonVariant.LUMO_ERROR);
    }

}
