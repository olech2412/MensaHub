package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.olech2412.mensahub.junction.security.SecurityService;
import de.olech2412.mensahub.models.authentification.Role;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

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
        List<? extends GrantedAuthority> authorities = securityService.getAuthenticatedUser().getAuthorities().stream().toList();
        if (authorities.contains(new SimpleGrantedAuthority(Role.Names.ROLE_API_USER))) {
            beforeEnterEvent.forwardTo("/dev");
        } else if (authorities.contains(new SimpleGrantedAuthority(Role.Names.ROLE_LOGIN_USER))) {
            beforeEnterEvent.forwardTo("/newsletter");
        } else {
            beforeEnterEvent.forwardTo("/admin");
        }
    }
}
