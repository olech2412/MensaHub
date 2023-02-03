package com.example.demo.JPA.entities.meals;

import com.example.demo.JPA.entities.mensen.Cafeteria_Dittrichring;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_cafeteria_dittrichring")
public class Meals_Cafeteria_Dittrichring extends Meal {

    @ManyToOne
    @JoinColumn(name = "cafeteria_dittrichring_id", nullable = false)
    private Cafeteria_Dittrichring cafeteria_dittrichring;

    public Meals_Cafeteria_Dittrichring() {

    }

}
