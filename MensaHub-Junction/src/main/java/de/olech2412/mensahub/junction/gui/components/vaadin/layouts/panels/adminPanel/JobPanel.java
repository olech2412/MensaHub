package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.panels.adminPanel;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.olech2412.mensahub.junction.gui.components.own.CustomDisplay;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.ButtonFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.ButtonType;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.ConfirmDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.EditJobDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.grids.JobGrid;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.services.JobService;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.models.authentification.Role;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
public class JobPanel extends VerticalLayout {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);

    JobGrid jobGrid;

    Button executeJobButton;

    public JobPanel(JobService jobService, Users currentAdmin, MailUserService mailUserService) {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        executeJobButton = ButtonFactory.create(ButtonType.CUSTOM, "Einen neuen Job anlegen");
        executeJobButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        executeJobButton.setIcon(VaadinIcon.FILE_PROCESS.create());

        HorizontalLayout infoDisplayLayout = new HorizontalLayout();

        Scroller jobInfoScroller = new Scroller();
        jobInfoScroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);

        CustomDisplay pendingJobs = new CustomDisplay("Jobs, die auf eine Genehmigung von dir warten");
        Result<List<Job>, JPAError> resultCurrentJobsWaiting = jobService.findAllByEnabledFalseAndProponentAndExecutedFalse(currentAdmin);
        if (resultCurrentJobsWaiting.isSuccess()) {
            pendingJobs.setValue(String.valueOf(resultCurrentJobsWaiting.getData().size()));
        } else {
            pendingJobs.setValue(resultCurrentJobsWaiting.getError().error().getCode());
        }
        pendingJobs.setThresholds(0, 1, 2);

        CustomDisplay outstandingJobs = new CustomDisplay("Ausstehende Jobs");
        Result<List<Job>, JPAError> resultOutstandingJobs = jobService.findAllByExecuteAtAfter(LocalDateTime.now());
        if (resultOutstandingJobs.isSuccess()) {
            outstandingJobs.setValue(String.valueOf(resultOutstandingJobs.getData().size()));
        } else {
            outstandingJobs.setValue(resultOutstandingJobs.getError().error().getCode());
        }
        outstandingJobs.setThresholds(0, 1, 2);

        CustomDisplay successJobs = new CustomDisplay("Erfolgreiche Jobs");
        Result<List<Job>, JPAError> resultSuccessJobs = jobService.findAllByJobStatus(JobStatus.SUCCESS);
        if (resultSuccessJobs.isSuccess()) {
            successJobs.setValue(String.valueOf(resultSuccessJobs.getData().size()));
        } else {
            successJobs.setValue(resultSuccessJobs.getError().error().getCode());
        }
        successJobs.setThresholds(0, 1, 2);

        CustomDisplay errorJobs = new CustomDisplay("Fehlgeschlagene Jobs");
        Result<List<Job>, JPAError> resultErrorJobs = jobService.findAllByJobStatus(JobStatus.HAS_FAILURES);
        if (resultErrorJobs.isSuccess()) {
            errorJobs.setValue(String.valueOf(resultErrorJobs.getData().size()));
        } else {
            errorJobs.setValue(resultErrorJobs.getError().error().getCode());
        }
        errorJobs.setThresholds(0, 1, 2);

        infoDisplayLayout.add(pendingJobs, outstandingJobs, errorJobs, successJobs);

        infoDisplayLayout.setPadding(true);
        infoDisplayLayout.getStyle().set("display", "inline-flex");
        infoDisplayLayout.setWidth(100f, Unit.PERCENTAGE);

        jobInfoScroller.setContent(infoDisplayLayout);

        jobInfoScroller.setVisible(true);
        jobInfoScroller.setWidth(100f, Unit.PERCENTAGE);

        jobGrid = new JobGrid(currentAdmin.getUsername());

        Result<List<Job>, JPAError> resultFetchAllJobs = jobService.findAll();
        GridListDataView<Job> gridListDataView;
        if (!resultFetchAllJobs.isSuccess()) {
            NotificationFactory.create(NotificationType.ERROR, "Fehler beim laden der Jobs").open();
            // if this happens, the view is fucked up so throw an error
            throw new RuntimeException(resultFetchAllJobs.getError().message());
        } else {
            gridListDataView = jobGrid.setItems(resultFetchAllJobs.getData());
        }

        GridContextMenu<Job> jobGridContextMenu = new GridContextMenu<>(jobGrid);
        GridMenuItem<Job> edit = jobGridContextMenu.addItem("Bearbeiten", event -> {
            if (event.getItem().isPresent()) {
                Job job = event.getItem().get();
                EditJobDialog editJobDialog = new EditJobDialog(mailUserService.findAll(), job.getExecuteAt(), job.getJobType());
                editJobDialog.open();
                editJobDialog.getFooterButtonLayout().acceptButton.addClickListener(buttonClickEvent -> {
                    if (editJobDialog.isFilledCorrect()) {
                        job.setMailUsers(editJobDialog.getEditJobLayout().getMailUserMultiSelectComboBox().getValue().stream().toList());
                        job.setExecuteAt(editJobDialog.getEditJobLayout().getExecuteAtTimePicker().getValue());
                        job.setJobType(editJobDialog.getEditJobLayout().getJobTypeComboBox().getValue());
                        Result<Job, JPAError> saveResult = jobService.saveJob(job);
                        if (saveResult.isSuccess()) {
                            NotificationFactory.create(NotificationType.SUCCESS, "Änderungen wurden erfolgreich gespeichert").open();
                            editJobDialog.close();
                        } else {
                            NotificationFactory.create(NotificationType.ERROR, "Fehler beim speichern der Änderungen").open();
                        }
                    }
                });
            }

        });
        edit.addComponentAsFirst(VaadinIcon.EDIT.create());
        GridMenuItem<Job> allow = jobGridContextMenu.addItem("Genehmigen", event -> {
            if (event.getItem().isPresent()) {
                Job job = event.getItem().get();
                if (!job.getJobStatus().equals(JobStatus.SUCCESS) && !job.getJobStatus().equals(JobStatus.HAS_FAILURES)) {
                    if (job.getExecuteAt() == null || job.getExecuteAt().isAfter(LocalDateTime.now())) {
                        //job.setEnabled(true);
                        Result<Job, JPAError> saveResult = jobService.saveJob(job);
                        if (saveResult.isSuccess()) {
                            NotificationFactory.create(NotificationType.SUCCESS, "Job wurde erfolgreich genehmigt").open();
                            gridListDataView.refreshItem(event.getItem().get());
                        } else {
                            log.error("Fehler beim speichern der Genehmigung: {}", saveResult.getError().error().getCode());
                            NotificationFactory.create(NotificationType.ERROR, "Genehmigung konnte nicht gespeichert werden").open();
                        }
                    } else {
                        NotificationFactory.create(NotificationType.ERROR, "Der Job wurde nicht ausgeführt, liegt jedoch in der Vergangenheit").open();
                    }
                } else {
                    NotificationFactory.create(NotificationType.ERROR, "Job wurde bereits ausgeführt").open();
                }
            }
        });
        allow.addComponentAsFirst(VaadinIcon.CHECK.create());

        jobGridContextMenu.addGridContextMenuOpenedListener(jobGridContextMenuOpenedEvent -> {
            if (jobGridContextMenuOpenedEvent.getItem().isPresent()) {
                Job job = jobGridContextMenuOpenedEvent.getItem().get();
                allow.setEnabled(job.getProponent() != null && job.getProponent().getUsername().equals(currentAdmin.getUsername()) &&
                        !job.isEnabled() && !job.isExecuted());

                if (!job.isExecuted() && job.getCreator().getUsername().equals(currentAdmin.getUsername()) || currentAdmin.getRole().equals(Role.ROLE_SUPER_ADMIN)) {
                    if (job.getExecuteAt() == null || job.getExecuteAt().isAfter(LocalDateTime.now())) {
                        edit.setEnabled(true);
                        return;
                    }
                    edit.setEnabled(false);
                } else {
                    edit.setEnabled(false);
                }
            }
        });

        GridMenuItem<Job> delete = jobGridContextMenu.addItem("Löschen", event -> {
            if (event.getItem().isPresent()) {
                ConfirmDialog confirmDialog = new ConfirmDialog(String.format("Möchtest du den Job %s, erstellt am %s wirklich löschen?",
                        event.getItem().get().getJobType().toString(),
                        DATE_FORMATTER.format(event.getItem().get().getCreationDate())), "Das Löschen des Jobs kann nicht rückgängig gemacht werden. Handle also mit bedacht.");
                confirmDialog.open();

                confirmDialog.acceptButton.addClickListener(buttonClickEvent -> {
                    Result<Job, JPAError> result = jobService.deleteJob(event.getItem().get());
                    if (result.isSuccess()) {
                        NotificationFactory.create(NotificationType.SUCCESS, "Job wurde gelöscht").open();
                        gridListDataView.removeItem(event.getItem().get());
                        gridListDataView.refreshAll();
                        confirmDialog.close();
                    } else {
                        NotificationFactory.create(NotificationType.ERROR, "Beim löschen des Jobs ist ein Fehler aufgetreten").open();
                        confirmDialog.close();
                    }
                });

                confirmDialog.declineButton.addClickListener(buttonClickEvent -> confirmDialog.close());
            }
        });
        delete.addComponentAsFirst(VaadinIcon.TRASH.create());

        add(executeJobButton, jobInfoScroller, jobGrid);
    }
}
