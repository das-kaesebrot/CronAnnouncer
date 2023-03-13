package eu.kaesebrot.dev.classes;

import java.util.Arrays;
import java.util.stream.Stream;

public enum MessageType {
    BROADCAST,
    TITLE,
    ;

    public static Stream<String> getValuesAsLowercase() {
        return Arrays.stream(MessageType.values())
                .map(Enum::name)
                .map(String::toLowerCase)
                .sorted();
    }
}
