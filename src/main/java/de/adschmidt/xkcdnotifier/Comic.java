package de.adschmidt.xkcdnotifier;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Comic {
    private final String title;
    private final String link;
    private final LocalDate updated;
    private final String altText;
    private final String imgLink;
}
