package de.olech2412.mensahub.junction.gui.components.vaadin.dialogs;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WebBrowser;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.authentification.MailUser;
import lombok.Getter;

import java.util.List;

public class MailUserSetupDialog extends Dialog {

    @Getter
    MultiSelectComboBox<Mensa> mensaComboBox;

    @Getter
    Checkbox wantsUpdateCheckbox;

    @Getter
    Button saveButton;

    public MailUserSetupDialog(MailUser mailUser, List<Mensa> mensen) {
        super(String.format("E-Mail Einstellungen für Benutzer %s", mailUser.getEmail()));

        mensaComboBox = new MultiSelectComboBox<>("Bearbeite deine ausgewählten Mensen");
        mensaComboBox.setItems(mensen);
        mensaComboBox.setItemLabelGenerator(Mensa::getName);
        mensaComboBox.select(mailUser.getMensas());
        mensaComboBox.setWidth(100, Unit.PERCENTAGE);


        wantsUpdateCheckbox = new Checkbox("Möchtest du benachrichtigt werden, wenn Änderungen am Speiseplan festgestellt werden?");
        wantsUpdateCheckbox.setValue(mailUser.isWantsUpdate());

        saveButton = new Button("Speichern");
        saveButton.setIcon(VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);

        Button cancelButton = new Button("Abbrechen");
        cancelButton.setIcon(VaadinIcon.CLOSE.create());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);

        cancelButton.addClickListener(buttonClickEvent -> close());

        HorizontalLayout chooserLayout = new HorizontalLayout(saveButton, cancelButton);
        chooserLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        chooserLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        add(new VerticalLayout(mensaComboBox, wantsUpdateCheckbox));
        add(chooserLayout);
    }

    public boolean isMobileDevice() {
        WebBrowser webBrowser = VaadinSession.getCurrent().getBrowser();
        return webBrowser.isAndroid() || webBrowser.isIPhone() || webBrowser.isWindowsPhone();
    }
}
