package com.forgeessentials.core.moduleLauncher.config;

import net.minecraftforge.common.config.Configuration;

public interface ConfigSaver extends ConfigLoader
{

    void save(Configuration config);

}
