package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.PreferencesRepository;
import de.olech2412.mensahub.models.Preferences;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class PreferencesService {

    @Autowired
    PreferencesRepository preferencesRepository;

    @Autowired
    ErrorEntityRepository errorEntityRepository;

    public Result<Preferences, JPAError> delete(Preferences preferences) {
        try {
            preferencesRepository.delete(preferences);
            return Result.success(preferences);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Preferences, JPAError> result = Result.error(new JPAError("Fehler beim löschen der Preferences: " + e.getMessage(), JPAErrors.ERROR_DELETE));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<Preferences, JPAError> save(Preferences preferences) {
        try {
            preferencesRepository.save(preferences);
            return Result.success(preferences);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Preferences, JPAError> result = Result.error(new JPAError("Fehler beim speichern der Preferences: " + e.getMessage(), JPAErrors.ERROR_SAVING));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

}
