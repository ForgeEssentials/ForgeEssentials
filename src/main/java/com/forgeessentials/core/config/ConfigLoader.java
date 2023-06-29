package com.forgeessentials.core.config;

import net.minecraftforge.common.ForgeConfigSpec;

public interface ConfigLoader {
	// Implemented methods
	void load(ForgeConfigSpec.Builder BUILDER, boolean isReload);

	void bakeConfig(boolean reload);

	// Return data
	ConfigData returnData();
}