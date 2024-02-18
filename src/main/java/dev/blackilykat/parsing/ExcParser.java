package dev.blackilykat.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExcParser {
    static public List<ExcRow> parse(File file) throws IOException {
        if(!file.exists()) {
            throw new FileNotFoundException();
        }
        ArrayList<ExcRow> value = new ArrayList<>();
        Scanner scanner = new Scanner(Files.newInputStream(file.toPath()));
        scanner.useDelimiter("");
        while(scanner.hasNext()) {
            ExcRow row = new ExcRow();
            char c;
            byte reading = 4; // 0=method, 1=type, 2=exceptions, 3=parameters, 4=clazz (added 4 later on)
            do {
                c = scanner.next().charAt(0);
                if(c == '\n') {
                    break;
                }
                // change what's getting read
                if(c == '=') {
                    reading = 2;
                    continue;
                }
                if(c == '|') {
                    reading = 3;
                    continue;
                }
                if(c == '.') {
                    reading = 0;
                    continue;
                }
                if(reading == 0 && c == '(') {
                    reading = 1;
                }
                // read
                switch (reading) {
                    case 0:
                        row.method.append(c);
                        break;
                    case 1:
                        row.type.append(c);
                        break;
                    case 2:
                        row.exceptions.append(c);
                        break;
                    case 3:
                        row.parameters.append(c);
                        break;
                    case 4:
                        row.clazz.append(c);
                        break;
                }
            } while(scanner.hasNext());
            value.add(row);
        }
        return value;
    }
}
