package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEChunkGenProvider;
import com.google.common.collect.ImmutableMap;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;

@FEChunkGenProvider(providerName = "minecraft:flat")
public class MinecraftFlatChunkGeneratorHolder extends ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeSource biome,
			Supplier<NoiseGeneratorSettings> dimSettings) {
		List<FlatLayerInfo> defaultLayers = new ArrayList<>();
		defaultLayers.add(new FlatLayerInfo(1, Blocks.BEDROCK));
		defaultLayers.add(new FlatLayerInfo(3, Blocks.DIRT));
		defaultLayers.add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
		ImmutableMap<StructureFeature<?>, StructureFeatureConfiguration> villages = ImmutableMap
				.<StructureFeature<?>, StructureFeatureConfiguration>builder()
				.put(StructureFeature.VILLAGE, new StructureFeatureConfiguration(32, 8, 10387312)).build();
		return new FlatLevelSource(new FlatLevelGeneratorSettings(
                new StructureSettings(Optional.empty(), villages), biomes));
	}

	@Override
	public String getClassName() {
		return "net.minecraft.world.gen.FlatChunkGenerator";
	}
}
