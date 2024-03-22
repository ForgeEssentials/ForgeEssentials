package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEChunkGenProvider;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;

@FEChunkGenProvider(providerName = "minecraft:noise")
public class MinecraftNoiseChunkGeneratorHolder extends ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeSource biome,
			Supplier<NoiseGeneratorSettings> dimSettings) {
		return new NoiseBasedChunkGenerator(biome, seed, dimSettings);
	}

	@Override
	public String getClassName() {
		return "net.minecraft.world.gen.NoiseChunkGenerator";
	}
}
