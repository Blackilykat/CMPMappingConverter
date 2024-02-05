package dev.blackilykat.structure;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class MappedField extends MappedTyped {
    @Override
    public String getIdentifier() {
        return "f";
    }

    @Override
    public String toTinyMappings() {
        String[] notchNameParts = notchName.split("/");
        try(URLClassLoader classLoader = new URLClassLoader(new URL[]{
                new File("game.jar").toURI().toURL(),
                new File("server.jar").toURI().toURL()
        })) {

            Class<?> fieldsClass = classLoader.loadClass(String.join(".", Arrays.copyOfRange(notchNameParts, 0, notchNameParts.length-1)));
            for (Field field : fieldsClass.getDeclaredFields()) {
                String type = field.getType().getName();
                if(field.getName().equals(notchNameParts[notchNameParts.length-1])) {
                    /*
                        boolean Z
                        byte B
                        char C
                        class or interface Lclassname;
                        double D
                        float F
                        int I
                        long J
                        short S
                     */
                    if(!type.startsWith("[")) {
                        switch (type) {
                            case "boolean":
                                type = "Z";
                                break;
                            case "byte":
                                type = "B";
                                break;
                            case "char":
                                type = "C";
                                break;
                            case "double":
                                type = "D";
                                break;
                            case "float":
                                type = "F";
                                break;
                            case "int":
                                type = "I";
                                break;
                            case "long":
                                type = "J";
                                break;
                            case "short":
                                type = "S";
                                break;
                            default:
                                type = "L" + type + ";";
                                break;
                        }
                    }
                    this.type = type;
                    break;
                }
            }
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            System.out.println("!! Couldn't find class " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return super.toTinyMappings();
    }
}
