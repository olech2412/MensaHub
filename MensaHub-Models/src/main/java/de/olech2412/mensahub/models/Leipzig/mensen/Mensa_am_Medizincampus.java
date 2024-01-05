package de.olech2412.mensahub.models.Leipzig.mensen;


import com.fasterxml.jackson.annotation.JsonIgnore;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import jakarta.persistence.*;

import java.util.Set;

/**
 * This class represents the mensa_am_medizincampus entity in the database.
 */
@Entity
@Table(name = "mensa_am_medizincampus")
public class Mensa_am_Medizincampus extends Mensa {

    @OneToMany(mappedBy = "mensa_am_medizincampus")
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_am_Medizincampus> meals_mensa_am_medizincampus;

    @OneToMany(mappedBy = "mensa_am_medizincampus", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users;
}