package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

public class TwilightForestChunkGeneratorHolder implements ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeProvider biome,
			Supplier<DimensionSettings> dimSettings) {
		return ProvidersReflection.getChunkProvider(getClassName(),
				new Class<?>[] { BiomeProvider.class, long.class, Supplier.class },
				new Object[] { biome, seed, dimSettings });
	}

	@Override
	public String getClassName() {
		return "twilightforest.world.ChunkGeneratorTwilightForest";
	}
}
