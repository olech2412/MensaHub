package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.gui.components.own.Divider;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.create_job_dialog.ExtendedConfLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.create_job_dialog.SimpleConfLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.generic.FooterButtonLayout;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import lombok.Getter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Getter
public class CreateJobDialog extends Dialog {

    FooterButtonLayout footerButtonLayout = new FooterButtonLayout();

    SimpleConfLayout simpleConfLayout;

    ExtendedConfLayout extendedConfLayout;

    public CreateJobDialog(List<MailUser> users, List<Users> proponents) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        super("Neuen Job anlegen");

        configureFooterButtonLayout(footerButtonLayout);

        simpleConfLayout = new SimpleConfLayout(users,
                Config.getInstance().getProperty("mensaHub.dataDispatcher.local.address"));
        extendedConfLayout = new ExtendedConfLayout(proponents);

        Accordion accordion = new Accordion();
        accordion.addClassName("smooth-accordion");

        AccordionPanel accordionPanel = new AccordionPanel("Erweiterte Einstellungen");
        accordionPanel.add(extendedConfLayout);
        accordion.add(accordionPanel);

        add(simpleConfLayout, new Divider(), accordionPanel);
        add(footerButtonLayout);
    }

    private void configureFooterButtonLayout(FooterButtonLayout footerButtonLayout) {
        Button acceptButton = footerButtonLayout.getAcceptButton();
        acceptButton.setText("Job abschicken");
        acceptButton.setIcon(VaadinIcon.ENVELOPE.create());

        footerButtonLayout.acceptButton = acceptButton;

        footerButtonLayout.declineButton.addClickListener(buttonClickEvent -> close());
    }

    public boolean isFilledCorrect(){
        return simpleConfLayout.isFilledCorrect() && extendedConfLayout.isFilledCorrect();
    }

}
