package com.forgeessentials.multiworld.v2;

import java.util.Map;
import java.util.Map.Entry;

import com.forgeessentials.multiworld.v2.MultiworldException.Type;
import com.forgeessentials.util.output.logger.LoggingHandler;

import java.util.HashMap;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ProviderHelper {
    public static final String PROVIDER_NORMAL = "normal";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";
    /**
     * Mapping from DimensionType names to DimensionType objects
     */
    protected Map<String, DimensionType> dimensionTypes = new HashMap<>();

    /**
     * Mapping from provider classnames to IDs
     */
    protected Map<String, String> worldProviderClasses = new HashMap<>();
	
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
		MutableRegistry<DimensionType> loadedProviders = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
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

//    public int getWorldProviderId(String providerName) throws MultiworldException
//    {
//        switch (providerName.toLowerCase())
//        {
//        // We use the hardcoded values as some mods just replace the class
//        // (BiomesOPlenty)
//        case PROVIDER_NORMAL:
//            return 0;
//        case PROVIDER_HELL:
//            return -1;
//        case PROVIDER_END:
//            return 1;
//        default:
//            // Otherwise we try to use the provider classname that was supplied
//            Integer providerId = worldProviderClasses.get(providerName);
//            if (providerId == null)
//                throw new MultiworldException(Type.NO_PROVIDER);
//            return providerId;
//        }
//    }
//
//    /**
//     * Use reflection to load the registered WorldProviders
//     */
//    public void loadWorldProviders()
//    {
//        try
//        {
//        	MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
//        	DynamicRegistries registries = server.registryAccess();
//            MutableRegistry<DimensionType> loadedProviders = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
//            for (Entry<RegistryKey<DimensionType>, DimensionType> provider : loadedProviders.entrySet())
//            {
//                worldProviderClasses.put(provider.getKey().getRegistryName().toString(), provider.getKey().location().toString());
//            }
//        }
//        catch (SecurityException | IllegalArgumentException e)
//        {
//            e.printStackTrace();
//        }
//        LoggingHandler.felog.debug("[Multiworld] Available world providers:");
//        for (Entry<String, String> provider : worldProviderClasses.entrySet())
//        {
//            LoggingHandler.felog.debug("# " + provider.getValue() + ":" + provider.getKey());
//        }
//    }
//
//    public Map<String, String> getWorldProviders()
//    {
//        return worldProviderClasses;
//    }
}
