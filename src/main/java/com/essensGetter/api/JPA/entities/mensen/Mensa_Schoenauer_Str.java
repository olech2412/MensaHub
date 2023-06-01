package com.essensGetter.api.JPA.entities.mensen;

import com.essensGetter.api.JPA.entities.MailUser;
import com.essensGetter.api.JPA.entities.meals.Meals_Schoenauer_Str;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_schoenauer_str")
public class Mensa_Schoenauer_Str extends Mensa {

    @OneToMany(mappedBy = "mensa_schoenauer_str")
    private Set<Meals_Schoenauer_Str> meals_schoenauer_strList;

    @OneToMany(mappedBy = "mensa_schoenauer_str", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}