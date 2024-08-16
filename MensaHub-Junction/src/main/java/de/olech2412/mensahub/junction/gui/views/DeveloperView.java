package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.CookieNotification;
import de.olech2412.mensahub.junction.jpa.repository.API_UserRepository;
import de.olech2412.mensahub.junction.security.SecurityService;
import de.olech2412.mensahub.models.authentification.API_User;
import de.olech2412.mensahub.models.authentification.Role;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedInputStream;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Route("dev")
@PageTitle("Developer Portal")
@RolesAllowed({Role.Names.ROLE_API_USER})
public class DeveloperView extends Composite implements BeforeEnterObserver {
    Logger logger = LoggerFactory.getLogger(MailSettingsView.class);

    private VerticalLayout layout;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private API_UserRepository apiUserRepository;

    private API_User apiUser;

    public DeveloperView() {

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        logger.debug("Developer portal visited");
    }

    private void findUser() {
        apiUser = apiUserRepository.findAPI_UserByApiUsername(securityService.getAuthenticatedUser().getUsername()).get();
    }

    @Override
    protected Component initContent() {
        new CookieNotification(); // check if cookies are already accepted or show the cookie banner
        findUser();
        layout = new VerticalLayout();

        HorizontalLayout headerLayout = new HorizontalLayout(new H1("Developer Portal - " + apiUser.getApiUsername()));
        headerLayout.setWidth(100f, Unit.PERCENTAGE);
        headerLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        VerticalLayout contentLayout = new VerticalLayout();
        contentLayout.setWidth(100f, Unit.PERCENTAGE);
        contentLayout.setHeight(100f, Unit.PERCENTAGE);

        FormLayout formLayout = new FormLayout();

        TextField apiUserName = new TextField("API-Nutzerkennung");
        apiUserName.setValue(apiUser.getApiUsername());
        apiUserName.setEnabled(false);
        apiUserName.setHelperText("Deine API-Nutzerkennung");

        TextField emailField = new TextField("E-Mail");
        emailField.setValue(apiUser.getEmail());
        emailField.setEnabled(false);
        emailField.setHelperText("Deine E-Mail");

        TextArea descriptionField = new TextArea("Beschreibung");
        descriptionField.setValue(apiUser.getDescription());
        descriptionField.setEnabled(false);
        descriptionField.setHelperText("Wofür wird dieser Account genutzt?");

        TextArea blockingReasonField = new TextArea("Sperrgrund");
        blockingReasonField.setValue(String.valueOf(apiUser.getBlockingReason()));
        blockingReasonField.setEnabled(false);
        blockingReasonField.setHelperText("Null wenn der Nutzer nicht gesperrt ist");

        TextField creationDateField = new TextField("Accounterstellung");
        creationDateField.setValue(apiUser.getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        creationDateField.setEnabled(false);
        creationDateField.setHelperText("Zeitpunkt der Erstellung des Accounts");

        TextField lastLoginField = new TextField("Letzte Anmeldung (via API)");
        if (apiUser.getLastLogin() != null) {
            lastLoginField.setValue(apiUser.getLastLogin().format(DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:ss")));
        } else {
            lastLoginField.setValue("Noch nicht über die API angemeldet");
        }

        lastLoginField.setEnabled(false);
        lastLoginField.setHelperText("Letzte Anmeldung an der API");

        TextField verifiedEmailField = new TextField("Ist die E-Mail verifiziert?");
        verifiedEmailField.setValue(apiUser.getVerified_email().toString());
        verifiedEmailField.setEnabled(false);
        verifiedEmailField.setHelperText("Wurde die E-Mail des Nutzers verifiziert");

        TextField enabledByAdmin = new TextField("Durch Admin freigeschaltet?");
        enabledByAdmin.setValue(apiUser.getEnabledByAdmin().toString());
        enabledByAdmin.setEnabled(false);
        enabledByAdmin.setHelperText("Hat der Admin den Nutzer freigeschaltet");

        StreamResource streamResource = new StreamResource("API_Description.pdf", () -> new BufferedInputStream(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("static/img/API_Description.pdf"))));
        Anchor link = new Anchor(streamResource, "Zur API-Dokumentation  ");
        link.add(VaadinIcon.ROCKET.create());

        formLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("500px", 2));

        formLayout.add(apiUserName, emailField, descriptionField, blockingReasonField, creationDateField,
                lastLoginField, verifiedEmailField, enabledByAdmin, link);
        formLayout.setColspan(descriptionField, 2);
        formLayout.setColspan(blockingReasonField, 2);
        formLayout.setColspan(link, 2);

        formLayout.setHeight(100f, Unit.PERCENTAGE);

        contentLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        contentLayout.add(formLayout);

        layout.add(headerLayout, contentLayout);

        return layout;
    }
}