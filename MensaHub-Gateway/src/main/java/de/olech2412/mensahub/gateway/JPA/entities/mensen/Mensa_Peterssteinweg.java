package de.olech2412.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_Peterssteinweg;

import java.util.Set;

@Entity
@Table(name = "mensa_peterssteinweg")
/**
 * Used to create a Mensa Peterssteinweg
 */
public class Mensa_Peterssteinweg extends Mensa {

    @OneToMany(mappedBy = "mensa_peterssteinweg", fetch = FetchType.LAZY)
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_Peterssteinweg> meals_mensa_peterssteinweg; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_peterssteinweg", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}