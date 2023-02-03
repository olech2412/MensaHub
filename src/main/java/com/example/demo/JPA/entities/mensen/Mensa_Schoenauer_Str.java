package com.example.demo.JPA.entities.mensen;

import com.example.demo.JPA.entities.MailUser;
import com.example.demo.JPA.entities.meals.Meals_Schoenauer_Str;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "mensa_schoenauer_str")
public class Mensa_Schoenauer_Str extends Mensa {

    @OneToMany(mappedBy = "mensa_schoenauer_str")
    private Set<Meals_Schoenauer_Str> meals_schoenauer_strList;

    @OneToMany(mappedBy = "mensa_schoenauer_str", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}