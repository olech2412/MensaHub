package de.olech2412.mensahub.models.result.errors.helper.jobdto;

import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobDTO;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.result.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JobDTOBuilder {
    private UUID uuid;
    private Job job;
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

    public JobDTOBuilder() {
    }

    public JobDTOBuilder job(Job job) {
        this.job = job;
        return this;
    }

    public JobDTOBuilder mailUsers(List<MailUser> mailUsers) {
        this.mailUsers = mailUsers;
        return this;
    }

    public JobDTOBuilder proponent(Users proponent) {
        this.proponent = proponent;
        return this;
    }

    public JobDTOBuilder executeAt(LocalDateTime executeAt) {
        this.executeAt = executeAt;
        return this;
    }

    public JobDTOBuilder transferredByInterface(boolean transferredByInterface) {
        this.transferredByInterface = transferredByInterface;
        return this;
    }

    public JobDTOBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }


    public Result<JobDTO, JobDTOError> build(Users user) {
        JobDTO jobDTO = new JobDTO();
        jobDTO.setUuid(this.uuid);
        jobDTO.setJob(this.job);
        jobDTO.setMailUsers(this.mailUsers);
        jobDTO.setCreator(this.creator);
        jobDTO.setProponent(this.proponent);
        jobDTO.setExecuteAt(this.executeAt);
        jobDTO.setTransferredByInterface(this.transferredByInterface);
        jobDTO.setExecuted(this.executed);
        jobDTO.setEnabled(this.enabled);
        jobDTO.setCreator(user);

        Result<JobDTO, JobDTOError> correctResult = correctJobDTO(jobDTO);

        if(!correctResult.isSuccess()) {
            return Result.error(correctResult.getError());
        }

        return Result.success(correctResult.getData());
    }

    private Result<JobDTO, JobDTOError> correctJobDTO(JobDTO jobDTO) {
        if(jobDTO.getMailUsers().size() >= 2){
            jobDTO.setNeedsPermission(true);
            jobDTO.setEnabled(false);
            jobDTO.setJobStatus(JobStatus.PENDING);
        }

        if(jobDTO.isNeedsPermission()){
            if(jobDTO.getProponent() == null){
                return Result.error(new JobDTOError("Job benötigt Erlaubnis, hat aber keinen Befürworter zugeordnet", JobDTOErrors.INVALID_CONFIGURATION));
            }
        }

        if(jobDTO.getCreator() == null || !jobDTO.getCreator().getProponent()){
            return Result.error(new JobDTOError("Es muss ein gültiger und ausreichend berechtigter Befürworter angegeben werden", JobDTOErrors.INVALID_CONFIGURATION));
        }

        return Result.success(jobDTO);
    }
}
