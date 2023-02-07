package com.example.demo.JPA.entities.meals;

import com.example.demo.JPA.entities.mensen.Mensa_am_Medizincampus;
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
@Table(name = "meals_mensa_am_medizincampus")
public class Meals_Mensa_am_Medizincampus extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_am_medizincampus_id", nullable = false)
    @JsonIgnore
    private Mensa_am_Medizincampus mensa_am_medizincampus;

    public Meals_Mensa_am_Medizincampus() {

    }

}
