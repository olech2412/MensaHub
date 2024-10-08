package de.olech2412.mensahub.junction.gui.components.own;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class CustomComboBoxLayout extends VerticalLayout {

    private final List<String> values = new ArrayList<>();
    TextField textField;
    FormLayout badges = new FormLayout();
    @Setter
    private Pattern pattern;

    public CustomComboBoxLayout(String textFieldLabel) {
        badges.getStyle().set("flex-wrap", "wrap");

        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        textField = new TextField(textFieldLabel);
        textField.setWidth(100f, Unit.PERCENTAGE);
        textField.setClearButtonVisible(true);
        textField.addValueChangeListener(e -> {
            addNewItem(e.getValue());
        });

        add(textField, badges);
        setPadding(false);
        setSizeUndefined();
    }

    public void addNewItem(String item) {
        Span filterBadge = createFilterBadge(item);
        if (item != null && !item.isEmpty() && !values.contains(item)) {
            if (pattern == null) {
                badges.add(filterBadge);
                values.add(item);
                textField.clear();
            } else {
                pattern = Pattern.compile(pattern.pattern());
                if (pattern.matcher(item).matches()) {
                    badges.add(filterBadge);
                    values.add(item);
                    textField.clear();
                }
            }
        }
    }

    private Span createFilterBadge(String input) {
        Button clearButton = new Button(VaadinIcon.CLOSE_SMALL.create());
        clearButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST,
                ButtonVariant.LUMO_TERTIARY_INLINE);
        clearButton.getStyle().set("margin-inline-start",
                "var(--lumo-space-xs)");
        clearButton.getElement().setAttribute("aria-label",
                "Clear filter: " + input);
        // Tooltip
        clearButton.getElement().setAttribute("title",
                "Clear filter: " + input);

        Span badge = new Span(new Span(String.valueOf(input)), clearButton);
        badge.getStyle().set("margin-top", "2px");
        badge.getStyle().set("margin-bottom", "2px");
        badge.getElement().getThemeList().add("badge contrast pill");

        // Add handler for removing the badge
        clearButton.addClickListener(event -> {
            badge.getElement().removeFromParent();
            values.remove(input);
        });

        return badge;
    }


    public Collection<String> getItems() {
        return values;
    }

    public void setItems(List<String> items) {
        for (String item : items) {
            Span badge = createFilterBadge(item);
            badges.add(badge);
            values.add(item);
        }
    }

    public void clear() {
        textField.clear();
        values.clear();
        badges.removeAll();
    }

}
