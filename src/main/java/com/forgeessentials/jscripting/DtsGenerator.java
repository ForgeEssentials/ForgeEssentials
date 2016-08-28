package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileOutputStream;
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
    private static final Pattern classDefPattern = Pattern.compile("public class (\\w+)(?:<[\\w ]+>)?(?: extends ([\\w ,<>]+)[\\n\\}])?");
    private static final Pattern customClassDefPattern = Pattern.compile("classdef (\\w+) (\\w+)?");
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
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static void processFile(File file) throws Exception
    {
        String fileName = file.getName();
        String typeName = fixType(fileName.substring(0, fileName.length() - 5));
        if (typeName == "JavaObject")
            return;
        System.out.println("Parsing " + typeName + "...");

        String text = FileUtils.readFileToString(file, Charsets.UTF_8);

        Matcher classDef = customClassDefPattern.matcher(text);
        if (!classDef.find())
            classDef = classDefPattern.matcher(text);
        if (!classDef.find())
            throw new Exception("Could not find class def");

        // Write interface header
        writeLn("interface ");
        write(typeName);
        if (classDef.group(2) != null)
        {
            write(" extends ");
            write(fixType(classDef.group(2)));
            write(" ");
        }
        write(" {");
        indention++;

        for (String line : text.split("\n"))
        {
            if (line.contains("tsgen ignore"))
                continue;
            Matcher mField = fieldPattern.matcher(line);
            Matcher mFunction = methodPattern.matcher(line);
            Matcher nCFunction = customMethodDefPattern.matcher(line);
            if (mFunction.find())
            {
                String name = mFunction.group(2);
                if (skipMethod(name))
                    continue;
                writeLn(name);
                write("(");
                if (mFunction.group(3) != null && !mFunction.group(3).isEmpty())
                    parseArgs(mFunction.group(3));
                write("): ");
                write(fixType(mFunction.group(1)));
                write(";");
            }
            else if (mField.find())
            {
                String name = mField.group(2);
                if (skipField(name))
                    continue;
                writeLn(name);
                write(": ");
                write(fixType(mField.group(1)));
                write(";");
            }
            else if (nCFunction.find())
            {
                writeLn(nCFunction.group(1));
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
