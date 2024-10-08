package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.CookieNotification;
import de.olech2412.mensahub.junction.gui.components.vaadin.panel.AdminPanel;
import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.UsersRepository;
import de.olech2412.mensahub.junction.jpa.services.JobService;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.jpa.services.UserService;
import de.olech2412.mensahub.junction.security.SecurityService;
import de.olech2412.mensahub.models.authentification.Role;
import jakarta.annotation.security.RolesAllowed;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Route("admin")
@PageTitle("Admin Oberfläche")
@RolesAllowed(value = {Role.Names.ROLE_SUPER_ADMIN, Role.Names.ROLE_ADMIN})
public class AdminView extends VerticalLayout {

    public AdminView(MailUserService mailUserService, UsersRepository usersRepository, SecurityService securityService,
                     JobService jobService, ErrorEntityRepository errorEntityRepository, UserService userService) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AdminPanel adminPanel = new AdminPanel(mailUserService, userService, securityService, jobService, usersRepository, errorEntityRepository);
        new CookieNotification(); // check if cookies are already accepted or show the cookie banner

        add(adminPanel);
    }

}
