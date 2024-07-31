package de.olech2412.mensahub.datadispatcher.data.jobs;

import de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher.LeipzigDataDispatcher;
import de.olech2412.mensahub.datadispatcher.jpa.services.JobService;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.mail.MailError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class JobManager {

    private final JobService jobService;

    private final LeipzigDataDispatcher leipzigDataDispatcher;

    public JobManager(JobService jobService, LeipzigDataDispatcher leipzigDataDispatcher) {
        this.jobService = jobService;
        this.leipzigDataDispatcher = leipzigDataDispatcher;
    }


    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    public void checkForJobs() {
        Result<List<Job>, JPAError> jobs = jobService.findAllByEnabledAndExecutedAndExecuteAtIsNullOrExecuteAtIsAfter(true, false, LocalDateTime.now());

        if (jobs.isSuccess()) {
            for (Job job : jobs.getData()) {
                List<Result<MailUser, MailError>> executeJobResults = new ArrayList<>();
                switch (job.getJobType()) {
                    case SEND_EMAILS -> {
                        List<Result<MailUser, MailError>> results = leipzigDataDispatcher.forceSendMail(job.getMailUsers(), false);
                        executeJobResults.addAll(results);
                    }
                    case SEND_UPDATES -> {
                        List<Result<MailUser, MailError>> results = leipzigDataDispatcher.forceSendMail(job.getMailUsers(), true);
                        executeJobResults.addAll(results);
                    }
                    default ->
                            log.error("Ungültiger Jobtyp gefunden: {} für den Job: {}", job.getJobType(), job.getUuid());
                }

                List<Result<MailUser, MailError>> errors = executeJobResults.stream().filter(mailUserMailErrorResult -> !mailUserMailErrorResult.isSuccess()).toList();
                if (errors.isEmpty()) {
                    job.setExecuted(true);
                    job.setJobStatus(JobStatus.SUCCESS);
                    Result<Job, JPAError> saveResult = jobService.saveJob(job);
                    if (saveResult.isSuccess()) {
                        log.info("Successful executed job {}", job.getUuid());
                    } else {
                        log.error("Error while saving job {} but execution was successful", job.getUuid());
                    }
                    return;
                }
                job.setExecuted(true);
                job.setJobStatus(JobStatus.HAS_FAILURES);
                Result<Job, JPAError> saveResult = jobService.saveJob(job);
                if (saveResult.isSuccess()) {
                    log.info("Error while executing job {} happened", job.getUuid());
                } else {
                    log.error("Error while saving job {} and execution had failures to", job.getUuid());
                }
            }
        }
    }

}
