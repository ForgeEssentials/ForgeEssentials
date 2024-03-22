package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.Optional;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEChunkGenProvider;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.feature.configurations.StructureFeatureConfiguration;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;

@FEChunkGenProvider(providerName = "the_bumblezone:chunk_generator")
public class BumbleZoneChunkGeneratorHolder extends ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeSource biome,
			Supplier<NoiseGeneratorSettings> dimSettings) {
		StructureFeature<?> str1 = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation("the_bumblezone:pollinated_stream"));
		StructureFeature<?> str2 = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation("the_bumblezone:honey_cave_room"));
		if(str1 == null) {throw new NullPointerException("BumbleZone biome 1 is null");}
		if(str2 == null) {throw new NullPointerException("BumbleZone biome 2 is null");}
		StructureSettings settings = new StructureSettings(Optional.empty(), ImmutableMap.<StructureFeature<?>, StructureFeatureConfiguration>builder()
				.put(str1, new StructureFeatureConfiguration(8, 6, 938497222))
				.put(str2, new StructureFeatureConfiguration(3, 1, 722299384)).build());
		return ProvidersReflection.getChunkProvider(getClassName(),
				new Class<?>[] { BiomeSource.class, StructureSettings.class },
				new Object[] { biome, settings });
	}

	@Override
	public String getClassName() {
		return "com.telepathicgrunt.the_bumblezone.world.dimension.BzChunkGenerator";
	}
}
