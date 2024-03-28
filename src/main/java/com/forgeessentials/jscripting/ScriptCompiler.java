package com.forgeessentials.jscripting;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.event.JsEvent;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import jdk.dynalink.beans.StaticClass;

public final class ScriptCompiler
{

    public static final String WRAPPER_PACKAGE = "com.forgeessentials.jscripting.wrapper";

    // private static String INIT_SCRIPT;

    // private static CompiledScript initScript;

    public static Map<String, Class<? extends JsEvent<?>>> eventTypes = new HashMap<>();

    private static SimpleBindings rootPkg = new SimpleBindings();

    private static List<ScriptExtension> extensions = new ArrayList<>();

    public static Object toNashornClass(Class<?> c)
    {
            return StaticClass.forClass(c);
    }

    private static void registerPackageClasses(String packageBase)
    {
        try
        {
            ImmutableSet<ClassInfo> classes = ClassPath.from(ScriptInstance.class.getClassLoader())
                    .getTopLevelClassesRecursive(packageBase);
            for (ClassInfo classInfo : classes)
            {
                registerWrapperClass(classInfo, packageBase);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void registerWrapperClass(ClassInfo classInfo, String packageBase)
    {
        if (!classInfo.getSimpleName().startsWith("Js") || classInfo.getName().equals(JsWrapper.class.getName()))
            return;
        registerWrapperClass(classInfo.load(), packageBase);
    }

    public static void registerWrapperClass(Class<?> clazz, String packageBase)
    {
        String jsName = clazz.getName();
        if (packageBase != null && !packageBase.isEmpty())
            jsName = jsName.substring(packageBase.length() + 1);

        String[] jsNameParts = jsName.split("\\.");
        SimpleBindings pkg = rootPkg;
        for (int i = 0; i < jsNameParts.length - 1; i++)
        {
            String name = jsNameParts[i];
            SimpleBindings parentPkg = pkg;
            pkg = (SimpleBindings) parentPkg.get(name);
            if (pkg == null)
            {
                pkg = new SimpleBindings();
                parentPkg.put(name, pkg);
            }
        }

        String className = jsNameParts[jsNameParts.length - 1];
        if (className.startsWith("Js"))
            className = className.substring(2);

        pkg.put(className, toNashornClass(clazz));

        // Check for event handlers
        try
        {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods)
            {
                if (method.getParameterCount() == 1 && Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                {
                    SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                    if (annotation != null)
                    {
                        eventTypes.put(clazz.getSimpleName().substring(2), (Class<? extends JsEvent<?>>) clazz);
                        break;
                    }
                }
            }
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void initEngine(ScriptEngine engine, ScriptInstance script) throws ScriptException
    {
        for (Entry<String, Object> pkg : rootPkg.entrySet())
            engine.put(pkg.getKey(), pkg.getValue());
        for (ScriptExtension extension : extensions)
            extension.initEngine(engine, script);
    }

    public static void registerExtension(ScriptExtension extension)
    {
        extensions.add(extension);
        registerPackageClasses(extension.getClass().getPackage().getName());
    }

}
