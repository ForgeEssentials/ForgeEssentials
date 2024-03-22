package com.forgeessentials.multiworld.v2.provider;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

public abstract class BiomeProviderHolderBase {
	public abstract BiomeSource createBiomeProvider(Registry<Biome> biomes, long seed);
	public abstract String getClassName();
}
