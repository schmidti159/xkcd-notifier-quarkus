package de.adschmidt.xkcdnotifier;

import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import io.quarkus.amazon.lambda.test.LambdaClient;
import io.quarkus.test.junit.QuarkusTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class NotifierLambdaTest {

    @InjectMock
    FeedReader feedReader;
    @InjectMock
    MailService mailService;

    @Test
    public void sendMailForNewComic() throws Exception {
        // arrange
        MailAddresses addresses = new MailAddresses();
        addresses.getMailAddresses()
                .add("test@ad-schmidt.de");
        addresses.getMailAddresses()
                .add("foo@ad-schmidt.de");
        Comic comic = mock(Comic.class);
        when(feedReader.readTodaysComic()).thenReturn(comic);

        // act
        String result = LambdaClient.invoke(String.class, addresses);

        // assert
        assertThat(result).isEqualTo("success");
        verify(mailService).sendMail("test@ad-schmidt.de", comic);
        verify(mailService).sendMail("foo@ad-schmidt.de", comic);
    }

    @Test
    public void doNothingForNoNewComic() throws Exception {
        // arrange
        MailAddresses addresses = new MailAddresses();
        addresses.getMailAddresses()
                .add("test@ad-schmidt.de");
        addresses.getMailAddresses()
                .add("foo@ad-schmidt.de");
        Comic comic = mock(Comic.class);
        when(feedReader.readTodaysComic()).thenReturn(null);

        // act
        String result = LambdaClient.invoke(String.class, addresses);

        // assert
        assertThat(result).isEqualTo("success");
        verify(mailService, never()).sendMail(any(), any());
    }

}
