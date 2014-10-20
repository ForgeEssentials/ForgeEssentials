package com.forgeessentials.core.moduleLauncher;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar;
import com.forgeessentials.core.CoreConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.command.ICommandSender;

import java.io.File;
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

public class ModuleLauncher {
    public ModuleLauncher()
    {
        instance = this;
    }

    public static ModuleLauncher instance;
    private static TreeMap<String, ModuleContainer> containerMap = new TreeMap<String, ModuleContainer>();

    public static String[] disabledModules;
    public static boolean useCanonicalConfig;

    public void preLoad(FMLPreInitializationEvent e)
    {
        OutputHandler.felog.info("Discovering and loading modules...");

        // started ASM handling for the module loading.
        Set<ASMData> data = e.getAsmData().getAll(FEModule.class.getName());

        // LOAD THE MODULES!
        ModuleContainer temp, other;
        for (ASMData asm : data)
        {
            temp = new ModuleContainer(asm);
            if (temp.isLoadable)
            {
                if (containerMap.containsKey(temp.name))
                {
                    other = containerMap.get(temp.name);
                    if (temp.doesOverride && other.mod == ForgeEssentials.instance)
                    {
                        containerMap.put(temp.name, temp);
                    }
                    else if (temp.mod == ForgeEssentials.instance && other.doesOverride)
                    {
                        continue;
                    }
                    else
                    {
                        throw new RuntimeException("{FE-Module-Launcher} " + temp.name + " is conflicting with " + other.name);
                    }
                }
                else
                {
                    containerMap.put(temp.name, temp);
                }

                temp.createAndPopulate();
                OutputHandler.felog.info("Loaded " + temp.name);
            }
        }

        Collection<ModuleContainer> modules = containerMap.values();

        CallableMap map = new CallableMap();

        data = e.getAsmData().getAll(ForgeEssentialsRegistrar.class.getName());
        Class<?> c;
        Object obj = null;
        for (ASMData asm : data)
        {
            try
            {
                obj = null;
                c = Class.forName(asm.getClassName());

                try
                {
                    obj = c.newInstance();
                    map.scanObject(obj);
                    // this works?? skip everything else and go on to the next one.
                    continue;
                }
                catch (Exception e1)
                {
                    // do nothing.
                }

                // if this isn't skipped.. it grabs the class, and all static methods.
                map.scanClass(c);

            }
            catch (ClassNotFoundException e1)
            {
                // nothing needed.
            }
        }

        for (ModContainer container : Loader.instance().getModList())
        {
            if (container.getMod() != null)
            {
                map.scanObject(container);
            }
        }

        // check modules for the CallableMap stuff.
        for (ModuleContainer module : modules)
        {
            map.scanObject(module);
        }

        // run the config init methods..
        boolean generate = false;
        for (ModuleContainer module : modules)
        {
            ModuleConfigBase cfg = module.getConfig();


            if (cfg != null)
            {
                if (cfg.universalConfigAllowed() && useCanonicalConfig)
                {
                    cfg.setFile(CoreConfig.mainconfig);
                }
                else
                {
                    cfg.setFile(new File(ForgeEssentials.FEDIR, module.name + "/config.cfg"));
                }

                File file = cfg.getFile();

                if (!file.getParentFile().exists())
                {
                    generate = true;
                    file.getParentFile().mkdirs();
                }

                if (!generate && (!file.exists() || !file.isFile()))
                {
                    generate = true;
                }

                cfg.setGenerate(generate);
                cfg.init();
            }
        }

        FunctionHelper.FE_INTERNAL_EVENTBUS.post(new FEModuleEvent.FEModulePreInitEvent(e));
    }

    public void reloadConfigs(ICommandSender sender)
    {
        ModuleConfigBase config;
        for (ModuleContainer module : containerMap.values())
        {
            config = module.getConfig();
            if (config != null)
            {
                config.forceLoad(sender);
            }
            module.runReload(sender);
        }
    }

    public void unregister(String moduleName)
    {
        ModuleContainer container = containerMap.get(moduleName);
        FunctionHelper.FE_INTERNAL_EVENTBUS.unregister(container.module);
        containerMap.remove(moduleName);
    }

    public static String[] getModuleList()
    {
        return containerMap.keySet().toArray(new String[] { });
    }
}
