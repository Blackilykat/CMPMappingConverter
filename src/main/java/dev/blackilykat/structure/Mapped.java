package dev.blackilykat.structure;

public abstract class Mapped {
    public String notchName = "";
    public String seargeName = "";
    public String cmpName = "";

    public String getUsableName() {
        if(!cmpName.isEmpty()) return cmpName;
        if(!seargeName.isEmpty()) return seargeName;
        return notchName;
    }
}
