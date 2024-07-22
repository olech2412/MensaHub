package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.dialogs.create_job_dialog;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import de.olech2412.mensahub.junction.gui.components.vaadin.datetimepicker.GermanDateTimePicker;
import de.olech2412.mensahub.models.authentification.Users;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ExtendedConfLayout extends FormLayout {

    ComboBox<Users> proponentComboBox = new ComboBox<>("Befürworter");
    GermanDateTimePicker executeAtTimePicker = new GermanDateTimePicker();

    public ExtendedConfLayout(List<Users> proponents) {
        proponentComboBox.setItems(proponents);
        proponentComboBox.setAllowCustomValue(false);
        proponentComboBox.setItemLabelGenerator(Users::getUsername);

        executeAtTimePicker.setTooltipText("Es wird alle 5 Minuten auf Jobs geprüft. Wenn z.B. 12:12 Uhr ausgewählt wird," +
                " wird dieser Job erst um 12:15 Uhr ausgeführt.");
        executeAtTimePicker.setLabel("Zeitpunkt der Ausführung");
        executeAtTimePicker.setMin(LocalDateTime.now().plusMinutes(10));

        Span infoSpan = new Span(VaadinIcon.EXCLAMATION_CIRCLE.create(),
                new Span(" Bei den folgenden Feldern handelt es sich um optionale Felder."));
        infoSpan.getElement().getThemeList().add("badge error");

        add(infoSpan);
        add(proponentComboBox);
        add(executeAtTimePicker);
    }

    public boolean isFilledCorrect() {
        return !proponentComboBox.isInvalid() && !executeAtTimePicker.isInvalid();
    }

}