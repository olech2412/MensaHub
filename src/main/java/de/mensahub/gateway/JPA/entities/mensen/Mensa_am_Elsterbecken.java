package de.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_elsterbecken")
/**
 * Used to create a Mensa am Elsterbecken
 */
public class Mensa_am_Elsterbecken extends Mensa {

    @OneToMany(mappedBy = "mensa_am_elsterbecken", fetch = FetchType.LAZY)
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_am_Elsterbecken> meals_mensa_am_elsterbecken; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_am_elsterbecken", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}