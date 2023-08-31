package com.forgeessentials.multiworld.v2.providers;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.Set;

import com.forgeessentials.multiworld.v2.MultiworldException;
import com.forgeessentials.multiworld.v2.MultiworldException.Type;
import com.forgeessentials.util.output.logger.LoggingHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import net.minecraft.world.gen.DimensionSettings;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProviderHelper {
    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    protected Map<String, DimensionType> dimensionTypes = new TreeMap<>();

    /**
     * Mapping from DimensionSettings names to DimensionSettings objects
     */
    protected Map<String, DimensionSettings> dimensionSettings = new TreeMap<>();
    /**
     * Mapping from BiomeProvider names to BiomeProvider objects
     */
    protected Map<String, String> biomeProviderTypes = new TreeMap<>();
	
    // ============================================================
    // DimensionType management

    /**
     * Returns the {@Link DimensionType} for a given dimensionType {@Link String}
     */
    public DimensionType getDimensionTypeByName(String dimensionType) throws MultiworldException
    {
    	DimensionType type = dimensionTypes.get(dimensionType);
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
			dimensionTypes.put(provider.getKey().location().toString(), provider.getValue());
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
    	if(!biomeProviderTypes.containsKey(biomeProviderType)) {
    		throw new MultiworldException(Type.NO_BIOME_PROVIDER);
    	}
    	String classPath = biomeProviderTypes.get(biomeProviderType);
    	BiomeProvider type=null;
		switch (biomeProviderType) {
		case ("Minecraft_Overworld"):
			type = new OverworldBiomeProvider(seed, false, false, biomes);
			break;
		case ("Minecraft_Nether"):
			type = NetherBiomeProvider.Preset.NETHER.biomeSource(biomes, seed);
			break;
		case ("Minecraft_End"):
			type = new EndBiomeProvider(biomes, seed);
			break;
		case ("Minecraft_Single"):
			type = new SingleBiomeProvider(biomes.get(Biomes.PLAINS));
			break;
		case ("Minecraft_Checkerboard"):
			final List<Supplier<Biome>> allowedBiomes = new ArrayList<>();
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			DynamicRegistries registries = server.registryAccess();
			Registry<Biome> biomes1 = registries.registryOrThrow(Registry.BIOME_REGISTRY);
			for (Entry<RegistryKey<Biome>, Biome> biome : biomes1.entrySet()) {
				allowedBiomes.add(() -> {
					return biome.getValue();
				});
			}
			type = new CheckerboardBiomeProvider(allowedBiomes, 2);
			break;
		case ("TwilightForest"):
			type = getProvider(classPath, seed, biomes);
			break;
		case ("TwilightForest_Dis"):
			type = getProvider(classPath, seed, biomes);
			break;
		case ("Lotr_MiddleEarth"):
			type = getProvider(classPath, seed, false, biomes);
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
    	biomeProviderInvalidated.put("Minecraft_Overworld", "net.minecraft.world.biome.provider.OverworldBiomeProvider");
    	//Vanilla Nether Biome Provider
    	biomeProviderInvalidated.put("Minecraft_Nether", "net.minecraft.world.biome.provider.NetherBiomeProvider");
    	//Vanilla End Biome Provider
    	biomeProviderInvalidated.put("Minecraft_End", "net.minecraft.world.biome.provider.EndBiomeProvider");
    	//Vanilla Single Biome Provider
    	biomeProviderInvalidated.put("Minecraft_Single", "net.minecraft.world.biome.provider.SingleBiomeProvider");
    	//Vanilla Checkerboard Biome Provider
    	biomeProviderInvalidated.put("Minecraft_Checkerboard", "net.minecraft.world.biome.provider.EndBiomeProvider");
    	//TwilightForest Biome Provider
    	biomeProviderInvalidated.put("TwilightForest", "twilightforest.world.TFBiomeProvider");
    	//TwilightForest Secondary? Biome Provider
    	biomeProviderInvalidated.put("TwilightForest_Dis", "twilightforest.world.TFBiomeDistributor");
    	//TheLordoftheRingsModRenewed Biome Provider
    	biomeProviderInvalidated.put("Lotr_MiddleEarth", "lotr.common.world.biome.provider.MiddleEarthBiomeProvider");

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
    }

    public Set<String> getBiomeProviders()
    {
        return biomeProviderTypes.keySet();
    }

    private BiomeProvider getProvider(String className, Object... initargs){
		try {
			Class<?> clazz = Class.forName(className);
	    	Constructor<?> ctor = clazz.getConstructor(String.class);
	    	Object object = ctor.newInstance(new Object[] { initargs });
	    	if(object.getClass().isAssignableFrom(BiomeProvider.class)) {
	    		return (BiomeProvider) object;
	    	}
	    	return null;
	    	//throw new RuntimeException("RecievedProvider "+object.getClass().toString()+"Is not assignable from net.minecraft.world.biome.provider.BiomeProvider");
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
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
            throw new MultiworldException(Type.NO_WORLD_SETTINGS);
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
