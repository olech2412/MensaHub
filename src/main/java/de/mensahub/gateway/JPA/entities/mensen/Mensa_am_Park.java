package de.mensahub.gateway.JPA.entities.mensen;

import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Park;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_park")
/**
 * Used to create a Mensa am Park
 */
public class Mensa_am_Park extends Mensa {

    @OneToMany(mappedBy = "mensa_am_park")
    private Set<Meals_Mensa_am_Park> meals_mensa_am_park; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_am_park", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}
