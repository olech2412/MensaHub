package de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class AcceptButton extends Button {

    public AcceptButton(String text) {
        super(text);
        setIcon(VaadinIcon.CHECK.create());
        addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
    }

}
