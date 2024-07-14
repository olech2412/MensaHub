package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
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
import de.olech2412.mensahub.junction.gui.components.own.InfoBox;
import de.olech2412.mensahub.junction.gui.components.own.MealBox;
import de.olech2412.mensahub.junction.gui.components.vaadin.GermanDatePicker;
import de.olech2412.mensahub.models.Meal;
import de.olech2412.mensahub.models.Mensa;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Route("mealPlan")
@PageTitle("Speiseplan")
@AnonymousAllowed
@Slf4j
public class MealPlan extends VerticalLayout implements BeforeEnterObserver {

    private final MealsService mealsService;

    private final MensaService mensaService;

    private ComboBox<Mensa> mensaComboBox = new ComboBox<>();

    private GermanDatePicker datePicker = new GermanDatePicker();

    private List<Meal> meals;

    public MealPlan(MealsService mealsService, MensaService mensaService) {
        this.mealsService = mealsService;
        this.mensaService = mensaService;

        datePicker.setValue(LocalDate.now());

        HorizontalLayout pageSelHeader = new HorizontalLayout();
        mensaComboBox.setItems(mensaService.getAllMensas());
        mensaComboBox.setItemLabelGenerator(Mensa::getName);

        HorizontalLayout headerComboboxLayout = new HorizontalLayout();
        headerComboboxLayout.setWidth(100f, Unit.PERCENTAGE);
        headerComboboxLayout.setAlignItems(Alignment.CENTER);
        headerComboboxLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        headerComboboxLayout.add(mensaComboBox, datePicker);

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidth(80f, Unit.PERCENTAGE);
        filterLayout.setAlignItems(Alignment.CENTER);
        filterLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        //adjust the width of the combobox and datePicker
        mensaComboBox.setWidth(60f, Unit.PERCENTAGE);
        datePicker.setWidth(40f, Unit.PERCENTAGE);

        VerticalLayout headerContent = new VerticalLayout();
        headerContent.setWidth(100f, Unit.PERCENTAGE);
        headerContent.setAlignItems(Alignment.CENTER);
        headerContent.setJustifyContentMode(JustifyContentMode.CENTER);
        headerContent.getStyle().set("text-align", "center");
        headerContent.add(new H2("Wähle deine Mensa aus, sowie das gewünschte Datum"));
        headerContent.add(headerComboboxLayout, filterLayout);

        pageSelHeader.add(headerContent);

        HorizontalLayout row = new HorizontalLayout();
        row.setJustifyContentMode(JustifyContentMode.CENTER);

        mensaComboBox.addValueChangeListener(comboBoxMensaComponentValueChangeEvent -> {
            if (comboBoxMensaComponentValueChangeEvent.getValue() == null) {
                return;
            }
            row.removeAll();
            meals = mealsService.findAllMealsByServingDateAndMensa(datePicker.getValue(), comboBoxMensaComponentValueChangeEvent.getValue());

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
            UI.getCurrent().getPage().getHistory().replaceState(null, String.format("/mealPlan?mensa=%s" + String.format("&date=%s", datePicker.getValue()), comboBoxMensaComponentValueChangeEvent.getValue().getId()));
            add(row);
        });

        datePicker.addValueChangeListener(comboBoxMensaComponentValueChangeEvent -> {
            if (comboBoxMensaComponentValueChangeEvent.getValue() == null || mensaComboBox.isEmpty()) {
                return;
            }

            row.removeAll();
            meals = mealsService.findAllMealsByServingDateAndMensa(comboBoxMensaComponentValueChangeEvent.getValue(), mensaComboBox.getValue());

            row.addClassName("meal-content");
            row.add(new InfoBox(mensaComboBox.getValue().getName(),
                    mensaComboBox.getValue().getApiUrl().replace("$date", comboBoxMensaComponentValueChangeEvent.getValue().toString())));
            row.addClassName("meal-row");
            row.setWidthFull();
            row.getStyle().set("flex-wrap", "wrap");

            for (Meal meal : meals) {
                MealBox mealBox = new MealBox(meal.getName(), meal.getDescription(), meal.getPrice(), meal.getAllergens(), meal.getCategory());
                row.add(mealBox);
            }
            UI.getCurrent().getPage().getHistory().replaceState(null, String.format("/mealPlan?date=%s" + String.format("&mensa=%s", mensaComboBox.getValue().getId()), comboBoxMensaComponentValueChangeEvent.getValue()));
            add(row);
        });

        add(pageSelHeader);

        // adjust the layout to center all elements in it
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    /**
     * @param beforeEnterEvent
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        log.info("Entered meal plan");
        Map<String, List<String>> params = beforeEnterEvent.getLocation().getQueryParameters().getParameters();

        String mensaParam;
        String date = "";

        if (params.containsKey("mensa")) {
            mensaParam = params.get("mensa").get(0);
        } else {
            return;
        }

        if (params.containsKey("date")) {
            date = params.get("date").get(0);
        }

        Optional<Mensa> mensa;
        // check if mensaParam is an integer
        try {
            mensa = Optional.of(mensaService.mensaById(Long.parseLong(mensaParam)));
        } catch (NumberFormatException numberFormatException) {
            mensa = mensaService.findAll().stream().filter(mensa1 -> mensa1.getName().equals(mensaParam)).findAny();
        }

        Optional<Mensa> finalMensa = mensa;
        mensa.ifPresent(value -> mensaComboBox.setValue(finalMensa.get()));

        if (!date.isEmpty()) {
            if(date.equals("today")){
                datePicker.setValue(LocalDate.now());
            } else {
                datePicker.setValue(LocalDate.parse(date));
            }
        }

    }

}