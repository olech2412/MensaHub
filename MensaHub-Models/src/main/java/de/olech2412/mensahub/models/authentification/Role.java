package de.olech2412.mensahub.models.authentification;

import lombok.Getter;

@Getter
public enum Role {

    LOGIN_USER(Names.LOGIN_USER),
    ADMIN(Names.ADMIN),
    API_USER(Names.API_USER),
    SUPER_ADMIN(Names.SUPER_ADMIN);

    private final String text;

    Role(String text){
        this.text = text;
    }

    public static class Names{
        public static final String LOGIN_USER = "LOGIN_USER";
        public static final String ADMIN = "ADMIN";
        public static final String API_USER = "API_USER";
        public static final String SUPER_ADMIN = "SUPER_ADMIN";
    }

}
