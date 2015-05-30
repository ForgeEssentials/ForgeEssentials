package com.forgeessentials.core.moduleLauncher;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.FEModuleEvent.FEModulePreInitEvent;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

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
        OutputHandler.felog.info("Discovering and loading modules...");

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
                OutputHandler.felog.info("Loaded " + temp.name);
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
                OutputHandler.felog.info("Registering configuration for FE module " + module.name);
                ForgeEssentials.getConfigManager().registerLoader(module.name, (ConfigLoader) module.module, false);
            }
            else
            {
                OutputHandler.felog.info("No configuration for FE module " + module.name);
            }
        }

        ForgeEssentials.BUS.post(new FEModulePreInitEvent(e));

        ForgeEssentials.getConfigManager().load(false);
    }

    public void reloadConfigs(ICommandSender sender)
    {
        ForgeEssentials.getConfigManager().load(true);
        for (ModuleContainer module : containerMap.values())
            module.runReload(sender);
    }

    public void unregister(String moduleName)
    {
        ModuleContainer container = containerMap.get(moduleName);
        ForgeEssentials.BUS.unregister(container.module);
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
