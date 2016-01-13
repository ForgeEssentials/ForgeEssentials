package com.forgeessentials.core.preloader.asminjector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.forgeessentials.core.preloader.asminjector.ASMUtil.IllegalInjectorException;
import com.forgeessentials.core.preloader.asminjector.annotation.At.Shift;

public abstract class InjectionPoint
{

    public static Map<String, Class<? extends InjectionPoint>> injectionPointTypes = new HashMap<>();

    static
    {
        registerInjectionPointType(Head.class);
        registerInjectionPointType(Tail.class);
        registerInjectionPointType(Invoke.class);
    }

    private static void registerInjectionPointType(Class<? extends InjectionPoint> clazz)
    {
        injectionPointTypes.put(clazz.getSimpleName().toUpperCase(), clazz);
    }

    public static InjectionPoint parse(String type, String target, Shift shift, Integer by, Integer ordinal)
    {
        if (by == null)
            by = 0;
        if (ordinal == null)
            ordinal = -1;
        if (shift == null)
            shift = Shift.BY;
        type = type.toUpperCase();
        Class<? extends InjectionPoint> t = injectionPointTypes.get(type);
        if (t == null)
            throw new IllegalInjectorException(String.format("Unknown InjectionPoint type %s", type));
        try
        {
            Constructor<? extends InjectionPoint> constructor = t.getConstructor(String.class, int.class);
            InjectionPoint ip = constructor.newInstance(target, ordinal);
            if (shift != Shift.BY || by != 0)
            {
                ip = new ShiftPoint(ip, shift, by);
            }
            return ip;
        }
        catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e)
        {
            throw new IllegalInjectorException(String.format("Error creating InjectionPoint of type type %s", type), e);
        }
    }

    /* ------------------------------------------------------------ */

    public abstract List<AbstractInsnNode> find(MethodNode method);

    @Override
    public String toString()
    {
        return getClass().getSimpleName().toUpperCase();
    }

    /* ------------------------------------------------------------ */

    public static class ShiftPoint extends InjectionPoint
    {

        private InjectionPoint source;

        private Shift shift;

        private int by;

        public ShiftPoint(InjectionPoint source, Shift shift, int by)
        {
            this.source = source;
            this.shift = shift;
            this.by = by;
        }

        @Override
        public List<AbstractInsnNode> find(MethodNode method)
        {
            List<AbstractInsnNode> points = source.find(method);
            if (points == null)
                return null;
            List<AbstractInsnNode> result = new ArrayList<>();
            for (AbstractInsnNode node : points)
            {
                node = shift.doShift(node, by);
                if (node != null)
                    result.add(node);
            }
            return result;
        }

        @Override
        public String toString()
        {
            return String.format("SHIFT[src = %s, shift = %s, by = %d]", source.toString(), shift.toString(), by);
        }

    }

    /* ------------------------------------------------------------ */

    public static class Head extends InjectionPoint
    {

        public Head(String target, int ordinal)
        {
        }

        @Override
        public List<AbstractInsnNode> find(MethodNode method)
        {
            return Arrays.asList(method.instructions.getFirst());
        }

    }

    public static class Tail extends InjectionPoint
    {

        public Tail(String target, int ordinal)
        {
        }

        @Override
        @SuppressWarnings("unused")
        public List<AbstractInsnNode> find(MethodNode method)
        {
            AbstractInsnNode last = method.instructions.getLast();
            while (last != null && last.getOpcode() < Opcodes.IRETURN || last.getOpcode() > Opcodes.RETURN)
                last = last.getPrevious();
            if (last == null)
                return null;
            return Arrays.asList(last);
        }

    }

    /* ------------------------------------------------------------ */

    public static class Invoke extends InjectionPoint
    {

        private String targetClass;

        private String targetName;

        private String targetDesc;

        private int ordinal;

        public Invoke(String target, int ordinal)
        {
            String[] parts = target.split("\\(");
            if (parts.length != 2)
                throw new IllegalInjectorException(String.format("Invalid target descriptor %s", target));

            int semIdx = parts[0].indexOf(';');
            if (semIdx < 0)
                throw new IllegalInjectorException(String.format("Invalid target descriptor %s", target));

            this.targetClass = Type.getType(parts[0].substring(0, semIdx + 1)).getInternalName();
            this.targetName = parts[0].substring(semIdx + 1);
            this.targetDesc = '(' + parts[1];
            this.ordinal = ordinal;
        }

        @Override
        public List<AbstractInsnNode> find(MethodNode method)
        {
            List<AbstractInsnNode> result = new ArrayList<>();
            for (AbstractInsnNode i = method.instructions.getFirst(); i != null; i = i.getNext())
            {
                if (i instanceof MethodInsnNode)
                {
                    if (matches((MethodInsnNode) i))
                    {
                        result.add(i);
                    }
                }
            }
            if (ordinal >= 0)
            {
                if (ordinal >= result.size())
                    return null;
                AbstractInsnNode element = result.get(ordinal);
                result.clear();
                result.add(element);
            }
            return result.isEmpty() ? null : result;
        }

        private boolean matches(MethodInsnNode node)
        {
            if (!targetClass.equals("*") && !targetClass.equals(node.owner))
                return false;
            if (!targetName.equals("*") && !targetName.equals(node.name))
                return false;
            if (!targetDesc.equals("*") && !targetDesc.equals(node.desc))
                return false;
            return true;
        }

        @Override
        public String toString()
        {
            return String.format("INVOKE[c = %s, n = %s, d = %s, idx = %d]", targetClass, targetName, targetDesc, ordinal);
        }

    }

}
