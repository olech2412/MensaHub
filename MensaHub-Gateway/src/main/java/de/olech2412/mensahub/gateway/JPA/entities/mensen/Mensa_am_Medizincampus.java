package de.olech2412.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Medizincampus;

import java.util.Set;

@Entity
@Table(name = "mensa_am_medizincampus")
/**
 * Used to create a Mensa am Medizincampus
 */
public class Mensa_am_Medizincampus extends Mensa {

    @OneToMany(mappedBy = "mensa_am_medizincampus", fetch = FetchType.LAZY)
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_am_Medizincampus> meals_mensa_am_medizincampus; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_am_medizincampus", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}