package de.olech2412.mensahub.models.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Elsterbecken;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_elsterbecken")
public class Mensa_am_Elsterbecken extends Mensa {

    @OneToMany(mappedBy = "mensa_am_elsterbecken")
    private Set<Meals_Mensa_am_Elsterbecken> meals_mensa_am_elsterbecken;

    @OneToMany(mappedBy = "mensa_am_elsterbecken", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}