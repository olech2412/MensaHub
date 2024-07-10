package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import de.olech2412.mensahub.junction.gui.components.MealBox;

@Route("mealPlan")
@PageTitle("Speiseplan")
@AnonymousAllowed
public class MealPlan extends VerticalLayout implements BeforeEnterObserver {

    public MealPlan() {
        MealBox mealBox = new MealBox("Bockwurst", "Einfach nh simple Bogger", "1,97€", "Gift", "Vegan");

        add(mealBox);
    }

    /**
     * @param beforeEnterEvent
     */
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }
}
