package de.olech2412.mensahub.junction.webpush;

import com.vaadin.flow.server.webpush.WebPush;
import com.vaadin.flow.server.webpush.WebPushMessage;
import de.olech2412.mensahub.junction.helper.SubscriptionConverter;
import de.olech2412.mensahub.junction.jpa.repository.SubscriptionEntityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WebPushService {

    CustomWebPush webPush;
    @Value("${push.public.key}") // env variables loaded in Spring init class from config
    private String publicKey;
    @Value("${push.private.key}")
    private String privateKey;
    @Value("${push.subject}")
    private String subject;

    @Autowired
    private SubscriptionEntityRepository subscriptionEntityRepository;

    /**
     * Initialize security and push service for initial get request.
     */
    public CustomWebPush getWebPush() {
        if (webPush == null) {
            webPush = new CustomWebPush(publicKey, privateKey, subject);
        }
        return webPush;
    }


    public boolean isEmpty() {
        return subscriptionEntityRepository.findAll().isEmpty();
    }

}