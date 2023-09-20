package com.forgeessentials.multiworld.v2.provider.chunkGenTypes;

import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.ChunkGeneratorHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEChunkGenProvider;
import com.forgeessentials.multiworld.v2.provider.ProvidersReflection;
import com.google.common.collect.ImmutableMap;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.util.ResourceLocation;

@FEChunkGenProvider(providerName = "the_bumblezone:chunk_generator")
public class BumbleZoneChunkGeneratorHolder extends ChunkGeneratorHolderBase {
	@Override
	public ChunkGenerator createChunkGenerator(Registry<Biome> biomes, long seed, BiomeProvider biome,
			Supplier<DimensionSettings> dimSettings) {
		Structure<?> str1 = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation("the_bumblezone:pollinated_stream"));
		Structure<?> str2 = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation("the_bumblezone:honey_cave_room"));
		if(str1 == null) {throw new NullPointerException("BumbleZone biome 1 is null");}
		if(str2 == null) {throw new NullPointerException("BumbleZone biome 2 is null");}
		DimensionStructuresSettings settings = new DimensionStructuresSettings(null, ImmutableMap.<Structure<?>, StructureSeparationSettings>builder()
				.put(str1, new StructureSeparationSettings(8, 6, 938497222))
				.put(str2, new StructureSeparationSettings(3, 1, 722299384)).build());
		return ProvidersReflection.getChunkProvider(getClassName(),
				new Class<?>[] { BiomeProvider.class, DimensionStructuresSettings.class },
				new Object[] { biome, settings });
	}

	@Override
	public String getClassName() {
		return "com.telepathicgrunt.the_bumblezone.world.dimension.BzChunkGenerator";
	}
}
