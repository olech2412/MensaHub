package de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;

@JavaScript("frontend://local-storage.js")
@Getter
public class InfoWithAnchorNotification extends MensaHubNotification {

    Button closeButton;

    public InfoWithAnchorNotification(String message, String anchorText, Class<? extends Component> navigationTarget) {
        super(message);

        RouterLink link = new RouterLink(anchorText, navigationTarget);
        link.addClassName("shimmer");
        Div text = new Div(new Text(message), link);

        closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.setAriaLabel("Close");

        HorizontalLayout layout = new HorizontalLayout(text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        add(layout);
        setDuration(-1);
        setPosition(Position.MIDDLE);
    }
}