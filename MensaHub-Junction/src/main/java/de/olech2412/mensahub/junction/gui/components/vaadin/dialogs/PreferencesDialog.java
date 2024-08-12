package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.olech2412.mensahub.junction.gui.components.own.CustomComboBoxLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.layouts.generic.FooterButtonLayout;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.services.meals.MealsService;
import de.olech2412.mensahub.models.Preferences;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class PreferencesDialog extends Dialog {

    private FooterButtonLayout footerButtonLayout = new FooterButtonLayout();

    private MultiSelectComboBox<String> categories = new MultiSelectComboBox<>("Auszuschließende Kategorien");

    private MultiSelectComboBox<String> allergens = new MultiSelectComboBox<>("Allergene");

    private CustomComboBoxLayout personalDislikes = new CustomComboBoxLayout("Persönliche Abneigungen");

    public PreferencesDialog(MealsService mealsService) {
        super("Persönliche Präferenzen angeben");

        VerticalLayout content = new VerticalLayout();
        content.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.setAlignItems(FlexComponent.Alignment.CENTER);

        categories.setAllowCustomValue(false);
        categories.setWidth(100f, Unit.PERCENTAGE);
        categories.setHelperText("Wähle Kategorien, für welche du keine Empfehlungen möchtest");

        allergens.setWidth(100f, Unit.PERCENTAGE);
        allergens.setAllowCustomValue(false);
        allergens.setClearButtonVisible(true);
        allergens.setHelperText("Gib Allergene oder Unverträglichkeiten an, für welche du keine Empfehlungen möchtest");

        Result<List<String>, JPAError> categoriesResult = mealsService.findAllDistinctCategories();
        if (categoriesResult.isSuccess()){
            categories.setItems(categoriesResult.getData());
        } else {
            NotificationFactory.create(NotificationType.ERROR, "Die Kategorien konnten nicht geladen werden, bitte versuche es erneut.").open();
        }

        Result<List<String>, JPAError> allergenResult = mealsService.findAllUniqueAllergens();
        if (allergenResult.isSuccess()){
            allergens.setItems(allergenResult.getData());
        } else {
            NotificationFactory.create(NotificationType.ERROR, "Die Allergene konnten nicht geladen werden, bitte versuche es erneut.").open();
        }

        personalDislikes.setWidth(90f, Unit.PERCENTAGE);
        personalDislikes.setPattern(Pattern.compile("^[a-zA-ZäöüÄÖÜß]{2,}(\\\\s[a-zA-ZäöüÄÖÜß]{2,})*$"));
        personalDislikes.getTextField().setHelperText("Gib z.B. Zutaten wie \"Pilze\" an, für welche du keine Empfehlungen möchtest");

        footerButtonLayout.acceptButton.setText("Speichern");

        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        footerButtonLayout.getAcceptButton().addClickListener(buttonClickEvent -> close());
        footerButtonLayout.getDeclineButton().addClickListener(buttonClickEvent -> {
            close();
            categories.clear();
            allergens.clear();
            personalDislikes.clear();
        });

        HorizontalLayout personalDislikesLayout = new HorizontalLayout();
        personalDislikesLayout.setWidth(100f, Unit.PERCENTAGE);
        Button enterButton = new Button(new Icon(VaadinIcon.ENTER_ARROW));
        enterButton.setWidth(10f, Unit.PERCENTAGE);
        enterButton.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_PRIMARY);
        enterButton.setAriaLabel("Enter");
        enterButton.getStyle().setMarginTop("30px");
        personalDislikesLayout.add(personalDislikes, enterButton);
        personalDislikesLayout.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, personalDislikes.getTextField(), enterButton);


        enterButton.addClickListener(buttonClickEvent -> {
            if (personalDislikes.getTextField().isEmpty()) return;
            personalDislikes.addNewItem(personalDislikes.getTextField().getValue());
        });

        content.add(categories, allergens, personalDislikesLayout, footerButtonLayout);
        Paragraph infoText = new Paragraph("Diese Daten kannst du freiwillig angeben. Die Daten werden lediglich dazu genutzt, " +
                "die Empfehlungen für dich zu verbessern. Dies ist besonders wichtig, wenn du den Newsletter lediglich erhalten möchtest, " +
                "wenn ein Gericht für dich empfohlen wird. Beachte bitte, dass trotz der Angaben, Empfehlungen berechnet werden können, die " +
                "dir nicht gefallen, oder gegen deine angegebenen Präferenzen verstoßen. Besonders beim Thema Allergene und Unverträglichkeiten " +
                "solltest du immer bei der Mensa vor Ort nachfragen, falls du dir unsicher bist.");
        add(infoText, content);
        setWidth(50f, Unit.PERCENTAGE);
        setMinWidth(350, Unit.PIXELS);
    }

    public Preferences buildPreferences(){
        Preferences preferences = new Preferences();

        if (categories.isEmpty()){
            preferences.setPreferredCategories(null);
        } else {
            preferences.setPreferredCategories(categories.getValue().stream().toList());
        }

        if (allergens.isEmpty()){
            preferences.setAvoidedAllergens(null);
        } else {
            preferences.setAvoidedAllergens(allergens.getValue().stream().toList());
        }

        if(personalDislikes.getValues().isEmpty()){
            preferences.setDislikedIngredients(null);
        } else {
            preferences.setDislikedIngredients(personalDislikes.getValues());
        }

        return preferences;
    }

    public void setPreferences(Preferences preferences){
        if(preferences == null){
            return;
        }
        if (preferences.getPreferredCategories() != null) categories.setValue(preferences.getPreferredCategories());
        if(preferences.getAvoidedAllergens() != null) allergens.setValue(preferences.getAvoidedAllergens());
        if(preferences.getDislikedIngredients() != null) personalDislikes.setItems(preferences.getDislikedIngredients());
    }

}
