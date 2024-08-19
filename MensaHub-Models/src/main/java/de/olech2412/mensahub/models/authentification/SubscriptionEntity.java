package de.olech2412.mensahub.models.authentification;

import jakarta.persistence.*;

@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Embedded
    private KeysEntity keys;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(String endpoint, KeysEntity keys) {
        this.endpoint = endpoint;
        this.keys = keys;
    }

    public Long getId() {
        return id;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public KeysEntity getKeys() {
        return keys;
    }

    @Embeddable
    public static class KeysEntity {

        @Column(name = "p256dh", nullable = false)
        private String p256dh;

        @Column(name = "auth", nullable = false)
        private String auth;

        public KeysEntity() {
        }

        public KeysEntity(String p256dh, String auth) {
            this.p256dh = p256dh;
            this.auth = auth;
        }

        public String getP256dh() {
            return p256dh;
        }

        public String getAuth() {
            return auth;
        }
    }
}
