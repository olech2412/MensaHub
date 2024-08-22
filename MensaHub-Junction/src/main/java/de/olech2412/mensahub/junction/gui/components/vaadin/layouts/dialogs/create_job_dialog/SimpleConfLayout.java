package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.create_job_dialog;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.jobs.JobType;
import lombok.Getter;

import java.util.List;

@Getter
public class SimpleConfLayout extends FormLayout {

    MultiSelectComboBox<MailUser> mailUserComboBox = new MultiSelectComboBox<>();

    TextField destinationTextField = new TextField("Destination");

    ComboBox<JobType> jobToExecuteComboBox = new ComboBox<>();

    public SimpleConfLayout(List<MailUser> mailUsers, String destinationAddress) {
        mailUserComboBox.setLabel("Betroffene User");
        mailUserComboBox.setItems(mailUsers);
        mailUserComboBox.setAllowCustomValue(false);
        mailUserComboBox.setItemLabelGenerator(MailUser::getEmail);

        destinationTextField.setLabel("Welche MensaHub Anwendung erhält den Job?");
        destinationTextField.setValue(destinationAddress);
        destinationTextField.setReadOnly(true);
        destinationTextField.setTooltipText("Stelle sicher, dass diese Addresse nicht von außen aufgerufen werden kann!");

        jobToExecuteComboBox.setLabel("Auszuführender Job");
        jobToExecuteComboBox.setAllowCustomValue(false);
        jobToExecuteComboBox.setItems(JobType.SEND_EMAILS, JobType.SEND_UPDATES, JobType.SEND_PUSH_NOTIFICATION);

        add(mailUserComboBox, jobToExecuteComboBox, destinationTextField);
        setColspan(destinationTextField, 2);
    }

    public boolean isFilledCorrect() {
        if (mailUserComboBox.isEmpty() || mailUserComboBox.getValue().isEmpty()) {
            mailUserComboBox.setInvalid(true);
            mailUserComboBox.setHelperText("Es müssen Nutzer ausgewählt werden.");
            return false;
        } else {
            mailUserComboBox.setInvalid(false);
        }

        if (jobToExecuteComboBox.isEmpty() || jobToExecuteComboBox.getValue() == null) {
            jobToExecuteComboBox.setInvalid(true);
            jobToExecuteComboBox.setHelperText("Die Art des Jobs muss angegeben werden.");
            return false;
        } else {
            jobToExecuteComboBox.setInvalid(false);
        }

        if (destinationTextField.isEmpty()) {
            destinationTextField.setInvalid(true);
            jobToExecuteComboBox.setHelperText("Der Zielendpunkt muss bekannt sein");
            return false;
        } else {
            destinationTextField.setInvalid(false);
        }

        return !mailUserComboBox.isInvalid() && !jobToExecuteComboBox.isInvalid() && !destinationTextField.isInvalid();
    }

}
