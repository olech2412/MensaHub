package de.olech2412.mensahub.models.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "cafeteria_dittrichring")
public class Cafeteria_Dittrichring extends Mensa {

    @OneToMany(mappedBy = "cafeteria_dittrichring")
    private Set<Meals_Cafeteria_Dittrichring> meals_cafeteria_dittrichrings;

    @OneToMany(mappedBy = "cafeteria_dittrichring", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;

}