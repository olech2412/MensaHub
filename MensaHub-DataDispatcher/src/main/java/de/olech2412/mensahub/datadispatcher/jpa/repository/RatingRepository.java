package de.olech2412.mensahub.datadispatcher.jpa.repository;

import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllByMailUserAndMealName(MailUser mailUser, String mealName);

    Optional<List<Rating>> findAllByMealId(Long mealId);

}