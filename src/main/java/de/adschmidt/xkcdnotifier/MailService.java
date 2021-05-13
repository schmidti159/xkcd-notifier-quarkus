package de.adschmidt.xkcdnotifier;

import lombok.extern.java.Log;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Log
@ApplicationScoped
public class MailService {

    @ConfigProperty(name="mail.user")
    String smtpUser;
    @ConfigProperty(name="mail.password")
    String smtpPassword;
    @ConfigProperty(name="mail.host")
    String smtpHost;
    @ConfigProperty(name="mail.port")
    String smtpPort;

    public void sendMail(String mailAddress, Comic comic) throws UnsupportedEncodingException, MessagingException {


        Session mailSession = Session.getDefaultInstance(buildProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });
        Message msg = new MimeMessage(mailSession);
        msg.setFrom(new InternetAddress("noreply@ad-schmidt.de", "noreply"));
        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(mailAddress));

        MimeMultipart content = new MimeMultipart();

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(buildHtmlMessage(comic), "text/html");
        content.addBodyPart(htmlPart);

        msg.setContent(content);
        msg.setSubject("[xkcd] "+comic.getTitle());
        msg.saveChanges();
        Transport.send(msg);
    }

    private Properties buildProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol","smtp");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.tls", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.protocols","TLSv1.2");
        return props;
    }

    String buildHtmlMessage(Comic comic) {
        StringBuilder result = new StringBuilder();
        result.append("<h1>"+comic.getTitle()+"</h1>");
        result.append("<p><a href=\""+comic.getLink()+"\"><img src=\""+comic.getImgLink()+"\" alt=\""+comic.getAltText()+"\"/></a></p>");
        result.append("<p>"+comic.getAltText()+"</p>");
        return result.toString();
    }
}
