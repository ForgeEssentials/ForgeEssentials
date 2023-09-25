package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;

@FEBiomeProvider(providerName = "minecraft:overworld_large")
public class MinecraftOverworldLargeBiomeProviderHolder extends BiomeProviderHolderBase {
	@Override
	public BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed) {
		return new OverworldBiomeProvider(seed, false, true, biomes);
	}

	@Override
	public String getClassName() {
		return OverworldBiomeProvider.class.getName();
	}
}
