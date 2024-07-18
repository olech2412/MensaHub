package de.olech2412.mensahub.junction.gui.components.vaadin.layouts;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.ButtonFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.ButtonType;
import lombok.Getter;

@Getter
public class FooterButtonLayout extends HorizontalLayout {

    public Button acceptButton = ButtonFactory.create(ButtonType.ACCEPT, "Akzeptieren");
    public Button declineButton = ButtonFactory.create(ButtonType.ABORT, "Abbrechen");

    public FooterButtonLayout() {
        setWidthFull();

        setSpacing(true);
        setPadding(true);

        add(acceptButton);
        add(declineButton);

        setVerticalComponentAlignment(Alignment.START, acceptButton);
        setVerticalComponentAlignment(Alignment.END, declineButton);

        expand(acceptButton, declineButton);
    }

}
