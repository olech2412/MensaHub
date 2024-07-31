package de.olech2412.mensahub.junction.gui.components.vaadin.layouts.panels.adminPanel;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.olech2412.mensahub.junction.gui.components.own.CustomDisplay;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.ButtonFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.buttons.types.ButtonType;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.ConfirmDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.dialogs.EditJobDialog;
import de.olech2412.mensahub.junction.gui.components.vaadin.grids.JobGrid;
import de.olech2412.mensahub.junction.gui.components.vaadin.grids.UserGrid;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.NotificationFactory;
import de.olech2412.mensahub.junction.gui.components.vaadin.notifications.types.NotificationType;
import de.olech2412.mensahub.junction.jpa.services.UserService;
import de.olech2412.mensahub.models.authentification.Role;
import de.olech2412.mensahub.models.authentification.Users;
import de.olech2412.mensahub.models.jobs.Job;
import de.olech2412.mensahub.models.jobs.JobStatus;
import de.olech2412.mensahub.models.result.Result;
import de.olech2412.mensahub.models.result.errors.jpa.JPAError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
public class UserPanel extends VerticalLayout {

    UserGrid userGrid;

    Button newUserButton;

    public UserPanel(UserService userService, Users currentUser) {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        HorizontalLayout infoDisplayLayout = new HorizontalLayout();

        newUserButton = ButtonFactory.create(ButtonType.CUSTOM, "Einen neuen User anlegen");
        newUserButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newUserButton.setIcon(VaadinIcon.USER.create());

        Scroller userInfoScroller = new Scroller();
        userInfoScroller.setScrollDirection(Scroller.ScrollDirection.HORIZONTAL);

        CustomDisplay allUsers = new CustomDisplay("Anzahl gespeicherter User");
        Result<List<Users>, JPAError> resultAllUsers = userService.findAll();
        if (resultAllUsers.isSuccess()) {
            allUsers.setValue(String.valueOf(resultAllUsers.getData().size()));
        } else {
            allUsers.setValue(resultAllUsers.getError().error().getCode());
        }
        allUsers.setThresholds(2, 1, 0);

        CustomDisplay blockedUsers = new CustomDisplay("Anzahl gesperrter Nutzer");
        Result<List<Users>, JPAError> resultBlockedUsers = userService.findAllByEnabled(false);
        if (resultBlockedUsers.isSuccess()) {
            blockedUsers.setValue(String.valueOf(resultBlockedUsers.getData().size()));
        } else {
            blockedUsers.setValue(resultBlockedUsers.getError().error().getCode());
        }
        blockedUsers.setThresholds(0, 1, 2);

        CustomDisplay adminUsers = new CustomDisplay("Anzahl Admins");
        Result<List<Users>, JPAError> resultAdminUsers = userService.findAllBy2Roles(Role.ROLE_ADMIN, Role.ROLE_SUPER_ADMIN);
        if (resultAdminUsers.isSuccess()) {
            adminUsers.setValue(String.valueOf(resultAdminUsers.getData().size()));
        } else {
            adminUsers.setValue(resultAdminUsers.getError().error().getCode());
        }
        adminUsers.setThresholds(0, 1, 2);

        CustomDisplay apiUsers = new CustomDisplay("Anzahl API-User");
        Result<List<Users>, JPAError> resultApiUsers = userService.findAllByRole(Role.ROLE_API_USER);
        if (resultApiUsers.isSuccess()) {
            apiUsers.setValue(String.valueOf(resultApiUsers.getData().size()));
        } else {
            apiUsers.setValue(resultApiUsers.getError().error().getCode());
        }
        apiUsers.setThresholds(0, 1, 2);

        CustomDisplay loginUsers = new CustomDisplay("Anzahl Login-User");
        Result<List<Users>, JPAError> resultLoginUsers = userService.findAllByRole(Role.ROLE_LOGIN_USER);
        if (resultLoginUsers.isSuccess()) {
            loginUsers.setValue(String.valueOf(resultLoginUsers.getData().size()));
        } else {
            loginUsers.setValue(resultLoginUsers.getError().error().getCode());
        }
        //loginUsers.setThresholds(0, 1, 2);

        infoDisplayLayout.add(allUsers, blockedUsers, adminUsers, apiUsers, loginUsers);

        infoDisplayLayout.setPadding(true);
        infoDisplayLayout.getStyle().set("display", "inline-flex");
        infoDisplayLayout.setWidth(100f, Unit.PERCENTAGE);

        userInfoScroller.setContent(infoDisplayLayout);

        userInfoScroller.setVisible(true);
        userInfoScroller.setWidth(100f, Unit.PERCENTAGE);

        userGrid = new UserGrid();

        Result<List<Users>, JPAError> resultFetchAllJobs = userService.findAll();
        GridListDataView<Users> gridListDataView;
        if (!resultFetchAllJobs.isSuccess()) {
            NotificationFactory.create(NotificationType.ERROR, "Fehler beim laden der User").open();
            // if this happens, the view is fucked up so throw an error
            throw new RuntimeException(resultFetchAllJobs.getError().message());
        } else {
            gridListDataView = userGrid.setItems(resultFetchAllJobs.getData());
        }

        GridContextMenu<Users> usersGridContextMenu = new GridContextMenu<>(userGrid);
        GridMenuItem<Users> edit = usersGridContextMenu.addItem("Bearbeiten", event -> {
        });
        edit.addComponentAsFirst(VaadinIcon.EDIT.create());
        GridMenuItem<Users> allow = usersGridContextMenu.addItem("", event -> {
            if (event.getItem().isPresent()){
                Users user = event.getItem().get();
                if(user.getEnabled()){
                    user.setEnabled(false);
                } else {
                    //user.setEnabled(true);
                }

                Result<Users, JPAError> result = userService.saveUser(event.getItem().get());
                if (result.isSuccess()) {
                    NotificationFactory.create(NotificationType.SUCCESS, "Änderung wurde gespeichert").open();
                    gridListDataView.refreshItem(user);
                } else {
                    NotificationFactory.create(NotificationType.ERROR, "Beim löschen des Users ist ein Fehler aufgetreten").open();
                }
            }
        });
        allow.addComponentAsFirst(VaadinIcon.CHECK.create());

        usersGridContextMenu.addGridContextMenuOpenedListener(usersGridContextMenuOpenedEvent -> {
            if (usersGridContextMenuOpenedEvent.getItem().isPresent()) {
                Users user = usersGridContextMenuOpenedEvent.getItem().get();
                if(user.getEnabled()){
                    allow.setText("Sperren");
                    allow.addComponentAsFirst(VaadinIcon.BAN.create());
                } else {
                    allow.setText("Freischalten");
                    allow.addComponentAsFirst(VaadinIcon.CHECK.create());
                }
            }
        });

        GridMenuItem<Users> delete = usersGridContextMenu.addItem("Löschen", event -> {
            if (event.getItem().isPresent()) {
                ConfirmDialog confirmDialog = new ConfirmDialog(String.format("Möchtest du den User %s wirklich löschen?",
                        event.getItem().get().getUsername()), "Das Löschen des Users kann nicht rückgängig gemacht werden. Handle also mit bedacht.");
                confirmDialog.open();

                confirmDialog.acceptButton.addClickListener(buttonClickEvent -> {
                    Result<Users, JPAError> result = userService.deleteUser(event.getItem().get());
                    if (result.isSuccess()) {
                        NotificationFactory.create(NotificationType.SUCCESS, "User wurde gelöscht").open();
                        gridListDataView.removeItem(event.getItem().get());
                        gridListDataView.refreshAll();
                        confirmDialog.close();
                    } else {
                        NotificationFactory.create(NotificationType.ERROR, "Beim löschen des Users ist ein Fehler aufgetreten").open();
                        confirmDialog.close();
                    }
                });

                confirmDialog.declineButton.addClickListener(buttonClickEvent -> confirmDialog.close());
            }
        });
        delete.addComponentAsFirst(VaadinIcon.TRASH.create());

        add(newUserButton, userInfoScroller, userGrid);
    }
}

