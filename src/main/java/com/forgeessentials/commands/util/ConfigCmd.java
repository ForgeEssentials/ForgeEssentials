package com.forgeessentials.commands.util;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.config.ConfigLoaderBase;

public class ConfigCmd extends ConfigLoaderBase {

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment("general", "General Commands configuration.");
        CommandRegistrar.commandConfigs(config);
    }

    @Override
    public void save(Configuration config)
    {
        config.addCustomCategoryComment("general", "General Commands configuration.");
        CommandRegistrar.commandConfigs(config);
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return false;
    }
    
}
