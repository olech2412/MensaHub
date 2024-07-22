package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.notification.NotificationVariant;

public class InfoNotification extends MensaHubNotification {

    public InfoNotification(String message) {
        super(message);
        addThemeVariants(NotificationVariant.LUMO_PRIMARY);
    }
}
