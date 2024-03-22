package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.OverworldBiomeSource;

@FEBiomeProvider(providerName = "minecraft:overworld")
public class MinecraftOverworldBiomeProviderHolder extends BiomeProviderHolderBase {
	@Override
	public BiomeSource createBiomeProvider(Registry<Biome> biomes, long seed) {
		return new OverworldBiomeSource(seed, false, false, biomes);
	}

	@Override
	public String getClassName() {
		return OverworldBiomeSource.class.getName();
	}
}
