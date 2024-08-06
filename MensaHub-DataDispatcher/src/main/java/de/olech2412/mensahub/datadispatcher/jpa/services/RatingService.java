package de.olech2412.mensahub.datadispatcher.jpa.services;

import de.olech2412.mensahub.datadispatcher.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.datadispatcher.jpa.repository.RatingRepository;
import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RatingService {

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    private ErrorEntityRepository errorEntityRepository;

    public Result<Rating, JPAError> saveRating(Rating rating) {
        try {
            ratingRepository.save(rating);
            return Result.success(rating);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Rating, JPAError> result = Result.error(new JPAError("Fehler beim speichern des Ratings: " + e.getMessage(), JPAErrors.ERROR_SAVING));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

    public Result<Rating, JPAError> deleteRating(Rating rating) {
        try {
            ratingRepository.delete(rating);
            return Result.success(rating);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<Rating, JPAError> result = Result.error(new JPAError("Fehler beim löschen des Ratings: " + e.getMessage(), JPAErrors.ERROR_DELETE));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

}
