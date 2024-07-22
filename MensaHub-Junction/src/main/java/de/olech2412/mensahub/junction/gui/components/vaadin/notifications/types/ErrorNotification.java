package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class ErrorNotification extends MensaHubNotification{

    public ErrorNotification(String message) {
        super(message);
        addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
