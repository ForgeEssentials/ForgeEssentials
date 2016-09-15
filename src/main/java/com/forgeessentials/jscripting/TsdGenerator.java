package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.forgeessentials.util.MappedList;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.javadoc.Main;

public class TsdGenerator extends Doclet
{

    public static TsdGenerator generator = new TsdGenerator();

    public File outFile = new File("jscripting/mc.d.ts");

    public File headerFile;

    public List<String> externalClasses = new ArrayList<>();

    public Map<String, String> staticClasses = new HashMap<>();

    public Map<String, String> interfaceClasses = new HashMap<>();

    /* ************************************************************ */

    private RootDoc rootDoc;

    private PrintStream writer;

    private int indention = 0;

    private String srcPackageBaseName;

    private String currentPackageName;

    private Set<String> declaredTypes = new HashSet<>();

    private Map<String, String> classNameMap = new HashMap<>();

    public boolean startImpl(RootDoc root)
    {
        this.rootDoc = root;
        declaredTypes.add("int");
        declaredTypes.add("long");
        declaredTypes.add("float");
        declaredTypes.add("double");
        declaredTypes.add("string");
        declaredTypes.add("boolean");
        declaredTypes.add("any");
        declaredTypes.add("number");
        declaredTypes.add("void");
        declaredTypes.add("JavaList");

        classNameMap.put("void", "void");
        classNameMap.put("java.lang.Object", "any");
        classNameMap.put("java.lang.String", "string");
        classNameMap.put("java.lang.Boolean", "boolean");
        classNameMap.put(MappedList.class.getName(), "JavaList");

        try
        {
            try (PrintStream w = new PrintStream(new FileOutputStream(outFile)))
            {
                writer = w;
                indention = 0;
                try (FileInputStream is = new FileInputStream(headerFile))
                {
                    write(IOUtils.toString(is));
                }

                List<PackageDoc> packages = new ArrayList<>(Arrays.asList(root.specifiedPackages()));
                packages.sort((a, b) -> a.name().compareTo(b.name()));
                srcPackageBaseName = packages.get(0).name();
                packages.add(packages.size() - 1, packages.remove(0));

                currentPackageName = "";
                for (String externalClass : externalClasses)
                {
                    ClassDoc classDoc = root.classNamed(externalClass);
                    if (classDoc == null)
                    {
                        System.err.println("Could not find class " + externalClass);
                        continue;
                    }
                    currentPackageName = stripPackageName(classDoc.qualifiedName());
                    preprocessClass(classDoc);
                }

                for (PackageDoc packageDoc : packages)
                {
                    currentPackageName = packageDoc.name().substring(Math.min(packageDoc.name().length(), srcPackageBaseName.length() + 1));
                    for (ClassDoc classDoc : packageDoc.allClasses())
                        preprocessClass(classDoc);
                }

                for (PackageDoc packageDoc : packages)
                {
                    currentPackageName = packageDoc.name().substring(Math.min(packageDoc.name().length(), srcPackageBaseName.length() + 1));
                    if (currentPackageName.length() > 0)
                    {
                        writeComment(packageDoc);
                        writeLn("declare namespace " + currentPackageName + " {");
                        indention++;
                        writeLn("");
                    }

                    List<ClassDoc> classes = Arrays.asList(packageDoc.allClasses());
                    classes.sort((a, b) -> a.name().compareTo(b.name()));

                    for (ClassDoc classDoc : classes)
                        generateClass(classDoc);

                    if (currentPackageName.length() > 0)
                    {
                        indention--;
                        writeLn("}");
                        writeLn("");
                    }
                }
                currentPackageName = "";

                for (String externalClass : externalClasses)
                {
                    ClassDoc classDoc = root.classNamed(externalClass);
                    if (classDoc == null)
                    {
                        System.err.println("Could not find class " + externalClass);
                        continue;
                    }
                    currentPackageName = stripPackageName(classDoc.qualifiedName());
                    writeLn("declare namespace ");
                    write(currentPackageName);
                    write(" { ");
                    indention++;
                    generateClass(classDoc);
                    indention--;
                    writeLn("}");
                }
                writeLn("");

                // Print interface alias
                for (Entry<String, String> className : interfaceClasses.entrySet())
                {
                    String mappedName = classNameMap.get(className.getKey());
                    writeLn("declare var ");
                    write(className.getValue());
                    write(": ");
                    write(mappedName);
                    write(";");
                }
                writeLn("");

                // Print static alias
                for (Entry<String, String> className : staticClasses.entrySet())
                {
                    String mappedName = classNameMap.get(className.getKey());
                    writeLn("declare var ");
                    write(className.getValue());
                    write(": typeof ");
                    write(mappedName);
                    write(";");
                }
                writeLn("");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return true;
    }

    private void preprocessClass(ClassDoc classDoc)
    {
        if (ignoreClass(classDoc))
            return;
        mapClassName(classDoc);
    }

    private void generateClass(ClassDoc classDoc)
    {
        if (ignoreClass(classDoc))
            return;
        String typeName = classNameMap.get(classDoc.qualifiedName());

        String interfaceType = getFirstTagText(classDoc, "tsd.interface");
        if (interfaceType != null)
            interfaceClasses.put(classDoc.qualifiedName(), interfaceType);

        String staticType = getFirstTagText(classDoc, "tsd.static");
        if (staticType != null)
            staticClasses.put(classDoc.qualifiedName(), staticType);

        boolean isClass = (interfaceType == null);

        writeComment(classDoc);

        // Write interface header
        writeLn(indention == 0 ? "declare " : "");
        write(isClass ? "class " : "interface ");
        write(stripClassName(typeName));
        if (classDoc.superclass() != null && !classDoc.superclass().qualifiedName().equals("java.lang.Object"))
        {
            write(" extends ");
            write(mapClassName(classDoc.superclassType()));
            if (classDoc.superclassType() instanceof ParameterizedType && classDoc.superclass().name().equals("MappedList"))
            {
                write("<");
                write(mapClassName(classDoc.superclassType().asParameterizedType().typeArguments()[1]));
                write(">");
            }
        }
        write(" {");

        indention++;

        for (FieldDoc fieldDoc : classDoc.fields())
            generateField(fieldDoc, true);

        for (FieldDoc fieldDoc : classDoc.fields())
            generateField(fieldDoc, false);

        if (isClass)
        {
            for (MethodDoc methodDoc : classDoc.methods())
                generateMethod(methodDoc, true);

            for (ConstructorDoc constructorDoc : classDoc.constructors())
                generateConstructor(constructorDoc);
        }

        for (MethodDoc methodDoc : classDoc.methods())
            generateMethod(methodDoc, false);

        indention--;
        writeLn("}");
        writeLn("");
        declaredTypes.add(typeName);
    }

    private void generateConstructor(ConstructorDoc constructorDoc)
    {
        if (!constructorDoc.isPublic() || ignoreDoc(constructorDoc))
            return;
        // Hide default constructors of classes extending Object
        if (constructorDoc.containingClass().superclass().qualifiedName().equals(Object.class.getName()) && constructorDoc.isSynthetic())
            return;
        // Hide wrapper constructors
        if (constructorDoc.parameters().length >= 1 && constructorDoc.parameters()[0].name().equals("that"))
            return;

        writeComment(constructorDoc);

        if (indention == 0)
            writeLn("declare function ");
        else
            writeLn("constructor");

        Tag[] defTags = constructorDoc.tags("tsd.def");
        if (defTags.length > 0)
        {
            for (Tag tag : defTags)
            {
                write(tag.text());
            }
            return;
        }

        write("(");
        Parameter[] parameters = constructorDoc.parameters();
        for (int i = 0; i < parameters.length; i++)
        {
            if (i > 0)
                write(", ");
            Parameter parameter = parameters[i];
            if (constructorDoc.isVarArgs() && i == parameters.length - 1)
                write("...");
            write(parameter.name());
            write(": ");
            write(mapClassName(parameter.type()));
            write(parameter.type().dimension());
        }
        write(");");
    }

    private void generateField(FieldDoc fieldDoc, boolean staticOnly)
    {
        if (!fieldDoc.isPublic() || ignoreDoc(fieldDoc))
            return;
        if (fieldDoc.isStatic() != staticOnly)
            return;

        writeComment(fieldDoc);

        writeLn(indention == 0 ? "declare var " : (staticOnly ? "static " : ""));

        Tag[] defTags = fieldDoc.tags("tsd.def");
        if (defTags.length > 0)
        {
            for (Tag tag : defTags)
            {
                write(tag.text());
            }
            return;
        }

        write(fieldDoc.name());

        if (fieldDoc.tags("tsd.optional").length > 0)
            write("?");
        write(": ");

        writeType(fieldDoc, fieldDoc.type());
        write(";");
    }

    private void generateMethod(MethodDoc methodDoc, boolean staticOnly)
    {
        if (!methodDoc.isPublic() || ignoreDoc(methodDoc))
            return;
        if (methodDoc.isStatic() != staticOnly)
            return;

        writeComment(methodDoc);

        writeLn(indention == 0 ? "declare function " : (staticOnly ? "static " : ""));

        Tag[] defTags = methodDoc.tags("tsd.def");
        if (defTags.length > 0)
        {
            for (Tag tag : defTags)
            {
                write(tag.text());
            }
            return;
        }

        write(methodDoc.name());

        write("(");
        Parameter[] parameters = methodDoc.parameters();
        for (int i = 0; i < parameters.length; i++)
        {
            if (i > 0)
                write(", ");
            Parameter parameter = parameters[i];
            if (methodDoc.isVarArgs() && i == parameters.length - 1)
                write("...");
            write(parameter.name());
            write(": ");
            write(mapClassName(parameter.type()));
            write(parameter.type().dimension());
        }
        write("): ");
        writeType(methodDoc, methodDoc.returnType());
        write(";");
    }

    private void writeType(Doc doc, Type type)
    {
        String typeOverride = getFirstTagText(doc, "tsd.type");
        write(typeOverride != null ? typeOverride : mapClassName(type));
        if (type instanceof ParameterizedType)
        {
            if (type.simpleTypeName().equals("MappedList"))
            {
                write("<");
                write(mapClassName(type.asParameterizedType().typeArguments()[1]));
                write(">");
            }
        }
        else
        {
            write(type.dimension());
        }
    }

    private void writeComment(Doc fieldDoc)
    {
        String deprecation = getFirstTagText(fieldDoc, "deprecated");
        if (fieldDoc.commentText().length() > 0 || deprecation != null)
        {
            writeLn("/**");
            String comment = fieldDoc.commentText()
                    .replace("\r", "")
                    .replace("<br>", "")
                    .replace("<b>", "")
                    .replace("</b>", "");
            if (comment.length() > 0)
                for (String line : comment.split("\n"))
                    writeLn(" * " + line.trim());
            if (deprecation != null)
                writeLn(" * @deprecated " + deprecation);
            writeLn(" */");
        }
    }

    private boolean ignoreDoc(Doc doc)
    {
        return doc.name().startsWith("_") || doc.tags("tsd.ignore").length > 0;
    }

    private boolean ignoreClass(ClassDoc classDoc)
    {
        return !classDoc.isPublic() || ignoreDoc(classDoc) || classDoc.name().equals("ScriptExtensionRoot");
    }

    private String getFirstTagText(Doc doc, String name)
    {
        Tag[] tags = doc.tags(name);
        return tags.length > 0 ? tags[0].text() : null;
    }

    private String mapClassName(Type type)
    {
        // Directly return simple types like void and int
        if (!type.qualifiedTypeName().contains("."))
            return type.qualifiedTypeName();

        String mappedName = classNameMap.get(type.qualifiedTypeName());
        if (mappedName == null)
        {
            String fqn = type.qualifiedTypeName();
            //            ClassDoc classDoc = rootDoc.classNamed(fqn);
            //            classDoc.containingPackage()

            int idx = fqn.lastIndexOf('.');
            String typeName = fqn.substring(idx + 1, fqn.length());
            String packageName = mapPackageRoot(fqn.substring(0, idx));

            if (typeName.startsWith("Js"))
                typeName = typeName.substring(2);
            if (typeName.endsWith("Static"))
                typeName = typeName.substring(0, typeName.length() - "Static".length());

            mappedName = (packageName.isEmpty() ? "" : packageName + ".") + typeName;
            classNameMap.put(type.qualifiedTypeName(), mappedName);
        }

        if (!currentPackageName.isEmpty() && mappedName.startsWith(currentPackageName))
            return mappedName.substring(currentPackageName.length() + 1);
        return mappedName;
    }

    private String mapPackageRoot(String name)
    {
        String baseName = name;
        while (true)
        {
            PackageDoc pkg = rootDoc.packageNamed(baseName);
            if (pkg != null)
            {
                String baseTag = getFirstTagText(pkg, "tsd.namespace");
                if (baseTag != null)
                {
                    return name.substring(Math.min(baseName.length() + 1, name.length()));
                }
            }
            ClassDoc wrapperClass = rootDoc.classNamed(baseName + ".ScriptExtensionRoot");
            if (wrapperClass != null)
                return name.substring(Math.min(baseName.length() + 1, name.length()));

            int idx = baseName.lastIndexOf('.');
            if (idx < 0)
                return name;
            baseName = baseName.substring(0, idx);
        }
    }

    private String stripPackageName(String name)
    {
        int idx = name.lastIndexOf('.');
        return idx < 0 ? "" : name.substring(0, idx);
    }

    private String stripClassName(String name)
    {
        int idx = name.lastIndexOf('.');
        return idx < 0 ? name : name.substring(idx + 1);
    }

    private void print(String text)
    {
        // System.out.print(text);
        writer.print(text);
    }

    private void writeLn(String text)
    {
        print("\r\n");
        for (int i = 0; i < indention; i++)
            print("\t");
        print(text);
    }

    private void write(String text)
    {
        print(text);
    }

    /* ************************************************************ */
    /* Doclet functions */

    public static boolean start(RootDoc root)
    {
        return generator.startImpl(root);
    }

    public static int optionLength(String arg)
    {
        switch (arg)
        {
        case "-out":
        case "-header":
        case "-external":
            // Ignored options for gradle compat:
        case "-d":
        case "-doctitle":
        case "-windowtitle":
            return 2;
        }
        return 0;
    }

    public static boolean validOptions(String[][] argGroups, DocErrorReporter arg0)
    {
        for (String[] argGroup : argGroups)
        {
            switch (argGroup[0])
            {
            case "-out":
                System.out.println("Set output file to " + argGroup[1]);
                generator.outFile = new File(argGroup[1]);
                break;
            case "-header":
                generator.headerFile = new File(argGroup[1]);
                if (!generator.headerFile.exists())
                {
                    System.err.println(String.format("Could not find header file %s", generator.headerFile.getAbsolutePath()));
                    System.exit(1);
                }
                System.out.println("Set header file to " + argGroup[1]);
                break;
            case "-external":
                System.out.println("Added external class " + argGroup[1]);
                generator.externalClasses.add(argGroup[1]);
                break;
            }
        }
        return true;
    }

    public static LanguageVersion languageVersion()
    {
        return LanguageVersion.JAVA_1_5;
    }

    public static void main(String[] args) throws IOException
    {
        File outDir = new File("jscripting");
        File feDtsFile = new File("src/main/resources/com/forgeessentials/jscripting/fe.d.ts");
        File mcDtsFile = new File("src/main/resources/com/forgeessentials/jscripting/mc.d.ts");

        generator = new TsdGenerator();
        Main.execute(ClassLoader.getSystemClassLoader(), "-doclet", TsdGenerator.class.getName(), "-public",
                "-sourcepath", "src/main/java",
                "-subpackages", "com.forgeessentials.jscripting.fewrapper",
                "-out", feDtsFile.getAbsolutePath(),
                "-header", "src/main/resources/com/forgeessentials/jscripting/fe_header.d.ts"
        );

        generator = new TsdGenerator();
        Main.execute(ClassLoader.getSystemClassLoader(), "-doclet", TsdGenerator.class.getName(), "-public",
                "-sourcepath", "src/main/java",
                "-subpackages", "com.forgeessentials.jscripting.wrapper",
                "-out", mcDtsFile.getAbsolutePath(),
                "-header", "src/main/resources/com/forgeessentials/jscripting/mc_header.d.ts",
                "-external", UUID.class.getName(),
                "-external", Date.class.getName(),
                "-external", Calendar.class.getName(),
                "-external", TimeZone.class.getName(),
                "-external", Locale.class.getName(),
                // "-external", Instant.class.getName(),
                // "-external", Collection.class.getName(),
                "-external", net.minecraft.world.GameType.class.getName()
        );

        FileUtils.copyFileToDirectory(feDtsFile, outDir);
        FileUtils.copyFileToDirectory(mcDtsFile, outDir);
    }

}
