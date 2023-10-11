package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEChunkGenProvider;
import com.google.common.collect.ImmutableMap;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;

@FEChunkGenProvider(providerName = "minecraft:flat")
public class MinecraftFlatChunkGeneratorHolder extends ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeProvider biome,
			Supplier<DimensionSettings> dimSettings) {
		List<FlatLayerInfo> defaultLayers = new ArrayList<>();
		defaultLayers.add(new FlatLayerInfo(1, Blocks.BEDROCK));
		defaultLayers.add(new FlatLayerInfo(3, Blocks.DIRT));
		defaultLayers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
		ImmutableMap<Structure<?>, StructureSeparationSettings> villages = ImmutableMap
				.<Structure<?>, StructureSeparationSettings>builder()
				.put(Structure.VILLAGE, new StructureSeparationSettings(32, 8, 10387312)).build();
		return new FlatChunkGenerator(new FlatGenerationSettings(biomes,
				new DimensionStructuresSettings(Optional.empty(), villages), defaultLayers, false, false, Optional.of(() -> {
					return biomes.getOrThrow(Biomes.PLAINS);
				})));
	}

	@Override
	public String getClassName() {
		return "net.minecraft.world.gen.FlatChunkGenerator";
	}
}
