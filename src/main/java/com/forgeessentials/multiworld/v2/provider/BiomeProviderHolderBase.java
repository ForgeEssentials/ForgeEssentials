package com.forgeessentials.multiworld.v2.provider;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public abstract class BiomeProviderHolderBase {
	public abstract BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed);
	public abstract String getClassName();
}
