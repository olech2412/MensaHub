package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.JobRepository;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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

    @Autowired
    private ErrorEntityRepository errorEntityRepository;

    public Result<Job, JPAError> saveJob(Job job) {
        try {

            jobRepository.save(job);
            return Result.success(job);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Job, JPAError> result = Result.error(new JPAError("Fehler beim speichern des Jobs: " + e.getMessage(), JPAErrors.ERROR_SAVING));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<Job, JPAError> deleteJob(Job job) {
        try {
            jobRepository.delete(job);
            return Result.success(job);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Job, JPAError> result = Result.error(new JPAError("Fehler beim löschen des Jobs: " + e.getMessage(), JPAErrors.ERROR_DELETE));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Job>, JPAError> findAllByEnabledFalseAndProponentAndExecutedFalse(Users users) {
        try {
            List<Job> jobs = jobRepository.findAllByEnabledFalseAndProponentAndExecutedFalse(users);
            return Result.success(jobs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Job>, JPAError> result = Result.error(new JPAError("Fehler bei Suche nach zugewiesenen Jobs: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Job>, JPAError> findAllByExecuteAtAfter(LocalDateTime localDateTime) {
        try {
            List<Job> jobs = jobRepository.findAllByExecuteAtAfter(localDateTime);
            return Result.success(jobs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Job>, JPAError> result = Result.error(new JPAError("Fehler bei Suche nach ausstehenden Jobs: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Job>, JPAError> findAll() {
        try {
            List<Job> jobs = jobRepository.findAll();
            jobs.forEach(job -> Hibernate.initialize(job.getMailUsers()));
            return Result.success(jobs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Job>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen aller Jobs: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<List<Job>, JPAError> findAllByJobStatus(JobStatus jobStatus) {
        try {
            List<Job> jobs = jobRepository.findAllByJobStatus(jobStatus);
            return Result.success(jobs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Job>, JPAError> result = Result.error(new JPAError(String.format("Fehler beim abrufen aller Jobs mit Status %s: " + e.getMessage(), jobStatus), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

}
