package com.forgeessentials.multiworld.v2.utils;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.Set;

import com.forgeessentials.multiworld.v2.utils.MultiworldException.Type;
import com.forgeessentials.util.output.logger.LoggingHandler;

import java.util.ArrayList;
import java.util.TreeMap;
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
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.NoiseChunkGenerator;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProviderHelper {
    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    protected Map<String, DimensionType> dimensionTypes = new TreeMap<>();

    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    protected Map<String, DimensionType> vanillaDimensionTypes = new TreeMap<>();

    /**
     * Mapping from DimensionSettings names to DimensionSettings objects
     */
    protected Map<String, DimensionSettings> dimensionSettings = new TreeMap<>();

    /**
     * Mapping from BiomeProvider names to BiomeProvider objects
     */
    protected Map<String, String> biomeProviderTypes = new TreeMap<>();

    /**
     * Mapping from BiomeProvider names to BiomeProvider objects
     */
    protected Map<String, String> chunkGenerators = new TreeMap<>();
	
    // ============================================================
    // DimensionType management

    /**
     * Returns the {@Link DimensionType} for a given dimensionType {@Link String}
     */
    public DimensionType getDimensionTypeByName(String dimensionType) throws MultiworldException
    {
    	DimensionType type = dimensionTypes.get(dimensionType);
        if (type == null)
            throw new MultiworldException(Type.NO_DIMENSION_TYPE);
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
			dimensionTypes.put(provider.getKey().location().toString(), provider.getValue());
			if(provider.getKey().location().getNamespace().equals("minecraft")) {
				vanillaDimensionTypes.put(provider.getKey().location().toString(), provider.getValue());
			}
		}

        LoggingHandler.felog.debug("[Multiworld] Available dimension types:");
        for (String worldType : dimensionTypes.keySet())
            LoggingHandler.felog.debug("# " + worldType);
    }

    public Map<String, DimensionType> getDimensionTypes()
    {
        return dimensionTypes;
    }
    // ============================================================
    // BiomeProvider management


    /**
     * Returns the {@Link BiomeProvider} for a given biomeProvider {@Link String}
     */
    public BiomeProvider generateBiomeProviderByName(String biomeProviderType, Registry<Biome> biomes, long seed) throws MultiworldException
    {
    	if(!biomeProviderTypes.containsKey(biomeProviderType)) {
    		throw new MultiworldException(Type.NO_BIOME_PROVIDER);
    	}
    	String classPath = biomeProviderTypes.get(biomeProviderType);
    	BiomeProvider type=null;
		switch (biomeProviderType) {
		case ("minecraft:vanilla_layered"):
			type = new OverworldBiomeProvider(seed, false, false, biomes);
			break;
		case ("minecraft:multi_noise"):
			type = NetherBiomeProvider.Preset.NETHER.biomeSource(biomes, seed);
			break;
		case ("minecraft:the_end"):
			type = new EndBiomeProvider(biomes, seed);
			break;
		case ("minecraft:fixed"):
			type = new SingleBiomeProvider(biomes.get(Biomes.PLAINS));
			break;
		case ("minecraft:checkerboard"):
			final List<Supplier<Biome>> allowedBiomes = new ArrayList<>();
			Registry<Biome> biomes1 = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
			for (Entry<RegistryKey<Biome>, Biome> biome : biomes1.entrySet()) {
				allowedBiomes.add(() -> {return biome.getValue();});
			}
			type = new CheckerboardBiomeProvider(allowedBiomes, 2);
			break;
		case ("twilightforest:grid"):
			type = ProvidersReflection.getBiomeProvider(classPath, new Class<?>[] {long.class, Registry.class}, new Object[] {seed, biomes});
			break;
		case ("twilightforest:smart_distribution"):
			type = ProvidersReflection.getBiomeProvider(classPath, new Class<?>[] {long.class, Registry.class}, new Object[] {seed, biomes});
			break;
		case ("lotr:middle_earth"):
			type = ProvidersReflection.getBiomeProvider(classPath, new Class<?>[] {long.class, boolean.class, Registry.class}, new Object[] {seed, false, biomes});
			break;
		case ("lotr:middle_earth_classic"):
			type = ProvidersReflection.getBiomeProvider(classPath, new Class<?>[] {long.class, boolean.class, Registry.class}, new Object[] {seed, true, biomes});
			break;
		default:
			if (type == null)
				throw new MultiworldException(Type.NO_BIOME_PROVIDER);
		}
        return type;
    }
    
	/**
     * Builds the map of valid { @Link BiomeProvider}
     */
    public void loadBiomeProviders()
    {
    	Map<String, String> biomeProviderInvalidated = new TreeMap<>();
    	//Vanilla Overworld Biome Provider
    	biomeProviderInvalidated.put("minecraft:vanilla_layered", "net.minecraft.world.biome.provider.OverworldBiomeProvider");
    	//Vanilla Nether Biome Provider
    	biomeProviderInvalidated.put("minecraft:multi_noise", "net.minecraft.world.biome.provider.NetherBiomeProvider");
    	//Vanilla End Biome Provider
    	biomeProviderInvalidated.put("minecraft:the_end", "net.minecraft.world.biome.provider.EndBiomeProvider");
    	//Vanilla Single Biome Provider
    	biomeProviderInvalidated.put("minecraft:fixed", "net.minecraft.world.biome.provider.SingleBiomeProvider");
    	//Vanilla Checkerboard Biome Provider
    	biomeProviderInvalidated.put("minecraft:checkerboard", "net.minecraft.world.biome.provider.EndBiomeProvider");
    	//TwilightForest Biome Provider
    	biomeProviderInvalidated.put("twilightforest:grid", "twilightforest.world.TFBiomeProvider");
    	//TwilightForest Secondary? Biome Provider
    	biomeProviderInvalidated.put("twilightforest:smart_distribution", "twilightforest.world.TFBiomeDistributor");
    	//TheLordoftheRingsModRenewed Biome Provider
    	biomeProviderInvalidated.put("lotr:middle_earth", "lotr.common.world.biome.provider.MiddleEarthBiomeProvider");
    	//TheLordoftheRingsModRenewed Biome Provider in classic biome mode
    	biomeProviderInvalidated.put("lotr:middle_earth_classic", "lotr.common.world.biome.provider.MiddleEarthBiomeProvider");

    	for (Entry<String, String> biomeProvType : biomeProviderInvalidated.entrySet()) {
    		try {
				if(Class.forName(biomeProvType.getValue()) != null) {
					biomeProviderTypes.put(biomeProvType.getKey(), biomeProvType.getValue());
				}
			} catch (ClassNotFoundException e) {
				 LoggingHandler.felog.debug("Removed Invalid BiomeProvider type: "+ biomeProvType.getValue());
			}
    	}
        LoggingHandler.felog.debug("[Multiworld] Available biome providers:");
        for (String biomeType : biomeProviderTypes.keySet())
            LoggingHandler.felog.debug("# " + biomeType);
        for (String biomeClassName : biomeProviderTypes.values())
            LoggingHandler.felog.debug("$ " + biomeClassName);
    }

    public Set<String> getBiomeProviders()
    {
        return biomeProviderTypes.keySet();
    }

    // ============================================================
    // ChunkGenerator management


    /**
     * Returns the {@Link ChunkGenerator} for a given chunkGenerator {@Link String}
     */
    public ChunkGenerator generateChunkGeneratorByName(Registry<Biome> biomes, String chunkGeneratorType, BiomeProvider biome, long seed, Supplier<DimensionSettings> dimSettings) throws MultiworldException
    {
    	if(!chunkGenerators.containsKey(chunkGeneratorType)) {
    		throw new MultiworldException(Type.NO_CHUNK_GENERATOR);
    	}
    	String classPath = chunkGenerators.get(chunkGeneratorType);
    	ChunkGenerator type=null;
		switch (chunkGeneratorType) {
		case ("minecraft:noise"):
			type = new NoiseChunkGenerator(biome, seed, dimSettings);
			break;
		case ("minecraft:flat"):
			//type = new FlatChunkGenerator();
			break;
		case ("minecraft:debug"):
			type = new DebugChunkGenerator(biomes);
			break;
		case ("twilightforest:featured_noise"):
			type = ProvidersReflection.getChunkProvider(classPath, new Class<?>[] {BiomeProvider.class, long.class, Supplier.class}, new Object[] {biome, seed, dimSettings});
			break;
		case ("twilightforest:sky_noise"):
			type = ProvidersReflection.getChunkProvider(classPath, new Class<?>[] {BiomeProvider.class, long.class, Supplier.class}, new Object[] {biome, seed, dimSettings});
			break;
		case ("lotr:middle_earth"):
			type = ProvidersReflection.getChunkProvider(classPath, new Class<?>[] {BiomeProvider.class, long.class, Supplier.class, Optional.class}, new Object[] {biome, seed, dimSettings, Optional.of(true)});
			break;
		default:
			if (type == null)
				throw new MultiworldException(Type.NO_BIOME_PROVIDER);
		}
        return type;
    }
    
	/**
     * Builds the map of valid { @Link ChunkGenerator}
     */
    public void loadChunkGenerators()
    {
    	Map<String, String> chunkGeneratorInvalidated = new TreeMap<>();
    	//Vanilla Noise Chunk Generator
    	chunkGeneratorInvalidated.put("minecraft:noise", "net.minecraft.world.gen.NoiseChunkGenerator");
    	//Vanilla Flat Chunk Generator
    	chunkGeneratorInvalidated.put("minecraft:flat", "net.minecraft.world.gen.FlatChunkGenerator");
    	//Vanilla Debug Chunk Generator
    	chunkGeneratorInvalidated.put("minecraft:debug", "net.minecraft.world.gen.DebugChunkGenerator");
    	//TwilightForest Chunk Generator
    	chunkGeneratorInvalidated.put("twilightforest:featured_noise", "twilightforest.world.ChunkGeneratorTwilightForest");
    	//TwilightForest Sky Chunk Generator
    	chunkGeneratorInvalidated.put("twilightforest:sky_noise", "twilightforest.world.ChunkGeneratorTwilightSky");
    	//TheLordoftheRingsModRenewed Middle Earth Chunk Generator
    	chunkGeneratorInvalidated.put("lotr:middle_earth", "lotr.common.world.gen.MiddleEarthChunkGenerator");

    	for (Entry<String, String> chunkGeneratorsFull : chunkGeneratorInvalidated.entrySet()) {
    		try {
				if(Class.forName(chunkGeneratorsFull.getValue()) != null) {
					chunkGenerators.put(chunkGeneratorsFull.getKey(), chunkGeneratorsFull.getValue());
				}
			} catch (ClassNotFoundException e) {
				 LoggingHandler.felog.debug("Removed Invalid ChunkGenerator: "+ chunkGeneratorsFull.getValue());
			}
    	}
        LoggingHandler.felog.debug("[Multiworld] Available Chunk Generators:");
        for (String generatorName : chunkGenerators.keySet())
            LoggingHandler.felog.debug("# " + generatorName);
        for (String generatorNameClassName : chunkGenerators.values())
            LoggingHandler.felog.debug("$ " + generatorNameClassName);
    }

    public Set<String> getChunkGenerators()
    {
        return chunkGenerators.keySet();
    }

    // ============================================================
    // DimensionSettings management

    /**
     * Returns the {@Link DimensionSettings} for a given dimensionSettings {@Link String}
     */
    public DimensionSettings getDimensionSettingsByName(String dimensionSetting) throws MultiworldException
    {
    	DimensionSettings type = dimensionSettings.get(dimensionSetting);
        if (type == null)
            throw new MultiworldException(Type.NO_DIMENSION_SETTINGS);
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
			dimensionSettings.put(provider.getKey().location().toString(), provider.getValue());
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
