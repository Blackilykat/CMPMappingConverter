package dev.blackilykat.parsing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CsvParser {
    public static List<List<String>> parse(Path file, String separator, boolean ignoreFirst) throws IOException {
        if(!Files.exists(file)) {
            throw new FileNotFoundException();
        }
        ArrayList<List<String>> value = new ArrayList<>();
        try(Stream<String> lines = Files.lines(file)) {
            lines.skip(ignoreFirst ? 1 : 0)
                    .forEach(line -> value.add(new ArrayList<>(List.of(line.split(separator)))));
        }
        return value;
    }
}
