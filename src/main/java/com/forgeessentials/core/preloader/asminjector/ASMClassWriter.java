package com.forgeessentials.core.preloader.asminjector;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

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
        if (type1.equals(type2))
            return type1;
        try
        {
            ClassNode clazz1 = ASMUtil.getClassNode(type1);
            ClassNode clazz2 = ASMUtil.getClassNode(type2);

            if (ASMUtil.isInterface(clazz1.access) && ASMUtil.isInterface(clazz2.access))
            {
                return "java/lang/Object";
            }

            Set<String> cAncestors = new HashSet<>();
            while (clazz1 != null)
            {
                cAncestors.add(clazz1.name);
                clazz1 = ASMUtil.loadSuperClassNode(clazz1);
            }
            while (clazz2 != null)
            {
                if (cAncestors.contains(clazz2.name))
                    return clazz2.name;
                clazz2 = ASMUtil.loadSuperClassNode(clazz2);
            }
            throw new RuntimeException("Could not find common superclass");
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(String.format("Error in getCommonSuperClass(\"%s\", \"%s\"): ", type1, type2), e);
        }
    }

}
