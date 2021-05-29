package de.adschmidt.xkcdnotifier;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MailTemplate;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.CheckedTemplate;
import io.smallrye.mutiny.Uni;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Slf4j
@ApplicationScoped
public class MailService {

    @CheckedTemplate
    static class Templates {
        public static native MailTemplate.MailTemplateInstance comicMail(Comic comic);
    }

    //@Inject
    //ReactiveMailer mailer;


    public Uni<Void> sendMail(String mailAddress, Comic comic) {

        log.info("sending mail to {}", mailAddress);
        return Templates.comicMail(comic)
                .to(mailAddress)
                .subject("[xkcd] "+comic.getTitle())
                .send()
                    .onFailure()
                        .invoke(error -> log.error("could not sent mail.",error))
                    .onCancellation()
                        .invoke(() -> log.error("sending the mail was cancelled."));
    }

}
