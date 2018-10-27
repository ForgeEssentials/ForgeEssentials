package com.forgeessentials.core.preloader.asminjector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeAnnotationNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.forgeessentials.core.preloader.asminjector.ASMUtil.IllegalInjectorException;
import com.forgeessentials.core.preloader.asminjector.ASMUtil.InjectionException;
import com.forgeessentials.core.preloader.asminjector.annotation.Local;
import com.forgeessentials.core.preloader.asminjector.annotation.Shadow;

public class MethodInjector implements Comparable<MethodInjector>
{

    public static final Logger log = LogManager.getLogger("ASM_MethodInjector");

    static final String CI_NAME = CallbackInfo.class.getName();

    static final String CI_RES_NAME = ASMUtil.resourceName(CI_NAME);

    static final String CI_METHOD_NAME = "doReturn";

    /* ------------------------------------------------------------ */

    protected ClassNode injectorClass;

    protected MethodNode injector;

    protected List<InjectionPoint> injectionPoints;

    protected Type injectorReturnType;

    protected Type[] injectorArguments;

    protected int injectorArgumentCount;

    protected int injectedLocalsCount;

    protected int targetArgumentCount;

    protected int callbackInfoIndex;

    protected int priority;

    /* ------------------------------------------------------------ */
    /* Variables used during injection */

    private ClassNode targetClass;

    private MethodNode targetMethod;

    private AbstractInsnNode insertPoint;

    private HashMap<LabelNode, LabelNode> labelMappings;

    private Type[] targetArguments;

    private Type targetReturnType;

    private int localOffset;

    private int returnOpcode;

    private int currentLine;

    private int isNonStatic;

    private List<List<String>> locals = new ArrayList<>();

    /* ------------------------------------------------------------ */

    public MethodInjector(ClassNode injectorClass, MethodNode method, List<InjectionPoint> injectionPoints, int priority)
    {
        this.injectorClass = injectorClass;
        this.injector = method;
        this.injectionPoints = injectionPoints;
        this.priority = priority;
        this.isNonStatic = ASMUtil.isStatic(injectorClass.access) ? 0 : 1;

        injectorReturnType = Type.getReturnType(injector.desc);
        if (!injectorReturnType.equals(Type.VOID_TYPE))
            throw new IllegalInjectorException("Can only use void methods as injector");

        injectorArguments = Type.getArgumentTypes(injector.desc);
        injectorArgumentCount = injectorArguments.length + isNonStatic;

        targetArgumentCount = 0;
        injectedLocalsCount = 0;
        callbackInfoIndex = -1;
        boolean isLocal = false;
        for (int i = 0; i < injectorArguments.length; i++)
        {
            Type type = injectorArguments[i];
            if (isLocal)
            {
                if (injector.visibleParameterAnnotations == null)
                    throw new IllegalInjectorException("@Local parameter expected");
                AnnotationNode aLocal = ASMUtil.getAnnotation(injector.visibleParameterAnnotations[i], Type.getDescriptor(Local.class));
                if (aLocal == null)
                    throw new IllegalInjectorException("@Local parameter expected");

                // TODO: Cache @Local info
                List<String> localAliases = ASMUtil.getAnnotationValue(aLocal, "value");
                localAliases = localAliases == null ? new ArrayList<String>() : new ArrayList<>(localAliases);
                localAliases.add(injector.localVariables.get(i + isNonStatic).name);
                locals.add(localAliases);

                injectedLocalsCount++;
            }
            else
            {
                if (type.getClassName().equals(CI_NAME))
                {
                    callbackInfoIndex = i + isNonStatic;
                    isLocal = true;
                }
                else
                    targetArgumentCount++;
            }
        }
    }

    public synchronized boolean inject(ClassNode targetClass, MethodNode target)
    {
        try
        {
            this.targetClass = targetClass;
            boolean modified = false;
            for (InjectionPoint injectionPoint : injectionPoints)
            {
                // TODO: Should search for injection points of ALL injectors BEFORE starting first injection!
                List<AbstractInsnNode> points = injectionPoint.find(target);
                if (points == null || points.isEmpty())
                {
                    log.error(String.format("Could not find injection point %s in %s.%s",
                            injectionPoint.toString(), ASMUtil.substringAfterLast(ASMUtil.javaName(targetClass.name), "."), target.name));
                    continue;
                }
                for (AbstractInsnNode point : points)
                {
                    if (point != null) {
                        log.info(String.format("Injecting %s.%s into %s.%s at %s",
                                ASMUtil.substringAfterLast(ASMUtil.javaName(injectorClass.name), "."), injector.name,
                                ASMUtil.substringAfterLast(ASMUtil.javaName(targetClass.name), "."), target.name, injectionPoint.toString()));
                        modified |= inject(target, point);
                    }
                }
            }
            return modified;
        }
        catch (RuntimeException e)
        {
            log.error("RuntimeException Encountered During Injection", e);
            throw new MethodInjectionError(this, e);
        }
    }

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
    public synchronized boolean inject(MethodNode target, AbstractInsnNode insertPoint)
    {
        this.targetMethod = target;
        this.insertPoint = insertPoint;
        this.currentLine = 0;

        targetArguments = Type.getArgumentTypes(target.desc);
        if (targetArguments.length != targetArgumentCount)
            throw new InjectionException(
                    String.format("Incorrect number of arguments in injector for target method %s.%s%s", targetClass.name, target.name, target.desc));

        targetReturnType = Type.getReturnType(target.desc);
        returnOpcode = ASMUtil.getReturnOpcodeFromType(targetReturnType);

        // Adjust available space for local variables
        localOffset = target.maxLocals - injectorArgumentCount;
        target.maxLocals += injector.maxLocals - injectorArgumentCount;

        // TODO: Detect local variables

        createLabelMappings();
        // Loop through all instructions in the source method and insert them into the target
        for (AbstractInsnNode i = injector.instructions.getFirst(); i != null; i = i.getNext())
        {
            if (callbackInfoIndex >= 0 && ASMUtil.isVarInsNode(i, Opcodes.ALOAD, callbackInfoIndex))
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
                        throw new InjectionException("Illegal return statement");
                    break;
                default:
                    inject(i);
                    break;
                }
            }
        }
        return true;
    }

    /**
     * Inject cloned instruction into target method
     * 
     * @param instruction
     */
    private synchronized void inject(AbstractInsnNode instruction)
    {
        // Clone instruction if necessary
        if (instruction.getNext() != null || instruction.getPrevious() != null)
            instruction = instruction.clone(labelMappings);

        if (instruction instanceof LineNumberNode)
        {
            LineNumberNode lln = (LineNumberNode) instruction;
            currentLine = lln.line;
        }
        else if (instruction instanceof FieldInsnNode)
        {
            FieldInsnNode fn = (FieldInsnNode) instruction;
            if (fn.owner.equals(injectorClass.name))
            {
                handleFieldShadowing(fn);
            }
        }
        else if (instruction instanceof MethodInsnNode)
        {
            MethodInsnNode mn = (MethodInsnNode) instruction;
            if (mn.owner.equals(injectorClass.name))
            {
                handleMethodShadowing(mn);
            }
        }
        else if (instruction instanceof VarInsnNode)
        {
            VarInsnNode vn = (VarInsnNode) instruction;
            TypeAnnotationNode aShadow = ASMUtil.getTypeAnnotation(vn.visibleTypeAnnotations, Type.getDescriptor(Shadow.class));
            if (aShadow != null)
            {
                // TODO: Check for shadowed local variable
                LocalVariableNode srcVar = injector.localVariables.get(vn.var);
                LocalVariableNode dstVar = targetMethod.localVariables.get(vn.var);
                if (srcVar == dstVar)
                    return;
            }
            else
            {
                if (vn.var >= injectorArgumentCount - injectedLocalsCount && vn.var < injectorArgumentCount)
                {
                    // We are trying to access an injected local variable
                    List<String> localNames = locals.get(vn.var - injectorArgumentCount + injectedLocalsCount);
                    LocalVariableNode varNode = null;
                    namesLoop: for (String varName : localNames)
                    {
                        for (LocalVariableNode lvn : targetMethod.localVariables)
                        {
                            if (lvn.name.equals(varName))
                            {
                                varNode = lvn;
                                break namesLoop;
                            }
                        }
                    }
                    if (varNode == null)
                    {
                        String message = String.format("Could not find local variable [%s]", StringUtils.join(localNames, ", "));
                        System.err.println(message);
                        System.err.println("Found local variables:");
                        for (LocalVariableNode lvn : targetMethod.localVariables)
                            System.err.println(String.format("  %s: %s", lvn.name, lvn.desc));
                        throw new InjectionException(message);
                    }
                    vn.var = varNode.index;
                }
                else if (vn.var >= injectorArgumentCount)
                {
                    // We are trying to access a local variable from the injector, so please offset it
                    vn.var += localOffset;
                }
            }
            targetMethod.localVariables.size();
        }
        targetMethod.instructions.insertBefore(insertPoint, instruction);
    }

    private synchronized void handleFieldShadowing(FieldInsnNode fieldNode)
    {
        FieldNode field = ASMUtil.findField(injectorClass, fieldNode.name);
        AnnotationNode aShadow = ASMUtil.getAnnotation(field.visibleAnnotations, Type.getDescriptor(Shadow.class));
        if (aShadow == null)
            throw new InjectionException("Injecting non-shadowed fields is not supported yet");
        List<String> names = ASMUtil.getAnnotationValue(aShadow, "value");
        if (names == null)
            names = new ArrayList<>();
        names.add(0, field.name);

        try
        {
            // Search original field
            ClassNode originalClass = targetClass;
            FieldNode originalField = null;
            classLoop: while (originalField == null && originalClass != null)
            {
                for (String alias : names)
                {
                    originalField = ASMUtil.findField(originalClass, alias);
                    if (originalField != null)
                        break classLoop;
                }
                originalClass = ASMUtil.getClassNode(ASMUtil.javaName(originalClass.superName));
            }
            if (originalField == null)
                throw new InjectionException(String.format("Could not find shadowed field %s in target class hierachy", field.name));

            // Check if original field is private and was accessed in a parent class
            if (ASMUtil.isPrivate(originalField.access) && originalClass != targetClass)
                throw new InjectionException(String.format("Illegal access to shadowed private parent field %s.%s", originalClass.name, field.name));

            // Check if shadowed field is of the same type as original field
            if (!originalField.desc.equals(field.desc))
                throw new InjectionException(String.format("Type %s of shadowed field %s does not match original field type %s",
                        Type.getType(field.desc).getClassName(), field.name, Type.getType(originalField.desc).getClassName()));

            // Remap field access
            fieldNode.owner = originalClass.name;
            fieldNode.name = originalField.name;
        }
        catch (ClassNotFoundException e)
        {
            throw new InjectionException(String.format("Unable to find shadowed field %s", field.name), e);
        }
    }

    private synchronized void handleMethodShadowing(MethodInsnNode methodNode)
    {
        MethodNode method = ASMUtil.findMethod(injectorClass, methodNode.name, methodNode.desc);
        AnnotationNode aShadow = ASMUtil.getAnnotation(method.visibleAnnotations, Type.getDescriptor(Shadow.class));
        if (aShadow == null)
            throw new InjectionException("Injecting non-shadowed methods is not supported yet");
        List<String> names = ASMUtil.getAnnotationValue(aShadow, "value");
        if (names == null)
            names = new ArrayList<>();
        names.add(0, method.name);

        try
        {
            // Search original field
            ClassNode originalClass = targetClass;
            MethodNode originalMethod = null;
            classLoop: while (originalMethod == null && originalClass != null)
            {
                for (String alias : names)
                {
                    originalMethod = ASMUtil.findMethod(originalClass, alias, methodNode.desc);
                    if (originalMethod != null)
                        break classLoop;
                }
                originalClass = ASMUtil.getClassNode(ASMUtil.javaName(originalClass.superName));
            }
            if (originalMethod == null)
                throw new InjectionException(String.format("Could not find shadowed method %s in target class hierachy", method.name));

            // Check if original field is private and was accessed in a parent class
            if (ASMUtil.isPrivate(originalMethod.access) && originalClass != targetClass)
                throw new InjectionException(String.format("Illegal access to shadowed private parent method %s.%s", originalClass.name, method.name));

            // Check if shadowed field is of the same type as original field
            if (!originalMethod.desc.equals(method.desc))
                throw new InjectionException(String.format("Type %s of shadowed method %s does not match original field type %s",
                        Type.getType(method.desc).getClassName(), method.name, Type.getType(originalMethod.desc).getClassName()));

            // Remap field access
            methodNode.owner = originalClass.name;
            methodNode.name = originalMethod.name;
        }
        catch (ClassNotFoundException e)
        {
            throw new InjectionException(String.format("Unable to find shadowed method %s", method.name), e);
        }
    }

    private synchronized AbstractInsnNode handleReturn(AbstractInsnNode start)
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
                        throw new InjectionException("Unexpected call to CallbackInfo.%s");

                    // Validate that the correct return method was called, i.e. that the return value has the correct type
                    Type[] returnTypeLst = Type.getArgumentTypes(returnNode.desc);
                    Type returnType = returnTypeLst.length == 0 ? Type.VOID_TYPE : returnTypeLst[0];
                    if (!returnType.equals(targetReturnType))
                        throw new InjectionException(String.format("Invalid return type %s", returnType.getClassName()));

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
        throw new InjectionException("Unexpected end of method");
    }

    /**
     * Creates new LabelNode instances for mapping labels during injection
     */
    private synchronized void createLabelMappings()
    {
        labelMappings = new HashMap<>();
        for (AbstractInsnNode i = injector.instructions.getFirst(); i != null; i = i.getNext())
            if (i instanceof LabelNode)
                labelMappings.put((LabelNode) i, new LabelNode());
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public static class MethodInjectionError extends RuntimeException
    {

        public MethodInjectionError(ClassNode src, MethodNode srcMethod, ClassNode dst, MethodNode dstMethod, Throwable cause)
        {
            super(String.format("Error injecting %s.%s(%s.java) into %s.%s", ASMUtil.javaName(src.name), srcMethod.name,
                    ASMUtil.substringAfterLast(ASMUtil.javaName(src.name), "."), ASMUtil.javaName(dst.name), dstMethod.name), cause);
        }

        public MethodInjectionError(ClassNode src, MethodNode srcMethod, ClassNode dst, MethodNode dstMethod, int line, Throwable cause)
        {
            super(String.format("Error injecting %s.%s(%s.java:%d) into %s.%s", ASMUtil.javaName(src.name), srcMethod.name,
                    ASMUtil.substringAfterLast(ASMUtil.javaName(src.name), "."), line, ASMUtil.javaName(dst.name), dstMethod.name), cause);
        }

        public MethodInjectionError(MethodInjector injector, Throwable cause)
        {
            this(injector.injectorClass, injector.injector, injector.targetClass, injector.targetMethod, injector.currentLine, cause);
        }

    }

    @Override
    public int compareTo(MethodInjector other)
    {
        if (this == other)
            return 0;
        int order = other.priority - priority;
        if (order != 0)
            return order;
        return System.identityHashCode(this) - System.identityHashCode(other);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((injectionPoints == null) ? 0 : injectionPoints.hashCode());
        result = prime * result + ((injector == null) ? 0 : injector.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodInjector other = (MethodInjector) obj;
        if (injectionPoints == null)
        {
            if (other.injectionPoints != null)
                return false;
        }
        else if (!injectionPoints.equals(other.injectionPoints))
            return false;
        if (injector == null)
        {
            if (other.injector != null)
                return false;
        }
        else if (!injector.equals(other.injector))
            return false;
        return true;
    }

}