package com.forgeessentials.multiworld.v2.provider;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.forgeessentials.multiworld.v2.utils.MultiworldException;
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
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.forgespi.language.ModFileScanData;

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
            throw new MultiworldException(MultiworldException.Type.NO_DIMENSION_TYPE);
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
		LoggingHandler.felog.info("Found {} DimensionTypes", loadedProviders.entrySet().size());
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
			throw new MultiworldException(MultiworldException.Type.NO_BIOME_PROVIDER);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link BiomeProvider}
     */
    public void loadBiomeProviders()
    {
    	Map<String, BiomeProviderHolderBase> biomeProviderUntested = new TreeMap<>();
    	final org.objectweb.asm.Type MOD = org.objectweb.asm.Type.getType(FEBiomeProvider.class);
    	
		final List<ModFileScanData.AnnotationData> data = ModList.get().getAllScanData().stream()
				.map(ModFileScanData::getAnnotations).flatMap(Collection::stream)
				.filter(a -> MOD.equals(a.getAnnotationType())).collect(Collectors.toList());

		for (ModFileScanData.AnnotationData asm : data) {
			try {
				Class<?> clazz = Class.forName(asm.getMemberName());
				if (BiomeProviderHolderBase.class.isAssignableFrom(clazz)) {
					BiomeProviderHolderBase handler = (BiomeProviderHolderBase) clazz.getDeclaredConstructor().newInstance();
					FEBiomeProvider annot = handler.getClass().getAnnotation(FEBiomeProvider.class);
					biomeProviderUntested.put(annot.providerName(), handler);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LoggingHandler.felog.debug("Could not load FEBiomeProvider: " + asm.getMemberName());
			} catch (IllegalArgumentException | SecurityException | NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}

    	for (Entry<String, BiomeProviderHolderBase> biomeProvType : biomeProviderUntested.entrySet()) {
    		try {
				if(Class.forName(biomeProvType.getValue().getClassName()) != null) {
					biomeProviderTypes.put(biomeProvType.getKey(), biomeProvType.getValue());
				}
			} catch (ClassNotFoundException e) {
				 LoggingHandler.felog.debug("Removed Invalid BiomeProvider type: "+ biomeProvType.getValue().getClassName());
			}
    	}

    	LoggingHandler.felog.info("Found {} FEBiomeProviders", biomeProviderTypes.size());
        LoggingHandler.felog.debug("[Multiworld] Available biome providers:");
        for (String biomeType : biomeProviderTypes.keySet())
            LoggingHandler.felog.debug("# " + biomeType);
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
			throw new MultiworldException(MultiworldException.Type.NO_CHUNK_GENERATOR);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link ChunkGenerator}
     */
    public void loadChunkGenerators()
    {
    	Map<String, ChunkGeneratorHolderBase> chunkGeneratorUntested = new TreeMap<>();
    	final org.objectweb.asm.Type MOD = org.objectweb.asm.Type.getType(FEChunkGenProvider.class);
    	
		final List<ModFileScanData.AnnotationData> data = ModList.get().getAllScanData().stream()
				.map(ModFileScanData::getAnnotations).flatMap(Collection::stream)
				.filter(a -> MOD.equals(a.getAnnotationType())).collect(Collectors.toList());

		for (ModFileScanData.AnnotationData asm : data) {
			try {
				Class<?> clazz = Class.forName(asm.getMemberName());
				if (ChunkGeneratorHolderBase.class.isAssignableFrom(clazz)) {
					ChunkGeneratorHolderBase handler = (ChunkGeneratorHolderBase) clazz.getDeclaredConstructor().newInstance();
					FEChunkGenProvider annot = handler.getClass().getAnnotation(FEChunkGenProvider.class);
					chunkGeneratorUntested.put(annot.providerName(), handler);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LoggingHandler.felog.debug("Could not load FEChunkGenProvider: " + asm.getMemberName());
			} catch (IllegalArgumentException | SecurityException | NoSuchMethodException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}

    	for (Entry<String, ChunkGeneratorHolderBase> chunkGeneratorsFull : chunkGeneratorUntested.entrySet()) {
    		try {
				if(Class.forName(chunkGeneratorsFull.getValue().getClassName()) != null) {
					chunkGenerators.put(chunkGeneratorsFull.getKey(), chunkGeneratorsFull.getValue());
				}
			} catch (ClassNotFoundException e) {
				 LoggingHandler.felog.debug("Removed Invalid ChunkGenerator: "+ chunkGeneratorsFull.getValue().getClassName());
			}
    	}

		LoggingHandler.felog.info("Found {} FEChunkGenProviders", chunkGenerators.size());
        LoggingHandler.felog.debug("[Multiworld] Available Chunk Generators:");
        for (String generatorName : chunkGenerators.keySet())
            LoggingHandler.felog.debug("# " + generatorName);
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
            throw new MultiworldException(MultiworldException.Type.NO_DIMENSION_SETTINGS);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link DimensionSettings}
     */
    public void loadDimensionSettings()
    {
    	LoggingHandler.felog.info("Found {} DimensionSettings", WorldGenRegistries.NOISE_GENERATOR_SETTINGS.entrySet().size());
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
