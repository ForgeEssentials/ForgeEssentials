package com.forgeessentials.multiworld.v2;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.Set;

import com.forgeessentials.multiworld.v2.MultiworldException.Type;
import com.forgeessentials.util.output.logger.LoggingHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.biome.provider.CheckerboardBiomeProvider;
import net.minecraft.world.biome.provider.EndBiomeProvider;
import net.minecraft.world.biome.provider.NetherBiomeProvider;
import net.minecraft.world.biome.provider.OverworldBiomeProvider;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProviderHelper {
    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    protected Map<String, DimensionType> dimensionTypes = new HashMap<>();

    /**
     * Mapping from DimensionSettings names to DimensionSettings objects
     */
    protected Map<String, DimensionSettings> dimensionSettings = new HashMap<>();
    /**
     * Mapping from BiomeProvider names to BiomeProvider objects
     */
    protected Map<String, Class<? extends BiomeProvider>> biomeProviderTypes = new HashMap<>();
	
    // ============================================================
    // DimensionType management

    /**
     * Returns the {@Link DimensionType} for a given dimensionType {@Link String}
     */
    public DimensionType getDimensionTypeByName(String dimensionType) throws MultiworldException
    {
    	DimensionType type = dimensionTypes.get(dimensionType.toUpperCase());
        if (type == null)
            throw new MultiworldException(Type.NO_WORLDTYPE);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link DimensionType}
     */
    public void loadDimensionTypes()
    {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		DynamicRegistries registries = server.registryAccess();
		Registry<DimensionType> loadedProviders = registries.dimensionTypes();
		for (Entry<RegistryKey<DimensionType>, DimensionType> provider : loadedProviders.entrySet()) {
			dimensionTypes.put(provider.getKey().location().toString().toUpperCase(), provider.getValue());
		}

        LoggingHandler.felog.debug("[Multiworld] Available world types:");
        for (String worldType : dimensionTypes.keySet())
            LoggingHandler.felog.debug("# " + worldType);
    }

    public Map<String, DimensionType> getDimensionTypes()
    {
        return dimensionTypes;
    }
    // ============================================================
    // WorldProvider management


    /**
     * Returns the {@Link BiomeProvider} for a given biomeProvider {@Link String}
     */
    public BiomeProvider getBiomeProviderByName(String biomeProviderType, Registry<Biome> biomes, long seed) throws MultiworldException
    {
    	BiomeProvider type=null;
    	switch(biomeProviderType.toLowerCase()) {
    		case("single"):
    			type = new SingleBiomeProvider(biomes.get(Biomes.PLAINS));
    			break;
    		case("nether"):
    			type = NetherBiomeProvider.Preset.NETHER.biomeSource(biomes, seed);
    			break;
    		case("checkerboard"):
    			final List<Supplier<Biome>> allowedBiomes= new ArrayList<>();
    			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
    			DynamicRegistries registries = server.registryAccess();
    			Registry<Biome> biomes1 = registries.registryOrThrow(Registry.BIOME_REGISTRY);
    			for(Entry<RegistryKey<Biome>, Biome> biome : biomes1.entrySet()) {
    				allowedBiomes.add(() -> {return biome.getValue();});
    			}
    			type = new CheckerboardBiomeProvider(allowedBiomes, 2);
    			break;
    		case("overworld"):
    			type = new OverworldBiomeProvider(seed, false, false, biomes);
    			break;
    		case("end"):
    			type = new EndBiomeProvider(biomes, seed);
    			break;
    		default:
    			if (type == null)
    	            throw new MultiworldException(Type.NO_PROVIDER);
    	}
        return type;
    }
    
	/**
     * Builds the map of valid { @Link BiomeProvider}
     */
    public void loadBiomeProviders()
    {
    	biomeProviderTypes.put("Single",  SingleBiomeProvider.class);
    	biomeProviderTypes.put("Nether",  NetherBiomeProvider.class);
    	biomeProviderTypes.put("Checkerboard",  CheckerboardBiomeProvider.class);
    	biomeProviderTypes.put("Overworld",  OverworldBiomeProvider.class);
    	biomeProviderTypes.put("End",  EndBiomeProvider.class);
//		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
//		DynamicRegistries registries = server.registryAccess();
//		RegistryKey.create(Registry.BIOME_SOURCE_REGISTRY, new ResourceLocation(""));
//		Registry<Codec<? extends BiomeProvider>> loadedProviders = Registry.BIOME_SOURCE;
//		for (ServerWorld world : server.getAllLevels()) {
//			biomeProviderTypes.put(world.getBiomeManager(), world.getBiomeManager());
//		}

        LoggingHandler.felog.debug("[Multiworld] Available biome providers:");
        for (String biomeType : biomeProviderTypes.keySet())
            LoggingHandler.felog.debug("# " + biomeType);
    }

    public Set<String> getBiomeProviders()
    {
        return biomeProviderTypes.keySet();
    }
    // ============================================================
    // DimensionSettings management

    /**
     * Returns the {@Link DimensionSettings} for a given dimensionSettings {@Link String}
     */
    public DimensionSettings getDimensionSettingsByName(String dimensionSetting) throws MultiworldException
    {
    	DimensionSettings type = dimensionSettings.get(dimensionSetting.toUpperCase());
        if (type == null)
            throw new MultiworldException(Type.NO_WORLDTYPE);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link DimensionSettings}
     */
    public void loadDimensionSettings()
    {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		DynamicRegistries registries = server.registryAccess();
		for (Entry<RegistryKey<DimensionSettings>, DimensionSettings> provider : WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet()) {
			dimensionSettings.put(provider.getKey().location().toString().toUpperCase(), provider.getValue());
		}

        LoggingHandler.felog.debug("[Multiworld] Available dimension settings:");
        for (String worldType : dimensionSettings.keySet())
            LoggingHandler.felog.debug("# " + worldType);
    }

    public Map<String, DimensionSettings> getDimensionSettings()
    {
        return dimensionSettings;
    }
}
