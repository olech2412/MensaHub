package de.olech2412.mensahub.junction.gui.components.vaadin.panel;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.ButtonFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.ButtonType;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.CreateJobDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.UsersRepository;
import de.olech2412.mensahub.junction.jpa.services.JobService;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.security.SecurityService;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.helper.JobBuilder;
import de.olech2412.mensahub.models.result.errors.job.JobError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class AdminPanel extends VerticalLayout {

    CreateJobDialog createJobDialog;

    JobService jobService;

    public AdminPanel(MailUserService mailUserService, UsersRepository usersRepository, SecurityService securityService,
                      JobService jobService) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.jobService = jobService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Button executeJobButton = ButtonFactory.create(ButtonType.CUSTOM, "Einen neuen Job anlegen");
        executeJobButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        executeJobButton.setIcon(VaadinIcon.FILE_PROCESS.create());

        createJobDialog = new CreateJobDialog(mailUserService.findAll(), usersRepository.findAll());

        executeJobButton.addClickListener(buttonClickEvent -> createJobDialog.open());

        createJobDialog.getFooterButtonLayout().getAcceptButton().addClickListener(buttonClickEvent -> {
            try {
                createJob(usersRepository.findAll().get(0));
            } catch (NoSuchPaddingException | IllegalBlockSizeException | IOException | NoSuchAlgorithmException |
                     BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

        add(new H3("Hallo Admin!"));
        add(new Paragraph("Das Admin Panel befindet sich gerade im Aufbau. Aktuell kannst du nur einen neuen Job erstellen."));
        add(executeJobButton);
    }

    /**
     * Build the job, fix potential stupid inputs from user and save the job in the database
     * @param jobCreator The user (admin) who created the job
     */
    private void createJob(Users jobCreator) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!createJobDialog.isFilledCorrect()) {
            NotificationFactory.create(NotificationType.ERROR, "Bitte fehlerhafte Eingaben prüfen").open();
        }

        Result<Job, JobError> jobBuildResult = new JobBuilder()
                .job(createJobDialog.getSimpleConfLayout().getJobToExecuteComboBox().getValue())
                .mailUsers(createJobDialog.getSimpleConfLayout().getMailUserComboBox().getValue().stream().toList())
                .executeAt(createJobDialog.getExtendedConfLayout().getExecuteAtTimePicker().getValue())
                .proponent(createJobDialog.getExtendedConfLayout().getProponentComboBox().getValue())
                .build(jobCreator);

        if (!jobBuildResult.isSuccess()) throw new IllegalArgumentException(jobBuildResult.getError().toString());

        // save the job
        Result<Job, JPAError> saveResult = jobService.saveJob(jobBuildResult.getData());

        if(!saveResult.isSuccess()) {
            NotificationFactory.create(NotificationType.ERROR, "Fehler beim Anlegen des Jobs: " +
                    jobBuildResult.getError().jobDTOError().getCode()).open();
        }
        NotificationFactory.create(NotificationType.SUCCESS, "Der Job wurde erfolgreich angelegt").open();
        createJobDialog.close();
    }

}
