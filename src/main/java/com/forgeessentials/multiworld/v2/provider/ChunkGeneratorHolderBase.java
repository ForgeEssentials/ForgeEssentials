package com.forgeessentials.multiworld.v2.provider;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public abstract class ChunkGeneratorHolderBase {
	public abstract ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeSource biome, Supplier<NoiseGeneratorSettings> dimSettings);
	public abstract String getClassName();
}
