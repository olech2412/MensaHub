package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class CookieNotification extends Notification {


    public CookieNotification() {
        // Überprüfen, ob die Zustimmung bereits gegeben wurde
        UI.getCurrent().getPage().executeJs("return !!window.localStorage.getItem('cookie-consent-accepted');").then(Boolean.class, consentGiven -> {
            if (Boolean.FALSE.equals(consentGiven)) {
                showNotification();
            }
        });
    }

    private void showNotification() {
        // Nachricht für das Cookie-Banner
        Span message = new Span("This website uses cookies to ensure you get the best experience.");

        // Link zur weiteren Information
        Anchor link = new Anchor("https://www.cookiesandyou.com/", "Learn more");
        link.setTarget("_blank"); // Öffnet den Link in einem neuen Tab

        Button acceptButton = new Button("Got it!", event -> {
            // Setzt die Zustimmung im localStorage
            UI.getCurrent().getPage().executeJs("window.localStorage.setItem('cookie-consent-accepted', 'true');");
            this.close();
        });

        // Styling des Buttons wie im Vaadin-Original
        acceptButton.getStyle().set("background-color", "#007bff");
        acceptButton.getStyle().set("color", "white");
        acceptButton.getStyle().set("border", "none");
        acceptButton.getStyle().set("padding", "5px 15px");
        acceptButton.getStyle().set("border-radius", "4px");

        HorizontalLayout contentLayout = new HorizontalLayout(message, link, acceptButton);
        contentLayout.setSpacing(true);
        contentLayout.setPadding(false);
        contentLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        this.add(contentLayout);
        this.setPosition(Position.BOTTOM_CENTER);
        this.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        this.setDuration(0);

        this.open();
    }
}
