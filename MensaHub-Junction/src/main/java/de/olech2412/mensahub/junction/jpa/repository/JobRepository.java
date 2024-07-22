package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.jobs.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

}