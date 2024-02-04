package dev.blackilykat.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CsvParser {
    public static ArrayList<ArrayList<String>> parse(File file, String separator, boolean ignoreFirst) throws FileNotFoundException {
        if(!file.exists()) {
            throw new FileNotFoundException();
        }
        ArrayList<ArrayList<String>> value = new ArrayList<>();
        Scanner scanner = new Scanner(new FileInputStream(file));
        if(ignoreFirst) scanner.nextLine();
        while(scanner.hasNext()) {
            value.add(new ArrayList<>(Arrays.stream(scanner.nextLine().split(separator)).toList()));
        }
        return value;
    }
}
