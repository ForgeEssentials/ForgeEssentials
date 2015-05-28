package com.forgeessentials.core.moduleLauncher.config;

import com.forgeessentials.util.OutputHandler;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConfigManager
{

    private static class ConfigFile
    {

        public ConfigFile(File path)
        {
            config = new Configuration(path, true);
        }

        public Configuration config;

        public Set<ConfigLoader> loaders = new HashSet<>();

        public Set<ConfigLoader> loaded = new HashSet<>();

    }

    private File rootDirectory;

    private Map<String, ConfigFile> configFiles = new HashMap<>();

    private boolean useCanonicalConfig = false;

    private String mainConfigName;

    public ConfigManager(File rootDirectory, String mainConfigName)
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
            loaders = new ConfigFile(new File(this.rootDirectory, configName + ".cfg"));
            configFiles.put(configName, loaders);
        }
        return loaders;
    }

    public Configuration getConfig(String configName)
    {
        return getConfigFile(configName).config;
    }

    public void registerLoader(String configName, ConfigLoader loader)
    {
        registerLoader(configName, loader, true);
    }

    public void registerLoader(String configName, ConfigLoader loader, boolean loadAfterRegistration)
    {
        if (useCanonicalConfig && loader.supportsCanonicalConfig())
            getConfigFile(mainConfigName).loaders.add(loader);
        else
            getConfigFile(configName).loaders.add(loader);
        if (loadAfterRegistration)
            load(false);
    }

    public void load(boolean reload)
    {
        OutputHandler.felog.finer("Loading configuration files");
        boolean changed = false;
        for (ConfigFile file : configFiles.values())
        {
            if (reload)
                file.config.load();
            for (ConfigLoader loader : file.loaders)
            {
                if (!reload)
                {
                    if (file.loaded.contains(loader))
                        continue;
                    file.loaded.add(loader);
                }
                changed |= true;
                loader.load(file.config, reload);
            }
            if (changed)
                file.config.save();
        }
        OutputHandler.felog.finer("Finished loading configuration files");
    }

    public void load(String configName)
    {
        ConfigFile file = configFiles.get(configName);
        if (file == null)
            return;
        for (ConfigLoader loader : file.loaders)
            loader.load(file.config, true);
        file.config.save();
    }

    public void saveAll()
    {
        OutputHandler.felog.finer("Saving configuration files");
        for (ConfigFile file : configFiles.values())
        {
            file.config.load();
            for (ConfigLoader loader : file.loaders)
                loader.save(file.config);
            file.config.save();
        }
    }

    public void save(String configName)
    {
        ConfigFile file = getConfigFile(configName);
        for (ConfigLoader loader : file.loaders)
            loader.save(file.config);
        file.config.save();
    }

    public boolean isUseCanonicalConfig()
    {
        return useCanonicalConfig;
    }

    public void setUseCanonicalConfig(boolean useCanonicalConfig)
    {
        this.useCanonicalConfig = useCanonicalConfig;
    }

    public String getMainConfigName()
    {
        return mainConfigName;
    }

    public Configuration getMainConfig()
    {
        return getConfig(mainConfigName);
    }

}
