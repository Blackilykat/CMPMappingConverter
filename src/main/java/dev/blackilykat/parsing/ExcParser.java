package dev.blackilykat.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExcParser {
    public List<ExcRow> parse(File file) throws FileNotFoundException {
        if(!file.exists()) {
            throw new FileNotFoundException();
        }
        ArrayList<ExcRow> value = new ArrayList<>();
        Scanner scanner = new Scanner(new FileInputStream(file));
        while(scanner.hasNext()) {
            ExcRow row = new ExcRow();
            char c;
            byte reading = 0; // 0=method, 1=type, 2=exceptions, 3=parameters
            do {
                c = (char) scanner.nextByte();
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
                if(reading == 0 && c == '(') {
                    reading = 1;
                }
                // read
                switch (reading) {
                    case 0 -> row.method.append(c);
                    case 1 -> row.type.append(c);
                    case 2 -> row.exceptions.append(c);
                    case 3 -> row.parameters.append(c);
                }
            } while(scanner.hasNext());
            value.add(row);
        }
        return value;
    }
}
