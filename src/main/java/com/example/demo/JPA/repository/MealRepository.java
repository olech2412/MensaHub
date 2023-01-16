package com.example.demo.JPA.repository;

import com.example.demo.JPA.Meal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealRepository extends CrudRepository<Meal, Long> {

    Meal findByName(String name);

}
