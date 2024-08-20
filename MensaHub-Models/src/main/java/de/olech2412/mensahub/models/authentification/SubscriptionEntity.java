package de.olech2412.mensahub.models.authentification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class SubscriptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Embedded
    private KeysEntity keys;

    private String deviceInfo;

    public SubscriptionEntity() {
    }

    public SubscriptionEntity(String endpoint, KeysEntity keys, String deviceInfo) {
        this.endpoint = endpoint;
        this.keys = keys;
        this.deviceInfo = deviceInfo;
    }


    @Embeddable
    @Getter
    @Setter
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
    }
}
