package dev.blackilykat;

import dev.blackilykat.parsing.CsvParser;
import dev.blackilykat.structure.MappedClass;
import dev.blackilykat.structure.MappedField;
import dev.blackilykat.structure.MappedMethod;
import dev.blackilykat.structure.MappedTyped;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        File clientSrg = new File("client.srg");
        File serverSrg = new File("server.srg");
        File joinedExc = new File("joined.exc");
        File fieldsCsv = new File("fields.csv");
        File methodsCsv = new File("methods.csv");
        File output = new File("mappings.tiny");

        ArrayList<ArrayList<String>> parsedClientSrg = CsvParser.parse(clientSrg, " ", false);
        ArrayList<ArrayList<String>> parsedServerSrg = CsvParser.parse(serverSrg, " ", false);

        ArrayList<MappedClass> mappedClasses = MappedClass.extract(parsedClientSrg);
        ArrayList<MappedClass> mappedServerClasses = MappedClass.extract(parsedServerSrg);

        for (MappedClass mappedServerClass : mappedServerClasses) {
            boolean alreadyThere = false;
            for (MappedClass mappedClass : mappedClasses) {
                if(mappedServerClass.seargeName.equals(mappedClass.seargeName)) {
                    alreadyThere = true;
                    break;
                }
            }
            if(!alreadyThere) {
                mappedClasses.add(mappedServerClass);
            }
        }

        ArrayList<ArrayList<String>> parsedFieldsCsv = CsvParser.parse(fieldsCsv, ",", true);
        ArrayList<ArrayList<String>> parsedMethodsCsv = CsvParser.parse(methodsCsv, ",", true);

        for (ArrayList<String> parsedField : parsedFieldsCsv) {
            classLoop:
            for (MappedClass mappedClass : mappedClasses) {
                for (MappedTyped mappedTyped : mappedClass.contained) {
                    if(!(mappedTyped instanceof MappedField mappedField)) continue;
                    if(!mappedField.seargeName.endsWith(parsedField.get(0))) continue;
                    mappedField.cmpName = parsedField.get(1);
                    break classLoop;
                }
            }
        }
        for (ArrayList<String> parsedMethod : parsedMethodsCsv) {
            classLoop:
            for (MappedClass mappedClass : mappedClasses) {
                for (MappedTyped mappedTyped : mappedClass.contained) {
                    if(!(mappedTyped instanceof MappedMethod mappedMethod)) continue;
                    if(!mappedMethod.seargeName.endsWith(parsedMethod.get(0))) continue;
                    mappedMethod.cmpName = parsedMethod.get(1);
                    break classLoop;
                }
            }
        }

        StringBuilder outputBuilder = new StringBuilder("tiny\t2\t0\tofficial\tnamed\n");

        for (MappedClass mappedClass : mappedClasses) {
            outputBuilder.append(mappedClass.toTinyMappings());
        }

        if(!output.exists()) output.createNewFile();
        try(FileWriter writer = new FileWriter(output)) {
            writer.write(outputBuilder.toString());
        }
    }
}