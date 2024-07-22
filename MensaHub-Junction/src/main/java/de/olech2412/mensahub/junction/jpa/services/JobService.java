package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.JobRepository;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ErrorEntityRepository errorEntityRepository;

    public Result<Job, JPAError> saveJob(Job job) {
        try {

            jobRepository.save(job);
            return Result.success(job);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Job, JPAError> saveResult = Result.error(new JPAError("Fehler beim speichern des Jobs: " + e.getMessage(), JPAErrors.ERROR_SAVING));
            errorEntityRepository.save(new ErrorEntity(saveResult.getError().message(), saveResult.getError().error().getCode(), Application.JUNCTION));
            return saveResult;
        }
    }

    public Result<Job, JPAError> deleteJob(Job job) {
        try {
            jobRepository.delete(job);
            return Result.success(job);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Job, JPAError> deleteResult = Result.error(new JPAError("Fehler beim löschen des Jobs: " + e.getMessage(), JPAErrors.ERROR_DELETE));
            errorEntityRepository.save(new ErrorEntity(deleteResult.getError().message(), deleteResult.getError().error().getCode(), Application.JUNCTION));
            return deleteResult;
        }
    }

}
