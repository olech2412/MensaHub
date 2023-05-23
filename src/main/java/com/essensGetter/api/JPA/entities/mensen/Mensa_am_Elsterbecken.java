package com.essensGetter.api.JPA.entities.mensen;

import com.essensGetter.api.JPA.entities.MailUser;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_am_Elsterbecken;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_elsterbecken")
public class Mensa_am_Elsterbecken extends Mensa {

    @OneToMany(mappedBy = "mensa_am_elsterbecken")
    private Set<Meals_Mensa_am_Elsterbecken> meals_mensa_am_elsterbecken;

    @OneToMany(mappedBy = "mensa_am_elsterbecken", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}