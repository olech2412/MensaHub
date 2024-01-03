package de.olech2412.mensahub.models.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Peterssteinweg;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_peterssteinweg")
public class Mensa_Peterssteinweg extends Mensa {

    @OneToMany(mappedBy = "mensa_peterssteinweg")
    private Set<Meals_Mensa_Peterssteinweg> meals_mensa_peterssteinweg;

    @OneToMany(mappedBy = "mensa_peterssteinweg", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}