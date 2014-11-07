package com.forgeessentials.core.config;

import net.minecraftforge.common.config.Configuration;

public interface IConfigLoader {

    void load(Configuration config, boolean isReload);

    void save(Configuration config);

    boolean supportsCanonicalConfig();
    
}
