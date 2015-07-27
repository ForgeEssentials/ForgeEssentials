package com.forgeessentials.core.moduleLauncher;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModuleLauncher
{
    public ModuleLauncher()
    {
        instance = this;
    }

    public static ModuleLauncher instance;
    
    private static TreeMap<String, ModuleContainer> containerMap = new TreeMap<String, ModuleContainer>();

    public void preLoad(FMLPreInitializationEvent e)
    {
        LoggingHandler.felog.info("Discovering and loading modules...");

        // started ASM handling for the module loading
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
                LoggingHandler.felog.debug("Discovered FE module " + temp.name);
            }
        }

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
            if (container.getMod() != null)
                map.scanObject(container);

        // Check modules for callables
        for (ModuleContainer module : containerMap.values())
            map.scanObject(module);

        // Register modules with configuration manager
        for (ModuleContainer module : containerMap.values())
        {
            if (module.module instanceof ConfigLoader)
            {
                LoggingHandler.felog.debug("Registering configuration for FE module " + module.name);
                ForgeEssentials.getConfigManager().registerLoader(module.name, (ConfigLoader) module.module, false);
            }
            else
            {
                LoggingHandler.felog.debug("No configuration for FE module " + module.name);
            }
        }

        APIRegistry.getFEEventBus().post(new FEModulePreInitEvent(e));

        ForgeEssentials.getConfigManager().load(false);
    }

    public void reloadConfigs()
    {
        ForgeEssentials.getConfigManager().load(true);
        APIRegistry.getFEEventBus().post(new ConfigReloadEvent());
    }

    public void unregister(String moduleName)
    {
        ModuleContainer container = containerMap.get(moduleName);
        APIRegistry.getFEEventBus().unregister(container.module);
        containerMap.remove(moduleName);
    }

    public static Collection<String> getModuleList()
    {
        return containerMap.keySet();
    }

    public static Map<String, ModuleContainer> getModuleMap()
    {
        return containerMap;
    }
}
