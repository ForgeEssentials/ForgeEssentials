package com.forgeessentials.core.moduleLauncher;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule.Config;
import com.forgeessentials.core.moduleLauncher.FEModule.Container;
import com.forgeessentials.core.moduleLauncher.FEModule.DummyConfig;
import com.forgeessentials.core.moduleLauncher.FEModule.Instance;
import com.forgeessentials.core.moduleLauncher.FEModule.ModuleDir;
import com.forgeessentials.core.moduleLauncher.FEModule.ParentMod;
import com.forgeessentials.core.moduleLauncher.FEModule.Reload;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.google.common.base.Throwables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraft.command.ICommandSender;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

@SuppressWarnings("rawtypes")
public class ModuleContainer implements Comparable {
    protected static HashSet<Class> modClasses = new HashSet<Class>();

    public Object module, mod;
    private ModuleConfigBase configObj;
    private Class<? extends ModuleConfigBase> configClass;

    // methods..
    private String reload;

    // fields
    private String instance, container, config, parentMod, moduleDir;

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

        try
        {
            c = Class.forName(className);
        }
        catch (Throwable e)
        {
            OutputHandler.felog.info("Error trying to load " + data.getClassName() + " as a FEModule!");
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
        configClass = annot.configClass();

        if (!ForgeEssentials.config.canLoadModule(name))
        {
            OutputHandler.felog.info("Requested to disable module " + name);
            isLoadable = false;
            return;
        }

        // try getting the parent mod.. and register it.
        {
            mod = handleMod(annot.parentMod());
        }

        // check method annotations. they are all optional...
        Class[] params;
        for (Method m : c.getDeclaredMethods())
        {
           if (m.isAnnotationPresent(Reload.class))
            {
                if (reload != null)
                {
                    throw new RuntimeException("Only one class may be marked as Reload");
                }
                params = m.getParameterTypes();
                if (params.length != 1)
                {
                    throw new RuntimeException(m + " may only have 1 argument!");
                }
                if (!params[0].equals(ICommandSender.class))
                {
                    throw new RuntimeException(m + " must take " + ICommandSender.class.getSimpleName() + " as a param!");
                }
                m.setAccessible(true);
                reload = m.getName();
            }
        }

        // collect field annotations... these are also optional.
        for (Field f : c.getDeclaredFields())
        {
            if (f.isAnnotationPresent(Instance.class))
            {
                if (instance != null)
                {
                    throw new RuntimeException("Only one field may be marked as Instance");
                }
                f.setAccessible(true);
                instance = f.getName();
            }
            else if (f.isAnnotationPresent(Container.class))
            {
                if (container != null)
                {
                    throw new RuntimeException("Only one field may be marked as Container");
                }
                if (f.getType().equals(ModuleContainer.class))
                {
                    throw new RuntimeException("This field must have the type ModuleContainer!");
                }
                f.setAccessible(true);
                container = f.getName();
            }
            else if (f.isAnnotationPresent(Config.class))
            {
                if (config != null)
                {
                    throw new RuntimeException("Only one field may be marked as Config");
                }
                if (!ModuleConfigBase.class.isAssignableFrom(f.getType()))
                {
                    throw new RuntimeException("This field must be the type ModuleConfigBase!");
                }
                f.setAccessible(true);
                config = f.getName();
            }
            else if (f.isAnnotationPresent(ParentMod.class))
            {
                if (parentMod != null)
                {
                    throw new RuntimeException("Only one field may be marked as ParentMod");
                }
                f.setAccessible(true);
                parentMod = f.getName();
            }
            else if (f.isAnnotationPresent(ModuleDir.class))
            {
                if (moduleDir != null)
                {
                    throw new RuntimeException("Only one field may be marked as ModuleDir");
                }
                if (!File.class.isAssignableFrom(f.getType()))
                {
                    throw new RuntimeException("This field must be the type File!");
                }
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
            OutputHandler.felog.warning(name + " could not be instantiated. FE will not load this module.");
            e.printStackTrace();
            isLoadable = false;
            return;
        }

        FunctionHelper.FE_INTERNAL_EVENTBUS.register(module);

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
                File file = new File(ForgeEssentials.FEDIR, name);
                file.mkdirs();

                f = c.getDeclaredField(moduleDir);
                f.setAccessible(true);
                f.set(module, file);
            }
        }
        catch (Throwable e)
        {
            OutputHandler.felog.info("Error populating fields of " + name);
            Throwables.propagate(e);
        }

        // now for the config..
        if (configClass.equals(DummyConfig.class))
        {
            OutputHandler.felog.info("No config specified for " + name);
            configObj = null;
            return;
        }

        try
        {
            configObj = configClass.getConstructor().newInstance();

            if (config != null)
            {
                f = c.getDeclaredField(config);
                f.setAccessible(true);
                f.set(module, configObj);
            }

        }
        catch (Throwable e)
        {
            OutputHandler.felog.info("Error Instantiating or populating config for " + name);
            Throwables.propagate(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void runReload(ICommandSender user)
    {
        if (!isLoadable || reload == null)
        {
            return;
        }

        try
        {
            Class c = Class.forName(className);
            Method m = c.getDeclaredMethod(reload, new Class[]
                    { ICommandSender.class });
            m.invoke(module, user);
        }
        catch (Throwable e)
        {
            OutputHandler.felog.info("Error while invoking Reload method for " + name);
            Throwables.propagate(e);
        }
    }

    public File getModuleDir()
    {
        return new File(ForgeEssentials.FEDIR, name);
    }

    /**
     * May be null if the module has no config
     *
     * @return
     */
    public ModuleConfigBase getConfig()
    {
        return configObj;
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

    private static Object handleMod(Class c)
    {
        String modid;
        Object obj = null;

        ModContainer contain = null;
        for (ModContainer container : Loader.instance().getModList())
        {
            if (container.getMod() != null && container.getMod().getClass().equals(c))
            {
                contain = container;
                obj = container.getMod();
                break;
            }
        }

        if (obj == null || contain == null)
        {
            throw new RuntimeException(c + " isn't an loaded mod class!");
        }

        modid = contain.getModId() + "--" + contain.getVersion();

        if (modClasses.add(c))
        {
            OutputHandler.felog.info("Modules from " + modid + " are being loaded");
        }
        return obj;
    }
}
