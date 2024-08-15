package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.server.VaadinService;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.generic.FooterButtonLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AppleDeviceUserCodeDialog extends Dialog {

    public AppleDeviceUserCodeDialog() {
        H2 title = new H2("Apple Link-Einschränkung");

        Paragraph description = new Paragraph("Apple erlaubt PWAs leider nicht, Links direkt zu öffnen. " +
                "Wenn du einen Link zum Speiseplan hast (mit Nutzerkennung), " +
                "kannst du diesen unten einfügen. Wir speichern deine Nutzerkennung, " +
                "und du kannst auch in der App die Bewertungsfunktion nutzen. Wenn du aktuell keinen Link hast, kannst du " +
                "\"Abbrechen\" drücken. Die Nachricht wird dir nicht erneut angezeigt. Wenn du den Link später nachreichen möchtest " +
                "kannst du einfach die Daten der App löschen. Der Dialog erscheint dann erneut.");

        TextField linkField = new TextField("Füge hier deinen Link ein:");
        linkField.setPlaceholder("https://example.com/mealPlan?userCode=XYZ123");
        linkField.setWidth(100f, Unit.PERCENTAGE);

        FooterButtonLayout footerButtonLayout = new FooterButtonLayout();

        footerButtonLayout.getAcceptButton().addClickListener(buttonClickEvent -> {
            String inputLink = linkField.getValue();
            String userCode = extractUserCodeFromLink(inputLink);

            if (userCode != null) {
                saveUserCodeAsCookie(userCode);
                NotificationFactory.create(NotificationType.SUCCESS, "Nutzerkennung erfolgreich gespeichert").open();
                // speichern im localstorage, damit info nicht erneut angezeigt wird sowie die nutzer scam notification
                UI.getCurrent().getPage().executeJs("window.localStorage.setItem('infoNotificationShown', 'true');");
                close();
                UI.getCurrent().getPage().reload();
            } else {
                log.error("Apple User inserted invalid link: {}", inputLink);
                NotificationFactory.create(NotificationType.ERROR, "Der Link ist leider ungültig, und kann nicht gelesen werden").open();
                close();
            }
        });

        footerButtonLayout.getDeclineButton().addClickListener(buttonClickEvent -> {
            UI.getCurrent().getPage().executeJs("window.localStorage.setItem('applePWAInfoNotificationShown', 'true');");
            close();
        });

        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        VerticalLayout layout = new VerticalLayout(title, description, linkField, footerButtonLayout);
        add(layout);
    }

    public static boolean isAppleDevice() {
        String userAgent = VaadinService.getCurrentRequest().getHeader("User-Agent");
        return userAgent != null && (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("Mac"));
    }

    private String extractUserCodeFromLink(String link) {
        // Regex, um den userCode aus dem Link zu extrahieren
        Pattern pattern = Pattern.compile("[?&]userCode=([^&]+)");
        Matcher matcher = pattern.matcher(link);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private void saveUserCodeAsCookie(String userCode) {
        Cookie userCodeCookie = new Cookie("userCode", userCode);
        userCodeCookie.setPath("/");
        userCodeCookie.setMaxAge(2147483647);
        userCodeCookie.setSecure(true);
        userCodeCookie.setHttpOnly(true);
        VaadinService.getCurrentResponse().addCookie(userCodeCookie);
    }
}
