package de.olech2412.mensahub.models.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Tierklinik;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_tierklinik")
public class Mensa_Tierklinik extends Mensa {

    @OneToMany(mappedBy = "mensa_tierklinik")
    private Set<Meals_Mensa_Tierklinik> meals_mensa_tierklinik;

    @OneToMany(mappedBy = "mensa_tierklinik", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}