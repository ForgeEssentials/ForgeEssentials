package com.forgeessentials.core.config;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.forgeessentials.commands.server.CommandHelp;
import com.forgeessentials.core.FEConfig;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.perftools.PerfToolsModule;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.forgeessentials.core.ForgeEssentials.MODID)
public class ConfigBase {

    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec MAIN_CONFIG;


    private static class ConfigFile
    {

        public ConfigFile(File path)
        {
            config = path;
        }

        public File config;

        public Set<ConfigLoader> loaders = new HashSet<>();

        public Set<ConfigLoader> loaded = new HashSet<>();

    }

    private File rootDirectory;

    private Map<String, ConfigFile> configFiles = new HashMap<>();

    private boolean useCanonicalConfig = false;

    private String mainConfigName;

    public ConfigBase(File rootDirectory, String mainConfigName)
    {
        this.rootDirectory = rootDirectory;
        this.mainConfigName = mainConfigName;
        load(false);
    }
    
    private ConfigFile getConfigFile(String configName)
    {
        ConfigFile loaders = configFiles.get(configName);
        if (loaders == null)
        {
            loaders = new ConfigFile(new File(this.rootDirectory, configName + ".toml"));
            configFiles.put(configName, loaders);
        }
        return loaders;
    }
    
    public static void registerConfig(){
        FEConfig.load(SERVER_BUILDER);
        ForgeEssentials.load(SERVER_BUILDER, true);//always true since We can't detect reloads?
        PerfToolsModule.load(SERVER_BUILDER);
        CommandHelp.load(SERVER_BUILDER);
        MAIN_CONFIG = SERVER_BUILDER.build();
    }




    public static void loadConfig(ForgeConfigSpec spec, Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        
        configData.load();
        spec.setConfig(configData);
    }
    
    public String getMainConfigName()
    {
        return mainConfigName;
    }

    public ForgeConfigSpec getMainConfig()
    {
        return getConfig(mainConfigName);
    }
}
