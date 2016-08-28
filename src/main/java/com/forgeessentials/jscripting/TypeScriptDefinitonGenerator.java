package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.Files;

/**
 * This class is not yet fully complete! It works for saving a lot of manual work, but it's not
 * yet ready for fully generating the mc.d.ts, so just copy and paste individual definitions for now.
 */
public class TypeScriptDefinitonGenerator
{
    private static final Pattern fieldPattern = Pattern.compile("public ([\\w <>,?\\[\\]]+) (\\w+)( =.+)?;");
    private static final Pattern methodPattern = Pattern.compile("public ([\\w <>,?\\[\\]]+) (\\w+)\\(([\\w <>,.?\\[\\]]*)\\)");
    //private static final Pattern constructorPattern = Pattern.compile("public (\\w+)\\(([\\w <>,?\\[\\]]*)\\)");
    private static final Pattern classDefPattern = Pattern.compile("public class (\\w+)(?:<[\\w ]+>)?(?: extends (\\w+))?");
    private static final Pattern customClassDefPattern = Pattern.compile("classdef (.*)");
    private static final Pattern customMethodDefPattern = Pattern.compile("methoddef (.*)");

    public static void main(String[] args)
    {
        try
        {
            File dir = new File("src/main/java/com/forgeessentials/jscripting/wrapper");
            File outFile = new File("jscripting/generated.d.ts");
            try (PrintStream ps = new PrintStream(new FileOutputStream(outFile)))
            {
                for (File file : dir.listFiles())
                {
                    String fileName = file.getName();
                    if (file.isFile() && fileName.startsWith("Js") && fileName.endsWith(".java"))
                    {
                        String name = fileName.substring(2, fileName.length() - 5);
                        System.out.println("Parsing " + name + "...");
                        List<String> lines = parseFile(file, name);
                        if (!lines.isEmpty())
                        {
                            for (String line : lines)
                                ps.println(line);
                            ps.println();
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private static List<String> parseFile(File file, String name) throws Exception
    {
        List<String> lines = Files.readLines(file, Charset.forName("UTF-8"));
        List<String> outLines = new ArrayList<>();
        boolean foundClassDef = false;
        for (String line : lines)
        {
            if (line.contains("tsgen ignore")) continue;
            Matcher mf = fieldPattern.matcher(line);
            Matcher mm = methodPattern.matcher(line);
            //Matcher mc = constructorPattern.matcher(line);
            Matcher mcd = classDefPattern.matcher(line);
            Matcher mccd = customClassDefPattern.matcher(line);
            Matcher mcmd = customMethodDefPattern.matcher(line);
            if (mm.find())
            {
                if (!foundClassDef) throw new RuntimeException("Found method before class definition!?");
                String methodName = mm.group(2);
                if (methodName.equals("equals") || methodName.equals("getThat")) continue;
                StringBuilder newLine = new StringBuilder("        ").append(methodName).append('(');
                if (mm.group(3) != null && !mm.group(3).isEmpty())
                    parseArgs(mm.group(3), newLine);
                newLine.append("): ").append(fixType(mm.group(1))).append(';');
                outLines.add(newLine.toString());
            }
            else if (mf.find())
            {
                if (!foundClassDef) throw new RuntimeException("Found field before class definition!?");
                String fieldName = mf.group(2);
                if (fieldName.equals("that")) continue;
                StringBuilder newLine = new StringBuilder("        ").append(fieldName).append(": ").append(fixType(mf.group(1))).append(';');
                outLines.add(newLine.toString());
            }
            else if (!foundClassDef && mcd.find())
            {
                StringBuilder newLine = new StringBuilder("    interface ").append(name).append(' ');
                if (mcd.group(2) != null)
                    newLine.append("extends ").append(fixType(mcd.group(2))).append(' ');
                newLine.append('{');
                outLines.add(newLine.toString());
                foundClassDef = true;
            }
            else if (!foundClassDef && mccd.find())
            {
                outLines.add("    " + mccd.group(1) + " {");
                foundClassDef = true;
            }
            else if (mcmd.find())
            {
                outLines.add("        " + mcmd.group(1));
            }
        }
        outLines.add("    }");
        return outLines;
    }

    private static void parseArgs(String args, StringBuilder sb) {
        String[] split = args.split(",");
        for (int i = 0; i < split.length; i++)
        {
            String[] argSplit = split[i].trim().split(" ");
            String name = argSplit[1];
            String type = fixType(argSplit[0]);
            if (type.endsWith("..."))
            {
                type = type.substring(0, type.length() - 3) + "[]";
                name = "..." + name;
            }
            sb.append(name).append(": ").append(type);
            if (i < split.length - 1) sb.append(", ");
        }
    }

    private static String fixType(String type) {
        String ret = type;
        if (type.startsWith("Js"))
        {
            if (type.endsWith("Static"))
                ret = type.substring(2, type.length() - 6);
            else
                ret = type.substring(2);
        }
        else if (type.equals("String") || type.equals("String[]") || type.equals("String..."))
            ret = "s" + type.substring(1);
        else if (type.equals("Object") || type.equals("Object[]") || type.equals("Object..."))
            ret = "any" + type.substring(6);

        if (ret.indexOf('<') != -1) // Strip generics
            ret = ret.substring(0, ret.indexOf('<'));
        return ret;
    }

}
