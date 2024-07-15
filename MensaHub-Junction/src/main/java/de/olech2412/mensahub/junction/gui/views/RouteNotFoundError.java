package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.servlet.http.HttpServletResponse;

@PageTitle("Not Found")
@Tag(Tag.DIV)
@AnonymousAllowed
public class RouteNotFoundError extends VerticalLayout
        implements HasErrorParameter<NotFoundException> {

    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<NotFoundException> parameter) {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames("custom-404-view");

        H1 title = new H1("404");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.FontWeight.BOLD);

        Div message = new Div(new Text("Oops! Hier ist etwas schief gelaufen. Die Seite existiert leider nicht :(."));
        message.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);

        Button homeButton = new Button("Zur Newsletter-Anmeldung", buttonClickEvent -> {
            getUI().ifPresent(ui -> ui.navigate(UserView.class));
        });
        homeButton.addClassNames(LumoUtility.Margin.Top.MEDIUM);
        homeButton.addClassName("button-link");
        homeButton.setIcon(VaadinIcon.ENVELOPE_OPEN.create());

        Button mealPlanButton = new Button("Zum Speiseplan", buttonClickEvent -> {
            getUI().ifPresent(ui -> ui.navigate(MealPlan.class));
        });
        mealPlanButton.addClassNames(LumoUtility.Margin.Top.MEDIUM);
        mealPlanButton.addClassName("button-link");
        mealPlanButton.setIcon(VaadinIcon.LINK.create());

        layout.add(title, message, homeButton, mealPlanButton);
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        layout.setHeight("60vh"); // Begrenze die Höhe des Banners
        layout.setWidth("80vw"); // Setze eine Breite für das Banner
        layout.getStyle().set("margin", "auto"); // Zentriere das Banner
        layout.addClassName("custom-404-view");

        add(layout);
        return HttpServletResponse.SC_NOT_FOUND;
    }
}