package com.forgeessentials.core.moduleLauncher.config;

import net.minecraftforge.common.config.Configuration;

public interface IConfigLoader {

    void load(Configuration config, boolean isReload);

    void save(Configuration config);

    boolean supportsCanonicalConfig();

    public abstract static class ConfigLoaderBase implements IConfigLoader {
    
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
    
}
