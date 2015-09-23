package com.forgeessentials.core.moduleLauncher.config;

import net.minecraftforge.common.config.Configuration;

public abstract class ConfigLoaderBase implements ConfigSaver
{

    @Override
    public void save(Configuration config)
    {
        /* do nothing */
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

}