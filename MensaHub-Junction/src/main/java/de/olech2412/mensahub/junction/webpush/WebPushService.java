package de.olech2412.mensahub.junction.webpush;

import de.olech2412.mensahub.junction.helper.SubscriptionConverter;
import de.olech2412.mensahub.junction.jpa.repository.SubscriptionEntityRepository;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SubscriptionEntityRepository subscriptionEntityRepository;

    WebPush webPush;

    /**
     * Initialize security and push service for initial get request.
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
        subscriptionEntityRepository.findAll().forEach(subscription -> {
            webPush.sendNotification(SubscriptionConverter.convertToModel(subscription), new WebPushMessage(title, body));
        });
    }


    public boolean isEmpty() {
        return subscriptionEntityRepository.findAll().isEmpty();
    }

}