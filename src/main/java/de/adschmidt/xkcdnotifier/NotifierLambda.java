package de.adschmidt.xkcdnotifier;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@ApplicationScoped
public class NotifierLambda implements RequestHandler<MailAddresses, String> {

    private final FeedReader feedReader;
    private final MailService mailService;

    NotifierLambda(FeedReader feedReader, MailService mailService) {
        this.feedReader = feedReader;
        this.mailService = mailService;
    }

    @SneakyThrows
    @Override
    public String handleRequest(MailAddresses mailAddresses, Context context) {
        Instant start =Instant.now();
        Comic comic = feedReader.readTodaysComic();
        if(comic != null) {
            Multi.createFrom().iterable(mailAddresses.getMailAddresses())
                    .map(address -> mailService.sendMail(address, comic))
                    .subscribe().asStream()
                    .forEach(uno -> uno.await().indefinitely()); // we have to block. Otherwise the process might end to fast
        }
        log.info("execution of function took {} ms", Duration.between(start, Instant.now()));
        return "success";
    }
}
