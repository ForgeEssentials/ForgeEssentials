package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;

public class MinecraftOverworldLargeBiomeProviderHolder implements BiomeProviderHolderBase {
	@Override
	public BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed) {
		return new OverworldBiomeProvider(seed, false, true, biomes);
	}

	@Override
	public String getClassName() {
		return "net.minecraft.world.biome.provider.OverworldBiomeProvider";
	}
}
