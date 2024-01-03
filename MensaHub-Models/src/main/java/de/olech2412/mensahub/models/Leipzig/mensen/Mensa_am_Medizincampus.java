package de.olech2412.mensahub.models.Leipzig.mensen;


import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_medizincampus")
public class Mensa_am_Medizincampus extends Mensa {

    @OneToMany(mappedBy = "mensa_am_medizincampus")
    private Set<Meals_Mensa_am_Medizincampus> meals_mensa_am_medizincampus;

    @OneToMany(mappedBy = "mensa_am_medizincampus", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}