package com.forgeessentials.core.moduleLauncher;

import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.objectweb.asm.Type;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.APIRegistry.ForgeEssentialsRegistrar;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.config.ConfigBase;
import com.forgeessentials.core.moduleLauncher.config.ConfigLoader;
import com.forgeessentials.util.events.ConfigReloadEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleCommonSetupEvent;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ObjectHolder;

import static net.minecraftforge.registries.ForgeRegistry.REGISTRIES;

public class ModuleLauncher
{
    public ModuleLauncher()
    {
        instance = this;
    }

    public static ModuleLauncher instance;

    private static TreeMap<String, ModuleContainer> containerMap = new TreeMap<String, ModuleContainer>();

    private static final Type MOD = Type.getType(FEModule.class);
    
    public void preLoad(FMLCommonSetupEvent e)
    {
        LoggingHandler.felog.info("Discovering and loading modules...");

        final List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> MOD.equals(a.getAnnotationType()))
                .collect(Collectors.toList());

        Map<Type, String> classModIds = Maps.newHashMap();
        Map<Type, Class<?>> classCache = Maps.newHashMap();

        // Gather all @FEModule classes
        annotations.stream().filter(a -> MOD.equals(a.getAnnotationType())).forEach(data -> classModIds.put(data.getClassType(), (String)data.getAnnotationData().get("value")));

            LoggingHandler.felog.info(REGISTRIES,"Found {} ObjectHolder annotations", annotations.size());
            
        // started ASM handling for the module loading
        //Set<ASMData> data = e.getAsmData().getAll(FEModule.class.getName());
        
        // LOAD THE MODULES!
        ModuleContainer temp, other;
        for (ModFileScanData.AnnotationData asm : annotations)
        {
            temp = new ModuleContainer(asm);
            if (temp.isLoadable && !APIRegistry.FE_EVENTBUS.post(new ModuleRegistrationEvent(temp)))
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

        for (ModContainer container : ModList.mods)
            if (container.getMod() != null)
                map.scanObject(container);

        // Check modules for callables
        for (ModuleContainer module : containerMap.values())
            map.scanObject(module);

        // Register modules with configuration manager
        for (ModuleContainer module : containerMap.values())
        {
            /* TODO Not usable until dynamic configs are re-added
            if (module.module instanceof ConfigLoader)
            {
                LoggingHandler.felog.debug("Registering configuration for FE module " + module.name);
                ForgeEssentials.getConfigManager().registerLoader(module.name, (ConfigLoader) module.module, false);
            }
            else
            {
                LoggingHandler.felog.debug("No configuration for FE module " + module.name);
            }*/
        }

        APIRegistry.getFEEventBus().post(new FEModuleCommonSetupEvent(e));

        // TODO Check if this works
        ConfigBase.BakeConfigs(false);
        // ForgeEssentials.getConfigManager().load(false);
    }

    public void reloadConfigs()
    {
        // TODO Check if this works
        ConfigBase.BakeConfigs(true);
        // ForgeEssentials.getConfigManager().load(true);
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

    @Nullable
    public static ModuleContainer getModuleContainer(String slug)
    {
        return containerMap.getOrDefault(slug, null);
    }
}
