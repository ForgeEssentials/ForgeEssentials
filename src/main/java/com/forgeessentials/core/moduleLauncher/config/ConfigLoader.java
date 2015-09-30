package com.forgeessentials.core.moduleLauncher.config;

import net.minecraftforge.common.config.Configuration;

public interface ConfigLoader
{

    void load(Configuration config, boolean isReload);

    boolean supportsCanonicalConfig();

}
