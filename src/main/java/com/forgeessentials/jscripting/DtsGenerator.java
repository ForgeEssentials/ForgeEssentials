package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;

public class DtsGenerator
{
    private static final Pattern fieldPattern = Pattern.compile("public (?:final )?([\\w <>,?\\[\\]]+) (\\w+)( =.+)?;");
    private static final Pattern methodPattern = Pattern.compile("public ([\\w <>,?\\[\\]]+) (\\w+)\\(([\\w <>,.?\\[\\]]*)\\)");
    // private static final Pattern constructorPattern = Pattern.compile("public (\\w+)\\(([\\w <>,?\\[\\]]*)\\)");
    private static final Pattern classDefPattern = Pattern.compile("public class (\\w+)(?:<[\\w ]+>)?(?: extends ([\\w ,<>]+)[\\s\\}])?");
    private static final Pattern customClassDefPattern = Pattern.compile("classdef (.*)");
    private static final Pattern customMethodDefPattern = Pattern.compile("methoddef (.*)");

    private static int indention = 0;
    private static PrintStream writer;

    private static HashSet<String> knownTypes = new HashSet<>();
    private static HashSet<String> declaredTypes = new HashSet<>();

    private static void writeLn(String text)
    {
        writer.print("\r\n");
        for (int i = 0; i < indention; i++)
            writer.print("\t");
        writer.print(text);
    }

    private static void write(String text)
    {
        writer.print(text);
    }

    public static void main(String[] args)
    {
        declaredTypes.clear();
        knownTypes.clear();
        declaredTypes.add("int");
        declaredTypes.add("long");
        declaredTypes.add("float");
        declaredTypes.add("double");
        declaredTypes.add("string");
        declaredTypes.add("boolean");
        declaredTypes.add("any");
        declaredTypes.add("number");
        declaredTypes.add("void");
        declaredTypes.add("JavaObject");
        declaredTypes.add("JavaList");
        try
        {
            File dir = new File("src/main/java/com/forgeessentials/jscripting/wrapper");
            File outFile = new File("jscripting/mc.d.ts");
            try (PrintStream w = new PrintStream(new FileOutputStream(outFile)))
            {
                writer = w;
                indention = 0;
                writeLn("declare type int = number;");
                writeLn("declare type long = number;");
                writeLn("declare type float = number;");
                writeLn("declare type double = number;");
                writeLn("");
                writeLn("declare namespace MC {");
                indention++;
                writeLn("");
                writeLn("interface JavaObject {");
                indention++;
                writeLn("equals(obj: JavaObject): boolean;");
                writeLn("toString(): string;");
                writeLn("hashCode(): int;");
                indention--;
                writeLn("}");
                writeLn("");
                writeLn("interface JavaList<T> {");
                indention++;
                writeLn("size(): int;");
                writeLn("isEmpty(): boolean;");
                writeLn("toArray(): any[];");
                // writeLn("toArray(in: T[]): T[];");
                writeLn("get(index: int): T;");
                writeLn("add(element: T): T;");
                writeLn("set(index: int, element: T): T;");
                writeLn("clear(): void;");
                writeLn("remove(index: int): T;");
                writeLn("remove(element: T): boolean;");
                indention--;
                writeLn("}");
                writeLn("");
                writeLn("type CommandCallback = (args: CommandArgs) => void;");
                writeLn("");

                for (Iterator<File> it = FileUtils.iterateFiles(dir, new String[] { "java" }, true); it.hasNext();)
                {
                    File file = it.next();
                    if (!file.getName().startsWith("Js"))
                        continue;
                    processFile(file);
                }

                for (String type : knownTypes)
                {
                    if (declaredTypes.contains(type))
                        continue;
                    writeLn("interface ");
                    write(type);
                    write(" { }");
                    writeLn("");
                }

                indention--;
                writeLn("}");
                writeLn("");
                writeLn("declare var Server: MC.ServerStatic;");
                writeLn("declare var World: MC.WorldStatic;");
                writeLn("declare var Block: MC.BlockStatic;");
                writeLn("declare var Item: MC.ItemStatic;");
                writeLn("");
                writeLn("declare function getNbt(entity: MC.Entity | MC.ItemStack): any;");
                writeLn("declare function setNbt(entity: MC.Entity | MC.ItemStack, data: any);");
                writeLn("");
                writeLn("/**");
                writeLn(" * Constants that tell getNbt and setNbt the types of entries. Use nbt[NBT_INT + 'myVar'] for access");
                writeLn(" */ ");
                writeLn("declare const NBT_BYTE: string;");
                writeLn("declare const NBT_SHORT: string;");
                writeLn("declare const NBT_INT: string;");
                writeLn("declare const NBT_LONG: string;");
                writeLn("declare const NBT_FLOAT: string;");
                writeLn("declare const NBT_DOUBLE: string;");
                writeLn("declare const NBT_BYTE_ARRAY: string;");
                writeLn("declare const NBT_STRING: string;");
                writeLn("declare const NBT_COMPOUND: string;");
                writeLn("declare const NBT_INT_ARRAY: string;");
                writeLn("");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void processFile(File file) throws IOException
    {
        String fileName = file.getName();
        String typeName = fixType(fileName.substring(0, fileName.length() - 5));
        if (typeName == "JavaObject")
            return;
        System.out.println("Parsing " + typeName + "...");

        String text = FileUtils.readFileToString(file, Charsets.UTF_8);

        Matcher mCClassDef = customClassDefPattern.matcher(text);
        Matcher mClassDef = classDefPattern.matcher(text);
        if (mCClassDef.find())
        {
            writeLn(mCClassDef.group(1));
            write(" {");
        }
        else if (mClassDef.find())
        {
            // Write interface header
            writeLn("interface ");
            write(typeName);
            if (mClassDef.group(2) != null)
            {
                write(" extends ");
                write(fixType(mClassDef.group(2)));
            }
            write(" {");
        }
        else
        {
            throw new RuntimeException("Could not find class def");
        }

        indention++;

        for (String line : text.split("\n"))
        {
            if (line.contains("tsgen ignore"))
                continue;
            Matcher mField = fieldPattern.matcher(line);
            Matcher mMethod = methodPattern.matcher(line);
            Matcher nCMethod = customMethodDefPattern.matcher(line);
            if (mMethod.find())
            {
                String name = mMethod.group(2);
                if (skipMethod(name))
                    continue;
                writeLn(name);
                write("(");
                if (mMethod.group(3) != null && !mMethod.group(3).isEmpty())
                    parseArgs(mMethod.group(3));
                write("): ");
                write(fixType(mMethod.group(1)));
                write(";");
            }
            else if (mField.find())
            {
                String name = mField.group(2);
                if (skipField(name))
                    continue;
                writeLn(name);
                if (line.contains("$optional"))
                    write("?");
                write(": ");
                int typeIdx = line.indexOf("$type=");
                if (typeIdx >= 0)
                {
                    typeIdx += "$type=".length();
                    String type = line.substring(typeIdx, line.indexOf("$", typeIdx));
                    write(type);
                }
                else
                {
                    write(fixType(mField.group(1)));
                }
                write(";");
            }
            else if (nCMethod.find())
            {
                writeLn(nCMethod.group(1));
            }
        }

        indention--;
        writeLn("}");
        writeLn("");
        declaredTypes.add(typeName);
    }

    private static String fixType(String type)
    {
        if (type.startsWith("MappedList<"))
            return "JavaList<" + fixType(type.substring("MappedList<".length(), type.indexOf(','))) + ">";

        if (type.startsWith("Object"))
            type = "any" + type.substring("Object".length());
        else if (type.startsWith("Js"))
            type = type.substring(2);

        // Handle varargs
        boolean isVarArg = false;
        if (type.endsWith("..."))
        {
            isVarArg = true;
            type = type.substring(0, type.length() - 3);
        }

        // Handle arrays
        boolean isArray = false;
        if (type.endsWith("[]"))
        {
            isArray = true;
            type = type.substring(0, type.length() - 2);
        }

        // Handle generics
        if (type.indexOf('<') != -1)
            type = type.substring(0, type.indexOf('<'));

        // Remap certain types
        if (type.equals("CommandSender"))
            type = "ICommandSender";
        if (type.equals("Wrapper"))
            type = "JavaObject";
        if (type.equals("String"))
            type = "string";
        if (type.equals("Boolean"))
            type = "boolean";

        knownTypes.add(type);

        if (isArray)
            type = type + "[]";
        if (isVarArg)
            type = type + "...";
        return type;
    }

    private static void parseArgs(String argsLine)
    {
        String[] args = argsLine.split(",");
        for (int i = 0; i < args.length; i++)
        {
            String[] argSplit = args[i].trim().split(" ");
            String name = argSplit[1];
            String type = fixType(argSplit[0]);
            if (type.endsWith("..."))
            {
                type = type.substring(0, type.length() - 3) + "[]";
                name = "..." + name;
            }
            write(name);
            write(": ");
            write(type);
            if (i < args.length - 1)
                write(", ");
        }
    }

    private static boolean skipMethod(String name)
    {
        return name.equals("equals") || name.equals("getThat");
    }

    private static boolean skipField(String name)
    {
        return name.equals("that");
    }

}
