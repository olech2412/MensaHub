package de.olech2412.mensahub.datadispatcher.data.jobs;

import de.olech2412.mensahub.datadispatcher.data.leipzig.leipzigDispatcher.LeipzigDataDispatcher;
import de.olech2412.mensahub.datadispatcher.jpa.services.JobService;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
                switch (job.getJobType()) {
                    case SEND_EMAILS -> leipzigDataDispatcher.forceSendMail(job.getMailUsers(), false);
                    case SEND_UPDATES -> leipzigDataDispatcher.forceSendMail(job.getMailUsers(), true);
                    default ->
                            log.error("Ungültiger Jobtyp gefunden: {} für den Job: {}", job.getJobType(), job.getUuid());
                }
            }
        }
    }

}
