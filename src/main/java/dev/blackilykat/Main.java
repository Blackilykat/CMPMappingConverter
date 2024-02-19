package dev.blackilykat;

import dev.blackilykat.parsing.CsvParser;
import dev.blackilykat.parsing.ExcParser;
import dev.blackilykat.parsing.ExcRow;
import dev.blackilykat.structure.MappedClass;
import dev.blackilykat.structure.MappedField;
import dev.blackilykat.structure.MappedMethod;
import dev.blackilykat.structure.MappedTyped;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
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
                    containedLoop:
                    for (MappedTyped mappedTyped : mappedServerClass.contained) {
                        for (MappedTyped typed : mappedClass.contained) {
//                            System.out.printf("a: %s | b: %s\n", typed.notchName, mappedTyped.notchName);
                            if(typed.notchName.equals(mappedTyped.notchName)) continue containedLoop;
                        }
                        System.out.printf("Found server-only: %s", mappedTyped.cmpName);
                        mappedClass.contained.add(mappedTyped);
                    }
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
                }
            }
        }

        List<ExcRow> excRows = ExcParser.parse(joinedExc);
        Iterator<ExcRow> iterator = excRows.iterator();
        while(iterator.hasNext()) {
            ExcRow excRow = iterator.next();
            if(excRow.exceptions.toString().isEmpty()) {
                iterator.remove();
                continue;
            }
//            System.out.printf("clazz:  %s | method: %s | type: %s | exceptions: %s\n", excRow.clazz, excRow.method, excRow.type, excRow.exceptions);
            classLoop:
            for (MappedClass mappedClass : mappedClasses) {
                for (MappedTyped mappedTyped : mappedClass.contained) {
                    if(!(mappedTyped instanceof MappedMethod)) continue;
                    MappedMethod mappedMethod = (MappedMethod) mappedTyped;
//                    System.out.printf("exc: %s | mapped: %s\n", excRow.method.toString(), mappedMethod.seargeName);
                    if(!excRow.method.toString().equals(mappedMethod.getPathlessSeargeName())) continue;
                    if(!mappedMethod.cmpName.isEmpty()) {
                        excRow.method = new StringBuilder(mappedMethod.cmpName);
                    }
                    System.out.printf(
                            "%s.%s%s=%s\n",
                            excRow.clazz.toString(),
                            excRow.method,
                            excRow.type.toString(),
                            excRow.exceptions.toString()
                    );
                    break classLoop;
                }
            }
        }

        StringBuilder outputBuilder = new StringBuilder("tiny\t2\t0\tofficial\tnamed\n");

        for (MappedClass mappedClass : mappedClasses) {
            outputBuilder.append(mappedClass.toTinyMappings());
            for (MappedTyped mappedTyped : mappedClass.contained) {
                if(!(mappedTyped instanceof MappedMethod)) continue;
                MappedMethod method = (MappedMethod) mappedTyped;
                if(method.exceptions.isEmpty()) continue;
                System.out.println(
                        ("".equals(method.cmpName) ? method.seargeName : method.cmpName) +
                                method.type +
                                "=" +
                                method.exceptions
                );
            }
        }

        if(!Files.exists(output)) Files.createFile(output);
        Files.write(output, outputBuilder.toString().getBytes());
    }
}