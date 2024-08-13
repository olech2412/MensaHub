package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.Preferences;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreferencesRepository extends JpaRepository<Preferences, Long> {

}