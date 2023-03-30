package com.forgeessentials.core.config;

import java.io.File;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = com.forgeessentials.core.ForgeEssentials.MODID)
public class ConfigBase
{
    protected static ModuleConfig moduleConfig;

    private Set<ConfigLoader> loaders = new HashSet<>();

    private Set<ConfigLoader> loaded = new HashSet<>();

    private Set<ConfigLoader> loadedBuilders = new HashSet<>();

    private Set<ForgeConfigSpec> specs = new HashSet<>();

    private Set<ForgeConfigSpec> loadedSpecs = new HashSet<>();

    private static File rootDirectory;

    public ConfigBase(File rootDirectory)
    {
        ConfigBase.rootDirectory = rootDirectory;
        moduleConfig = new ModuleConfig();
    }

    public void registerSpecs(String configName, ConfigLoader loader)
    {
    	//make list of unique specs or config files
    	if (!specs.contains(loader.returnData().getSpec()))
    		specs.add(loader.returnData().getSpec());
    	
    	loaders.add(loader);
    }

    public void loadAllRegisteredConfigs()
    {
        LoggingHandler.felog.info("Loading configuration files");
        
        for (ConfigLoader loader : loaders)
        {
        	if (loaded.contains(loader))
                continue;
            loaded.add(loader);
            loader.load(loader.returnData().getSpecBuilder(), false);
        }

        LoggingHandler.felog.debug("Finished loading configuration files");
    }

    public void buildAllRegisteredConfigs() {
    	LoggingHandler.felog.info("Building configuration files");
    	for (ForgeConfigSpec spec : specs)
    	{
        	for(ConfigLoader loader : loaders)
        	{
        		if(loadedBuilders.contains(loader))
        			continue;
        		
        		if(loadedSpecs.contains(loader.returnData().getSpec()) && !loadedBuilders.contains(loader))
        		{
        			loadedBuilders.add(loader);
        			continue;
        		}else {
        			if(loader.returnData().getSpec()==spec && !loadedBuilders.contains(loader)) {
        				loader.returnData().setSpec(loader.returnData().getSpecBuilder().build());
        				loadedBuilders.add(loader);
        				loadedSpecs.add(spec);
        				registerConfigManual(loader.returnData().getSpec(),loader.returnData().getName(),true);
        			}
        		}
        	}
        }
        LoggingHandler.felog.debug("Finished building configuration files");
    }

    public void bakeAllRegisteredConfigs(boolean reload)
    {
        LoggingHandler.felog.info("Baking configuration files");
        for (ConfigLoader loader : loaders)
        {
            //loader.bakeConfig(reload);
            LoggingHandler.felog.info(loader.returnData().getName());
        }
        
        LoggingHandler.felog.debug("Finished baking configuration files");
    }

    public static void registerConfigManual(ForgeConfigSpec spec, Path path, boolean autoSave)
    {
    	LoggingHandler.felog.debug("Registering configuration file: "+path);
        if (autoSave) {
            final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                    .sync()
                    .autosave()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            configData.load();
            spec.setConfig(configData);
        }else {
            final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                    .sync()
                    .writingMode(WritingMode.REPLACE)
                    .build();
            configData.load();
            spec.setConfig(configData);
        }
    }

    public static void registerConfigManual(ForgeConfigSpec spec, String name, boolean autoSave)
    {
    	LoggingHandler.felog.debug("Registering configuration file: "+name);
    	FEModConfig peModConfig;
        if (autoSave) {
            peModConfig = new FEModConfig(ForgeEssentials.MOD_CONTAINER, ModConfig.Type.SERVER, spec, name, true);
//            final CommentedFileConfig configData = CommentedFileConfig.builder(rootDirectory+"/"+name+".toml")
//                    .sync()
//                    .autosave()
//                    .writingMode(WritingMode.REPLACE)
//                    .build();
//            configData.load();
//            spec.setConfig(configData);
        }else {
            peModConfig = new FEModConfig(ForgeEssentials.MOD_CONTAINER, ModConfig.Type.SERVER, spec, name, false);
//            final CommentedFileConfig configData = CommentedFileConfig.builder(rootDirectory+"/"+name+".toml")
//                    .sync()
//                    .writingMode(WritingMode.REPLACE)
//                    .build();
//            configData.load();
//            spec.setConfig(configData);
        }
        ForgeEssentials.MOD_CONTAINER.addConfig(peModConfig);
    }

    public static void registerConfigAutomatic(List<ConfigData> data)
    {
    	LoggingHandler.felog.debug("Registering configuration files AUTO");
        for(ConfigData config : data) {
            FEModConfig peModConfig = new FEModConfig(ForgeEssentials.MOD_CONTAINER, ModConfig.Type.SERVER, config.getSpec(), config.getName(), true);
            ForgeEssentials.MOD_CONTAINER.addConfig(peModConfig);
//            final CommentedFileConfig configData = CommentedFileConfig.builder(rootDirectory+"/"+Name+".toml")
//                    .sync()
//                    .autosave()
//                    .writingMode(WritingMode.REPLACE)
//                    .build();
//
//            configData.load();
//            spec.setConfig(configData);
        }
        LoggingHandler.felog.debug("Finished registering configuration files AUTO");
    }

    public String getMainConfigName()
    {
        return "main";
    }

    public static ModuleConfig getModuleConfig()
    {
        return moduleConfig;
    }
}
