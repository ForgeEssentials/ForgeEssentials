package com.forgeessentials.core.preloader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.google.common.base.Throwables;

public class ASMInjector
{

    private static final String ERR_UNEXPECTED_END = "Unexpected end of injector method in injector %s:%d";

    /* ------------------------------------------------------------ */

    private static final String CI_NAME = CallbackInfo.class.getName();
    private static final String CI_RES_NAME = resourceName(CI_NAME);
    private static final String CI_METHOD_NAME = "doReturn";

    /* ------------------------------------------------------------ */

    private ClassNode injectorClass;

    private MethodNode injector;

    private String injectorName;

    private Type injectorReturnType;

    private Type[] injectorArguments;

    private boolean hasCallbackInfo;

    /* ------------------------------------------------------------ */
    /* Variables used during injection */

    private HashMap<LabelNode, LabelNode> labelMappings;

    private int returnOpcode;

    private MethodNode target;

    private AbstractInsnNode insertPoint;

    private Type targetReturnType;

    private int currentLine;

    /* ------------------------------------------------------------ */

    public ASMInjector(ClassNode classNode, MethodNode method)
    {
        this.injectorClass = classNode;
        this.injector = method;

        injectorReturnType = Type.getReturnType(injector.desc);
        injectorArguments = Type.getArgumentTypes(injector.desc);
        if (!injectorReturnType.equals(Type.VOID_TYPE))
            throw new RuntimeException("Can only use void methods as injector");

        String shortClassName = injectorClass.name.substring(injectorClass.name.lastIndexOf('/') + 1);
        injectorName = String.format("%s.%s(%s)", javaName(shortClassName), injector.name, StringUtils.join(typesToClassNames(injectorArguments), ", "));

        String callbackInfoArg = last(injectorArguments).getClassName();
        hasCallbackInfo = callbackInfoArg.equals(CI_NAME);
    }

    /* ------------------------------------------------------------ */
    /* Constructor helpers */

    public static ASMInjector create(ClassNode classNode, String methodName)
    {
        MethodNode method = findMethod(classNode, methodName);
        if (method == null)
            throw new RuntimeException("Could not find injector method");
        return new ASMInjector(classNode, method);
    }

    public static ASMInjector create(ClassNode classNode, String methodName, String methodDesc)
    {
        MethodNode method = findMethod(classNode, methodName, methodDesc);
        if (method == null)
            throw new RuntimeException("Could not find injector method");
        return new ASMInjector(classNode, method);
    }

    public static ASMInjector create(String className, String methodName)
    {
        try
        {
            ClassNode classNode = loadClassNode(getClassResourceStream(className));
            return create(classNode, methodName);
        }
        catch (IOException e)
        {
            throw Throwables.propagate(e);
        }
    }

    public static ASMInjector create(String className, String methodName, String methodDesc)
    {
        try
        {
            ClassNode classNode = loadClassNode(getClassResourceStream(className));
            return create(classNode, methodName, methodDesc);
        }
        catch (IOException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * Start injection process. <br>
     * <br>
     * This method runs synchronized to prevent concurrent modification of internal states
     * 
     * @param target
     *            The target method where the code should be injected into
     * @param insertPoint
     *            Node in the target method before which the code should be injected
     */
    public synchronized void inject(MethodNode target, AbstractInsnNode insertPoint)
    {
        this.target = target;
        this.insertPoint = insertPoint;
        this.currentLine = 0;

        // Validate injector arguments
        Type[] targetArguments = Type.getArgumentTypes(target.desc);
        if (targetArguments.length != injectorArguments.length - (hasCallbackInfo ? 1 : 0))
            throw new RuntimeException(
                    String.format("Invalid number of arguments in target method %s%s for injector %s", target.name, target.desc, injectorName));

        targetReturnType = Type.getReturnType(target.desc);
        returnOpcode = getReturnOpcodeFromType(targetReturnType);

        createLabelMappings();

        // Loop through all instructions in the source method and insert them into the target
        for (AbstractInsnNode i = injector.instructions.getFirst(); i != null; i = i.getNext())
        {
            if (hasCallbackInfo && isVarInsNode(i, Opcodes.ALOAD, injectorArguments.length))
            {
                i = handleReturn(i);
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
                    // Normal return statements should NEVER be in the injector methods.
                    // This would need special handling that is not implemented yet.
                    // The only thing we do is to ignore the last return in the injector method.
                    AbstractInsnNode next = i.getNext();
                    if (next != null && next.getNext() != null)
                        throw new RuntimeException(String.format("Illegal return in injector %s:%d", injectorName, currentLine));
                    break;
                default:
                    inject(i);
                    break;
                }
            }
        }
    }

    /**
     * Creates new LabelNode instances for mapping labels during injection
     */
    private void createLabelMappings()
    {
        labelMappings = new HashMap<LabelNode, LabelNode>();
        for (AbstractInsnNode i = injector.instructions.getFirst(); i != null; i = i.getNext())
            if (i instanceof LabelNode)
                labelMappings.put((LabelNode) i, new LabelNode());
    }

    /**
     * Inject cloned instruction into target method
     * 
     * @param instruction
     */
    private void inject(AbstractInsnNode instruction)
    {
        if (instruction instanceof LineNumberNode)
        {
            LineNumberNode lln = (LineNumberNode) instruction;
            currentLine = lln.line;
        }
        if (instruction.getNext() == null && instruction.getPrevious() == null)
            target.instructions.insertBefore(insertPoint, instruction);
        else
            target.instructions.insertBefore(insertPoint, instruction.clone(labelMappings));
    }

    private AbstractInsnNode handleReturn(AbstractInsnNode start)
    {
        // Code is trying to load callback info. We intercept this and instead generate the code we actually want
        for (AbstractInsnNode end = start; end != null; end = end.getNext())
        {
            // Find the end of the instruction
            if (end.getOpcode() == Opcodes.INVOKEINTERFACE)
            {
                MethodInsnNode returnNode = (MethodInsnNode) end;
                if (returnNode.owner.equals(CI_RES_NAME))
                {
                    // Check for calls different than the return method
                    if (!returnNode.name.equals(CI_METHOD_NAME))
                        throw new RuntimeException(
                                String.format("Unexpected call to CallbackInfo.%s in injector %s:%d", returnNode.name, injectorName, currentLine));

                    // Validate that the correct return method was called, i.e. that the return value has the correct type
                    Type returnType = Type.getArgumentTypes(returnNode.desc)[0];
                    if (!returnType.equals(targetReturnType))
                        throw new RuntimeException(String.format("Invalid return type in injector %s:%d", injectorName, currentLine));

                    if (returnOpcode != Opcodes.RETURN)
                    {
                        // Return with value - first inject all the instructions to generate the return value
                        for (start = start.getNext(); start != end; start = start.getNext())
                            inject(start);
                    }
                    inject(new InsnNode(returnOpcode));
                    return end;
                }
            }
        }
        throw new RuntimeException(String.format(ERR_UNEXPECTED_END, injectorName, currentLine));
    }

    /* ------------------------------------------------------------ */

    /**
     * This dummy interface is used to generate return statements inside injectors. <br>
     * Injectors <b>must never</b> call the wrong {@link CallbackInfo#doReturn()} function or injection will fail.
     * 
     * Always use the one which matches the return type of the original function.
     */
    public static interface CallbackInfo
    {

        public void doReturn();

        public void doReturn(boolean value);

        public void doReturn(byte value);

        public void doReturn(short value);

        public void doReturn(int value);

        public void doReturn(long value);

        public void doReturn(float value);

        public void doReturn(double value);

        public void doReturn(Object value);

        public void doReturn(boolean[] value);

        public void doReturn(byte[] value);

        public void doReturn(short[] value);

        public void doReturn(int[] value);

        public void doReturn(long[] value);

        public void doReturn(float[] value);

        public void doReturn(double[] value);

        public void doReturn(Object[] value);

    }

    /* ------------------------------------------------------------ */
    /* Class loading utilities */

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

    public static ClassNode loadClassNode(String className) throws IOException
    {
        try (InputStream is = getClassResourceStream(className))
        {
            return loadClassNode(is);
        }
    }

    public static InputStream getClassResourceStream(String name)
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName(name) + ".class");
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
    /* Other utilities */

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

    private static boolean isVarInsNode(AbstractInsnNode i, int opcode, int var)
    {
        return i instanceof VarInsnNode && i.getOpcode() == Opcodes.ALOAD && ((VarInsnNode) i).var == var;
    }

    private static <T> T last(T[] array)
    {
        return array[array.length - 1];
    }

}