package com.forgeessentials.multiworld.v2.provider;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public interface BiomeProviderHolderBase {
	BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed);
	String getClassName();
}
