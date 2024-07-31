package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.notification.Notification;

public class MensaHubNotification extends Notification {

    public MensaHubNotification(String message) {
        super(message);
        setDuration(3000);
        setPosition(Position.BOTTOM_START);
    }

}
