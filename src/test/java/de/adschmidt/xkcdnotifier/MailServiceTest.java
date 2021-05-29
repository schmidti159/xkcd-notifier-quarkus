package de.adschmidt.xkcdnotifier;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class MailServiceTest {

    @Inject
    MailService mailService;

    @Test
    @Disabled("Disabled for automatic tests, can be used to manually test the mail sending")
    public void sendTestMail() throws Exception {
        // arrange
        Comic comic = new Comic("New Bug", "https://xkcd.com/1700/", LocalDate.now(),
                "There's also a unicode-handling bug in the URL request library, and we're storing the passwords unsalted ... so if we salt them with emoji, we can close three issues at once!",
                "https://imgs.xkcd.com/comics/new_bug.png");

        // act
        mailService.sendMail("daniel-xkcd-test@ad-schmidt.de", comic)
            .await().indefinitely();

        // assert: check mail
    }

}