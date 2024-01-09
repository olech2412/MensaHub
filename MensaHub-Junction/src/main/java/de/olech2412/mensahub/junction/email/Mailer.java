package de.olech2412.mensahub.junction.email;

import de.olech2412.mensahub.junction.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Component
public class Mailer {

    private final String emailAddress = Config.getInstance().getProperty("mensaHub.junction.mail.senderMail");
    private final String webAddress = Config.getInstance().getProperty("mensaHub.junction.address");
    @Value("${adminMail}")
    private String adminMail;


    public Mailer() throws IOException {
    }

    /**
     * Sends an email to the given address with the given subject and content.
     * For Activation
     *
     * @param firstName
     * @param emailTarget
     * @param activationCode
     * @param deactivationCode
     * @throws MessagingException
     */
    public void sendActivationEmail(String firstName, String emailTarget, String activationCode, String deactivationCode) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget));
        message.setSubject("Aktivierung deines MensaHub-Newsletter-Accounts");

        String msg = getActivationText(firstName, activationCode, deactivationCode);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    public void sendAPIActivationEmail(String username, String emailTarget, String activationCode, String deactivationCode) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget));
        message.setSubject("Aktivierung deines MensaHub-Gateway-Accounts");

        String msg = getAPIActivationText(username, activationCode, deactivationCode);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    public void sendAPIAdminRequest(String activationCode) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(adminMail));
        message.setSubject("Admin Request für MensaHub-Gateway");

        String msg = getAPIAdminRequestText(activationCode);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    public void sendAPIAdminRequestSuccess(String username, String emailTarget, String deactivationCode) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget));
        message.setSubject("Admin Request für MensaHub-Gateway");

        String msg = getAPIAdminRequestSuccessText(username, deactivationCode);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    public void sendAPIAdminRequestDecline(String username, String emailTarget) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget));
        message.setSubject("Admin Request für MensaHub-Gateway");

        String msg = getAPIAdminRequestDeclineText(username);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    /**
     * Sends an email to the given address with the given subject and content.
     * For Deactivation
     *
     * @param firstName
     * @param emailTarget
     * @throws MessagingException
     */
    public void sendDeactivationEmail(String firstName, String emailTarget) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget));
        message.setSubject("Deaktivierung deines MensaHub-Newsletter-Accounts");

        String msg = getDeactivationText(firstName);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    /**
     * Sends an email to the given address with the given subject and content.
     * For Deactivation
     *
     * @param firstName
     * @param emailTarget
     * @throws MessagingException
     */
    public void sendTemporaryDeactivationEmail(String firstName, String emailTarget, String deactivationCode, LocalDate deactivateUntil) throws MessagingException, IOException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", Boolean.getBoolean(Config.getInstance().getProperty("mensaHub.junction.mail.smtpAuth")));
        prop.put("mail.smtp.host", Config.getInstance().getProperty("mensaHub.junction.mail.smtpHost"));
        prop.put("mail.smtp.port", Config.getInstance().getProperty("mensaHub.junction.mail.smtpPort"));

        Message message = new MimeMessage(Session.getInstance(prop));
        message.setFrom(new InternetAddress(emailAddress));
        message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(emailTarget));
        message.setSubject("Temporäre Deaktivierung deines MensaHub-Newsletter-Accounts");

        String msg = getTemporaryDeactivationText(firstName, deactivationCode, deactivateUntil);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    private String getAPIAdminRequestSuccessText(String username, String deactivationCode) {
        String deactivateUrl = webAddress + "/deactivate?code=" + deactivationCode;
        String msg = StaticEmailText.API_USER_ADMIN_REQUEST_ACCEPT_TEXT;
        msg = msg.replaceFirst("%s", username);
        msg = msg.replaceFirst("%s", deactivateUrl);

        return msg;
    }

    private String getAPIAdminRequestDeclineText(String username) {
        String msg = StaticEmailText.API_USER_ADMIN_REQUEST_DECLINE_TEXT;
        msg = msg.replaceFirst("%s", username);

        return msg;
    }

    private String getAPIActivationText(String username, String activationCode, String deactivationCode) {
        String activateUrl = webAddress + "/activate?code=" + activationCode;
        String deactivateUrl = webAddress + "/deactivate?code=" + deactivationCode;
        String msg = StaticEmailText.API_USER_ACTIVATION_TEXT;
        msg = msg.replaceFirst("%s", username);
        msg = msg.replaceFirst("%s", activateUrl);
        msg = msg.replaceFirst("%s", deactivateUrl);

        return msg;
    }

    private String getAPIAdminRequestText(String activationCode) {
        String activateUrl = webAddress + "/activate?code=" + activationCode;
        String msg = StaticEmailText.API_USER_ADMIN_REQUEST_TEXT;
        msg = msg.replaceFirst("%s", "Admin");
        msg = msg.replaceFirst("%s", activateUrl);

        return msg;
    }

    private String getActivationText(String firstName, String activationCode, String deactivationCode) {
        String activateUrl = webAddress + "/activate?code=" + activationCode;
        String deactivateUrl = webAddress + "/deactivate?code=" + deactivationCode;
        String msg = StaticEmailText.ACTIVATION_TEXT;
        msg = msg.replaceFirst("%s", firstName);
        msg = msg.replaceFirst("%s", activateUrl);
        msg = msg.replaceFirst("%s", deactivateUrl);

        return msg;
    }

    private String getDeactivationText(String firstName) {
        String msg = StaticEmailText.DEACTIVATION_TEXT;
        msg = msg.replaceFirst("%s", firstName);
        msg = msg.replaceFirst("%s", webAddress);

        return msg;
    }

    private String getTemporaryDeactivationText(String firstName, String deactivationCode, LocalDate deactivateUntil) {
        String msg = StaticEmailText.TEMPORARY_DEACTIVATION_TEXT;
        msg = msg.replaceFirst("%s", firstName);
        msg = msg.replaceFirst("%s", deactivateUntil.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        msg = msg.replaceFirst("%s", deactivateUntil.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        String deactivateUrl = webAddress + "/deactivate?code=" + deactivationCode;
        msg = msg.replaceFirst("%s", deactivateUrl);

        return msg;
    }
}
