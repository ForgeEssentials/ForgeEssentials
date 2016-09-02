package com.forgeessentials.jscripting;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.jscripting.wrapper.JsWindowStatic;
import com.forgeessentials.jscripting.wrapper.event.JsEvent;
import com.forgeessentials.jscripting.wrapper.item.JsItemStatic;
import com.forgeessentials.jscripting.wrapper.server.JsPermissionsStatic;
import com.forgeessentials.jscripting.wrapper.server.JsServerStatic;
import com.forgeessentials.jscripting.wrapper.world.JsBlockStatic;
import com.forgeessentials.jscripting.wrapper.world.JsWorldStatic;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class ScriptCompiler
{

    public static final String WRAPPER_PACKAGE = "com.forgeessentials.jscripting.wrapper";

    private static String INIT_SCRIPT;

    @SuppressWarnings("unused")
    private static CompiledScript initScript;

    @SuppressWarnings("rawtypes")
    static Map<String, Class<? extends JsEvent>> eventTypes = new HashMap<>();

    private static SimpleBindings rootPkg = new SimpleBindings();

    private static PermissionLevelObj permissionLevelObj = new PermissionLevelObj();

    static
    {
        try
        {
            INIT_SCRIPT = IOUtils.toString(ScriptInstance.class.getResource("init.js"));
            ScriptCompiler.initScript = ModuleJScripting.getCompilable().compile(INIT_SCRIPT);
        }
        catch (IOException | ScriptException e)
        {
            Throwables.propagate(e);
        }
        try
        {
            ImmutableSet<ClassInfo> classes = ClassPath.from(ScriptInstance.class.getClassLoader()).getTopLevelClassesRecursive(WRAPPER_PACKAGE);
            for (ClassInfo classInfo : classes)
            {
                registerWrapperClass(classInfo);
            }
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
        }
    }

    public static class PermissionLevelObj
    {
        public PermissionLevel TRUE = PermissionLevel.TRUE;
        public PermissionLevel OP = PermissionLevel.OP;
        public PermissionLevel FALSE = PermissionLevel.FALSE;
    }

    public static Object toNashornClass(Class<?> c)
    {
        try
        {
            Class<?> cl = Class.forName("jdk.internal.dynalink.beans.StaticClass", true, ClassLoader.getSystemClassLoader());
            Constructor<?> constructor = cl.getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return constructor.newInstance(c);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e)
        {
            throw Throwables.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerWrapperClass(ClassInfo classInfo)
    {
        if (!classInfo.getSimpleName().startsWith("Js"))
            return;
        Class<?> clazz = classInfo.load();
        // if (!JsWrapper.class.isAssignableFrom(clazz))
        // return;

        String jsName = classInfo.getName().substring(WRAPPER_PACKAGE.length() + 1);
        String[] jsNameParts = jsName.split("\\.");
        SimpleBindings pkg = rootPkg;
        for (int i = 0; i < jsNameParts.length - 1; i++)
        {
            String name = StringUtils.capitalize(jsNameParts[i]);
            SimpleBindings parentPkg = pkg;
            pkg = (SimpleBindings) parentPkg.get(name);
            if (pkg == null)
            {
                pkg = new SimpleBindings();
                parentPkg.put(name, pkg);
            }
        }
        pkg.put(jsNameParts[jsNameParts.length - 1].substring(2), toNashornClass(clazz));

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
            throw Throwables.propagate(e);
        }
    }

    public static void initEngine(ScriptEngine engine, ScriptInstance script) throws ScriptException
    {
        engine.put("PermissionLevel", permissionLevelObj);
        engine.put("mc", rootPkg);

        engine.put("window", new JsWindowStatic(script));
        engine.put("Server", new JsServerStatic(script));
        engine.put("Block", new JsBlockStatic());
        engine.put("Item", new JsItemStatic());
        engine.put("World", new JsWorldStatic());
        engine.put("Permissions", new JsPermissionsStatic());

        // INIT_SCRIPT = IOUtils.toString(ScriptInstance.class.getResource("init.js")); // TODO: DEV ONLY REALOD OF INIT SCRIPT
        engine.eval(ScriptCompiler.INIT_SCRIPT);
    }
}
