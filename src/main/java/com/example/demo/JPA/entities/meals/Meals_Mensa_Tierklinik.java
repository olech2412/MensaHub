package com.example.demo.JPA.entities.meals;

import com.example.demo.JPA.entities.mensen.Mensa_Tierklinik;
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
@Table(name = "meals_mensa_tierklinik")
public class Meals_Mensa_Tierklinik extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_tierklinik_id", nullable = false)
    @JsonIgnore
    private Mensa_Tierklinik mensa_tierklinik;

    public Meals_Mensa_Tierklinik() {

    }

}
