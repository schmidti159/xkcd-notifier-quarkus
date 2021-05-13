package de.adschmidt.xkcdnotifier;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@QuarkusTest
class FeedReaderTest {

    @Inject
    FeedReader feedReader;

    @Test
    public void readTodaysComicFromFeed() throws Exception {
        // arrange
        LocalDate today = LocalDate.now();
        File tmpFile = writeSampleFeedWithDatesToTmpFile(List.of(today.minusDays(2), today.minusDays(1), today, today.plusDays(1)));
        // set the file as input for the feedReader
        System.setProperty("feed.url", tmpFile.toURI().toURL().toExternalForm());

        // act
        Comic comic = feedReader.readTodaysComic();

        // assert
        assertThat(comic).isNotNull();
        assertThat(comic.getTitle()).isEqualTo("Titel 2");
        assertThat(comic.getAltText()).isEqualTo("alt text 2");
        assertThat(comic.getLink()).isEqualTo("https://example.com/2/");
        assertThat(comic.getImgLink()).isEqualTo("https://imgs.example.com/2.png");
        assertThat(comic.getUpdated()).isEqualTo(today);
    }

    @Test
    public void readNothingFromFeedIfThereIsNoNewComic() throws Exception {
        // arrange
        LocalDate today = LocalDate.now();
        File tmpFile = writeSampleFeedWithDatesToTmpFile(List.of(today.minusDays(1), today.plusDays(1)));
        // set the file as input for the feedReader
        System.setProperty("feed.url", tmpFile.toURI().toURL().toExternalForm());

        // act
        Comic comic = feedReader.readTodaysComic();

        // assert
        assertThat(comic).isNull();
    }
    private File writeSampleFeedWithDatesToTmpFile(List<LocalDate> dates) throws IOException {
        // generate a tmp file and set it as feed input
        File tmpFile = File.createTempFile("xkcdnotifier-test",".xml");
        tmpFile.deleteOnExit();
        String feedContent = generateSampleFeedWithDates(dates);
        try(FileWriter writer = new FileWriter(tmpFile)) {
            writer.write(feedContent);
        }
        return tmpFile;
    }

    private String generateSampleFeedWithDates(List<LocalDate> dates) {
        String feed = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
                "<feed xmlns=\"http://www.w3.org/2005/Atom\" xml:lang=\"en\">"+
                "<title>Feed-Title</title>"+
                "<link href=\"https://example.com/\" rel=\"alternate\"/>"+
                "<id>https://example.com/</id>"+
                "<updated>2021-05-12T00:00:00Z</updated>";
        for(int i=0; i<dates.size(); i++) {
            feed += generateSampleFeedEntryForDate(dates.get(i), i);
        }
        feed += "</feed>";
        return feed;
    }

    private String generateSampleFeedEntryForDate(LocalDate date, int index) {
        return "<entry>" +
                "<title>Titel "+index+"</title>" +
                "<link href=\"https://example.com/"+index+"/\" rel=\"alternate\"/>" +
                "<updated>"+DateTimeFormatter.ISO_DATE_TIME.format(date.atStartOfDay().atZone(ZoneOffset.UTC))+"</updated>" +
                "<id>https://example.com/"+index+"</id>" +
                "<summary type=\"html\"><img src=\"https://imgs.example.com/"+index+".png\" title=\"img title "+index+"\" alt=\"alt text "+index+"\"/></summary>" +
                "</entry>";
    }
}