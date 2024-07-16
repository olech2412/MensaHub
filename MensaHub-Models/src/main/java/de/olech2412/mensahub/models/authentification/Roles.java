package de.olech2412.mensahub.models.authentification;

public enum Roles {

    ROLE_USER ("ROLE_USER"),
    ROLE_ADMIN ("ROLE_ADMIN"),
    ROLE_API_USER("ROLE_API_USER"),
    ROLE_SUPER_ADMIN ("ROLE_SUPER_ADMIN");

    private String text;

    Roles(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
