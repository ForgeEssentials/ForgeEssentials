package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

@FEBiomeProvider(providerName = "minecraft:checkerboard")
public class MinecraftCheckerboardBiomeProviderHolder extends BiomeProviderHolderBase {
	@Override
	public BiomeSource createBiomeProvider(Registry<Biome> biomes, long seed) {
		final List<Supplier<Biome>> allowedBiomes = new ArrayList<>();
		Registry<Biome> biomes1 = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		for (Entry<ResourceKey<Biome>, Biome> biome : biomes1.entrySet()) {
			allowedBiomes.add(() -> {return biome.getValue();});
		}
		return new CheckerboardColumnBiomeSource(allowedBiomes, 2);
	}

	@Override
	public String getClassName() {
		return CheckerboardColumnBiomeSource.class.getName();
	}
}
