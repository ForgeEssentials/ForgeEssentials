package com.forgeessentials.core.preloader.asminjector;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ASMClassWriter extends ClassWriter
{

    public ASMClassWriter(int flags)
    {
        super(flags);
    }

    public ASMClassWriter(ClassReader classReader, int flags)
    {
        super(classReader, flags);
    }

    @Override
    protected String getCommonSuperClass(final String type1, final String type2)
    {
        // TODO: VERY DIRTY HACK! I wonder what this function even does
        // However it is necessary for recalculating frames so there's nothing else we can do
        return "java/lang/Object";
        // if (type1.equals(type2))
        // return type1;
        // try
        // {
        // ClassNode c = ASMUtil.loadClassNode(type1);
        // ClassNode d = ASMUtil.loadClassNode(type2);
        //
        // if (ASMUtil.isInterface(c.access) && ASMUtil.isInterface(d.access))
        // {
        // return "java/lang/Object";
        // }
        //
        // Set<String> cAncestors = new HashSet<>();
        // while (c != null)
        // {
        // cAncestors.add(c.name);
        // c = ASMUtil.loadSuperClassNode(c);
        // }
        // while (d != null)
        // {
        // if (cAncestors.contains(d.name))
        // return d.name;
        // d = ASMUtil.loadSuperClassNode(d);
        // }
        // throw new RuntimeException("Could not find common superclass");
        // }
        // catch (IOException e)
        // {
        // throw new RuntimeException(String.format("Error in getCommonSuperClass(\"%s\", \"%s\"): ", type1, type2) + e.toString(), e);
        // }
    }

}
