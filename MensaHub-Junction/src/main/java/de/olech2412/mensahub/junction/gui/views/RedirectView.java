package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.olech2412.mensahub.junction.security.SecurityService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@PageTitle("MensaHub-Redirect")
@Route(value = "")
@PermitAll
public class RedirectView extends HorizontalLayout implements BeforeEnterObserver {

    @Autowired
    private SecurityService securityService;

    public RedirectView() {

    }

    /**
     * based on the user, this view only redirect the user to admin view if he is a developer or to newsletter for standard users
     *
     * @param beforeEnterEvent
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (securityService.getAuthenticatedUser().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DEV"))) {
            beforeEnterEvent.forwardTo("/dev");
        } else {
            beforeEnterEvent.forwardTo("/newsletter");
        }
    }
}
