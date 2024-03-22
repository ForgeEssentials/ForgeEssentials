package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.Optional;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEChunkGenProvider;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

@FEChunkGenProvider(providerName = "lotr:middle_earth")
public class MiddleEarthChunkGeneratorHolder extends ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeSource biome,
			Supplier<NoiseGeneratorSettings> dimSettings) {
		return ProvidersReflection.getChunkProvider(getClassName(),
				new Class<?>[] { BiomeSource.class, long.class, Supplier.class, Optional.class },
				new Object[] { biome, seed, dimSettings, Optional.of(true) });
	}

	@Override
	public String getClassName() {
		return "lotr.common.world.gen.MiddleEarthChunkGenerator";
	}
}
