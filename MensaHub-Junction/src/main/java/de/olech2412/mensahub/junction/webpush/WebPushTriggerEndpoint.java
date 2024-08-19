package de.olech2412.mensahub.junction.webpush;

import com.vaadin.flow.server.webpush.WebPushMessage;
import de.olech2412.mensahub.junction.config.Config;
import de.olech2412.mensahub.junction.jpa.services.MailUserService;
import de.olech2412.mensahub.models.authentification.MailUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webpush")
@Slf4j
public class WebPushTriggerEndpoint {

    @Autowired
    private WebPushService webPushService;

    @Autowired
    private MailUserService mailUserService;

    @PostMapping("/send")
    public ResponseEntity<String> sendPushNotification(@RequestParam String message,
                                                       @RequestParam String title,
                                                       @RequestParam String mailAdress,
                                                       @RequestParam String apiKey) {
        try {
            MailUser mailUser;

            if(!Config.getInstance().getProperty("mensaHub.junction.push.notification.api.key").equals(apiKey)){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }


                if (mailUserService.findMailUserByEmail(mailAdress).isEmpty()) {
                    log.error("Cannot find mail adress: {}", mailAdress);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot find mailAdress");
            }

            mailUser = mailUserService.findMailUserByEmail(mailAdress).get(0);

            if (!mailUser.isPushNotificationsEnabled()) {
                log.error("Mail notifications are disabled for user {}", mailAdress);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mail notifications are disabled for this user");
            }

            if (mailUser.getSubscription() == null) {
                log.error("Subscription is null for user {}", mailAdress);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No subscription found for this user");
            }

            webPushService.webPush.sendNotification(mailUserService.convertToModel(mailUser.getSubscription()),
                    new WebPushMessage(title, message));
            log.info("WebPush notification sent to user {} with title {} and message {}", mailAdress, title, message);
            return ResponseEntity.ok("Push notification sent successfully");
        } catch (Exception e) {
            log.error("Error while sending web push notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send push notification");
        }
    }
}