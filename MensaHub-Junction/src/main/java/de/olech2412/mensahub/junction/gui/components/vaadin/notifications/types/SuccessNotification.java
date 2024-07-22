package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.notification.NotificationVariant;

public class SuccessNotification extends MensaHubNotification {

    public SuccessNotification(String message) {
        super(message);
        addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
