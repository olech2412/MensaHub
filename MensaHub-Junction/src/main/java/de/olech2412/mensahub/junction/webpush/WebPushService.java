package de.olech2412.mensahub.junction.webpush;

import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.server.webpush.WebPush;
import com.vaadin.flow.server.webpush.WebPushMessage;

@Service
@Slf4j
public class WebPushService {

    @Value("${push.public.key}") // env variables loaded in Spring init class from config
    private String publicKey;
    @Value("${push.private.key}")
    private String privateKey;
    @Value("${push.subject}")
    private String subject;

    private final Map<String, Subscription> endpointToSubscription = new HashMap<>();

    WebPush webPush;

    /**
     * Initialize security and push service for initial get request.
     *
     * @throws GeneralSecurityException security exception for security complications
     */
    public WebPush getWebPush() {
        if(webPush == null) {
            webPush = new WebPush(publicKey, privateKey, subject);
        }
        return webPush;
    }

    /**
     * Send a notification to all subscriptions.
     *
     * @param title message title
     * @param body message body
     */
    public void notifyAll(String title, String body) {
        endpointToSubscription.values().forEach(subscription -> {
            webPush.sendNotification(subscription, new WebPushMessage(title, body));
        });
    }

    public void store(Subscription subscription) {
        log.info("Subscribed to {}", subscription.endpoint());
        /*
         * Note, in a real world app you'll want to persist these
         * in the backend. Also, you probably want to know which
         * subscription belongs to which user to send custom messages
         * for different users. In this demo, we'll just use
         * endpoint URL as key to store subscriptions in memory.
         */
        endpointToSubscription.put(subscription.endpoint(), subscription);
    }


    public void remove(Subscription subscription) {
        log.info("Unsubscribed from {}", subscription.endpoint());
        endpointToSubscription.remove(subscription.endpoint());
    }

    public boolean isEmpty() {
        return endpointToSubscription.isEmpty();
    }

}