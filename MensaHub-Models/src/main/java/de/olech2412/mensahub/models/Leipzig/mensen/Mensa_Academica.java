package de.olech2412.mensahub.models.Leipzig.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Academica;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.OneToMany;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_academica")
public class Mensa_Academica extends Mensa {

    @OneToMany(mappedBy = "mensa_academica")
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_Academica> meals_mensa_academica;

    @OneToMany(mappedBy = "mensa_academica", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users;
}