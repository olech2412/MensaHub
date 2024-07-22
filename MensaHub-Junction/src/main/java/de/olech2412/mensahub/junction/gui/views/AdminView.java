package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.jpa.repository.UsersRepository;
import de.olech2412.mensahub.junction.jpa.services.JobService;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.gui.components.vaadin.panel.AdminPanel;
import de.olech2412.mensahub.junction.security.SecurityService;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Route("admin")
@PageTitle("Admin Oberfläche")
@AnonymousAllowed
public class AdminView extends VerticalLayout {

    public AdminView(MailUserService mailUserService, UsersRepository usersRepository, SecurityService securityService,
                     JobService jobService) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        AdminPanel adminPanel = new AdminPanel(mailUserService, usersRepository, securityService, jobService);

        add(adminPanel);
    }

}
