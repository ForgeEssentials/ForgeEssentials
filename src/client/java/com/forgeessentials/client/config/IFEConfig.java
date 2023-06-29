package com.forgeessentials.client.config;

import com.forgeessentials.client.config.ValuesCached.ValueCachedPrimitive;
import com.forgeessentials.client.config.ValuesCached.ValueCachedResolvableConfig;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public interface IFEConfig {

	String getFileName();

	ForgeConfigSpec getConfigSpec();

	ModConfig.Type getConfigType();

	void clearListenerCache();

	<T, R> void addCachedValue(ValueCachedResolvableConfig<T, R> configValue);

	<T> void addCachedValue(ValueCachedPrimitive<T> configValue);

	default boolean addToContainer() {
		return true;
	}
}
