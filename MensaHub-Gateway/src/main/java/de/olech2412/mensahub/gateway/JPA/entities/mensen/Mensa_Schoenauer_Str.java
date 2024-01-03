package de.olech2412.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Schoenauer_Str;

import java.util.Set;

@Entity
@Table(name = "mensa_schoenauer_str")
/**
 * Used to create a Mensa Schoenauer Str
 */
public class Mensa_Schoenauer_Str extends Mensa {

    @OneToMany(mappedBy = "mensa_schoenauer_str", fetch = FetchType.LAZY)
    @JsonIgnore
    @Transient
    private Set<Meals_Schoenauer_Str> meals_schoenauer_strList; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_schoenauer_str", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}