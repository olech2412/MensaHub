package com.essensGetter.api.JPA.entities.meals;

import com.essensGetter.api.JPA.entities.mensen.Mensa_Peterssteinweg;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_peterssteinweg")
public class Meals_Mensa_Peterssteinweg extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_peterssteinweg_id", nullable = false)
    @JsonIgnore
    private Mensa_Peterssteinweg mensa_peterssteinweg;

    public Meals_Mensa_Peterssteinweg() {

    }

}
