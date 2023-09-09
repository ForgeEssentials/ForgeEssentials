package com.forgeessentials.multiworld.v2.provider;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MiddleEarthBiomeProviderHelper;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MiddleEarthClassicBiomeProviderHelper;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MinecraftCheckerboardBiomeProviderHolder;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MinecraftEndBiomeProviderHolder;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MinecraftNetherBiomeProviderHolder;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MinecraftOverworldBiomeProviderHolder;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MinecraftOverworldLargeBiomeProviderHolder;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.MinecraftSingleBiomeProviderHolder;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.TwilightForestBiomeProviderHelper;
import com.forgeessentials.multiworld.v2.provider.biomeProviderTypes.TwilightForestDistBiomeProviderHelper;
import com.forgeessentials.multiworld.v2.provider.chunkGenTypes.MiddleEarthChunkGeneratorHolder;
import com.forgeessentials.multiworld.v2.provider.chunkGenTypes.MinecraftDebugChunkGeneratorHolder;
import com.forgeessentials.multiworld.v2.provider.chunkGenTypes.MinecraftFlatChunkGeneratorHolder;
import com.forgeessentials.multiworld.v2.provider.chunkGenTypes.MinecraftNoiseChunkGeneratorHolder;
import com.forgeessentials.multiworld.v2.provider.chunkGenTypes.TwilightForestChunkGeneratorHolder;
import com.forgeessentials.multiworld.v2.provider.chunkGenTypes.TwilightForestSkyChunkGeneratorHolder;
import com.forgeessentials.multiworld.v2.utils.MultiworldException;
import com.forgeessentials.multiworld.v2.utils.MultiworldException.Type;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProviderHelper {
    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    private Map<String, DimensionType> dimensionTypes = new TreeMap<>();

    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    private Map<String, DimensionType> vanillaDimensionTypes = new TreeMap<>();

    /**
     * Mapping from DimensionSettings names to DimensionSettings objects
     */
    private Map<String, DimensionSettings> dimensionSettings = new TreeMap<>();

    /**
     * Mapping from BiomeProvider names to BiomeProvider objects
     */
    private Map<String, BiomeProviderHolderBase> biomeProviderTypes = new TreeMap<>();

    /**
     * Mapping from BiomeProvider names to BiomeProvider objects
     */
    private Map<String, ChunkGeneratorHolderBase> chunkGenerators = new TreeMap<>();
	
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
    public Map<String, DimensionType> getVanillaDimensionTypes()
    {
        return vanillaDimensionTypes;
    }
    // ============================================================
    // BiomeProvider management


    /**
     * Returns the {@Link BiomeProvider} for a given biomeProvider {@Link String}
     */
    public BiomeProvider generateBiomeProviderByName(String biomeProviderType, Registry<Biome> biomes, long seed) throws MultiworldException
    {
    	BiomeProvider type=null;
    	try {
    		BiomeProviderHolderBase holder =  biomeProviderTypes.get(biomeProviderType);
    		type = holder.createBiomeProvider(biomes, seed);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if (type == null)
			throw new MultiworldException(Type.NO_BIOME_PROVIDER);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link BiomeProvider}
     */
    public void loadBiomeProviders()
    {
    	Map<String, BiomeProviderHolderBase> biomeProviderInvalidated = new TreeMap<>();
    	//Vanilla Overworld Biome Provider
    	biomeProviderInvalidated.put("minecraft:overworld", new MinecraftOverworldBiomeProviderHolder());
    	//Vanilla Overworld large Biome Provider
    	biomeProviderInvalidated.put("minecraft:overworld_large", new MinecraftOverworldLargeBiomeProviderHolder());
    	//Vanilla Nether Biome Provider
    	biomeProviderInvalidated.put("minecraft:nether", new MinecraftNetherBiomeProviderHolder());
    	//Vanilla End Biome Provider
    	biomeProviderInvalidated.put("minecraft:end", new MinecraftEndBiomeProviderHolder());
    	//Vanilla Single Biome Provider
    	biomeProviderInvalidated.put("minecraft:single", new MinecraftSingleBiomeProviderHolder());
    	//Vanilla Checkerboard Biome Provider
    	biomeProviderInvalidated.put("minecraft:checkerboard", new MinecraftCheckerboardBiomeProviderHolder());
    	//TwilightForest Biome Provider
    	biomeProviderInvalidated.put("twilightforest:grid", new TwilightForestBiomeProviderHelper());
    	//TwilightForest Secondary? Biome Provider
    	biomeProviderInvalidated.put("twilightforest:smart_distribution", new TwilightForestDistBiomeProviderHelper());
    	//TheLordoftheRingsModRenewed Biome Provider
    	biomeProviderInvalidated.put("lotr:middle_earth", new MiddleEarthBiomeProviderHelper());
    	//TheLordoftheRingsModRenewed Biome Provider in classic biome mode
    	biomeProviderInvalidated.put("lotr:middle_earth_classic", new MiddleEarthClassicBiomeProviderHelper());

    	for (Entry<String, BiomeProviderHolderBase> biomeProvType : biomeProviderInvalidated.entrySet()) {
    		try {
				if(Class.forName(biomeProvType.getValue().getClassName()) != null) {
					biomeProviderTypes.put(biomeProvType.getKey(), biomeProvType.getValue());
				}
			} catch (ClassNotFoundException e) {
				 LoggingHandler.felog.debug("Removed Invalid BiomeProvider type: "+ biomeProvType.getValue().getClassName());
			}
    	}
        LoggingHandler.felog.debug("[Multiworld] Available biome providers:");
        for (String biomeType : biomeProviderTypes.keySet())
            LoggingHandler.felog.debug("# " + biomeType);
        for (BiomeProviderHolderBase biomeClassName : biomeProviderTypes.values())
            LoggingHandler.felog.debug("$ " + (biomeClassName.getClassName()));
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
    	ChunkGenerator type=null;
    	try {
    		ChunkGeneratorHolderBase holder =  chunkGenerators.get(chunkGeneratorType);
    		type = holder.createChunkGenerator(biomes, seed, biome, dimSettings);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	if (type == null)
			throw new MultiworldException(Type.NO_CHUNK_GENERATOR);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link ChunkGenerator}
     */
    public void loadChunkGenerators()
    {
    	Map<String, ChunkGeneratorHolderBase> chunkGeneratorInvalidated = new TreeMap<>();
    	//Vanilla Noise Chunk Generator
    	chunkGeneratorInvalidated.put("minecraft:noise", new MinecraftNoiseChunkGeneratorHolder());
    	//Vanilla Flat Chunk Generator
    	chunkGeneratorInvalidated.put("minecraft:flat", new MinecraftFlatChunkGeneratorHolder());
    	//Vanilla Debug Chunk Generator
    	chunkGeneratorInvalidated.put("minecraft:debug", new MinecraftDebugChunkGeneratorHolder());
    	//TwilightForest Chunk Generator
    	chunkGeneratorInvalidated.put("twilightforest:featured_noise", new TwilightForestChunkGeneratorHolder());
    	//TwilightForest Sky Chunk Generator
    	chunkGeneratorInvalidated.put("twilightforest:sky_noise", new TwilightForestSkyChunkGeneratorHolder());
    	//TheLordoftheRingsModRenewed Middle Earth Chunk Generator
    	chunkGeneratorInvalidated.put("lotr:middle_earth", new MiddleEarthChunkGeneratorHolder());

    	for (Entry<String, ChunkGeneratorHolderBase> chunkGeneratorsFull : chunkGeneratorInvalidated.entrySet()) {
    		try {
				if(Class.forName(chunkGeneratorsFull.getValue().getClassName()) != null) {
					chunkGenerators.put(chunkGeneratorsFull.getKey(), chunkGeneratorsFull.getValue());
				}
			} catch (ClassNotFoundException e) {
				 LoggingHandler.felog.debug("Removed Invalid ChunkGenerator: "+ chunkGeneratorsFull.getValue().getClassName());
			}
    	}
        LoggingHandler.felog.debug("[Multiworld] Available Chunk Generators:");
        for (String generatorName : chunkGenerators.keySet())
            LoggingHandler.felog.debug("# " + generatorName);
        for (ChunkGeneratorHolderBase generatorNameClassName : chunkGenerators.values())
            LoggingHandler.felog.debug("$ " + generatorNameClassName.getClassName());
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
