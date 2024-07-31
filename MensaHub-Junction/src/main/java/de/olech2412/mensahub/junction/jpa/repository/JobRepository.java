package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findAllByEnabledFalseAndProponentAndExecutedFalse(Users proponent);

    List<Job> findAllByExecuteAtAfter(LocalDateTime localDateTime);

    List<Job> findAllByJobStatus(JobStatus jobStatus);

}