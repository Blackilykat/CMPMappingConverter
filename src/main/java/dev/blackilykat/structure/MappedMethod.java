package dev.blackilykat.structure;

public class MappedMethod extends MappedTyped {
    public String exceptions = "";

    @Override
    public String getIdentifier() {
        return "m";
    }

    public String getPathlessSeargeName() {
        String[] parts = seargeName.split("/");
        return parts[parts.length-1];
    }
}
