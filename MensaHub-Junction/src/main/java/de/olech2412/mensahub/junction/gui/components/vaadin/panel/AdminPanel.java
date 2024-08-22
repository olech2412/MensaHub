package de.olech2412.mensahub.junction.gui.components.vaadin.panel;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.CreateJobDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.panels.adminPanel.JobPanel;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.panels.adminPanel.UserPanel;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.UsersRepository;
import de.olech2412.mensahub.junction.jpa.services.JobService;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.junction.jpa.services.UserService;
import de.olech2412.mensahub.junction.security.SecurityService;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.helper.JobBuilder;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.job.JobError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AdminPanel extends VerticalLayout {

    private final JobService jobService;
    private final ErrorEntityRepository errorEntityRepository;
    private final Users currentUser;
    private final VerticalLayout content = new VerticalLayout();
    private final JobPanel jobPanelLayout;
    private final UserPanel userPanelLayout;
    CreateJobDialog createJobDialog;

    public AdminPanel(MailUserService mailUserService, UserService userService, SecurityService securityService,
                      JobService jobService, UsersRepository usersRepository, ErrorEntityRepository errorEntityRepository) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        this.jobService = jobService;
        this.errorEntityRepository = errorEntityRepository;
        setWidth(70f, Unit.PERCENTAGE);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        currentUser = usersRepository.findByUsername(securityService.getAuthenticatedUser().getUsername());

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        List<MailUser> mailUsers = mailUserService.findAll();
        List<MailUser> initializedMailUsers = new ArrayList<>();
        for (MailUser m : mailUsers) {
            MailUser initializedMailUser = mailUserService.initialize(m);
            initializedMailUsers.add(initializedMailUser);
        }

        createJobDialog = new CreateJobDialog(initializedMailUsers, usersRepository.findAllByEnabledTrueAndProponentTrueAndUsernameNot(securityService.getAuthenticatedUser().getUsername()));

        createJobDialog.getFooterButtonLayout().getAcceptButton().addClickListener(buttonClickEvent -> {
            try {
                createJob(currentUser);
            } catch (NoSuchPaddingException | IllegalBlockSizeException | IOException | NoSuchAlgorithmException |
                     BadPaddingException | InvalidKeyException e) {
                throw new RuntimeException(e);
            }
        });

        Tab jobPanel = new Tab(
                VaadinIcon.AUTOMATION.create(),
                new Span("Job-Panel")
        );
        Tab userPanel = new Tab(
                VaadinIcon.USER.create(),
                new Span("Nutzer-Panel")
        );
        Tab mailUserPanel = new Tab(
                VaadinIcon.ENVELOPE.create(),
                new Span("Newsletter-Panel")
        );
        Tab systemPanel = new Tab(
                VaadinIcon.DATABASE.create(),
                new Span("System-Panel")
        );

        for (Tab tab : new Tab[]{jobPanel, userPanel, mailUserPanel, systemPanel}) {
            tab.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        }

        Tabs tabs = new Tabs(jobPanel, userPanel, mailUserPanel, systemPanel);

        jobPanelLayout = new JobPanel(jobService, currentUser, mailUserService);
        jobPanelLayout.getExecuteJobButton().addClickListener(buttonClickEvent -> createJobDialog.open());

        userPanelLayout = new UserPanel(userService, currentUser);

        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            content.removeAll();
            if (selectedChangeEvent.getSelectedTab().equals(jobPanel)) {
                content.add(jobPanelLayout);
            } else {
                content.add(userPanelLayout);
            }
        });

        content.add(jobPanelLayout);

        add(new H3(String.format("Hallo \"%s\"! Willkommen im Admin Panel", currentUser.getUsername())));
        add(new Paragraph("Denke dran, mit viel Macht kommt viel Verantwortung. Handle also mit bedacht!"));
        add(tabs);
        add(content);
    }

    /**
     * Build the job, fix potential stupid inputs from user and save the job in the database
     *
     * @param jobCreator The user (admin) who created the job
     */
    private void createJob(Users jobCreator) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!createJobDialog.isFilledCorrect()) {
            NotificationFactory.create(NotificationType.ERROR, "Bitte fehlerhafte Eingaben prüfen").open();
            return;
        }

        Result<Job, JobError> jobBuildResult = new JobBuilder()
                .job(createJobDialog.getSimpleConfLayout().getJobToExecuteComboBox().getValue())
                .mailUsers(createJobDialog.getSimpleConfLayout().getMailUserComboBox().getValue().stream().toList())
                .executeAt(createJobDialog.getExtendedConfLayout().getExecuteAtTimePicker().getValue())
                .proponent(createJobDialog.getExtendedConfLayout().getProponentComboBox().getValue())
                .title(createJobDialog.getExtendedConfLayout().getPushMessageTitle().getValue())
                .message(createJobDialog.getExtendedConfLayout().getPushMessageMessage().getValue())
                .build(jobCreator);

        if (!jobBuildResult.isSuccess()) {
            errorEntityRepository.save(new ErrorEntity(jobBuildResult.getError().message(), jobBuildResult.getError().error().getCode(), Application.JUNCTION));
            return;
        }

        // save the job
        Result<Job, JPAError> saveResult = jobService.saveJob(jobBuildResult.getData());

        if (!saveResult.isSuccess()) {
            NotificationFactory.create(NotificationType.ERROR, "Fehler beim Anlegen des Jobs: " +
                    jobBuildResult.getError().jobDTOError().getCode()).open();
            return;
        }
        NotificationFactory.create(NotificationType.SUCCESS, "Der Job wurde erfolgreich angelegt").open();
        createJobDialog.close();
    }

}
