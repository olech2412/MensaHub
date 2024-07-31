package de.olech2412.mensahub.models.helper;

import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.JobType;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.job.JobError;
import de.olech2412.mensahub.models.result.errors.job.JobErrors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JobBuilder {
    private UUID uuid;
    private JobType jobType;
    private List<MailUser> mailUsers;
    private Users creator;
    private Users proponent;
    private LocalDateTime creationDate;
    private LocalDateTime executeAt;
    private boolean transferredByInterface;
    private boolean executed;
    private boolean enabled;
    private boolean needsPermission;
    private JobStatus jobStatus;

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

    public JobBuilder transferredByInterface(boolean transferredByInterface) {
        this.transferredByInterface = transferredByInterface;
        return this;
    }

    public JobBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }


    public Result<Job, JobError> build(Users user) {
        Job job = new Job();
        job.setUuid(this.uuid);
        job.setJobType(this.jobType);
        job.setMailUsers(this.mailUsers);
        job.setCreator(this.creator);
        job.setProponent(this.proponent);
        job.setExecuteAt(this.executeAt);
        job.setExecuted(this.executed);
        job.setEnabled(this.enabled);
        job.setCreator(user);
        job.setJobStatus(JobStatus.PENDING);

        Result<Job, JobError> correctResult = correctJobDTO(job);

        if(!correctResult.isSuccess()) {
            return Result.error(correctResult.getError());
        }

        return Result.success(correctResult.getData());
    }

    private Result<Job, JobError> correctJobDTO(Job job) {
        // first repair the stupid input
        if(job.getMailUsers().size() >= 2){ // if more than 1 MailUser is affected by the job a permission is required
            job.setNeedsPermission(true);
            job.setEnabled(false);
        }

        if(job.getProponent() != null){
            job.setNeedsPermission(true);
        }

        if(job.isNeedsPermission()){
            if(job.getProponent() == null){
                return Result.error(new JobError("Job benötigt Erlaubnis, hat aber keinen Befürworter zugeordnet", JobErrors.INVALID_CONFIGURATION));
            }
        }

        if(job.getCreator() == null || !job.getCreator().getProponent()){
            return Result.error(new JobError("Es muss ein gültiger und ausreichend berechtigter Befürworter angegeben werden", JobErrors.INVALID_CONFIGURATION));
        }

        return Result.success(job);
    }
}
