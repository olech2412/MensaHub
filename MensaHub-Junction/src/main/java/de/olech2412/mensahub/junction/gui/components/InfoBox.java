package de.olech2412.mensahub.junction.gui.components;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.accordion.Accordion;

public class InfoBox extends VerticalLayout {


    public InfoBox(String mensaName, String linkForMensa) {
        addClassName("info-box");

        H4 h4 = new H4(mensaName);
        h4.addClassName("info-box-title");

        Accordion accordion = new Accordion();
        accordion.close();

        Span linkText = new Span(linkForMensa);

        VerticalLayout linkLayout = new VerticalLayout(linkText);
        linkLayout.setSpacing(false);
        linkLayout.setPadding(false);
        linkLayout.addClassName("accordion-content");

        accordion.add("Link zur Mensa", linkLayout);
        accordion.addClassName("accordion-summary");

        Paragraph infoText = new Paragraph("Achtung! Die angezeigten Daten können von der Realität abweichen," +
                " bei Allergenen oder Fragen zu den Gerichten solltest du dich unbedingt an die Mensa wenden." +
                " Für die Korrektheit der Daten übernehmen wir keine Haftung. Es ist außerdem möglich, dass die Mensa" +
                " weitere Angebote zur Verfügung stellt, diese jedoch nicht auf der Website anpreist.");
        infoText.addClassName("info-box-text");

        add(h4, infoText, accordion);
    }
}