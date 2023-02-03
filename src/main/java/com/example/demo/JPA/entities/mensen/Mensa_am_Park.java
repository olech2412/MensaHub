package com.example.demo.JPA.entities.mensen;

import com.example.demo.JPA.entities.MailUser;
import com.example.demo.JPA.entities.meals.Meals_Mensa_am_Park;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_park")
public class Mensa_am_Park extends Mensa {

    @OneToMany(mappedBy = "mensa_am_park")
    private Set<Meals_Mensa_am_Park> meals_mensa_am_park;

    @OneToMany(mappedBy = "mensa_am_park", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}
