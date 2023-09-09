package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public class MiddleEarthClassicBiomeProviderHelper implements BiomeProviderHolderBase {
	@Override
	public BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed) {
		return ProvidersReflection.getBiomeProvider(getClassName(),
				new Class<?>[] { long.class, boolean.class, Registry.class }, new Object[] { seed, true, biomes });
	}

	@Override
	public String getClassName() {
		return "lotr.common.world.biome.provider.MiddleEarthBiomeProvider";
	}
}
