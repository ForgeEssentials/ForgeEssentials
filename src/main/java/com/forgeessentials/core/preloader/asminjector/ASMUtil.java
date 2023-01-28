package com.forgeessentials.core.preloader.asminjector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;

public final class ASMUtil
{

    public static class InjectionException extends RuntimeException
    {

        public InjectionException()
        {
        }

        public InjectionException(String message)
        {
            super(message);
        }

        public InjectionException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public InjectionException(Throwable cause)
        {
            super(cause);
        }

    }

    public static class IllegalInjectorException extends InjectionException
    {

        public IllegalInjectorException(String message)
        {
            super(String.format("Illegal injector: %s", message));
        }

        public IllegalInjectorException(String message, Exception cause)
        {
            super(String.format("Illegal injector: %s", message), cause);
        }

    }

    /* ------------------------------------------------------------ */
    /* Class loading utilities */

    public static Map<String, ClassNode> classCache = new HashMap<>();

    private static IClassNameTransformer classNameTransformer;

    public static ClassNode getClassNode(String className) throws ClassNotFoundException
    {
        ClassNode classNode = classCache.get(className);
        if (classNode == null)
        {
            classNode = loadClassNode(className, classNode);
            // Transform class names from obfuscated to non-obfusctated ones
            classNode.name = className;
            classNode.superName = classNode.superName == null ? null : resourceName(transformName(classNode.superName));
            classCache.put(className, classNode);
        }
        return classNode;
    }

    public static ClassNode loadClassNode(byte[] b)
    {
        ClassReader cr = new ClassReader(b);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);
        return classNode;
    }

    public static ClassNode loadClassNode(InputStream is) throws IOException
    {
        ClassReader cr = new ClassReader(is);
        ClassNode classNode = new ClassNode();
        cr.accept(classNode, 0);
        return classNode;
    }

    private static ClassNode loadClassNode(String className, ClassNode classNode) throws ClassNotFoundException
    {
        try
        {
            String untransformedName = untransformName(className);

            byte[] classBytes = Launch.classLoader.getClassBytes(untransformedName);
            if (classBytes != null)
            {
                return loadClassNode(classBytes);
            }

            ClassLoader appClassLoader = Launch.class.getClassLoader();
            String resourcePath = untransformedName.replace('.', '/').concat(".class");
            try (InputStream is = appClassLoader.getResourceAsStream(resourcePath))
            {
                return loadClassNode(is);
            }
        }
        catch (IOException e)
        {
            throw new ClassNotFoundException(className);
        }
    }

    public static ClassNode loadSuperClassNode(ClassNode c) throws ClassNotFoundException
    {
        return c.superName == null ? null : getClassNode(c.superName);
    }

    public static IClassNameTransformer getClassNameTransformer()
    {
        if (classNameTransformer == null)
            classNameTransformer = FluentIterable.from(Launch.classLoader.getTransformers()).filter(IClassNameTransformer.class).first().orNull();
        return classNameTransformer;
    }

    public static String untransformName(String transformedName)
    {
        IClassNameTransformer t = getClassNameTransformer();
        return t == null ? transformedName : t.unmapClassName(resourceName(transformedName));
    }

    public static String transformName(String untransformedName)
    {
        IClassNameTransformer t = getClassNameTransformer();
        return t == null ? untransformedName : t.remapClassName(resourceName(untransformedName));
    }

    public static String javaName(String resourceName)
    {
        return resourceName.replace('/', '.');
    }

    public static String resourceName(String javaName)
    {
        return javaName.replace('.', '/');
    }

    /* ------------------------------------------------------------ */
    /* Node utilities */

    public static MethodNode findMethod(ClassNode classNode, String name)
    {
        for (MethodNode method : classNode.methods)
            if (method.name.equals(name))
                return method;
        return null;
    }

    public static MethodNode findMethod(ClassNode classNode, String name, String desc)
    {
        for (MethodNode method : classNode.methods)
            if (method.name.equals(name) && method.desc.equals(desc))
                return method;
        return null;
    }

    public static FieldNode findField(ClassNode classNode, String name)
    {
        for (FieldNode f : classNode.fields)
        {
            if (f.name.equals(name))
            {
                return f;
            }
        }
        return null;
    }

    public static AnnotationNode getAnnotation(List<AnnotationNode> annotations, String type)
    {
        if (annotations == null)
            return null;
        for (AnnotationNode a : annotations)
            if (a.desc.equals(type))
                return a;
        return null;
    }

    public static TypeAnnotationNode getTypeAnnotation(List<TypeAnnotationNode> annotations, String type)
    {
        if (annotations == null)
            return null;
        for (TypeAnnotationNode a : annotations)
            if (a.desc.equals(type))
                return a;
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationValue(AnnotationNode annotation, String key)
    {
        if (annotation == null || annotation.values == null)
            return null;
        for (int i = 0; i < annotation.values.size() - 1; i += 2)
            if (annotation.values.get(i).equals(key))
                return (T) annotation.values.get(i + 1);
        return null;
    }

    public static boolean isPrivate(int access)
    {
        return (access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_NATIVE)) != 0;
    }

    public static boolean isStatic(int access)
    {
        return (access & Opcodes.ACC_STATIC) != 0;
    }

    public static boolean isInterface(int access)
    {
        return (access & Opcodes.ACC_INTERFACE) != 0;
    }

    public static boolean isVarInsNode(AbstractInsnNode i, int opcode, int var)
    {
        return i instanceof VarInsnNode && i.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) i).var == var;
    }

    public static boolean isReturn(AbstractInsnNode node)
    {
        return node.getOpcode() >= Opcodes.IRETURN && node.getOpcode() <= Opcodes.RETURN;
    }

    /* ------------------------------------------------------------ */
    /* Class loading utilities */

    public static int getReturnOpcodeFromType(Type type)
    {
        switch (type.getSort())
        {
        case Type.VOID:
            return Opcodes.RETURN;
        case Type.BOOLEAN:
        case Type.CHAR:
        case Type.BYTE:
        case Type.SHORT:
        case Type.INT:
            return Opcodes.IRETURN;
        case Type.LONG:
            return Opcodes.LRETURN;
        case Type.FLOAT:
            return Opcodes.FRETURN;
        case Type.DOUBLE:
            return Opcodes.DRETURN;
        case Type.ARRAY:
        case Type.OBJECT:
            return Opcodes.ARETURN;
        default:
            throw new RuntimeException("Unknown type");
        }
    }

    public static String[] typesToClassNames(Type[] injectorArguments2)
    {
        String[] argumentNames = new String[injectorArguments2.length];
        for (int i = 0; i < injectorArguments2.length; i++)
            argumentNames[i] = injectorArguments2[i].getClassName();
        return argumentNames;
    }

    public static String substringAfterLast(String value, String separator)
    {
        int idx = value.lastIndexOf(separator);
        if (idx < 0)
            return value;
        return value.substring(idx + 1);
    }

    public static <T> T last(T[] array)
    {
        return array[array.length - 1];
    }

    public static int boolInt(boolean b)
    {
        return b ? 1 : 0;
    }

    public static Map<String, String> keyValueListToMap(List<String> keyValueList)
    {
        Map<String, String> aliasMap = new HashMap<>();
        if (keyValueList != null)
        {
            for (String alias : keyValueList)
            {
                String[] aliasPair = alias.split("=");
                if (aliasPair.length != 2)
                    throw new IllegalArgumentException(String.format("Invalid alias %s", alias));
                aliasMap.put(aliasPair[0], aliasPair[1]);
            }
        }
        return aliasMap;
    }

    /* ------------------------------------------------------------ */

    public static class ClassInfo
    {

        public final ClassNode classNode;

        public ClassInfo(ClassNode classNode)
        {
            this.classNode = classNode;
        }

        public static ClassInfo forName(String className) throws ClassNotFoundException
        {
            ClassNode cn = getClassNode(className);
            if (cn == null)
                return null;
            return new ClassInfo(cn);
        }

        public static ClassInfo forType(Type type)
        {
            try
            {
                ClassNode cn = getClassNode(type.getClassName());
                if (cn == null)
                    return null;
                return new ClassInfo(cn);
            }
            catch (ClassNotFoundException e)
            {
                throw Throwables.propagate(e);
            }
        }

        public boolean isInterface()
        {
            return ((classNode.access & Opcodes.ACC_INTERFACE) != 0);
        }

        public ClassInfo getSuperClass()
        {
            if (classNode.superName == null)
                return null;
            try
            {
                ClassNode cn = getClassNode(classNode.superName);
                if (cn == null)
                    return null;
                return new ClassInfo(cn);
            }
            catch (ClassNotFoundException e)
            {
                throw Throwables.propagate(e);
            }
        }

        public String getName()
        {
            return classNode.name;
        }

        public boolean hasSuperClass(ClassInfo superClass)
        {
            if (superClass.equals(this))
                return true;
            ClassInfo parent = getSuperClass();
            return parent == null ? false : parent.hasSuperClass(superClass);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (obj instanceof ClassInfo)
            {
                ClassInfo otherClass = (ClassInfo) obj;
                return getName().equals(otherClass.getName());
            }
            return false;
        }

        @Override
        public int hashCode()
        {
            return super.hashCode();
        }

    }

}
