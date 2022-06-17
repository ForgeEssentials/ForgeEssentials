package com.forgeessentials.core.moduleLauncher.config;

import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigLoader
{

    void load(ForgeConfigSpec.Builder config, boolean isReload);

}
