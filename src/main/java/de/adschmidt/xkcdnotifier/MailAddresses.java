package de.adschmidt.xkcdnotifier;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@RegisterForReflection
public class MailAddresses {
    private final List<String> mailAddresses = new ArrayList<>();
}
