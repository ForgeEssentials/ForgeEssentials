package com.forgeessentials.multiworld.v2;

import java.util.Map;
import java.util.HashMap;

import net.minecraft.world.DimensionType;

public class ProviderHelper {
    public static final String PROVIDER_NORMAL = "normal";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";
    /**
     * Mapping from worldType names to WorldType objects
     */
    protected Map<String, DimensionType> worldTypes = new HashMap<>();
    /**
     * Mapping from provider classnames to IDs
     */
    protected Map<String, String> worldProviderClasses = new HashMap<>();
	
//    // ============================================================
//    // WorldType management
//
//    /**
//     * Returns the WorldType for a given worldType string
//     */
//    public DimensionType getDimensionTypeByName(String worldType) throws MultiworldException
//    {
//    	DimensionType type = worldTypes.get(worldType.toUpperCase());
//        if (type == null)
//            throw new MultiworldException(Type.NO_WORLDTYPE);
//        return type;
//    }
//	/**
//     * Builds the map of valid worldTypes
//     */
//    public void loadWorldTypes()
//    {
//        for (int i = 0; i < DimensionType.WORLD_TYPES.length; ++i)
//        {
//        	DimensionType type = DimensionType.WORLD_TYPES[i];
//            if (type == null)
//                continue;
//
//            String name = type.getName().toUpperCase();
//
//            /*
//             * MC does not allow creation of this worldType, so we should not either
//             */
//            if (name.equals("DEFAULT_1_1"))
//                continue;
//
//            worldTypes.put(name, type);
//        }
//
//        LoggingHandler.felog.debug("[Multiworld] Available world types:");
//        for (String worldType : worldTypes.keySet())
//            LoggingHandler.felog.debug("# " + worldType);
//    }
//
//    public Map<String, DimensionType> getWorldTypes()
//    {
//        return worldTypes;
//    }
//    // ============================================================
//    // WorldProvider management
//
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
