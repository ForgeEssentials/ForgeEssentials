package com.forgeessentials.multiworld.v2.provider.biomeProviderTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.BiomeProviderHolderBase;
import com.forgeessentials.multiworld.v2.provider.FEBiomeProvider;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@FEBiomeProvider(providerName = "minecraft:checkerboard")
public class MinecraftCheckerboardBiomeProviderHolder extends BiomeProviderHolderBase {
	@Override
	public BiomeProvider createBiomeProvider(Registry<Biome> biomes, long seed) {
		final List<Supplier<Biome>> allowedBiomes = new ArrayList<>();
		Registry<Biome> biomes1 = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
		for (Entry<RegistryKey<Biome>, Biome> biome : biomes1.entrySet()) {
			allowedBiomes.add(() -> {return biome.getValue();});
		}
		return new CheckerboardBiomeProvider(allowedBiomes, 2);
	}

	@Override
	public String getClassName() {
		return CheckerboardBiomeProvider.class.getName();
	}
}
