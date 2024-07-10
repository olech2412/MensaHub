package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.JPA.services.meals.MealsService;
import de.olech2412.mensahub.junction.JPA.services.mensen.MensaService;
import de.olech2412.mensahub.junction.gui.components.InfoBox;
import de.olech2412.mensahub.junction.gui.components.MealBox;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
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

    private final MensaService mensaService;

    public MealPlan(MealsService mealsService, MensaService mensaService) {
        this.mealsService = mealsService;
        this.mensaService = mensaService;

        HorizontalLayout pageSelHeader = new HorizontalLayout();

        ComboBox<Mensa> mensaComboBox = new ComboBox<>();
        mensaComboBox.setItems(mensaService.getAllMensas());
        mensaComboBox.setItemLabelGenerator(Mensa::getName);

        pageSelHeader.add(new VerticalLayout(new H2("Wähle deine Mensa aus und der Speiseplan erscheint auf magische Weise."), mensaComboBox));

        HorizontalLayout row = new HorizontalLayout();
        mensaComboBox.addValueChangeListener(comboBoxMensaComponentValueChangeEvent -> {
            if(comboBoxMensaComponentValueChangeEvent.getValue() == null) {
                return;
            }

            row.removeAll();
            List<Meal> meals = mealsService.findAllMealsByServingDateAndMensa(LocalDate.now(), comboBoxMensaComponentValueChangeEvent.getValue());

            row.addClassName("meal-content");
            row.add(new InfoBox(comboBoxMensaComponentValueChangeEvent.getValue().getName(),
                    comboBoxMensaComponentValueChangeEvent.getValue().getApiUrl().replace("&date=$date", "")));
            row.addClassName("meal-row");
            row.setWidthFull();
            row.getStyle().set("flex-wrap", "wrap");

            for (Meal meal : meals) {
                MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory());
                row.add(mealBox);
            }

            add(row);
        });

        add(pageSelHeader);
    }

    /**
     * @param beforeEnterEvent
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        log.info("MealPlan entered");
    }
}