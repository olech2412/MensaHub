package de.olech2412.mensahub.datadispatcher.jpa.services;

import de.olech2412.mensahub.datadispatcher.jpa.repository.JobRepository;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public Result<Job, JPAError> saveJob(Job job) {
        try {

            jobRepository.save(job);
            return Result.success(job);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(new JPAError("Fehler beim speichern des Jobs: " + e.getMessage(), JPAErrors.ERROR_SAVING));
        }
    }

    public Result<Job, JPAError> deleteJob(Job job) {
        try {
            jobRepository.delete(job);
            return Result.success(job);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(new JPAError("Fehler beim löschen des Jobs: " + e.getMessage(), JPAErrors.ERROR_DELETE));
        }
    }

    public Result<List<Job>, JPAError> findAllByEnabledAndExecutedAndExecuteAtIsNullOrExecuteAtIsAfter(Boolean enabled, Boolean executed, LocalDateTime executedAt) {
        try {
            return Result.success(jobRepository.findAllByEnabledAndExecutedAndExecuteAtIsNullOrExecuteAtIsAfter(enabled, executed, executedAt));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(new JPAError("Fehler beim abrufen der Jobs: " + e.getMessage(), JPAErrors.ERROR_READ));
        }
    }

}
