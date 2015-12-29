package com.forgeessentials.core.preloader;

import java.io.IOException;
import java.util.HashMap;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.collect.FluentIterable;

public class TransformerUtil
{

    public static final boolean isObfuscated;

    static
    {
        isObfuscated = !((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment"));
    }

    private static IClassNameTransformer classNameTransformer;

    private static String javaName(String internalName)
    {
        return internalName.replace('/', '.');
    }

    private static String resourceName(String binaryName)
    {
        return binaryName.replace('.', '/');
    }

    public static synchronized IClassNameTransformer getClassNameTransformer()
    {
        if (classNameTransformer == null)
            classNameTransformer = FluentIterable.from(Launch.classLoader.getTransformers()).filter(IClassNameTransformer.class).first().orNull();
        return classNameTransformer;
    }

    public static String untransformName(String transformedName)
    {
        IClassNameTransformer t = getClassNameTransformer();
        return resourceName(t == null ? transformedName : t.unmapClassName(transformedName));
    }

    public static byte[] getClassBytes(String name) throws IOException
    {
        return Launch.classLoader.getClassBytes(javaName(untransformName(name)));
    }

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

    public static int countParameters(String desc)
    {
        int count = 0;
        int index = 1;
        char c;
        while (index < desc.length())
        {
            c = desc.charAt(index);
            if (c == ')')
                break;
            if (c == 'L')
                index = desc.indexOf(";", index);
            count++;
            index++;
        }
        return count;
    }

    public static void insertCodeFromMethod(MethodNode target, AbstractInsnNode insertPoint, String className, String methodName)
    {
        try
        {
            ClassReader cr = new ClassReader(getClassBytes(className));
            ClassNode classNode = new ClassNode();
            cr.accept(classNode, 0);

            MethodNode method = findMethod(classNode, methodName);
            if (method == null)
                throw new RuntimeException("Could not find source method");
            if (!method.desc.endsWith("V"))
                throw new RuntimeException("Can only use void methods as source");

            boolean hasCallbackInfo = method.desc.endsWith("Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;)V")
                    || method.desc.endsWith("Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;)V");
            int paramCount = countParameters(target.desc);
            int indexCi = paramCount + 1;
            if (paramCount != countParameters(method.desc) - (hasCallbackInfo ? 1 : 0))
                throw new RuntimeException("Invalid number of arguments in source method");

            int returnOpcode = Opcodes.RETURN;
            if (hasCallbackInfo && !target.desc.endsWith("V"))
            {
                switch (target.desc.charAt(target.desc.length() - 1))
                {
                case ';':
                    returnOpcode = Opcodes.ARETURN;
                    break;
                case 'I':
                    returnOpcode = Opcodes.IRETURN;
                    break;
                case 'Z':
                    returnOpcode = Opcodes.IRETURN;
                    break;
                case 'L':
                    returnOpcode = Opcodes.LRETURN;
                    break;
                case 'F':
                    returnOpcode = Opcodes.FRETURN;
                    break;
                case 'D':
                    returnOpcode = Opcodes.DRETURN;
                    break;
                default:
                    throw new RuntimeException("Unknown return type");
                }
            }

            HashMap<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
            for (AbstractInsnNode i = method.instructions.getFirst(); i != null; i = i.getNext())
                if (i instanceof LabelNode)
                    labels.put((LabelNode) i, new LabelNode());

            // Loop through all instructions in the source method and insert them into the target
            for (AbstractInsnNode i = method.instructions.getFirst(); i != null; i = i.getNext())
            {
                if (hasCallbackInfo && i.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) i).var == indexCi)
                {
                    for (AbstractInsnNode j = i; j != null; j = j.getNext())
                    {
                        if (j.getOpcode() == Opcodes.INVOKEVIRTUAL)
                        {
                            MethodInsnNode jj = (MethodInsnNode) j;
                            if (jj.owner.equals("org/spongepowered/asm/mixin/injection/callback/CallbackInfo"))
                            {
                                if (!jj.name.equals("cancel"))
                                    throw new RuntimeException("Invalid call on CallbackInfo");
                            }
                            else if (jj.owner.equals("org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable"))
                            {
                                if (!jj.name.equals("setReturnValue"))
                                    throw new RuntimeException("Invalid call on CallbackInfo");

                                AbstractInsnNode end = j;
                                if (returnOpcode != Opcodes.ARETURN)
                                    end = end.getPrevious();
                                for (i = i.getNext(); i != end; i = i.getNext())
                                {
                                    target.instructions.insertBefore(insertPoint, i.clone(labels));
                                }
                            }
                            else
                                throw new RuntimeException("Unexpected callback class");
                            target.instructions.insertBefore(insertPoint, new InsnNode(returnOpcode));
                            i = j;
                            break;
                        }
                    }
                }
                else
                {
                    switch (i.getOpcode())
                    {
                    case Opcodes.IRETURN:
                    case Opcodes.LRETURN:
                    case Opcodes.FRETURN:
                    case Opcodes.DRETURN:
                    case Opcodes.ARETURN:
                    case Opcodes.RETURN:
                        AbstractInsnNode next = i.getNext();
                        if (next != null && next.getNext() != null)
                            throw new RuntimeException("Illegal return in ASM method");
                        break;
                    default:
                        target.instructions.insertBefore(insertPoint, i.clone(labels));
                        break;
                    }
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void printClassDetails(Class<?> clazz, String filter)
    {
        try
        {
            ClassReader cr = new ClassReader(getClassBytes(clazz.getName()));
            cr.accept(new ClassVisitorDebugInfo(filter), 0);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void printClassDetails(Class<?> clazz)
    {
        printClassDetails(clazz, null);
    }

    private static final class ClassVisitorDebugInfo extends ClassVisitor
    {

        private String className;

        private String filter;

        public ClassVisitorDebugInfo()
        {
            super(Opcodes.ASM5);
        }

        public ClassVisitorDebugInfo(String filter)
        {
            this();
            this.filter = filter;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces)
        {
            className = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            if (filter == null || !name.contains(filter))
            {
                System.out.println(String.format("L%s;%s%s", className, name, desc));
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

    }

}