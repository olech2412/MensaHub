package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.CookieNotification;
import lombok.extern.slf4j.Slf4j;

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
@Slf4j
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();

        new CookieNotification(); // check if cookies are already accepted or show the cookie banner

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);

        LoginI18n loginI18n = new LoginI18n();

        LoginI18n.ErrorMessage loginI18nError = new LoginI18n.ErrorMessage();
        loginI18nError.setMessage("Korrigiere deine Eingaben");
        loginI18nError.setTitle("Anmeldung fehlgeschlagen");

        LoginI18n.Header loginI18nHeader = new LoginI18n.Header();
        loginI18nHeader.setTitle("Anmeldung");

        LoginI18n.Form loginI18nForm = new LoginI18n.Form();
        loginI18nForm.setTitle("Anmeldung");
        loginI18nForm.setPassword("Passwort");
        loginI18nForm.setSubmit("Anmelden");
        loginI18nForm.setUsername("Nutzerkennung");

        loginI18n.setErrorMessage(loginI18nError);
        loginI18n.setForm(loginI18nForm);
        loginI18n.setHeader(loginI18nHeader);

        login.setI18n(loginI18n);

        log.info(VaadinSession.getCurrent().getBrowser().getAddress());

        StreamResource logoStream = new StreamResource("mensaHub_logo.webp", () -> getClass().getResourceAsStream("/static/img/MensaHub_logo.webp"));
        Image logoImage = new Image(logoStream, "Logo");

        Span info = new Span("Diese Anwendung wird ausschließlich für private Zwecke genutzt!");
        info.getElement().getThemeList().add("badge error");

        add(logoImage, login, info);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}