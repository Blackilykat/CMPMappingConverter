package dev.blackilykat;

import dev.blackilykat.parsing.CsvParser;
import dev.blackilykat.structure.MappedClass;
import dev.blackilykat.structure.MappedField;
import dev.blackilykat.structure.MappedMethod;
import dev.blackilykat.structure.MappedTyped;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Path clientSrg = Paths.get("client.srg");
        Path serverSrg = Paths.get("server.srg");
        File joinedExc = new File("joined.exc");
        Path fieldsCsv = Paths.get("fields.csv");
        Path methodsCsv = Paths.get("methods.csv");
        Path output = Paths.get("mappings.tiny");

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
                    if(!(mappedTyped instanceof MappedField)) continue;
                    MappedField mappedField = (MappedField) mappedTyped;
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
                    if(!(mappedTyped instanceof MappedMethod)) continue;
                    MappedMethod mappedMethod = (MappedMethod) mappedTyped;
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

        if(!Files.exists(output)) Files.createFile(output);
        Files.write(output, outputBuilder.toString().getBytes());
    }
}