package dev.blackilykat.structure;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MappedClass extends Mapped {
    public ArrayList<MappedTyped> contained = new ArrayList<>();

    public static @NotNull ArrayList<MappedClass> extract(ArrayList<ArrayList<String>> parsedSrg) {
        ArrayList<MappedClass> mappedClasses = new ArrayList<>();
        for (ArrayList<String> clientRow : parsedSrg) {
            MappedClass lastMappedClass = new MappedClass();
            switch(clientRow.get(0)) {
                case "PK:" -> {
                    // nothing, it's not present in the RetroCMP mappings
                }
                case "CL:" -> {
                    MappedClass mappedClass = new MappedClass();
                    mappedClass.notchName = clientRow.get(1);
                    mappedClass.seargeName = clientRow.get(2);
                    lastMappedClass = mappedClass;
                    mappedClasses.add(mappedClass);
                }
                case "FD:" -> {
                    MappedField mappedField = new MappedField();
                    mappedField.notchName = clientRow.get(1);
                    mappedField.seargeName = clientRow.get(2);
                    String notchClassName = mappedField.notchName.split("/")[0];
                    for(MappedClass mappedClass : mappedClasses) {
                        if(mappedClass.notchName.equals(notchClassName)) {
                            mappedClass.contained.add(mappedField);
                            break;
                        }
                    }
                }
                case "MD:" -> {
                    MappedMethod mappedMethod = new MappedMethod();
                    mappedMethod.notchName = clientRow.get(1);
                    mappedMethod.seargeName = clientRow.get(3);
                    mappedMethod.type = clientRow.get(2); // RetroMCP mappings seem to use notch type declarations
                    String notchClassName = mappedMethod.notchName.split("/")[0];
                    for(MappedClass mappedClass : mappedClasses) {
                        if(mappedClass.notchName.equals(notchClassName)) {
                            mappedClass.contained.add(mappedMethod);
                            break;
                        }
                    }
                }
            }
        }
        return mappedClasses;
    }

    public String toTinyMappings() {
        StringBuilder value = new StringBuilder();

        value
                .append("c\t")
                .append(notchName)
                .append("\t")
                .append("".equals(cmpName) ? seargeName : cmpName)
                .append("\n");

        for (MappedTyped mappedTyped : contained) {
            value.append(mappedTyped.toTinyMappings());
        }

        return value.toString();
    }
}
