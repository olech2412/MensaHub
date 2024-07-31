package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.notification.NotificationVariant;

public class WarnNotification extends MensaHubNotification {

    public WarnNotification(String message) {
        super(message);
        addThemeVariants(NotificationVariant.LUMO_WARNING);
    }

}
