package de.olech2412.mensahub.junction.gui.components.vaadin;

import com.vaadin.flow.component.datepicker.DatePicker;

import java.util.List;
import java.util.Locale;

public class GermanDatePicker extends DatePicker {

    public GermanDatePicker(){
        DatePicker.DatePickerI18n germanI18n = new DatePicker.DatePickerI18n();
        germanI18n.setMonthNames(List.of("Januar", "Februar", "März", "April",
                "Mai", "Juni", "Juli", "August", "September", "Oktober",
                "November", "Dezember"));
        germanI18n.setWeekdays(List.of("Sonntag", "Montag", "Dienstag",
                "Mittwoch", "Donnerstag", "Freitag", "Samstag"));
        germanI18n.setWeekdaysShort(
                List.of("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"));
        germanI18n.setToday("Heute");
        germanI18n.setCancel("Abbrechen");

        setI18n(germanI18n);

        setLocale(Locale.GERMANY);
    }

}
