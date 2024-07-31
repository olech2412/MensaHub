package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.edit_job_dialog.EditJobLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.generic.FooterButtonLayout;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.jobs.JobType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EditJobDialog extends Dialog {

    FooterButtonLayout footerButtonLayout = new FooterButtonLayout();

    EditJobLayout editJobLayout;

    public EditJobDialog(List<MailUser> users, LocalDateTime executionTime, JobType jobType) {
        super("Job Bearbeiten");

        configureFooterButtonLayout(footerButtonLayout);

        editJobLayout = new EditJobLayout(users, executionTime, jobType);

        add(editJobLayout);
        add(footerButtonLayout);
    }

    private void configureFooterButtonLayout(FooterButtonLayout footerButtonLayout) {
        Button acceptButton = footerButtonLayout.getAcceptButton();
        acceptButton.setText("Änderungen speichern");
        acceptButton.setIcon(VaadinIcon.ENVELOPE.create());

        footerButtonLayout.acceptButton = acceptButton;

        footerButtonLayout.declineButton.addClickListener(buttonClickEvent -> close());
    }

    public boolean isFilledCorrect() {
        return editJobLayout.isFilledCorrect();
    }

}
