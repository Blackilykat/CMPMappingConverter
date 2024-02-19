package dev.blackilykat.structure;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MappedClass extends Mapped {
    public ArrayList<MappedTyped> contained = new ArrayList<>();

    public static @NotNull List<MappedClass> extract(List<List<String>> parsedSrg) {
        ArrayList<MappedClass> mappedClasses = new ArrayList<>();
        for (List<String> clientRow : parsedSrg) {
            switch (clientRow.get(0)) {
                case "PK:": // nothing, it's not present in the RetroCMP mappings
                    break;
                case "CL:":
                    MappedClass clMappedClass = new MappedClass();
                    clMappedClass.notchName = clientRow.get(1);
                    clMappedClass.seargeName = clientRow.get(2);
                    mappedClasses.add(clMappedClass);
                    break;
                case "FD:":
                    MappedField mappedField = new MappedField();
                    mappedField.notchName = clientRow.get(1);
                    mappedField.seargeName = clientRow.get(2);
                    String[] fdNotchClassNameParts = mappedField.notchName.split("/");
                    String fdNotchClassName = String.join("/", Arrays.copyOfRange(fdNotchClassNameParts, 0, fdNotchClassNameParts.length-1));
                    for (MappedClass mappedClass : mappedClasses) {
                        if (mappedClass.notchName.equals(fdNotchClassName)) {
                            mappedClass.contained.add(mappedField);
                            break;
                        }
                    }
                    break;
                case "MD:":
                    MappedMethod mappedMethod = new MappedMethod();
                    mappedMethod.notchName = clientRow.get(1);
                    mappedMethod.seargeName = clientRow.get(3);
                    mappedMethod.type = clientRow.get(2); // RetroMCP mappings seem to use notch type declarations
                    String[] mdNotchClassNameParts = mappedMethod.notchName.split("/");
                    String mdNotchClassName = String.join("/", Arrays.copyOfRange(mdNotchClassNameParts, 0, mdNotchClassNameParts.length-1));
                    for (MappedClass mappedClass : mappedClasses) {
                        if (mappedClass.notchName.equals(mdNotchClassName)) {
                            mappedClass.contained.add(mappedMethod);
                            break;
                        }
                    }
                    break;
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
