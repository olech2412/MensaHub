package de.olech2412.mensahub.models.helper;

import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.jobs.JobType;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.job.JobError;
import de.olech2412.mensahub.models.result.errors.job.JobErrors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JobBuilder {
    private JobType jobType;
    private List<MailUser> mailUsers;
    private Users proponent;
    private LocalDateTime executeAt;
    private boolean needsPermission;
    private String title;
    private String message;

    public JobBuilder() {
    }

    public JobBuilder job(JobType jobType) {
        this.jobType = jobType;
        return this;
    }

    public JobBuilder mailUsers(List<MailUser> mailUsers) {
        this.mailUsers = mailUsers;
        return this;
    }

    public JobBuilder proponent(Users proponent) {
        this.proponent = proponent;
        return this;
    }

    public JobBuilder executeAt(LocalDateTime executeAt) {
        this.executeAt = executeAt;
        return this;
    }

    public JobBuilder needsPermission(boolean needsPermission) {
        this.needsPermission = needsPermission;
        return this;
    }

    public JobBuilder title(String title) {
        this.title = title;
        return this;
    }

    public JobBuilder message(String message) {
        this.message = message;
        return this;
    }


    public Result<Job, JobError> build(Users user) {
        Job job = new Job();
        job.setJobType(this.jobType);
        job.setMailUsers(this.mailUsers);
        job.setProponent(this.proponent);
        job.setExecuteAt(this.executeAt);
        job.setCreator(user);
        job.setJobStatus(JobStatus.PENDING);
        job.setNeedsPermission(this.needsPermission);
        job.setTitle(this.title);
        job.setMessage(this.message);

        Result<Job, JobError> correctResult = correctJobDTO(job);

        if (!correctResult.isSuccess()) {
            return Result.error(correctResult.getError());
        }

        return Result.success(correctResult.getData());
    }

    private Result<Job, JobError> correctJobDTO(Job job) {

        if (job.getProponent() != null) {
            job.setNeedsPermission(true);
        }

        if (job.isNeedsPermission()) {
            if (job.getProponent() == null) {
                return Result.error(new JobError("Job benötigt Erlaubnis, hat aber keinen Befürworter zugeordnet", JobErrors.INVALID_CONFIGURATION));
            }
        }

        if (job.getCreator() == null || !job.getCreator().getProponent()) {
            return Result.error(new JobError("Es muss ein gültiger und ausreichend berechtigter Befürworter angegeben werden", JobErrors.INVALID_CONFIGURATION));
        }

        if(job.getJobType().equals(JobType.SEND_PUSH_NOTIFICATION) && job.getTitle().isEmpty() || job.getMessage().isEmpty()){
            return Result.error(new JobError("Ein Push Notification Job benötigt immer einen Titel und eine Nachricht", JobErrors.INVALID_CONFIGURATION));
        }

        job.setEnabled(job.getProponent() == null);

        return Result.success(job);
    }
}
