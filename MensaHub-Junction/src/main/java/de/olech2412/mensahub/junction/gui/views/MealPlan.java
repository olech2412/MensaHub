package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.JPA.services.meals.MealsService;
import de.olech2412.mensahub.junction.gui.components.InfoBox;
import de.olech2412.mensahub.junction.gui.components.MealBox;
import de.olech2412.mensahub.models.Meal;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Route("mealPlan")
@PageTitle("Speiseplan")
@AnonymousAllowed
@Slf4j
public class MealPlan extends VerticalLayout implements BeforeEnterObserver {

    private final MealsService mealsService;

    public MealPlan(MealsService mealsService) {
        this.mealsService = mealsService;

        List<Meal> meals = this.mealsService.findAll();
        List<Meal> realMeals = meals.stream().filter(meal -> meal.getServingDate().equals(LocalDate.now())).toList();

        HorizontalLayout row = new HorizontalLayout();
        row.add(new InfoBox("Mensa am Park", "https://test.de"));
        row.addClassName("meal-row");
        row.setWidthFull();
        row.getStyle().set("flex-wrap", "wrap");

        for (int i = 0; i < 10; i++) {
            Meal meal = realMeals.get(i);
            MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory());
            row.add(mealBox);
        }

        add(row);
    }

    /**
     * @param beforeEnterEvent
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        log.info("MealPlan entered");
    }
}