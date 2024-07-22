package de.olech2412.mensahub.junction.gui.components.vaadin.datetimepicker;

import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;

import java.util.List;
import java.util.Locale;

public class GermanDateTimePicker extends DateTimePicker {

    public GermanDateTimePicker(){
        DatePicker.DatePickerI18n germanI18nDatePicker = new DatePicker.DatePickerI18n();
        germanI18nDatePicker.setMonthNames(List.of("Januar", "Februar", "März", "April",
                "Mai", "Juni", "Juli", "August", "September", "Oktober",
                "November", "Dezember"));
        germanI18nDatePicker.setWeekdays(List.of("Sonntag", "Montag", "Dienstag",
                "Mittwoch", "Donnerstag", "Freitag", "Samstag"));
        germanI18nDatePicker.setWeekdaysShort(
                List.of("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa"));
        germanI18nDatePicker.setToday("Heute");
        germanI18nDatePicker.setCancel("Abbrechen");

        setDateAriaLabel("Datum");
        setTimeAriaLabel("Uhrzeit");
        setDatePickerI18n(germanI18nDatePicker);

        setLocale(Locale.GERMANY);
    }

}
