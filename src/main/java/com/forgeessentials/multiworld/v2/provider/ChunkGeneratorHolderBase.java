package com.forgeessentials.multiworld.v2.provider;

import java.util.function.Supplier;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

public abstract class ChunkGeneratorHolderBase {
	public abstract ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeProvider biome, Supplier<DimensionSettings> dimSettings);
	public abstract String getClassName();
}
