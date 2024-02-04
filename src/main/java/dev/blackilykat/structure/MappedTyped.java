package dev.blackilykat.structure;

public abstract class MappedTyped extends Mapped {
    public String type = "";
    public abstract String getIdentifier();
    public String toTinyMappings() {
        String[] notchNameParts = notchName.split("/");
        String[] seargeNameParts = seargeName.split("/");
        return "\t" +
                getIdentifier() +
                "\t" +
                ("".equals(type) ? "?" : type) +
                "\t" +
                notchNameParts[notchNameParts.length-1] +
                "\t" +
                ("".equals(cmpName) ? seargeNameParts[seargeNameParts.length-1] : cmpName) +
                "\n";
    }
}
