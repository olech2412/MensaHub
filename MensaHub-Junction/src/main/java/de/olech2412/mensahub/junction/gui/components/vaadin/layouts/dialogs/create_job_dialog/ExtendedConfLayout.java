package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.create_job_dialog;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextField;
import de.olech2412.mensahub.junction.gui.components.vaadin.datetimepicker.GermanDateTimePicker;
import de.olech2412.mensahub.models.authentification.Users;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ExtendedConfLayout extends FormLayout {

    ComboBox<Users> proponentComboBox = new ComboBox<>("Befürworter");
    GermanDateTimePicker executeAtTimePicker = new GermanDateTimePicker();
    TextField pushMessageTitle = new TextField("Push Message Titel");
    TextField pushMessageMessage = new TextField("Push Message Message");

    public ExtendedConfLayout(List<Users> proponents) {
        proponentComboBox.setItems(proponents);
        proponentComboBox.setAllowCustomValue(false);
        proponentComboBox.setItemLabelGenerator(Users::getUsername);

        if (proponents.isEmpty()) {
            proponentComboBox.setHelperText("Du musst zuerst einen gültigen Befürworter anlegen um diesen nutzen zu können.");
            proponentComboBox.setEnabled(false);
        }

        executeAtTimePicker.setTooltipText("Es wird alle 5 Minuten auf Jobs geprüft. Wenn z.B. 12:12 Uhr ausgewählt wird," +
                " wird dieser Job erst um 12:15 Uhr ausgeführt.");
        executeAtTimePicker.setLabel("Zeitpunkt der Ausführung");
        executeAtTimePicker.setMin(LocalDateTime.now().plusMinutes(10));

        pushMessageTitle.setHelperText("Der Titel der Push Message");
        pushMessageTitle.setTooltipText("Hat nur Auswirkungen, wenn der Job mit Push Messages zu tun hat");
        pushMessageMessage.setHelperText("Die Nachricht der Push Message");
        pushMessageMessage.setTooltipText("Hat nur Auswirkungen, wenn der Job mit Push Messages zu tun hat");

        Span infoSpan = new Span(VaadinIcon.EXCLAMATION_CIRCLE.create(),
                new Span(" Bei den folgenden Feldern handelt es sich um optionale Felder."));
        infoSpan.getElement().getThemeList().add("badge error");

        add(infoSpan);
        add(proponentComboBox);
        add(executeAtTimePicker);
        add(pushMessageTitle);
        add(pushMessageMessage);
    }

    public boolean isFilledCorrect() {
        return !proponentComboBox.isInvalid() && !executeAtTimePicker.isInvalid();
    }

}