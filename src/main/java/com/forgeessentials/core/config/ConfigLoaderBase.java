package com.forgeessentials.core.config;

import net.minecraftforge.common.config.Configuration;

public abstract class ConfigLoaderBase implements IConfigLoader {

    @Override
    public void save(Configuration config)
    {
    }

    @Override
    public boolean supportsCanonicalConfig()
    {
        return true;
    }

}
