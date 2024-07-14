package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.accordion.Accordion;

public class InfoBox extends VerticalLayout {


    public InfoBox(String mensaName, String linkForMensa) {
        addClassName("info-box");

        H4 h4 = new H4(mensaName);
        h4.addClassName("info-box-title");

        Paragraph infoText = new Paragraph("Achtung! Die angezeigten Daten können von der Realität abweichen," +
                " bei Allergenen oder Fragen zu den Gerichten solltest du dich unbedingt an die Mensa wenden." +
                " Für die Korrektheit der Daten übernehmen wir keine Haftung. Es ist außerdem möglich, dass die Mensa" +
                " weitere Angebote zur Verfügung stellt, diese jedoch nicht auf der Website anpreist.");
        infoText.addClassName("info-box-text");

        Button goToMensa = new Button("Zur Website der Mensa");
        goToMensa.setClassName("button-mensa-link");
        goToMensa.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        goToMensa.setIcon(VaadinIcon.LINK.create());
        goToMensa.addClickListener(buttonClickEvent -> {
            UI.getCurrent().getPage().setLocation(linkForMensa);
        });

        add(h4, infoText, goToMensa);
    }
}