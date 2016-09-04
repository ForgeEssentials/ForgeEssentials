package com.forgeessentials.jscripting;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.io.IOUtils;

import com.forgeessentials.jscripting.fewrapper.fe.JsFEServer;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.JsWindow;
import com.forgeessentials.jscripting.wrapper.mc.event.JsEvent;
import com.forgeessentials.jscripting.wrapper.mc.item.JsItem;
import com.forgeessentials.jscripting.fewrapper.fe.JsPermissions;
import com.forgeessentials.jscripting.wrapper.mc.JsServer;
import com.forgeessentials.jscripting.wrapper.mc.world.JsBlock;
import com.forgeessentials.jscripting.wrapper.mc.world.JsWorld;
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

        registerPackageClasses("com.forgeessentials.jscripting.wrapper");
        registerPackageClasses("com.forgeessentials.jscripting.fewrapper");
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

    public static void registerPackageClasses(String packageBase)
    {
        try
        {
            ImmutableSet<ClassInfo> classes = ClassPath.from(ScriptInstance.class.getClassLoader()).getTopLevelClassesRecursive(packageBase);
            for (ClassInfo classInfo : classes)
            {
                registerWrapperClass(classInfo, packageBase);
            }
        }
        catch (IOException e)
        {
            Throwables.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerWrapperClass(ClassInfo classInfo, String packageBase)
    {
        if (!classInfo.getSimpleName().startsWith("Js") || classInfo.getName().equals(JsWrapper.class.getName()))
            return;
        Class<?> clazz = classInfo.load();
        // if (!JsWrapper.class.isAssignableFrom(clazz))
        // return;

        String jsName = classInfo.getName().substring(packageBase.length() + 1);

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
        for (Entry<String, Object> pkg : rootPkg.entrySet())
            engine.put(pkg.getKey(), pkg.getValue());

        engine.put("window", new JsWindow(script));
        engine.put("Server", new JsServer(script));
        engine.put("Block", toNashornClass(JsBlock.class));
        engine.put("Item", toNashornClass(JsItem.class));
        engine.put("World", toNashornClass(JsWorld.class));

        engine.put("Permissions", toNashornClass(JsPermissions.class));
        engine.put("FEServer", new JsFEServer(script));

        //        try
        //        {
        //            INIT_SCRIPT = IOUtils.toString(ScriptInstance.class.getResource("init.js")); // TODO: DEV ONLY: REALOD OF INIT SCRIPT
        //        }
        //        catch (IOException e)
        //        {
        //            e.printStackTrace();
        //        }
        engine.eval(ScriptCompiler.INIT_SCRIPT);
    }
}
