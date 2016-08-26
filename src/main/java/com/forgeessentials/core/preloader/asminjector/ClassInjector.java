package com.forgeessentials.core.preloader.asminjector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.forgeessentials.core.preloader.asminjector.ASMUtil.IllegalInjectorException;
import com.forgeessentials.core.preloader.asminjector.annotation.At.Shift;
import com.forgeessentials.core.preloader.asminjector.annotation.Inject;
import com.forgeessentials.core.preloader.asminjector.annotation.Mixin;
import com.google.common.base.Throwables;

public class ClassInjector
{

    public static final Logger log = LogManager.getLogger("ASM_ClassInjector");

    protected ClassNode injectorClass;

    protected List<String> classes = new ArrayList<>();

    protected List<String> excludedClasses = new ArrayList<>();

    protected Map<String, Set<MethodInjector>> injectors = new HashMap<>();

    /* ------------------------------------------------------------ */

    public ClassInjector(ClassNode classNode, boolean useAliases)
    {
        this.injectorClass = classNode;

        AnnotationNode aMain = ASMUtil.getAnnotation(classNode.visibleAnnotations, Type.getDescriptor(Mixin.class));
        if (aMain == null)
            throw new IllegalInjectorException("Missing @" + Mixin.class.getSimpleName() + " annotation");

        // Initialize class filter
        classes = ASMUtil.getAnnotationValue(aMain, "classNames");
        if (classes == null)
            classes = new ArrayList<>();

        // Append typed classes
        List<Type> classTypes = ASMUtil.getAnnotationValue(aMain, "value");
        if (classTypes != null)
            for (Type type : classTypes)
                classes.add(type.getClassName());

        // Initialize exclude filter
        List<Type> excludedClassTypes = ASMUtil.getAnnotationValue(aMain, "exclude");
        if (excludedClassTypes != null)
            for (Type type : excludedClassTypes)
                excludedClasses.add(type.getClassName());

        log.info(String.format("Scanning injector class %s", classNode.name));

        for (MethodNode methodNode : classNode.methods)
        {
            AnnotationNode aInject = ASMUtil.getAnnotation(methodNode.visibleAnnotations, Type.getDescriptor(Inject.class));
            if (aInject == null)
                continue;

            String injectTarget = ASMUtil.getAnnotationValue(aInject, "target");
            if (injectTarget == null)
                throw new RuntimeException("Missing method name");

            Integer priority = ASMUtil.getAnnotationValue(aInject, "priority");
            if (priority == null)
                priority = 0;

            Map<String, String> aliases;
            if (useAliases)
                aliases = ASMUtil.keyValueListToMap(ASMUtil.<List<String>> getAnnotationValue(aInject, "aliases"));
            else
                aliases = new HashMap<>();
            if (useAliases)
                injectTarget = replaceAliases(injectTarget, aliases);

            log.info(String.format("Found injector method %s => %s", methodNode.name, injectTarget));

            List<AnnotationNode> ats = ASMUtil.getAnnotationValue(aInject, "at");
            List<InjectionPoint> injectionPoints = parseInjectionPoints(ats, aliases);
            MethodInjector injector = new MethodInjector(injectorClass, methodNode, injectionPoints, priority);
            registerInjector(injectTarget, injector);
        }
    }

    private List<InjectionPoint> parseInjectionPoints(List<AnnotationNode> annotations, Map<String, String> aliases)
    {
        List<InjectionPoint> injectionPoints = new ArrayList<>();
        for (AnnotationNode annotation : annotations)
        {
            String type = ASMUtil.getAnnotationValue(annotation, "value");
            String target = replaceAliases(ASMUtil.<String> getAnnotationValue(annotation, "target"), aliases);
            Shift shift = Shift.fromAnnotation(ASMUtil.<String[]> getAnnotationValue(annotation, "shift"));
            Integer by = ASMUtil.getAnnotationValue(annotation, "by");
            Integer ordinal = ASMUtil.getAnnotationValue(annotation, "ordinal");
            InjectionPoint ip = InjectionPoint.parse(type, target, shift, by, ordinal);
            injectionPoints.add(ip);
        }
        return injectionPoints;
    }

    private String replaceAliases(String value, Map<String, String> aliases)
    {
        if (value == null)
            return null;
        for (Entry<String, String> alias : aliases.entrySet())
            value = value.replace(alias.getKey(), alias.getValue());
        return value;
    }

    public static ClassInjector create(String className, boolean useAliases)
    {
        try
        {
            ClassNode classNode = ASMUtil.getClassNode(className);
            return new ClassInjector(classNode, useAliases);
        }
        catch (ClassNotFoundException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /* ------------------------------------------------------------ */

    protected void registerInjector(String target, MethodInjector injector)
    {
        Set<MethodInjector> lst = injectors.get(target);
        if (lst == null)
        {
            lst = new TreeSet<>();
            injectors.put(target, lst);
        }
        lst.add(injector);
    }

    public Set<MethodInjector> getInjectors(String method, String desc)
    {
        return injectors.get(method + desc);
    }

    /* ------------------------------------------------------------ */

    public boolean inject(ClassNode target)
    {
        if (!handles(target.name))
            return false;

        boolean genericInject = classes.isEmpty();
        if (!genericInject)
            log.info(String.format("Starting injection into %s", ASMUtil.javaName(target.name)));

        Set<String> processedInjectors = new HashSet<>();

        boolean modified = false;
        for (MethodNode method : target.methods)
        {
            Set<MethodInjector> injectors = getInjectors(method.name, method.desc);
            if (injectors != null && !injectors.isEmpty())
            {
                processedInjectors.add(method.name + method.desc);
                for (MethodInjector injector : injectors)
                {
                    modified |= injector.inject(target, method);
                }
            }
        }

        if (!genericInject || modified)
        {
            boolean error = false;
            for (Entry<String, Set<MethodInjector>> injector : injectors.entrySet())
            {
                if (!processedInjectors.contains(injector.getKey()))
                {
                    error = true;
                    log.warn(String.format("Did not find target method %s", injector.getKey()));
                }
            }
            if (error)
            {
                log.warn(String.format("Methods in %s", ASMUtil.javaName(target.name)));
                for (MethodNode method : target.methods)
                    log.warn(String.format("> %s%s", method.name, method.desc));
            }
        }
        return modified;
    }

    public boolean handles(String className)
    {
        String normalizedName = ASMUtil.javaName(className);
        if (excludedClasses.contains(normalizedName))
            return false;
        if (classes.size() == 0)
            return true;
        for (String cls : classes)
            if (normalizedName.equals(cls))
                return true;
        return false;
    }

}