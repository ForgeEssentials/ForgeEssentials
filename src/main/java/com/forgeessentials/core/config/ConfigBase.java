package com.forgeessentials.core.config;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import com.forgeessentials.util.output.LoggingHandler;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = com.forgeessentials.core.ForgeEssentials.MODID)
public class ConfigBase
{
    protected static ModuleConfig moduleConfig;

    private Set<ConfigLoader> loaders = new HashSet<>();

    private Set<ConfigLoader> loadedLoaders = new HashSet<>();

    private Set<ConfigLoader> builtLoaders = new HashSet<>();


    public static Predicate<Object> stringValidator = a -> a instanceof String;
    public static Predicate<Object> intValidator = b -> b instanceof Integer;
    public static Predicate<Object> booleanValidator = c -> c instanceof Boolean;

    public ConfigBase(File rootDirectory1)
    {
        //rootDirectory = rootDirectory1;
        moduleConfig = new ModuleConfig();
    }

    public void registerSpecs(ConfigLoader loader)
    {
    	//make list of unique specs or config files
    	if (!loaders.contains(loader))
    	    loaders.add(loader);
    }
    public void loadNBuildSpec(ConfigLoader loader)
    {
    	//make list of unique specs or config files
    	if (!loaders.contains(loader))
    	    loaders.add(loader);
    	loadConfig(loader);
    	buildConfig(loader);
    }
    public void loadNBuildNBakeSpec(ConfigLoader loader)
    {
    	//make list of unique specs or config files
    	if (!loaders.contains(loader))
    	    loaders.add(loader);
    	loadConfig(loader);
    	buildConfig(loader);
    	bakeConfig(loader, false);
    }

    /*
     * Should only be called once
     * */
    public void loadAllRegisteredConfigs()
    {
        LoggingHandler.felog.info("Loading configuration files");
        
        for (ConfigLoader loader : loaders)
        {
        	if (loadedLoaders.contains(loader)) {
        	    LoggingHandler.felog.error("Configuration file: "+loader.returnData().getName()+" is alredy loaded");
                continue;
        	}
        	if (builtLoaders.contains(loader)) {
                LoggingHandler.felog.error("Configuration file: "+loader.returnData().getName()+" is alredy built");
                continue;
            }

        	loadedLoaders.add(loader);
            LoggingHandler.felog.debug("Loading configuration file: "+loader.returnData().getName());
            loader.load(loader.returnData().getSpecBuilder(), false);
        }

        LoggingHandler.felog.debug("Finished loading configuration files");
    }
    public void loadConfig(ConfigLoader loader)
    {
        if (loadedLoaders.contains(loader)) {
    	    LoggingHandler.felog.error("Configuration file: "+loader.returnData().getName()+" is alredy loaded");
            return;
    	}
    	if (builtLoaders.contains(loader)) {
            LoggingHandler.felog.error("Configuration file: "+loader.returnData().getName()+" is alredy built");
            return;
        }

    	loadedLoaders.add(loader);
        LoggingHandler.felog.debug("Loading configuration file: "+loader.returnData().getName());
        loader.load(loader.returnData().getSpecBuilder(), false);
    }

    /*
     * Should only be called once
     * */
    public void buildAllRegisteredConfigs() {
    	LoggingHandler.felog.info("Building configuration files");
    	for(ConfigLoader loader : loaders)
        {
    	    if(!loadedLoaders.contains(loader))
            {
                builtLoaders.add(loader);
                LoggingHandler.felog.error("Cant Build config: "+loader.returnData().getName()+" because it hasen't been loaded");
                continue;
            }
            if(builtLoaders.contains(loader)) {
                LoggingHandler.felog.error("Configuration file: "+loader.returnData().getName()+" is alredy built");
                continue;
            }else {
                LoggingHandler.felog.debug("Building configuration file : "+loader.returnData().getName());
                loader.returnData().setSpec(loader.returnData().getSpecBuilder().build());
                builtLoaders.add(loader);
                registerConfigManual(loader.returnData().getSpec(),loader.returnData().getName(),true);
            }
        }
        LoggingHandler.felog.debug("Finished building configuration files");
    }
    public void buildConfig(ConfigLoader loader)
    {
    	if(!loadedLoaders.contains(loader))
        {
            builtLoaders.add(loader);
            LoggingHandler.felog.error("Cant Build config: "+loader.returnData().getName()+" because it hasen't been loaded");
            return;
        }
        if(builtLoaders.contains(loader)) {
            LoggingHandler.felog.error("Configuration file: "+loader.returnData().getName()+" is alredy built");
            return;
        }else {
            LoggingHandler.felog.debug("Building configuration file : "+loader.returnData().getName());
            loader.returnData().setSpec(loader.returnData().getSpecBuilder().build());
            builtLoaders.add(loader);
            registerConfigManual(loader.returnData().getSpec(),loader.returnData().getName(),true);
        }
    }

    /*
     * Can be called any number of times
     * loading, building, and baking needing to be done after this is called
     * use ForgeEssentials.getConfigManager().loadNBuildNBakeSpec(ConfigLoader);
     * */
    public void bakeAllRegisteredConfigs(boolean reload)
    {
        LoggingHandler.felog.info("Baking configuration files");
        for (ConfigLoader loader : loaders)
        {
            if(!loadedLoaders.contains(loader))
            {
                builtLoaders.add(loader);
                LoggingHandler.felog.error("Cant Bake config: "+loader.returnData().getName()+" because it hasen't been loaded");
                continue;
            }
            if(!builtLoaders.contains(loader)) {
                LoggingHandler.felog.error("Cant Bake config: "+loader.returnData().getName()+" because it hasen't been built");
                continue;
            }
            LoggingHandler.felog.info("Baked config:"+loader.returnData().getName());
            loader.bakeConfig(reload);
        }
        
        LoggingHandler.felog.debug("Finished baking configuration files");
    }
    public void bakeConfig(ConfigLoader loader,boolean reload)
    {
        if(!loadedLoaders.contains(loader))
        {
            builtLoaders.add(loader);
            LoggingHandler.felog.error("Cant Bake config: "+loader.returnData().getName()+" because it hasen't been loaded");
            return;
        }
        if(!builtLoaders.contains(loader)) {
            LoggingHandler.felog.error("Cant Bake config: "+loader.returnData().getName()+" because it hasen't been built");
            return;
        }
        LoggingHandler.felog.info("Baked config:"+loader.returnData().getName());
        loader.bakeConfig(reload);
    }
/*
    public static void registerConfigManual(ForgeConfigSpec spec, Path path, boolean autoSave)
    {
    	LoggingHandler.felog.debug("Registering configuration fileZYA: "+path);
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
*/
    public static void registerConfigManual(ForgeConfigSpec spec, String name, boolean autoSave)
    {
    	LoggingHandler.felog.debug("Registering configuration fileM: "+name);
    	FEModConfig peModConfig;
        if (autoSave) {
            LoggingHandler.felog.debug("Registering configuration fileT: "+name);
            peModConfig = new FEModConfig(ModLoadingContext.get().getActiveContainer(), ModConfig.Type.SERVER, spec, name, true);
//            final CommentedFileConfig configData = CommentedFileConfig.builder(rootDirectory+"/"+name+".toml")
//                    .sync()
//                    .autosave()
//                    .writingMode(WritingMode.REPLACE)
//                    .build();
//            configData.load();
//            spec.setConfig(configData);
        }else {
            LoggingHandler.felog.debug("Registering configuration fileF: "+name);
            peModConfig = new FEModConfig(ModLoadingContext.get().getActiveContainer(), ModConfig.Type.SERVER, spec, name, false);
//            final CommentedFileConfig configData = CommentedFileConfig.builder(rootDirectory+"/"+name+".toml")
//                    .sync()
//                    .writingMode(WritingMode.REPLACE)
//                    .build();
//            configData.load();
//            spec.setConfig(configData);
        }
        ModLoadingContext.get().getActiveContainer().addConfig(peModConfig);
        LoggingHandler.felog.debug("Registering done for configuration fileF: "+name);

    }

//    public static void registerConfigAutomatic(List<ConfigData> data)
//    {
//    	LoggingHandler.felog.debug("Registering configuration files AUTO");
//        for(ConfigData config : data) {
//            FEModConfig peModConfig = new FEModConfig(ForgeEssentials.MOD_CONTAINER, ModConfig.Type.SERVER, config.getSpec(), config.getName(), true);
//            ForgeEssentials.MOD_CONTAINER.addConfig(peModConfig);
//            final CommentedFileConfig configData = CommentedFileConfig.builder(rootDirectory+"/"+Name+".toml")
//                    .sync()
//                    .autosave()
//                    .writingMode(WritingMode.REPLACE)
//                    .build();
//
//            configData.load();
//            spec.setConfig(configData);
//        }
//        LoggingHandler.felog.debug("Finished registering configuration files AUTO");
//    }

    public String getMainConfigName()
    {
        return "main";
    }

    public static ModuleConfig getModuleConfig()
    {
        return moduleConfig;
    }
}
