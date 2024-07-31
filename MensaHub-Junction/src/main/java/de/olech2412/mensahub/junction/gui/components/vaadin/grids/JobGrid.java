package de.olech2412.mensahub.junction.gui.components.vaadin.grids.jobs;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import de.olech2412.mensahub.models.jobs.Job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class JobGrid extends Grid<Job> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);

    public JobGrid(String currentUsername){
        setNestedNullBehavior(NestedNullBehavior.ALLOW_NULLS);
        setWidth(100f, Unit.PERCENTAGE);

        addColumn(job -> job.getJobType().toString()).setHeader("Typ").setAutoWidth(true).setSortable(true);
        addColumn(job -> job.getCreator().getUsername()).setHeader("Ersteller").setAutoWidth(true).setSortable(true);
        addColumn(job -> {
            if (job.getMailUsers() == null || job.getMailUsers().isEmpty()){
                return 0;
            } else {
                return job.getMailUsers().size();
            }
        }).setHeader("Betroffene Nutzer").setAutoWidth(true).setSortable(true);
        addColumn(job -> job.getProponent().getUsername()).setHeader("Befürworter").setAutoWidth(true).setSortable(true);
        addColumn(job -> formatDateTime(job.getCreationDate())).setHeader("Zeitpunkt Erstellung").setAutoWidth(true).setSortable(true);
        addColumn(job -> formatDateTime(job.getExecuteAt())).setHeader("Zeitpunkt Ausführung").setAutoWidth(true).setSortable(true);
        addComponentColumn(job -> formatBoolean(job.isExecuted())).setHeader("Ausgeführt").setAutoWidth(true).setSortable(true);
        addComponentColumn(job -> formatBoolean(job.isEnabled())).setHeader("Freigeschaltet").setAutoWidth(true).setSortable(true);
        addComponentColumn(job -> formatBoolean(job.isNeedsPermission())).setHeader("Benötigt Erlaubnis").setAutoWidth(true).setSortable(true);
        addColumn(job -> job.getJobStatus().toString()).setHeader("Status").setAutoWidth(true).setSortable(true);
        setTooltipGenerator(job -> "Eindeutige Id: " + job.getUuid().toString());

        setClassNameGenerator(job -> {
            if(job.getProponent() != null && !job.isExecuted()){
                if (job.getProponent().getUsername().equals(currentUsername)){
                    return "pending-job";
                }
            }
            return "";
        });
        addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.MATERIAL_COLUMN_DIVIDERS);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? DATE_FORMATTER.format(dateTime) : "";
    }

    private Span formatBoolean(boolean value) {
        Span badge = new Span(value ? "Ja" : "Nein");
        badge.getElement().getThemeList().add(value ? "badge success" : "badge error");
        return badge;
    }

}
