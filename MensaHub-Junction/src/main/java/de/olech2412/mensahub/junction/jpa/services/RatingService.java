package de.olech2412.mensahub.junction.jpa.services;

import de.olech2412.mensahub.junction.jpa.repository.ErrorEntityRepository;
import de.olech2412.mensahub.junction.jpa.repository.RatingRepository;
import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.Application;
import de.olech2412.mensahub.models.result.errors.ErrorEntity;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import de.olech2412.mensahub.models.result.errors.jpa.JPAErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Result<List<Rating>, JPAError> findAllByMailUserAndMealName(MailUser mailUser, String mealName) {
        try {
            List<Rating> ratings = ratingRepository.findAllByMailUserAndMealName(mailUser, mealName);
            return Result.success(ratings);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result<List<Rating>, JPAError> result = Result.error(new JPAError("Fehler beim abrufen der Ratings mit MailUser und MealName: " + e.getMessage(), JPAErrors.ERROR_READ));
            errorEntityRepository.save(new ErrorEntity(result.getError().message(), result.getError().error().getCode(), Application.JUNCTION));
            return result;
        }
    }

}
