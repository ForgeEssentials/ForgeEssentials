package com.forgeessentials.core.moduleLauncher.config;

import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigSaver extends ConfigLoader
{

    void save(ForgeConfigSpec config);

}
