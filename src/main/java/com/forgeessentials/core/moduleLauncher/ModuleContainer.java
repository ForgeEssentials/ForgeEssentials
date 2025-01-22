package com.forgeessentials.core.moduleLauncher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule.Container;
import com.forgeessentials.core.moduleLauncher.FEModule.Instance;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.core.moduleLauncher.FEModule.ParentMod;
import com.forgeessentials.core.moduleLauncher.FEModule.Preconditions;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.base.Throwables;

@SuppressWarnings("rawtypes")
public class ModuleContainer implements Comparable
{

    protected static HashSet<Class> modClasses = new HashSet<Class>();

    public Object module, mod;

    // methods..
    private String reload;

    // fields
    private String instance, container, parentMod, moduleDir;

    // other vars..
    public final String className;
    public final String name;
    private final boolean isCore;
    public boolean isLoadable = true;
    protected boolean doesOverride;

    @SuppressWarnings("unchecked")
    public ModuleContainer(ASMData data)
    {
        // get the class....
        Class c = null;
        className = data.getClassName();

        LoggingHandler.felog.debug("Loading Module: " + className);
        try
        {
            c = Class.forName(className);
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.info("Error trying to load " + data.getClassName() + " as a FEModule!");
            e.printStackTrace();

            isCore = false;
            name = "INVALID-MODULE";
            return;
        }

        // checks original FEModule annotation.
        if (!c.isAnnotationPresent(FEModule.class))
        {
            throw new IllegalArgumentException(c.getName() + " doesn't have the @FEModule annotation!");
        }
        FEModule annot = (FEModule) c.getAnnotation(FEModule.class);
        if (annot == null)
        {
            throw new IllegalArgumentException(c.getName() + " doesn't have the @FEModule annotation!");
        }
        name = annot.name();
        isCore = annot.isCore();
        doesOverride = annot.doesOverride();

        if (annot.canDisable())
        {
            if (!ForgeEssentials.getConfigManager().getMainConfig().get("Core.Modules", name, annot.defaultModule()).getBoolean(true))
            {
                LoggingHandler.felog.info("Requested to disable module " + name);
                isLoadable = false;
                return;
            }
        }

        // try getting the parent mod.. and register it.
        mod = handleMod(annot.parentMod());

        // check method annotations. they are all optional...
        Class[] params;
        for (Method m : c.getDeclaredMethods())
        {
            if (m.isAnnotationPresent(Preconditions.class))
            {
                LoggingHandler.felog.debug("Checking Preconditions... " + m.getName());
                params = m.getParameterTypes();
                if (params.length != 0)
                {
                    throw new RuntimeException(m + " must take no arguments!");
                }
                if (!m.getReturnType().equals(boolean.class))
                {
                    throw new RuntimeException(m + " must return a boolean!");
                }
                m.setAccessible(true);

                try
                {
                    Object inst = c.newInstance();
                    if (!(boolean) m.invoke(inst))
                    {
                        LoggingHandler.felog.debug("Disabled module " + name);
                        isLoadable = false;
                        APIRegistry.getFEEventBus().unregister(inst);
                        return;
                    }
                }
                catch (InstantiationException e)
                {
                }
                catch (IllegalAccessException e)
                {
                }
                catch (InvocationTargetException e)
                {
                }
            }
        }

        // collect field annotations... these are also optional.
        for (Field f : c.getDeclaredFields())
        {
            if (f.isAnnotationPresent(Instance.class))
            {
                if (instance != null)
                    throw new RuntimeException("Only one field may be marked as Instance");
                f.setAccessible(true);
                instance = f.getName();
            }
            else if (f.isAnnotationPresent(Container.class))
            {
                if (container != null)
                    throw new RuntimeException("Only one field may be marked as Container");
                if (f.getType().equals(ModuleContainer.class))
                    throw new RuntimeException("This field must have the type ModuleContainer!");
                f.setAccessible(true);
                container = f.getName();
            }
            else if (f.isAnnotationPresent(ParentMod.class))
            {
                if (parentMod != null)
                    throw new RuntimeException("Only one field may be marked as ParentMod");
                f.setAccessible(true);
                parentMod = f.getName();
            }
            else if (f.isAnnotationPresent(ModuleDir.class))
            {
                if (moduleDir != null)
                    throw new RuntimeException("Only one field may be marked as ModuleDir");
                if (!File.class.isAssignableFrom(f.getType()))
                    throw new RuntimeException("This field must be the type File!");
                f.setAccessible(true);
                moduleDir = f.getName();
            }
        }
    }

    protected void createAndPopulate()
    {
        Field f;
        Class c;
        // instantiate.
        try
        {
            c = Class.forName(className);
            module = c.newInstance();
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.warn(name + " could not be instantiated. FE will not load this module.");
            e.printStackTrace();
            isLoadable = false;
            return;
        }

        APIRegistry.getFEEventBus().register(module);

        // now for the fields...
        try
        {
            if (instance != null)
            {
                f = c.getDeclaredField(instance);
                f.setAccessible(true);
                f.set(module, module);
            }

            if (container != null)
            {
                f = c.getDeclaredField(container);
                f.setAccessible(true);
                f.set(module, this);
            }

            if (parentMod != null)
            {
                f = c.getDeclaredField(parentMod);
                f.setAccessible(true);
                f.set(module, mod);
            }

            if (moduleDir != null)
            {
                File file = new File(ForgeEssentials.getFEDirectory(), name);
                file.mkdirs();

                f = c.getDeclaredField(moduleDir);
                f.setAccessible(true);
                f.set(module, file);
            }
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.info("Error populating fields of " + name);
            Throwables.propagate(e);
        }
    }

    public void runReload(ICommandSender user)
    {
        if (!isLoadable || reload == null)
        {
            return;
        }

        try
        {
            Class<?> c = Class.forName(className);
            Method m = c.getDeclaredMethod(reload, new Class<?>[] { ICommandSender.class });
            m.invoke(module, user);
        }
        catch (Throwable e)
        {
            LoggingHandler.felog.info("Error while invoking Reload method for " + name);
            Throwables.propagate(e);
        }
    }

    public File getModuleDir()
    {
        return new File(ForgeEssentials.getFEDirectory(), name);
    }

    @Override
    public int compareTo(Object o)
    {
        if (!(o instanceof ModuleContainer))
            return -1;

        ModuleContainer other = (ModuleContainer) o;

        if (equals(other))
        {
            return 0;
        }

        if (isCore && !other.isCore)
        {
            return 1;
        }
        else if (!isCore && other.isCore)
        {
            return -1;
        }

        return name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ModuleContainer))
        {
            return false;
        }

        ModuleContainer c = (ModuleContainer) o;

        return isCore == c.isCore && name.equals(c.name) && className.equals(c.className);
    }

    @Override
    public int hashCode()
    {
        return (11 + name.hashCode()) * 29 + className.hashCode();
    }

    private static Object handleMod(Class modClass)
    {
        String modid;
        Object obj = null;

        ModContainer contain = null;
        for (ModContainer c : Loader.instance().getModList())
        {
            if (c.getMod() != null && c.getMod().getClass().equals(modClass))
            {
                contain = c;
                obj = c.getMod();
                break;
            }
        }

        if (obj == null || contain == null)
            throw new RuntimeException(modClass + " isn't an loaded mod class!");

        modid = contain.getModId() + "-" + contain.getVersion();
        if (modClasses.add(modClass))
            LoggingHandler.felog.info("Modules from " + modid + " are being loaded");
        return obj;
    }
}
