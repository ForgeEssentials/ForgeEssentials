package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

@FEBiomeProvider(providerName = "minecraft:nether")
public class MinecraftNetherBiomeProviderHolder extends BiomeProviderHolderBase {
	@Override
	public BiomeSource createBiomeProvider(Registry<Biome> biomes, long seed) {
		return MultiNoiseBiomeSource.Preset.NETHER.biomeSource(biomes, seed);
	}

	@Override
	public String getClassName() {
		return MultiNoiseBiomeSource.class.getName();
	}
}
