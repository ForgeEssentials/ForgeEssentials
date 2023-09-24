package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

@FEBiomeProvider(providerName = "the_bumblezone:biome_source")
public class BumbleZoneBiomeProviderHelper extends BiomeProviderHolderBase {
	@Override
	public BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed) {
		return ProvidersReflection.getBiomeProvider(getClassName(),
				new Class<?>[] { long.class, Registry.class }, new Object[] { seed, biomes });
	}

	@Override
	public String getClassName() {
		return "com.telepathicgrunt.the_bumblezone.world.dimension.BzBiomeProvider";
	}
}
