package de.olech2412.mensahub.datadispatcher.jpa.repository;

import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorEntityRepository extends JpaRepository<ErrorEntity, Long> {
}