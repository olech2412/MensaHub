package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.edit_job_dialog;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.jobs.JobType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class EditJobLayout extends FormLayout {

    MultiSelectComboBox<MailUser> mailUserMultiSelectComboBox;

    DateTimePicker executeAtTimePicker;

    ComboBox<JobType> jobTypeComboBox;

    public EditJobLayout(List<MailUser> mailUsers, LocalDateTime executionTime, JobType jobType) {
        mailUserMultiSelectComboBox = new MultiSelectComboBox<>("Betroffene E-Mail Accounts anpassen");
        mailUserMultiSelectComboBox.setItems(mailUsers);
        mailUserMultiSelectComboBox.setItemLabelGenerator(MailUser::getEmail);
        mailUserMultiSelectComboBox.setAllowCustomValue(false);
        mailUserMultiSelectComboBox.setValue(mailUsers);

        executeAtTimePicker = new DateTimePicker();
        executeAtTimePicker.setTooltipText("Es wird alle 5 Minuten auf Jobs geprüft. Wenn z.B. 12:12 Uhr ausgewählt wird," +
                " wird dieser Job erst um 12:15 Uhr ausgeführt.");
        executeAtTimePicker.setLabel("Zeitpunkt der Ausführung");
        executeAtTimePicker.setMin(LocalDateTime.now().plusMinutes(10));
        executeAtTimePicker.setValue(executionTime);

        jobTypeComboBox = new ComboBox<>();
        jobTypeComboBox.setLabel("Auszuführender Job");
        jobTypeComboBox.setAllowCustomValue(false);
        jobTypeComboBox.setItems(JobType.SEND_EMAILS, JobType.SEND_UPDATES);
        jobTypeComboBox.setValue(jobType);

        add(mailUserMultiSelectComboBox, executeAtTimePicker, jobTypeComboBox);
    }

    public boolean isFilledCorrect() {
        if (!mailUserMultiSelectComboBox.isEmpty() && !mailUserMultiSelectComboBox.isInvalid()) {
            if (!jobTypeComboBox.isInvalid() && !jobTypeComboBox.isEmpty()) {
                if (executeAtTimePicker.isEmpty()) {
                    return true;
                } else {
                    if (!executeAtTimePicker.isInvalid()) {
                        return true;
                    }
                }
            }
        }
        NotificationFactory.create(NotificationType.ERROR, "Ungültige Eingaben").open();
        return false;
    }

}
