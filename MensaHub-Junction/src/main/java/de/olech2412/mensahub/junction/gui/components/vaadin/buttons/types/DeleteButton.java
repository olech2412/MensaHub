package de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class DeleteButton extends Button {

    public DeleteButton(String text) {
        super(text);
        setIcon(VaadinIcon.TRASH.create());
        addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
    }

}
