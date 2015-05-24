package com.forgeessentials.commands.util;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoader.ConfigLoaderBase;
import net.minecraftforge.common.config.Configuration;

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
