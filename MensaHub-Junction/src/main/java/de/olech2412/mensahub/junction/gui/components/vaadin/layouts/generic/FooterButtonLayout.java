package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.generic;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.olech2412.mensahub.junction.gui.components.own.Divider;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.ButtonFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.ButtonType;
import lombok.Getter;

@Getter
public class FooterButtonLayout extends VerticalLayout {

    public Button acceptButton = ButtonFactory.create(ButtonType.ACCEPT, "Akzeptieren");
    public Button declineButton = ButtonFactory.create(ButtonType.ABORT, "Abbrechen");

    public FooterButtonLayout() {
        setWidthFull();

        setSpacing(true);
        setPadding(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(acceptButton, declineButton);
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);

        add(new Divider(), buttonLayout);

        acceptButton.getElement().getStyle().set("margin-right", "auto");
        declineButton.getElement().getStyle().set("margin-left", "auto");

        acceptButton.addFocusShortcut(Key.ENTER);
        declineButton.addFocusShortcut(Key.ENTER);
    }

}
