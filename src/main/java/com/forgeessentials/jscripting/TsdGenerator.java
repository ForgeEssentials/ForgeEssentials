package com.forgeessentials.jscripting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Doc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.tools.javadoc.Main;

public class TsdGenerator extends Doclet
{

    public static TsdGenerator generator = new TsdGenerator();

    private File outFile = new File("jscripting/mc.d.ts");

    private PrintStream writer;

    private int indention = 0;

    private Set<String> declaredTypes = new HashSet<>();

    private Map<String, String> classNameMap = new HashMap<>();

    public boolean startImpl(RootDoc root)
    {
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

        classNameMap.put("Object", "any");
        classNameMap.put("String", "string");
        classNameMap.put("Boolean", "boolean");
        classNameMap.put("JsWrapper", "JavaObject");
        classNameMap.put("MappedList", "JavaList");
        classNameMap.put("ICommandSender", "CommandSender");

        try
        {
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

                List<ClassDoc> allClasses = Arrays.asList(root.packageNamed("com.forgeessentials.jscripting.wrapper").allClasses());
                allClasses.sort((a, b) -> a.name().compareTo(b.name()));

                for (ClassDoc classDoc : allClasses)
                    preprocessClass(classDoc);
                for (ClassDoc classDoc : allClasses)
                    generateClass(classDoc);

                // for (String type : knownTypes)
                for (String type : classNameMap.values())
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
        return false;
    }

    private void preprocessClass(ClassDoc classDoc)
    {
        if (ignoreClass(classDoc))
            return;
        String typeName = getFirstTagText(classDoc, "tsd.type");
        if (typeName == null)
            typeName = mapClassName(classDoc.name());
        classNameMap.put(classDoc.name(), typeName);
    }

    private void generateClass(ClassDoc classDoc)
    {
        if (ignoreClass(classDoc))
            return;
        String typeName = classNameMap.get(classDoc.name());

        writeComment(classDoc);

        // Write interface header
        writeLn("interface ");
        write(typeName);
        if (!classDoc.superclass().qualifiedName().equals("java.lang.Object"))
        {
            write(" extends ");
            write(mapClassName(classDoc.superclassType().typeName()));
            if (classDoc.superclassType() instanceof ParameterizedType && classDoc.superclass().name().equals("MappedList"))
            {
                write("<");
                write(mapClassName(classDoc.superclassType().asParameterizedType().typeArguments()[1].typeName()));
                write(">");
            }
        }
        write(" {");

        indention++;

        for (FieldDoc fieldDoc : classDoc.fields())
            generateField(fieldDoc);

        for (MethodDoc methodDoc : classDoc.methods())
            generateMethod(methodDoc);

        indention--;
        writeLn("}");
        writeLn("");
        declaredTypes.add(typeName);
    }

    private void generateField(FieldDoc fieldDoc)
    {
        if (!fieldDoc.isPublic() || fieldDoc.isStatic() || ignoreDoc(fieldDoc))
            return;

        writeComment(fieldDoc);

        Tag[] defTags = fieldDoc.tags("tsd.def");
        if (defTags.length > 0)
        {
            for (Tag tag : defTags)
            {
                writeLn(tag.text());
            }
            return;
        }

        writeLn(fieldDoc.name());

        if (fieldDoc.tags("tsd.optional").length > 0)
            write("?");
        write(": ");

        writeType(fieldDoc, fieldDoc.type());
        write(";");
    }

    private void generateMethod(MethodDoc methodDoc)
    {
        if (!methodDoc.isPublic() || methodDoc.isStatic() || ignoreDoc(methodDoc))
            return;

        writeComment(methodDoc);

        Tag[] defTags = methodDoc.tags("tsd.def");
        if (defTags.length > 0)
        {
            for (Tag tag : defTags)
            {
                writeLn(tag.text());
            }
            return;
        }

        writeLn(methodDoc.name());

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
            write(mapClassName(parameter.type().simpleTypeName()));
            write(parameter.type().dimension());
        }
        write("): ");
        writeType(methodDoc, methodDoc.returnType());
        write(";");
    }

    private void writeType(Doc doc, Type type)
    {
        String typeOverride = getFirstTagText(doc, "tsd.type");
        write(typeOverride != null ? typeOverride : mapClassName(type.simpleTypeName()));
        if (type instanceof ParameterizedType)
        {
            if (type.simpleTypeName().equals("MappedList"))
            {
                write("<");
                write(mapClassName(type.asParameterizedType().typeArguments()[1].simpleTypeName()));
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
        if (fieldDoc.commentText().length() > 0)
        {
            writeLn("/**");
            for (String line : fieldDoc.commentText().replace("\r", "").replace("<br>", "").split("\n"))
                writeLn(" * " + line.trim());
            writeLn(" */");
        }
    }

    private boolean ignoreDoc(Doc doc)
    {
        return doc.tags("tsd.ignore").length > 0;
    }

    private boolean ignoreClass(ClassDoc classDoc)
    {
        return !classDoc.isPublic() || ignoreDoc(classDoc) || !classDoc.name().startsWith("Js");
    }

    private String getFirstTagText(Doc doc, String name)
    {
        Tag[] tags = doc.tags(name);
        return tags.length > 0 ? tags[0].text() : null;
    }

    private String mapClassName(String type)
    {
        type = stripInnerClasses(type);
        String mappedName = classNameMap.get(type);
        if (mappedName != null)
            return mappedName;

        mappedName = type;
        if (mappedName.startsWith("Js"))
            mappedName = mappedName.substring(2);

        classNameMap.put(type, mappedName);
        // knownTypes.add(mappedName);
        return mappedName;
    }

    private String stripInnerClasses(String type)
    {
        int idx;
        while ((idx = type.indexOf('.')) >= 0)
            type = type.substring(idx + 1);
        return type;
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
            }
        }
        return true;
    }

    public static LanguageVersion languageVersion()
    {
        return LanguageVersion.JAVA_1_5;
    }

    public static void main(String[] args)
    {
        Main.main(new String[] {
                "-sourcepath", "src/main/java",
                "-doclet", TsdGenerator.class.getName(),
                "-out", "jscripting/mc.d.ts",
                "-public",
                "com.forgeessentials.jscripting.wrapper"
        });
    }

}
