package de.olech2412.mensahub.models.authentification;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_LOGIN_USER(Names.ROLE_LOGIN_USER),
    ROLE_ADMIN(Names.ROLE_ADMIN),
    ROLE_API_USER(Names.ROLE_API_USER),
    ROLE_SUPER_ADMIN(Names.ROLE_SUPER_ADMIN);

    private final String text;

    Role(String text){
        this.text = text;
    }

    public static class Names{
        public static final String ROLE_LOGIN_USER = "ROLE_LOGIN_USER";
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_API_USER = "ROLE_API_USER";
        public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    }

}
