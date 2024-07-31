package de.olech2412.mensahub.junction.gui.components.vaadin.grids;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import de.olech2412.mensahub.models.authentification.Users;

public class UserGrid extends Grid<Users> {

    public UserGrid() {
        setNestedNullBehavior(NestedNullBehavior.ALLOW_NULLS);
        setSelectionMode(SelectionMode.SINGLE);
        setWidth(100f, Unit.PERCENTAGE);
        addClassName("job-grid");

        addColumn(Users::getUsername).setHeader("Nutzerkennung").setAutoWidth(true).setSortable(true);
        addComponentColumn(users -> formatBoolean(users.getEnabled())).setHeader("Freigeschaltet").setAutoWidth(true).setSortable(true);
        addComponentColumn(users -> formatBoolean(users.getProponent())).setHeader("Befürworter").setAutoWidth(true).setSortable(true);
        addColumn(users -> users.getRole().toString()).setHeader("Rolle").setAutoWidth(true).setSortable(true);
    }

    private Span formatBoolean(boolean value) {
        Span badge = new Span(value ? "Ja" : "Nein");
        badge.getElement().getThemeList().add(value ? "badge success" : "badge error");
        return badge;
    }

}