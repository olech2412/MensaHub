package de.olech2412.mensahub.datadispatcher.jpa.repository;

import de.olech2412.mensahub.models.jobs.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findAllByEnabledAndExecutedAndExecuteAtIsNullOrExecuteAtIsAfter(Boolean enabled, Boolean executed, LocalDateTime executedAt);

}