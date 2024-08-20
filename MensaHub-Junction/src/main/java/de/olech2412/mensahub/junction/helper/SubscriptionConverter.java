package de.olech2412.mensahub.junction.helper;

import de.olech2412.mensahub.models.authentification.SubscriptionEntity;
import nl.martijndwars.webpush.Subscription;

/**
 * Used to convert a nl.martijndwars.webpush.Subscription to de.olech2412.mensahub.models.authentification.SubscriptionEntity
 * and backwards
 */
public class SubscriptionConverter {

    /**
     * Convert the model object to the database entity
     * @param subscription as nl.martijndwars.webpush.Subscription
     * @return subscription as de.olech2412.mensahub.models.authentification.SubscriptionEntity
     */
    public static SubscriptionEntity convertToEntity(Subscription subscription, String deviceInformation) {
        return new SubscriptionEntity(
                subscription.endpoint(),
                new SubscriptionEntity.KeysEntity(subscription.keys().p256dh(), subscription.keys().auth()),
                deviceInformation
        );
    }

    /**
     * Convert the database entity to the model object
     * @param entity as de.olech2412.mensahub.models.authentification.SubscriptionEntity
     * @return subscription as nl.martijndwars.webpush.Subscription
     */
    public static Subscription convertToModel(SubscriptionEntity entity) {
        return new Subscription(
                entity.getEndpoint(),
                new Subscription.Keys(entity.getKeys().getP256dh(), entity.getKeys().getAuth())
        );
    }

}