package de.olech2412.mensahub.models.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Park;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_park")
public class Mensa_am_Park extends Mensa {

    @OneToMany(mappedBy = "mensa_am_park")
    private Set<Meals_Mensa_am_Park> meals_mensa_am_park;

    @OneToMany(mappedBy = "mensa_am_park", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}
