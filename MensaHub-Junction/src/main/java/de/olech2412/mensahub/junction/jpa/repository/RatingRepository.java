package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.Rating;
import de.olech2412.mensahub.models.authentification.MailUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findAllByMailUserAndMealName(MailUser mailUser, String mealName);

    List<Rating> findAllByMailUser(MailUser mailUser);

}