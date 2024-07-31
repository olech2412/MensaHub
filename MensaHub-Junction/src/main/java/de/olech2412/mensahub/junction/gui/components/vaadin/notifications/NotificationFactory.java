package de.olech2412.mensahub.junction.gui.components.vaadin.notifications;

import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.*;

public class NotificationFactory {

    public static MensaHubNotification create(NotificationType type, String message) {
        switch (type) {
            case INFO:
                return new InfoNotification(message);
            case WARN:
                return new WarnNotification(message);
            case ERROR:
                return new ErrorNotification(message);
            case SUCCESS:
                return new SuccessNotification(message);
            case CUSTOM:
                return new MensaHubNotification(message);
            default:
                throw new IllegalArgumentException("Unsupported notification type: " + type);
        }
    }

}
