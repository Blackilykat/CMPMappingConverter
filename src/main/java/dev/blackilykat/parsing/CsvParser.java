package dev.blackilykat.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CsvParser {
    public static List<List<String>> parse(File file, String separator, boolean ignoreFirst) throws FileNotFoundException {
        if(!file.exists()) {
            throw new FileNotFoundException();
        }
        ArrayList<List<String>> value = new ArrayList<>();
        Scanner scanner = new Scanner(new FileInputStream(file));
        if(ignoreFirst) scanner.nextLine();
        while(scanner.hasNext()) {
            value.add(new ArrayList<>(Arrays.stream(scanner.nextLine().split(separator)).toList()));
        }
        return value;
    }
}
