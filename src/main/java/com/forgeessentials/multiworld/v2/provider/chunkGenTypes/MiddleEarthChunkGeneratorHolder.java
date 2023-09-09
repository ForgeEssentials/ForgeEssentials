package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.Optional;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

public class MiddleEarthChunkGeneratorHolder implements ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeProvider biome,
			Supplier<DimensionSettings> dimSettings) {
		return ProvidersReflection.getChunkProvider(getClassName(),
				new Class<?>[] { BiomeProvider.class, long.class, Supplier.class, Optional.class },
				new Object[] { biome, seed, dimSettings, Optional.of(true) });
	}

	@Override
	public String getClassName() {
		return "lotr.common.world.gen.MiddleEarthChunkGenerator";
	}
}
