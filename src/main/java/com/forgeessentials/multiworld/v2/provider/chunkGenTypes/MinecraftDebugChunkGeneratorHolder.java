package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;

public class MinecraftDebugChunkGeneratorHolder implements ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeProvider biome,
			Supplier<DimensionSettings> dimSettings) {
		return new DebugChunkGenerator(biomes);
	}

	@Override
	public String getClassName() {
		return "net.minecraft.world.gen.DebugChunkGenerator";
	}
}
