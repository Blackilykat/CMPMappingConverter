package dev.blackilykat;

import dev.blackilykat.parsing.CsvParser;
import dev.blackilykat.structure.MappedClass;
import dev.blackilykat.structure.MappedField;
import dev.blackilykat.structure.MappedMethod;
import dev.blackilykat.structure.MappedTyped;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        File clientSrg = new File("client.srg");
        File serverSrg = new File("server.srg");
        File joinedExc = new File("joined.exc");
        File fieldsCsv = new File("fields.csv");
        File methodsCsv = new File("methods.csv");
        File output = new File("mappings.tiny");

        List<List<String>> parsedClientSrg = CsvParser.parse(clientSrg, " ", false);
        List<List<String>> parsedServerSrg = CsvParser.parse(serverSrg, " ", false);

        List<MappedClass> mappedClasses = MappedClass.extract(parsedClientSrg);
        List<MappedClass> mappedServerClasses = MappedClass.extract(parsedServerSrg);

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

        List<List<String>> parsedFieldsCsv = CsvParser.parse(fieldsCsv, ",", true);
        List<List<String>> parsedMethodsCsv = CsvParser.parse(methodsCsv, ",", true);

        for (List<String> parsedField : parsedFieldsCsv) {
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
        for (List<String> parsedMethod : parsedMethodsCsv) {
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