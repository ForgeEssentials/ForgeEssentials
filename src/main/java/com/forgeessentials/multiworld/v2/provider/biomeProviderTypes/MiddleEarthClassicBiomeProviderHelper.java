package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

@FEBiomeProvider(providerName = "lotr:middle_earth_classic")
public class MiddleEarthClassicBiomeProviderHelper extends BiomeProviderHolderBase {
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
