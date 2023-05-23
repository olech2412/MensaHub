package com.essensGetter.api.JPA.entities.meals;

import com.essensGetter.api.JPA.entities.mensen.Mensa_Schoenauer_Str;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "meals_schoenauer_str")
public class Meals_Schoenauer_Str extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_schoenauer_str_id", nullable = false)
    @JsonIgnore
    private Mensa_Schoenauer_Str mensa_schoenauer_str;

    public Meals_Schoenauer_Str() {

    }

}
