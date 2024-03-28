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
import com.google.common.collect.Iterables;

import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
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
    private Map<String, NoiseGeneratorSettings> dimensionSettings = new TreeMap<>();

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
		Iterable<ServerLevel> serverLevels = ServerLifecycleHooks.getCurrentServer().getAllLevels();

		LoggingHandler.felog.info("Found {} DimensionTypes", Iterables.size(serverLevels));
		for (ServerLevel serverLevel : serverLevels) {
			dimensionTypes.put(serverLevel.dimension().location().toString(), serverLevel.dimensionType());
			if(serverLevel.dimension().location().getNamespace().equals("minecraft")) {
				vanillaDimensionTypes.put(serverLevel.dimension().location().toString(), serverLevel.dimensionType());
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
    public BiomeSource generateBiomeProviderByName(String biomeProviderType, Registry<Biome> biomes, long seed) throws MultiworldException
    {
    	BiomeSource type=null;
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
				.filter(a -> MOD.equals(a.annotationType())).collect(Collectors.toList());

		for (ModFileScanData.AnnotationData asm : data) {
			try {
				Class<?> clazz = Class.forName(asm.memberName());
				if (BiomeProviderHolderBase.class.isAssignableFrom(clazz)) {
					BiomeProviderHolderBase handler = (BiomeProviderHolderBase) clazz.getDeclaredConstructor().newInstance();
					FEBiomeProvider annot = handler.getClass().getAnnotation(FEBiomeProvider.class);
					biomeProviderUntested.put(annot.providerName(), handler);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LoggingHandler.felog.debug("Could not load FEBiomeProvider: " + asm.memberName());
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
    public ChunkGenerator generateChunkGeneratorByName(Registry<Biome> biomes, String chunkGeneratorType, BiomeSource biome, long seed, Supplier<NoiseGeneratorSettings> dimSettings) throws MultiworldException
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
				.filter(a -> MOD.equals(a.annotationType())).collect(Collectors.toList());

		for (ModFileScanData.AnnotationData asm : data) {
			try {
				Class<?> clazz = Class.forName(asm.memberName());
				if (ChunkGeneratorHolderBase.class.isAssignableFrom(clazz)) {
					ChunkGeneratorHolderBase handler = (ChunkGeneratorHolderBase) clazz.getDeclaredConstructor().newInstance();
					FEChunkGenProvider annot = handler.getClass().getAnnotation(FEChunkGenProvider.class);
					chunkGeneratorUntested.put(annot.providerName(), handler);
				}
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				LoggingHandler.felog.debug("Could not load FEChunkGenProvider: " + asm.memberName());
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
    public NoiseGeneratorSettings getDimensionSettingsByName(String dimensionSetting) throws MultiworldException
    {
    	NoiseGeneratorSettings type = dimensionSettings.get(dimensionSetting);
        if (type == null)
            throw new MultiworldException(MultiworldException.Type.NO_DIMENSION_SETTINGS);
        return type;
    }
    
	/**
     * Builds the map of valid { @Link DimensionSettings}
     */
    public void loadDimensionSettings()
    {
    	LoggingHandler.felog.info("Found {} DimensionSettings", BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet().size());
		for (Entry<ResourceKey<NoiseGeneratorSettings>, NoiseGeneratorSettings> provider : BuiltinRegistries.NOISE_GENERATOR_SETTINGS.entrySet()) {
			dimensionSettings.put(provider.getKey().location().toString(), provider.getValue());
		}

        LoggingHandler.felog.debug("[Multiworld] Available dimension settings:");
        for (String worldType : dimensionSettings.keySet())
            LoggingHandler.felog.debug("# " + worldType);
    }

    public Map<String, NoiseGeneratorSettings> getDimensionSettings()
    {
        return dimensionSettings;
    }
}
