package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.webpush.WebPush;
import com.vaadin.flow.server.webpush.WebPushMessage;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.generic.FooterButtonLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.helper.SubscriptionConverter;
import de.olech2412.mensahub.junction.jpa.repository.SubscriptionEntityRepository;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.webpush.WebPushService;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.SubscriptionEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Subscription;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Slf4j
public class PushNotificationDialog extends Dialog {

    private FooterButtonLayout footerButtonLayout = new FooterButtonLayout();

    private WebPushService webPushService;

    private MailUserService mailUserService;

    private SubscriptionEntityRepository subscriptionEntityRepository;

    private MailUser currentUser;

    public PushNotificationDialog(WebPushService webPushService, MailUserService mailUserService, MailUser mailUser, SubscriptionEntityRepository subscriptionEntityRepository) {
        super("Push-Benachrichtigung");

        this.subscriptionEntityRepository = subscriptionEntityRepository;
        this.webPushService = webPushService;
        this.mailUserService = mailUserService;

        currentUser = mailUserService.initialize(mailUser);

        // iOS & iPadOS Header
        Icon warningIcon = new Icon(VaadinIcon.WARNING);
        warningIcon.getStyle().set("color", "#ffcc00");
        warningIcon.getStyle().set("margin-right", "10px");

        Span iosTitle = new Span("iOS & iPadOS Support");
        iosTitle.getStyle().set("font-weight", "bold");
        iosTitle.getStyle().set("font-size", "16px");

        Div iosHeader = new Div(warningIcon, iosTitle);
        iosHeader.getStyle().set("display", "flex");
        iosHeader.getStyle().set("align-items", "center");

        // iOS & iPadOS Beschreibung und Liste
        Span iosDescription = new Span("Mobile Web Push für iOS und iPadOS erfordert Folgendes:");

        UnorderedList iosRequirementsList = new UnorderedList(
                new ListItem("iOS- oder iPadOS-Version 16.4 oder höher;"),
                new ListItem("Der Benutzer muss die Webanwendung über das Menü „Teilen“ in Safari zum Startbildschirm hinzufügen; und"),
                new ListItem("Eine vom Benutzer generierte Aktion ist erforderlich, um die Berechtigungsaufforderung auf der zum Startbildschirm hinzugefügten Webanwendung zu aktivieren.")
        );

        Span iosAdditionalInfo = new Span("Für iOS und iPadOS muss die Registrierung in der installierten Webanwendung erfolgen. ");

        Span safariInfo = new Span("Außerdem müssen in Safari die Web-Push-Benachrichtigungsfunktionen aktiviert sein. Gehe dazu zu ");
        Span safariPath = new Span("Einstellungen → Safari → Erweitert → Experimentelle Funktionen.");
        safariPath.getStyle().set("font-style", "italic");
        safariInfo.add(safariPath);
        Span enableNotifications = new Span(" Dort kannst du ");
        Span notifications = new Span("Benachrichtigungen");
        notifications.getStyle().set("background-color", "#e0e0e0").set("padding", "2px 4px").set("border-radius", "4px");
        Span andText = new Span(" und ");
        Span pushApi = new Span("Push-API");
        pushApi.getStyle().set("background-color", "#e0e0e0").set("padding", "2px 4px").set("border-radius", "4px");
        safariInfo.add(enableNotifications, notifications, andText, pushApi, new Text(" aktivieren."));

        Div iosContent = new Div(iosHeader, iosDescription, iosRequirementsList, iosAdditionalInfo, safariInfo);
        iosContent.getStyle().set("padding", "10px");
        iosContent.getStyle().set("border", "1px solid #ffcc00");
        iosContent.getStyle().set("border-radius", "8px");
        iosContent.getStyle().set("background-color", "#fdfde8");
        iosContent.getStyle().set("margin-bottom", "20px");

        // Brave Browser Header
        Span braveTitle = new Span("Brave Browser Support");
        braveTitle.getStyle().set("font-weight", "bold");
        braveTitle.getStyle().set("font-size", "16px");

        Div braveHeader = new Div(warningIcon, braveTitle);
        braveHeader.getStyle().set("display", "flex");
        braveHeader.getStyle().set("align-items", "center");

        // Brave Beschreibung
        Span braveDescription = new Span("Für den Brave-Browser können Web-Push-Benachrichtigungen standardmäßig funktionieren, wenn der Browser erstmals installiert wird. Falls nicht, müssen Benachrichtigungen im Browser aktiviert werden. ");

        Span braveAdditionalInfo = new Span("Der Benutzer sollte die Datenschutzeinstellungen des Browsers öffnen (z. B. ");
        Span bravePath = new Span("brave://settings/privacy");
        bravePath.getStyle().set("font-style", "italic");
        braveAdditionalInfo.add(bravePath, new Text(") und die Option „Google-Dienste für Push-Nachrichten verwenden“ aktivieren."));

        Div braveContent = new Div(braveHeader, braveDescription, braveAdditionalInfo);
        braveContent.getStyle().set("padding", "10px");
        braveContent.getStyle().set("border", "1px solid #ffcc00");
        braveContent.getStyle().set("border-radius", "8px");
        braveContent.getStyle().set("background-color", "#fdfde8");

        footerButtonLayout.declineButton.addClickListener(buttonClickEvent -> this.close());
        footerButtonLayout.acceptButton.setVisible(false);
        footerButtonLayout.acceptButton.setWidth(0, Unit.PIXELS);
        footerButtonLayout.declineButton.setWidth(100f, Unit.PERCENTAGE);

        WebPush webpush = webPushService.getWebPush();

        Button subscribe = new Button("Anmelden");
        subscribe.setIcon(VaadinIcon.CHECK.create());
        Button unsubscribe = new Button("Abmelden");
        unsubscribe.setIcon(VaadinIcon.TRASH.create());

        webpush.fetchExistingSubscription(UI.getCurrent(), subscription -> {
            log.info(subscription != null ? subscription.toString() : "No subscription found");

            subscribe.setEnabled(subscription == null);
            unsubscribe.setEnabled(subscription != null);
        });

        subscribe.addClickListener(e -> {
            webpush.subscribe(subscribe.getUI().get(), subscription -> {
                subscribe.setEnabled(false);
                unsubscribe.setEnabled(true);
                currentUser.setPushNotificationsEnabled(true);

                List<SubscriptionEntity> existingSubscriptions = new java.util.ArrayList<>(currentUser.getSubscriptions().stream().toList());
                existingSubscriptions.add(SubscriptionConverter.convertToEntity(subscription, VaadinSession.getCurrent().getBrowser().getBrowserApplication()));
                currentUser.setSubscriptions(existingSubscriptions);

                mailUserService.saveMailUser(currentUser);

                NotificationFactory.create(NotificationType.SUCCESS, "Push Notifications wurden abonniert. Überprüfe den Empfang der Testnachricht," +
                        " wenn du diese nicht erhalten hast, überprüfe deine System-/Browsereinstellungen").open();

                webpush.sendNotification(subscription, new WebPushMessage("MensaHub-Test", "Wenn du diese Nachricht empfangen kannst," +
                        " wurden die Push Benachrichtigungen erfolgreich eingerichtet"));

                log.info("User {} enabled push notifications. Endpoint: {}. Device: {}", currentUser.getEmail(),
                        subscription.endpoint(), VaadinSession.getCurrent().getBrowser());
            });
        });

        unsubscribe.addClickListener(e -> {
            webpush.unsubscribe(unsubscribe.getUI().get(), subscription -> {

                subscribe.setEnabled(true);
                unsubscribe.setEnabled(false);
                currentUser.setPushNotificationsEnabled(false);

                SubscriptionEntity subscriptionEntity = subscriptionEntityRepository.findByEndpoint(subscription.endpoint());
                subscriptionEntityRepository.delete(subscriptionEntity);

                NotificationFactory.create(NotificationType.SUCCESS, "Push Notifications wurden für dich deaktiviert").open();

                log.info("User {} disabled push notifications. Endpoint: {}. Device: {}", currentUser.getEmail(),
                        subscription.endpoint(), VaadinSession.getCurrent().getBrowser());
            });
        });

        HorizontalLayout subscribeLayout = new HorizontalLayout(subscribe, unsubscribe);
        subscribeLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        subscribeLayout.setSpacing(true);
        subscribeLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        add(iosContent, braveContent, subscribeLayout, footerButtonLayout);
    }
}