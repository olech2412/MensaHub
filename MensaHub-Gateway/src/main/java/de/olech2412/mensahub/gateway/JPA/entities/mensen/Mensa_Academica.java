package de.olech2412.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_Academica;

import java.util.Set;

@Entity
@Table(name = "mensa_academica")
/**
 * Used to create a Mensa Academica
 */
public class Mensa_Academica extends Mensa {

    @OneToMany(mappedBy = "mensa_academica", fetch = FetchType.LAZY)
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_Academica> meals_mensa_academica; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_academica", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}