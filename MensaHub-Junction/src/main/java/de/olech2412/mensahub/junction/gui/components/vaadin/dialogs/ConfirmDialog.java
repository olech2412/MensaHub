package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.olech2412.mensahub.junction.gui.components.own.Divider;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.ButtonFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.ButtonType;
import lombok.Getter;

@Getter
public class ConfirmDialog extends Dialog {

    public Button acceptButton = ButtonFactory.create(ButtonType.ACCEPT, "Bestätigen");
    public Button declineButton = ButtonFactory.create(ButtonType.ABORT, "Abbrechen");

    public ConfirmDialog(String header, String infoMessage) {
        setHeaderTitle(header);
        VerticalLayout layout = new VerticalLayout();
        layout.setWidthFull();

        layout.setSpacing(true);
        layout.setPadding(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(acceptButton, declineButton);
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);

        layout.add(new Paragraph());
        layout.add(new Divider(), buttonLayout);

        //add(layout);

        acceptButton.getElement().getStyle().set("margin-right", "auto");
        declineButton.getElement().getStyle().set("margin-left", "auto");

        acceptButton.addFocusShortcut(Key.ENTER);
        declineButton.addFocusShortcut(Key.ENTER);
    }

}
