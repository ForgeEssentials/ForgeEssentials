package com.forgeessentials.core.moduleLauncher;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.objectweb.asm.Type;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.config.ConfigLoader;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

public class ModuleLauncher
{
    public ModuleLauncher()
    {
        instance = this;
    }

    public static ModuleLauncher instance;

    private static TreeMap<String, ModuleContainer> containerMap = new TreeMap<>();

    private static final Type MOD = Type.getType(FEModule.class);

    public void init()
    {
        LoggingHandler.felog.info("Discovering and loading modules...");

        final List<ModFileScanData.AnnotationData> data = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations).flatMap(Collection::stream)
                .filter(a -> MOD.equals(a.annotationType())).collect(Collectors.toList());

        LoggingHandler.felog.info("Found {} FEModule annotations", data.size());
        for (ModFileScanData.AnnotationData asm : data)
        {
            LoggingHandler.felog.debug("Found FEModule {}", asm.memberName());
        }

        // Create THE MODULES!
        ModuleContainer temp, other;
        for (ModFileScanData.AnnotationData asm : data)
        {
            temp = new ModuleContainer(asm);
            if(temp.version<ForgeEssentials.CURRENT_MODULE_VERSION) {
            	temp.isLoadable=false;
            	LoggingHandler.felog.error("Module: [" + temp.name + "] is outdated! Please update this module to use the latest dev jar! Disabling Module!");
            }
            if(temp.version>ForgeEssentials.CURRENT_MODULE_VERSION) {
            	temp.isLoadable=false;
            	LoggingHandler.felog.error("Module: [" + temp.name + "] is too new for this version of ForgeEssentials! Please update your ForgeEssentials installation! Disabling Module!");
            }
            if (temp.isLoadable && !APIRegistry.FE_EVENTBUS.post(new ModuleRegistrationEvent(temp)))
            {
                //LoggingHandler.felog.debug("Checking if contanerMap contains: " + temp.name);
                if (containerMap.containsKey(temp.name))
                {
                    other = containerMap.get(temp.name);
                    if (temp.doesOverride && other.mod == ForgeEssentials.instance)
                    {
                        LoggingHandler.felog.debug("Duplicate module overrided the existing one");
                        containerMap.put(temp.name, temp);
                    }
                    else if (temp.mod == ForgeEssentials.instance && other.doesOverride)
                    {
                        LoggingHandler.felog.debug("Duplicate module was overrided by the existing one");
                        continue;
                    }
                    else
                    {
                        throw new RuntimeException(
                                "{FE-Module-Launcher} " + temp.name + " is conflicting with " + other.name);
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

        // Register modules with configuration manager
        for (ModuleContainer module : containerMap.values())
        {
            if (module.module instanceof ConfigLoader)
            {
                LoggingHandler.felog.debug("Registering configuration for FE module " + module.name);
                ForgeEssentials.getConfigManager().registerSpecs((ConfigLoader) module.module);
            }
            else
            {
                LoggingHandler.felog.debug("No configuration for FE module " + module.name);
            }
        }

        ConfigBase.getModuleConfig().saveConfig();

        ForgeEssentials.getConfigManager().loadAllRegisteredConfigs();
        ForgeEssentials.getConfigManager().buildAllRegisteredConfigs();
        // Moved to ServerAboutToStart Event in Main Class
        // ForgeEssentials.getConfigManager().bakeAllRegisteredConfigs(false);
    }

    public void reloadConfigs()
    {
        ForgeEssentials.getConfigManager().bakeAllRegisteredConfigs(true);
        APIRegistry.getFEEventBus().post(new ConfigReloadEvent());
    }

    public void unregister(String moduleName)
    {
        ModuleContainer container = containerMap.get(moduleName);
        try
        {
            if (container == null)
            {
                LoggingHandler.felog.error("Module " + moduleName + " has a null containerMap entry!");
            }
            if (container.module == null)
            {
                LoggingHandler.felog
                        .error("Module " + moduleName + " has a null module entry in the containerMap entry!");
            }
            MinecraftForge.EVENT_BUS.unregister(container.module);
            LoggingHandler.felog.error("Un-Registered module:  " + moduleName);
        }
        catch (NullPointerException e)
        {
            LoggingHandler.felog.error("Failed to un-register module:  " + moduleName);
            LoggingHandler.felog.error(
                    "This could be a major issue, if anything unexpected happens please contact the ForgeEssentials team!");
        }
        containerMap.remove(moduleName);
    }

    public void handleModuleParents() {
    	for(ModuleContainer mod :getModuleMap().values()) {
    		mod.handleParentMod();
    	}
    }

    public static Collection<String> getModuleList()
    {
        return containerMap.keySet();
    }

    public static Map<String, ModuleContainer> getModuleMap()
    {
        return containerMap;
    }

    @Nullable
    public static ModuleContainer getModuleContainer(String slug)
    {
        return containerMap.getOrDefault(slug, null);
    }
}
