package de.olech2412.mensahub.junction.gui.components.vaadin.buttons;

import com.vaadin.flow.component.button.Button;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.*;

public class ButtonFactory {

    public static Button create(ButtonType type, String text) {
        switch (type) {
            case SAVE -> {
                return new SaveButton(text);
            }
            case WARN -> {
                return new WarnButton(text);
            }
            case ABORT -> {
                return new AbortButton(text);
            }
            case ACCEPT -> {
                return new AcceptButton(text);
            }
            case DELETE -> {
                return new DeleteButton(text);
            }
            case CUSTOM -> {
                return new Button(text);
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

}
