package de.adschmidt.xkcdnotifier;

import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Provider;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@ApplicationScoped
public class FeedReader {

    @ConfigProperty(name="feed.url")
    Provider<String> feedUrl;

    public Comic readTodaysComic() throws ParserConfigurationException, SAXException, IOException {
        log.info("reading comics from {}",feedUrl.get());
        SAXParser parser = buildSecureSaxParser();
        FeedHandler feedHandler = new FeedHandler();

        parser.parse(feedUrl.get(), feedHandler);
        List<Comic> comics = feedHandler.getComics();
        log.debug("all comics: {}", comics);
        LocalDate today = LocalDate.now();
        Comic todaysComic = comics.stream()
                .filter(comic -> today.equals(comic.getUpdated()))
                .findFirst().orElse(null);
        log.info("found comic for today: {}",todaysComic);
        return  todaysComic;
    }

    private SAXParser buildSecureSaxParser() throws ParserConfigurationException, SAXException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        return parserFactory.newSAXParser();
    }

    private static class FeedHandler extends DefaultHandler {
        @Getter
        private final List<Comic> comics = new ArrayList<>();

        private String title;
        private String link;
        private String imgLink;
        private LocalDate updated;
        private String altText;
        private StringBuilder currentString;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if("entry".equals(qName)) {
                // reset state
                title = null;
                link = null;
                updated = null;
                altText = null;
            } else if("title".equals(qName) || "updated".equals(qName)) {
                currentString = new StringBuilder();
            } else if("link".equals(qName)) {
                link = attributes.getValue("href");
            } else if("img".equals(qName)) {
                altText = attributes.getValue("alt");
                imgLink = attributes.getValue("src");
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            if(currentString != null) {
                currentString.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if("entry".equals(qName)) {
                comics.add(new Comic(title, link, updated, altText, imgLink));
            } else if("title".equals(qName)) {
                title = currentString.toString();
                currentString = null;
            } else if("updated".equals(qName)) {
                updated = ZonedDateTime.parse(currentString.toString()).toLocalDate(); // the feed uses ISO_LOCAL_DATE
                currentString = null;
            }
        }
    }
}
